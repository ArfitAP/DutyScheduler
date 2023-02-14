import { DatePipe } from '@angular/common';
import { HttpClient } from '@angular/common/http';
import { Component, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { SelectedMonth } from 'src/app/scheduler/home/home.component';
import { ISchedule } from 'src/app/_models/Schedule';
import { ColorService } from 'src/app/_services/color.service';
import { TokenStorageService } from '../../_services/token-storage.service';
import { AppSettings } from  'src/app/_services/app.settings';

@Component({
  selector: 'app-profile',
  templateUrl: './profile.component.html',
  styleUrls: ['./profile.component.css'],
  providers: [DatePipe]
})
export class ProfileComponent implements OnInit {
  currentUser: any;
  userId = 0;

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
  hoursSum: number = 0;

  constructor(private token: TokenStorageService, private http: HttpClient, private datePipe: DatePipe, private fb: FormBuilder, private colorService: ColorService) { }

  ngOnInit(): void {
    this.currentUser = this.token.getUser();
    this.userId = this.currentUser.id;

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
        selected: false,
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
        selected: false,
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
          selected: false,
          holyday: false,
          hours: 0
        });
      }

      this.MonthWeeks.push(week);
    }

    this.http.get(AppSettings.API_ENDPOINT + "schedule/schedule/" + requestedMonth, { responseType: 'text' })
               .subscribe({
                  next: data => {
                    this.schedule = JSON.parse(data);

                    this.hoursSum = 0;

                    this.MonthWeeks.forEach( (value) => {
                      value.forEach( (dayinweek: { dayNo: number; hidden: boolean; date: any; selected: boolean; holyday: boolean; hours: number;}) => {
                        
                        var duty = this.schedule.userDuties.find(duty => duty.day == dayinweek.date);
                        if(duty != undefined && duty.username == this.currentUser.username) 
                        {                        
                          dayinweek.selected = true;
                          dayinweek.hours = duty.hours;

                          this.hoursSum += duty.hours;
                        }

                      });
                    }); 

                  },
                  error: err => {
                    
                  }
                });

    this.http.get(AppSettings.API_ENDPOINT + "schedule/getHolydaysForMonth/" + requestedMonth, { responseType: 'text' })
                .subscribe({
                   next: data => {

                     this.Holydays = JSON.parse(data).map((item: { day: Date; }) => item.day);;  

                     console.log(this.Holydays);

                     this.MonthWeeks.forEach( (value) => {
                      value.forEach( (dayinweek: { dayNo: number; hidden: boolean; date: any;  selected: boolean; holyday: boolean; hours: number; }) => {

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