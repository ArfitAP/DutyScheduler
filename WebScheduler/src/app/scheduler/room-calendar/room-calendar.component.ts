import { DatePipe } from '@angular/common';
import { Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { ISchedule } from 'src/app/_models/Schedule';
import { ColorService } from 'src/app/_services/color.service';
import { RoomService } from 'src/app/_services/room.service';

@Component({
    selector: 'app-room-calendar',
    templateUrl: './room-calendar.component.html',
    styleUrls: ['./room-calendar.component.css'],
    providers: [DatePipe],
    standalone: false
})
export class RoomCalendarComponent implements OnInit {

  dayNames: string[] = ['Ponedjeljak', 'Utorak', 'Srijeda', 'Četvrtak', 'Petak', 'Subota', 'Nedjelja'];
  dayNamesShort: string[] = ['Pon', 'Uto', 'Sri', 'Čet', 'Pet', 'Sub', 'Ned'];
  monthNames: string[] = ['Siječanj', 'Veljača', 'Ožujak', 'Travanj', 'Svibanj', 'Lipanj', 'Srpanj', 'Kolovoz', 'Rujan', 'Listopad', 'Studeni', 'Prosinac'];

  roomId = 0;
  roomName = '';
  selectedMonth = new Date(new Date().getFullYear(), new Date().getMonth(), 1);

  schedule: ISchedule = {
    id: 0, valid: false, month: new Date(),
    generatedDateTime: new Date(), generatedByUser: '', userDuties: []
  };

  MonthWeeks: any[] = [];
  Holydays: any[] = [];
  Usernames: string[] = [];
  UserColors = new Map<string, string>();
  UserHours = new Map<string, number>();

  constructor(
    private route: ActivatedRoute,
    private roomService: RoomService,
    private datePipe: DatePipe,
    private colorService: ColorService
  ) { }

  ngOnInit(): void {
    this.roomId = Number(this.route.snapshot.paramMap.get('id'));
    this.roomService.getRoomDetail(this.roomId).subscribe({
      next: (data) => { this.roomName = data.name; }
    });
    this.loadMonth();
  }

  prevMonth(): void {
    this.selectedMonth = new Date(this.selectedMonth.getFullYear(), this.selectedMonth.getMonth() - 1, 1);
    this.loadMonth();
  }

  nextMonthNav(): void {
    this.selectedMonth = new Date(this.selectedMonth.getFullYear(), this.selectedMonth.getMonth() + 1, 1);
    this.loadMonth();
  }

  today(): void {
    this.selectedMonth = new Date(new Date().getFullYear(), new Date().getMonth(), 1);
    this.loadMonth();
  }

  loadMonth(): void {
    let requestedMonth = this.datePipe.transform(this.selectedMonth, 'yyyy-MM-dd')!;
    var numOfDays = this.getNumberOfDaysInMonth(this.selectedMonth.getMonth(), this.selectedMonth.getFullYear());
    var dayofweekfirstday = this.selectedMonth.getDay();
    if (dayofweekfirstday == 0) dayofweekfirstday = 7;

    var week: any[] = [];
    var currentDay = 1;
    this.MonthWeeks = [];

    for (let i = 1; i < dayofweekfirstday; i++) {
      week.push({ dayNo: currentDay++, hidden: true, date: null, color: '', user: '', holyday: false, hours: 0 });
    }

    for (let i = 1; i <= numOfDays; i++) {
      if (currentDay > 7) { this.MonthWeeks.push(week); week = []; currentDay = 1; }
      week.push({
        dayNo: currentDay++, hidden: false,
        date: this.datePipe.transform(new Date(this.selectedMonth.getFullYear(), this.selectedMonth.getMonth(), i), 'yyyy-MM-dd'),
        color: '', user: '', holyday: false, hours: 0
      });
    }

    if (currentDay > 1) {
      for (let i = currentDay; i <= 7; i++) {
        week.push({ dayNo: currentDay++, hidden: true, date: null, color: '', user: '', holyday: false, hours: 0 });
      }
      this.MonthWeeks.push(week);
    }

    this.roomService.getSchedule(this.roomId, requestedMonth).subscribe({
      next: (data) => {
        this.schedule = data;
        this.Usernames = this.schedule.userDuties.map(d => d.username).filter((v, i, a) => a.indexOf(v) === i);
        this.UserColors.clear();
        this.UserHours.clear();
        this.colorService.resetIndex();
        this.Usernames.forEach(u => {
          this.UserColors.set(u, this.colorService.getNextColor());
          this.UserHours.set(u, 0);
        });
        this.MonthWeeks.forEach(w => w.forEach((d: any) => {
          var duty = this.schedule.userDuties.find(du => du.day == d.date);
          if (duty) {
            d.user = duty.username;
            d.color = this.UserColors.get(duty.username);
            d.hours = duty.hours;
            this.UserHours.set(duty.username, (this.UserHours.get(duty.username) || 0) + duty.hours);
          }
        }));
      },
      error: () => { }
    });

    this.roomService.getHolydays(requestedMonth).subscribe({
      next: (data) => {
        this.Holydays = data.map((item: any) => item.day);
        this.MonthWeeks.forEach(w => w.forEach((d: any) => {
          if (this.Holydays.includes(d.date) || d.dayNo >= 6) d.holyday = true;
        }));
      },
      error: () => { this.Holydays = []; }
    });
  }

  getNumberOfDaysInMonth(month: number, year: number): number {
    var sm = new Date(year, month, 1);
    var nm = new Date(sm.setMonth(sm.getMonth() + 1, 1));
    return new Date(nm.getFullYear(), nm.getMonth(), 0).getDate();
  }
}
