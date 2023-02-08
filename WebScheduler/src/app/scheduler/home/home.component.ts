import { DatePipe } from '@angular/common';
import { HttpClient } from '@angular/common/http';
import { Component, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { ISchedule } from 'src/app/_models/Schedule';
import { ColorService } from 'src/app/_services/color.service';
import { TokenStorageService } from 'src/app/_services/token-storage.service';
import { UserService } from '../../_services/user.service';

export interface SelectedMonth {
  value: Date;
  viewValue: string | null;
}

@Component({
  selector: 'app-home',
  templateUrl: './home.component.html',
  styleUrls: ['./home.component.css'],
  providers: [DatePipe]
})
export class HomeComponent implements OnInit {
  content?: string;

  dayNames: string[] = ['Ponedjeljak', 'Utorak', 'Srijeda', 'Četvrtak', 'Petak', 'Subota', 'Nedjelja'];
  monthNames: string[] = ['Siječanj', 'Veljača', 'Ožujak', 'Travanj', 'Svibanj', 'Lipanj', 'Srpanj', 'Kolovoz', 'Rujan', 'Listopad', 'Studeni', 'Prosinac'];

  thisMonth = new Date(new Date().setMonth(new Date().getMonth(), 1));
  nextMonth = new Date(new Date().setMonth(new Date().getMonth() + 1, 1));
  selectedMonth = this.thisMonth;
  monthList: SelectedMonth[] = [];

  form: FormGroup = this.fb.group({
    month: [new Date(), Validators.required]
  });

  schedule: ISchedule = {
    id: 0,
    valid: false,
    month: this.nextMonth,
    generatedDateTime: new Date(),
    generatedByUser: "",
    userDuties: []
  };

  MonthWeeks: any[] = [];

  Holydays: any[] = [];
  Usernames: string[] = [];
  UserColors = new Map<string, string>();
  UserHours = new Map<string, number>();

  constructor(private tokenStorageService: TokenStorageService, private http: HttpClient, private datePipe: DatePipe, private fb: FormBuilder, private colorService: ColorService) { }

  ngOnInit(): void {
    
    for (let i = 1; i >= -10; i--) {
        this.monthList.push({value: new Date(new Date().setMonth(new Date().getMonth() + i, 1)), viewValue: this.monthNames[new Date(new Date().setMonth(new Date().getMonth() + i, 1)).getMonth()] + " " + this.datePipe.transform(new Date(new Date().setMonth(new Date().getMonth() + i, 1)), 'yyyy') } );
      }
      
    this.form.patchValue({month: this.thisMonth});

    this.getDataForMonth();
  }

  getDataForMonth(){  
    this.selectedMonth = this.form.value.month;

    let year = this.datePipe.transform(this.selectedMonth, 'yyyy'); 
    let month = this.datePipe.transform(this.selectedMonth, 'MM'); 

    this.selectedMonth = new Date(Number(year), Number(month) - 1, 1);

    //console.log(this.selectedMonth.getDay());
    
    let requestedMonth = this.datePipe.transform(this.selectedMonth, 'yyyy-MM-dd'); 

    var numOfDays = this.getNumberOfDaysInMonth(this.selectedMonth.getMonth(), this.selectedMonth.getFullYear());
    var dayofweekfirstday = this.selectedMonth.getDay();
    if(dayofweekfirstday == 0) dayofweekfirstday = 7;

    var week = [];
    var currentDay = 1;
    this.MonthWeeks = [];

    for (let i = 1; i < dayofweekfirstday; i++) {
      week.push({
        dayNo: currentDay++,
        hidden: true,
        date: null,
        color: '',
        user: '',
        holyday: false,
        hours: 0
      });
    }

    for (let i = 1; i <= numOfDays; i++) {
      if(currentDay > 7)
      {
        this.MonthWeeks.push(week);
        week = [];
        currentDay = 1;
      }

      week.push({
        dayNo: currentDay++,
        hidden: false,
        date: this.datePipe.transform(new Date(this.selectedMonth.getFullYear(), this.selectedMonth.getMonth(), i), 'yyyy-MM-dd'),
        color: '',
        user: '',
        holyday: false,
        hours: 0
      });       
    }

    if(currentDay > 1)
    {
      for (let i = currentDay; i <= 7; i++) {
        week.push({
          dayNo: currentDay++,
          hidden: true,
          date: null,
          color: '',
          user: '',
          holyday: false,
          hours: 0
        });
      }

      this.MonthWeeks.push(week);
    }

    this.http.get("http://localhost:180/api/schedule/schedule/" + requestedMonth, { responseType: 'text' })
               .subscribe({
                  next: data => {
                    this.schedule = JSON.parse(data);

                    this.Usernames = this.schedule.userDuties.map(duty => duty.username).filter(function(elem, index, self) {
                      return index === self.indexOf(elem);
                    });

                    this.UserColors.clear();
                    this.UserHours.clear();

                    this.colorService.resetIndex();

                    this.Usernames.forEach(username => {
                      this.UserColors.set(username, this.colorService.getNextColor());
                      this.UserHours.set(username, 0);
                    });
                 
                    this.MonthWeeks.forEach( (value) => {
                      value.forEach( (dayinweek: { dayNo: number; hidden: boolean; date: any; color: string | undefined; user: string; holyday: boolean; hours: number;}) => {
                        
                        var duty = this.schedule.userDuties.find(duty => duty.day == dayinweek.date);
                        if(duty != undefined) 
                        {                        
                          dayinweek.user = duty.username;
                          dayinweek.color = this.UserColors.get(duty.username);
                          dayinweek.hours = duty.hours;

                          this.UserHours.set(duty.username, this.UserHours.get(duty.username) + duty.hours); 
                        }

                      });
                    }); 

                  },
                  error: err => {
                    
                  }
                });

    this.http.get("http://localhost:180/api/schedule/getHolydaysForMonth/" + requestedMonth, { responseType: 'text' })
                .subscribe({
                   next: data => {

                     this.Holydays = JSON.parse(data).map((item: { day: Date; }) => item.day);;  

                     console.log(this.Holydays);

                     this.MonthWeeks.forEach( (value) => {
                      value.forEach( (dayinweek: { dayNo: number; hidden: boolean; date: any; color: string | undefined; user: string; holyday: boolean; hours: number; }) => {

                        if(this.Holydays.includes(dayinweek.date) || dayinweek.dayNo >= 6) 
                        {                        
                          dayinweek.holyday = true;
                        }

                      });
                    }); 
                   },
                   error: err => {
                     this.Holydays = [];
                   }
                 });
  }

  getNumberOfDaysInMonth(month: number, year: number) : number {

    var selectedmonth = new Date(year, month, 1);
    var nextMonth = new Date(selectedmonth.setMonth(selectedmonth.getMonth() + 1, 1));
    return new Date(nextMonth.getFullYear(), nextMonth.getMonth(), 0).getDate();
  }
}