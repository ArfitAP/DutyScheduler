import { DatePipe } from '@angular/common';
import { Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { TranslateService } from '@ngx-translate/core';
import { IUserApplication } from 'src/app/_models/UserApplication';
import { LanguageService } from 'src/app/_services/language.service';
import { RoomService } from 'src/app/_services/room.service';
import { TokenStorageService } from 'src/app/_services/token-storage.service';

@Component({
    selector: 'app-room-user-select',
    templateUrl: './room-user-select.component.html',
    styleUrls: ['../board-user/board-user.component.css'],
    providers: [DatePipe],
    standalone: false
})
export class RoomUserSelectComponent implements OnInit {

  dayNames: string[] = [];

  roomId = 0;
  roomName = '';
  userId = 0;
  isLoggedIn = false;

  selectedMonth: Date = new Date();
  monthList: { value: Date; label: string }[] = [];

  userApplication: IUserApplication = {
    id: 0, active: false, month: new Date(), grouped: false, user_id: 0, applicationDays: []
  };

  userSelectedDays: Date[] = [];
  notWantedDays: Date[] = [];
  groupSelected = false;
  monthWeeks: any[] = [];
  Holydays: any[] = [];

  constructor(
    private route: ActivatedRoute,
    private roomService: RoomService,
    private tokenStorageService: TokenStorageService,
    private datePipe: DatePipe,
    private translate: TranslateService,
    private languageService: LanguageService
  ) { }

  ngOnInit(): void {
    this.isLoggedIn = !!this.tokenStorageService.getToken();
    if (!this.isLoggedIn) return;

    this.dayNames = this.languageService.getDayNames();
    this.userId = this.tokenStorageService.getUser().id;
    this.roomId = Number(this.route.snapshot.paramMap.get('id'));

    this.roomService.getRoomDetail(this.roomId).subscribe({
      next: (data) => { this.roomName = data.name; }
    });

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
      this.dayNames = this.languageService.getDayNames();
      const newMonthNames = this.languageService.getMonthNames();
      this.monthList = this.monthList.map(m => ({
        ...m,
        label: newMonthNames[m.value.getMonth()] + ' ' + m.value.getFullYear()
      }));
    });

    this.loadMonth();
  }

  onMonthChange(): void {
    this.loadMonth();
  }

  loadMonth(): void {
    this.userSelectedDays = [];
    this.notWantedDays = [];
    this.monthWeeks = [];

    let numOfDays = this.getNumberOfDaysInMonth(this.selectedMonth.getMonth(), this.selectedMonth.getFullYear());
    let dayofweekfirstday = this.selectedMonth.getDay();
    if (dayofweekfirstday == 0) dayofweekfirstday = 7;

    let week: any[] = [];
    let currentDay = 1;

    for (let i = 1; i < dayofweekfirstday; i++) {
      week.push({ dayNo: currentDay++, hidden: true, date: null, selected: false, notWanted: false, holyday: false });
    }

    for (let i = 1; i <= numOfDays; i++) {
      if (currentDay > 7) { this.monthWeeks.push(week); week = []; currentDay = 1; }
      week.push({
        dayNo: currentDay++, hidden: false,
        date: this.datePipe.transform(new Date(this.selectedMonth.getFullYear(), this.selectedMonth.getMonth(), i), 'yyyy-MM-dd'),
        selected: false, notWanted: false, holyday: false
      });
    }

    if (currentDay > 1) {
      for (let i = currentDay; i <= 7; i++) {
        week.push({ dayNo: currentDay++, hidden: true, date: null, selected: false, notWanted: false, holyday: false });
      }
      this.monthWeeks.push(week);
    }

    let mon = this.datePipe.transform(this.selectedMonth, 'yyyy-MM-dd')!;

    this.roomService.getUserApplications(this.roomId, this.userId, mon).subscribe({
      next: (data) => {
        this.userApplication = data;
        this.userApplication.user_id = this.userId;
        try {
          this.userSelectedDays = this.userApplication.applicationDays.filter(i => i.wantedDay == 1).map(i => i.day);
          this.notWantedDays = this.userApplication.applicationDays.filter(i => i.wantedDay == 0).map(i => i.day);
          this.monthWeeks.forEach(w => w.forEach((d: any) => {
            if (this.userSelectedDays.includes(d.date)) d.selected = true;
            else if (this.notWantedDays.includes(d.date)) d.notWanted = true;
          }));
        } catch {
          this.userSelectedDays = [];
          this.notWantedDays = [];
        }
        this.checkGroupSelected();
      },
      error: () => { this.userSelectedDays = []; this.notWantedDays = []; }
    });

    this.roomService.getHolydays(mon).subscribe({
      next: (data) => {
        this.Holydays = data.map((item: any) => item.day);
        this.monthWeeks.forEach(w => w.forEach((d: any) => {
          if (this.Holydays.includes(d.date) || d.dayNo >= 6) d.holyday = true;
        }));
      },
      error: () => { this.Holydays = []; }
    });
  }

  save(): void {
    this.userApplication.applicationDays = this.userSelectedDays.map(d => ({ id: 0, day: d, wantedDay: 1 }));
    this.userApplication.applicationDays.push(...this.notWantedDays.map(d => ({ id: 0, day: d, wantedDay: 0 })));

    this.roomService.saveUserApplications(this.roomId, this.userApplication).subscribe({
      next: (res) => {
        if (res) alert(this.translate.instant('USER_SELECT.SELECTION_SAVED'));
        else alert(this.translate.instant('USER_SELECT.ERROR'));
      },
      error: () => { alert(this.translate.instant('USER_SELECT.ERROR')); }
    });
  }

  dayClick(date: string): void {
    this.monthWeeks.forEach(w => w.forEach((d: any) => {
      if (d.date == date) {
        if (this.userSelectedDays.includes(d.date)) {
          d.selected = false; d.notWanted = true;
          this.userSelectedDays = this.userSelectedDays.filter(x => x !== d.date);
          this.notWantedDays.push(d.date);
        } else if (this.notWantedDays.includes(d.date)) {
          d.selected = false; d.notWanted = false;
          this.notWantedDays = this.notWantedDays.filter(x => x !== d.date);
        } else {
          d.selected = true; d.notWanted = false;
          this.userSelectedDays.push(d.date);
        }
      }
    }));
    this.checkGroupSelected();
  }

  checkGroupSelected(): void {
    let days: number[] = [];
    this.monthWeeks.forEach(w => w.forEach((d: any) => {
      if (d.selected) days.push(Number(this.datePipe.transform(d.date, 'dd')));
    }));
    days.sort((a, b) => a - b);
    this.groupSelected = false;
    let last = -1;
    for (let i = 0; i < days.length; i++) {
      if (days[i] - 1 == last) { this.groupSelected = true; break; }
      last = days[i];
    }
    if (!this.groupSelected) {
      let hasNotWanted = this.monthWeeks.some(w => w.some((d: any) => d.notWanted));
      if (hasNotWanted) this.groupSelected = true;
      else this.userApplication.grouped = false;
    }
  }

  getNumberOfDaysInMonth(month: number, year: number): number {
    let sm = new Date(year, month, 1);
    let nm = new Date(sm.setMonth(sm.getMonth() + 1, 1));
    return new Date(nm.getFullYear(), nm.getMonth(), 0).getDate();
  }
}
