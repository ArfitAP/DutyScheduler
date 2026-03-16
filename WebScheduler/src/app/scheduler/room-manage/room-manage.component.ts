import { DatePipe } from '@angular/common';
import { Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { IRoomDetail, IRoomUser } from 'src/app/_models/Room';
import { IUserActivation } from 'src/app/_models/UserActivation';
import { RoomService } from 'src/app/_services/room.service';
import { TokenStorageService } from 'src/app/_services/token-storage.service';

@Component({
    selector: 'app-room-manage',
    templateUrl: './room-manage.component.html',
    styleUrls: ['./room-manage.component.css'],
    providers: [DatePipe],
    standalone: false
})
export class RoomManageComponent implements OnInit {

  monthNames: string[] = ['Siječanj', 'Veljača', 'Ožujak', 'Travanj', 'Svibanj', 'Lipanj', 'Srpanj', 'Kolovoz', 'Rujan', 'Listopad', 'Studeni', 'Prosinac'];

  roomId = 0;
  room: IRoomDetail | null = null;
  userId = 0;
  loading = false;
  error = '';

  // Invite
  allUsers: IRoomUser[] = [];
  filteredUsers: IRoomUser[] = [];
  searchText = '';
  inviteMessage = '';

  // Activation & Generation
  selectedMonth: Date = new Date();
  monthList: { value: Date; label: string }[] = [];
  userActivations: IUserActivation[] = [];
  generationMessage = '';
  statusMessage = '';

  // Day Hours
  dayHours: { day: string; hours: number; dayOfWeek: number }[] = [];
  dayNames: string[] = ['Ned', 'Pon', 'Uto', 'Sri', 'Čet', 'Pet', 'Sub'];

  constructor(
    private route: ActivatedRoute,
    private roomService: RoomService,
    private tokenStorageService: TokenStorageService,
    private datePipe: DatePipe
  ) { }

  ngOnInit(): void {
    this.userId = this.tokenStorageService.getUser().id;
    this.roomId = Number(this.route.snapshot.paramMap.get('id'));

    // Build month list: current month + 12 future months
    for (let i = 0; i <= 12; i++) {
      let d = new Date(new Date().getFullYear(), new Date().getMonth() + i, 1);
      this.monthList.push({
        value: d,
        label: this.monthNames[d.getMonth()] + ' ' + d.getFullYear()
      });
    }
    this.selectedMonth = this.monthList[0].value;

    this.loadRoom();
    this.loadActivations();
    this.loadDayHours();
    this.checkBusy();
  }

  loadRoom(): void {
    this.roomService.getRoomDetail(this.roomId).subscribe({
      next: (data) => {
        this.room = data;
        // Load users after room is loaded so filterUsers() works
        this.loadUsers();
      },
      error: () => { this.error = 'Greška pri učitavanju sobe'; }
    });
  }

  loadUsers(): void {
    this.roomService.getAllUsers().subscribe({
      next: (users) => {
        this.allUsers = users;
        this.filterUsers();
      }
    });
  }

  filterUsers(): void {
    if (!this.room) { this.filteredUsers = []; return; }
    const memberIds = this.room.members.map(m => m.id);
    let filtered = this.allUsers.filter(u => !memberIds.includes(u.id));
    if (this.searchText.trim()) {
      const search = this.searchText.toLowerCase();
      filtered = filtered.filter(u => u.username.toLowerCase().includes(search));
    }
    this.filteredUsers = filtered;
  }

  invite(userId: number): void {
    this.inviteMessage = '';
    this.roomService.inviteUser(this.roomId, userId).subscribe({
      next: () => {
        this.inviteMessage = 'Pozivnica poslana';
      },
      error: () => {
        this.inviteMessage = 'Greška - korisnik je već pozvan ili je već član';
      }
    });
  }

  removeMember(userId: number): void {
    this.roomService.removeMember(this.roomId, userId).subscribe({
      next: () => { this.loadRoom(); },
      error: () => { alert('Greška pri uklanjanju člana'); }
    });
  }

  onMonthChange(): void {
    this.generationMessage = '';
    this.statusMessage = '';
    this.checkBusy();
    this.loadActivations();
    this.loadDayHours();
  }

  loadActivations(): void {
    let mon = this.datePipe.transform(this.selectedMonth, 'yyyy-MM-dd')!;
    this.roomService.getUsersForActivation(this.roomId, mon).subscribe({
      next: (data) => { this.userActivations = data; },
      error: () => { this.userActivations = []; }
    });
  }

  saveActivations(): void {
    let mon = this.datePipe.transform(this.selectedMonth, 'yyyy-MM-dd')!;
    this.roomService.saveUserActives(this.roomId, mon, this.userId, this.userActivations).subscribe({
      next: (res) => {
        if (res) alert('Aktivni korisnici spremljeni');
        else alert('Greška!');
      },
      error: () => { alert('Greška!'); }
    });
  }

  generateSchedule(): void {
    this.loading = true;
    this.generationMessage = '';
    let mon = this.datePipe.transform(this.selectedMonth, 'yyyy-MM-dd')!;
    this.roomService.generateSchedule(this.roomId, mon, this.userId).subscribe({
      next: (res) => {
        if (res) this.generationMessage = 'Generiranje rasporeda pokrenuto!';
        else this.generationMessage = 'Greška!';
      },
      error: () => {
        this.generationMessage = 'Greška pri generiranju!';
        this.loading = false;
      }
    });
  }

  checkBusy(): void {
    let mon = this.datePipe.transform(this.selectedMonth, 'yyyy-MM-dd')!;
    this.roomService.isServerBusy(this.roomId, mon).subscribe({
      next: (busy) => {
        this.loading = busy;
        this.statusMessage = busy ? 'Server je zauzet generiranjem rasporeda' : 'Server je slobodan';
      },
      error: () => { this.statusMessage = 'Greška pri provjeri statusa'; }
    });
  }

  // Day Hours
  loadDayHours(): void {
    let mon = this.datePipe.transform(this.selectedMonth, 'yyyy-MM-dd')!;

    // Build all days of the selected month with defaults
    let year = this.selectedMonth.getFullYear();
    let month = this.selectedMonth.getMonth();
    let numDays = new Date(year, month + 1, 0).getDate();

    this.dayHours = [];
    for (let i = 1; i <= numDays; i++) {
      let d = new Date(year, month, i);
      let dayStr = this.datePipe.transform(d, 'yyyy-MM-dd')!;
      let dow = d.getDay(); // 0=Sun, 6=Sat
      let defaultHours = (dow === 0 || dow === 6) ? 16 : 8;
      this.dayHours.push({ day: dayStr, hours: defaultHours, dayOfWeek: dow });
    }

    // Load saved custom hours and override defaults
    this.roomService.getDayHours(this.roomId, mon).subscribe({
      next: (saved) => {
        for (let s of saved) {
          let found = this.dayHours.find(d => d.day === s.day);
          if (found) found.hours = s.hours;
        }
      }
    });
  }

  saveDayHours(): void {
    let mon = this.datePipe.transform(this.selectedMonth, 'yyyy-MM-dd')!;
    let payload = this.dayHours.map(d => ({ day: d.day, hours: d.hours }));
    this.roomService.setDayHours(this.roomId, mon, payload).subscribe({
      next: (res) => {
        if (res) alert('Sati po danima spremljeni');
        else alert('Greška!');
      },
      error: () => { alert('Greška!'); }
    });
  }

  isWeekendOrHoliday(dayOfWeek: number): boolean {
    return dayOfWeek === 0 || dayOfWeek === 6;
  }
}
