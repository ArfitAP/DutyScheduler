import { DatePipe } from '@angular/common';
import { Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { TranslateService } from '@ngx-translate/core';
import { IRoomDetail, IRoomJoinRequest, IRoomUser } from 'src/app/_models/Room';
import { IUserActivation } from 'src/app/_models/UserActivation';
import { LanguageService } from 'src/app/_services/language.service';
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

  // Join Requests
  joinRequests: IRoomJoinRequest[] = [];

  // Activation & Generation
  selectedMonth: Date = new Date();
  monthList: { value: Date; label: string }[] = [];
  userActivations: IUserActivation[] = [];
  generationMessage = '';
  statusMessage = '';

  // Day Hours
  dayHours: { day: string; hours: number; dayOfWeek: number }[] = [];
  dayNames: string[] = [];

  constructor(
    private route: ActivatedRoute,
    private roomService: RoomService,
    private tokenStorageService: TokenStorageService,
    private datePipe: DatePipe,
    private translate: TranslateService,
    private languageService: LanguageService
  ) { }

  ngOnInit(): void {
    this.userId = this.tokenStorageService.getUser().id;
    this.roomId = Number(this.route.snapshot.paramMap.get('id'));

    this.dayNames = this.languageService.getDayNamesSundayFirst();

    const monthNames = this.languageService.getMonthNames();
    for (let i = 0; i <= 12; i++) {
      let d = new Date(new Date().getFullYear(), new Date().getMonth() + i, 1);
      this.monthList.push({
        value: d,
        label: monthNames[d.getMonth()] + ' ' + d.getFullYear()
      });
    }
    this.selectedMonth = this.monthList[0].value;

    this.translate.onLangChange.subscribe(() => {
      this.dayNames = this.languageService.getDayNamesSundayFirst();
      const newMonthNames = this.languageService.getMonthNames();
      this.monthList = this.monthList.map((m, i) => ({
        ...m,
        label: newMonthNames[m.value.getMonth()] + ' ' + m.value.getFullYear()
      }));
    });

    this.loadRoom();
    this.loadActivations();
    this.loadDayHours();
    this.checkBusy();
  }

  loadRoom(): void {
    this.roomService.getRoomDetail(this.roomId).subscribe({
      next: (data) => {
        this.room = data;
        this.loadUsers();
        if (data.isOwner) {
          this.loadJoinRequests();
        }
      },
      error: () => { this.error = this.translate.instant('ROOM_MANAGE.ROOM_LOAD_ERROR'); }
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
        this.inviteMessage = this.translate.instant('ROOM_MANAGE.INVITATION_SENT');
      },
      error: () => {
        this.inviteMessage = this.translate.instant('ROOM_MANAGE.INVITE_ERROR');
      }
    });
  }

  removeMember(userId: number): void {
    this.roomService.removeMember(this.roomId, userId).subscribe({
      next: () => { this.loadRoom(); },
      error: () => { alert(this.translate.instant('ROOM_MANAGE.REMOVE_MEMBER_ERROR')); }
    });
  }

  loadJoinRequests(): void {
    this.roomService.getPendingJoinRequests(this.roomId).subscribe({
      next: (data) => { this.joinRequests = data; },
      error: () => { this.joinRequests = []; }
    });
  }

  approveJoinRequest(requestId: number): void {
    this.roomService.approveJoinRequest(requestId).subscribe({
      next: () => { this.loadRoom(); },
      error: () => { alert(this.translate.instant('ROOM_MANAGE.APPROVE_ERROR')); }
    });
  }

  rejectJoinRequest(requestId: number): void {
    this.roomService.rejectJoinRequest(requestId).subscribe({
      next: () => { this.loadJoinRequests(); },
      error: () => { alert(this.translate.instant('ROOM_MANAGE.REJECT_ERROR')); }
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
        if (res) alert(this.translate.instant('ROOM_MANAGE.ACTIVES_SAVED'));
        else alert(this.translate.instant('ROOM_MANAGE.ERROR'));
      },
      error: () => { alert(this.translate.instant('ROOM_MANAGE.ERROR')); }
    });
  }

  generateSchedule(): void {
    this.loading = true;
    this.generationMessage = '';
    let mon = this.datePipe.transform(this.selectedMonth, 'yyyy-MM-dd')!;
    this.roomService.generateSchedule(this.roomId, mon, this.userId).subscribe({
      next: (res) => {
        if (res) this.generationMessage = this.translate.instant('ROOM_MANAGE.GENERATION_STARTED');
        else this.generationMessage = this.translate.instant('ROOM_MANAGE.ERROR');
      },
      error: () => {
        this.generationMessage = this.translate.instant('ROOM_MANAGE.GENERATION_ERROR');
        this.loading = false;
      }
    });
  }

  checkBusy(): void {
    let mon = this.datePipe.transform(this.selectedMonth, 'yyyy-MM-dd')!;
    this.roomService.isServerBusy(this.roomId, mon).subscribe({
      next: (busy) => {
        this.loading = busy;
        this.statusMessage = busy
          ? this.translate.instant('ROOM_MANAGE.SERVER_BUSY')
          : this.translate.instant('ROOM_MANAGE.SERVER_FREE');
      },
      error: () => { this.statusMessage = this.translate.instant('ROOM_MANAGE.STATUS_CHECK_ERROR'); }
    });
  }

  // Day Hours
  loadDayHours(): void {
    let mon = this.datePipe.transform(this.selectedMonth, 'yyyy-MM-dd')!;

    let year = this.selectedMonth.getFullYear();
    let month = this.selectedMonth.getMonth();
    let numDays = new Date(year, month + 1, 0).getDate();

    this.dayHours = [];
    for (let i = 1; i <= numDays; i++) {
      let d = new Date(year, month, i);
      let dayStr = this.datePipe.transform(d, 'yyyy-MM-dd')!;
      let dow = d.getDay();
      let defaultHours = (dow === 0 || dow === 6) ? 16 : 8;
      this.dayHours.push({ day: dayStr, hours: defaultHours, dayOfWeek: dow });
    }

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
        if (res) alert(this.translate.instant('ROOM_MANAGE.DAY_HOURS_SAVED'));
        else alert(this.translate.instant('ROOM_MANAGE.ERROR'));
      },
      error: () => { alert(this.translate.instant('ROOM_MANAGE.ERROR')); }
    });
  }

  isWeekendOrHoliday(dayOfWeek: number): boolean {
    return dayOfWeek === 0 || dayOfWeek === 6;
  }
}
