import { DatePipe } from '@angular/common';
import { HttpClient } from '@angular/common/http';
import { Component, OnInit } from '@angular/core';
import { IUserApplication } from 'src/app/_models/UserApplication';
import { TokenStorageService } from 'src/app/_services/token-storage.service';

@Component({
  selector: 'app-board-user',
  templateUrl: './board-user.component.html',
  styleUrls: ['./board-user.component.css'],
  providers: [DatePipe]
})
export class BoardUserComponent implements OnInit {

  isLoggedIn = false;
  userId = 0;
  userApplication: IUserApplication = {
    id: 0,
    month: new Date(),
    grouped: false,
    user_id: 0,
    applicationDays: []
  };
  userDays: Date[] = [];
  nextMonth = new Date(new Date().setMonth(new Date().getMonth() + 1, 1));

  nextMonthWeeks: any[] = [];

  constructor(private tokenStorageService: TokenStorageService, private http: HttpClient, private datePipe: DatePipe) { }

  ngOnInit(): void {
    this.isLoggedIn = !!this.tokenStorageService.getToken();
    
    if (this.isLoggedIn) {
      this.userId = this.tokenStorageService.getUser().id;
      this.nextMonth = new Date(new Date().setMonth(new Date().getMonth() + 1, 1));

      this.userApplication.user_id = this.userId;
      this.userApplication.month = this.nextMonth;

      const numOfDays = this.getNumberOfDaysInMonth(this.nextMonth.getMonth(), this.nextMonth.getFullYear());

      var dayofweekfirstday = this.nextMonth.getDay();
      var week = [];
      var currentDay = 1;

      for (let i = 1; i < dayofweekfirstday; i++) {
        week.push({
          dayNo: currentDay++,
          hidden: true,
          date: null,
          selected: false
        });
      }

      for (let i = 1; i <= numOfDays; i++) {
        if(currentDay > 7)
        {
          this.nextMonthWeeks.push(week);
          week = [];
          currentDay = 1;
        }

        week.push({
          dayNo: currentDay++,
          hidden: false,
          date: this.datePipe.transform(new Date(this.nextMonth.getFullYear(), this.nextMonth.getMonth(), i), 'yyyy-MM-dd'),
          selected: false
        });       
      }

      if(currentDay > 1)
      {
        for (let i = currentDay; i <= 7; i++) {
          week.push({
            dayNo: currentDay++,
            hidden: true,
            date: null,
            selected: false
          });
        }

        this.nextMonthWeeks.push(week);
      }

      //console.log(this.nextMonthWeeks);

      this.http.get("http://localhost:180/api/test/schedule/userapplications/" + this.userId + '/' + this.datePipe.transform(this.nextMonth, 'yyyy-MM-dd'), { responseType: 'text' })
               .subscribe({
                  next: data => {
                    this.userApplication = JSON.parse(data)[0];

                    try {
                      this.userDays = this.userApplication.applicationDays.map((item: { day: Date; }) => item.day);

                      this.nextMonthWeeks.forEach( (value) => {
                        value.forEach( (dayinweek: { dayNo: number; hidden: boolean; date: any; selected: boolean; }) => {
                          if(this.userDays.includes(dayinweek.date))
                          {
                            dayinweek.selected = true;
                          }
                        });
                      }); 

                      
                    } catch (error) {
                      this.userDays = [];
                    }

                    //console.log(this.nextMonthWeeks);
                    //console.log(this.userDays);

                  },
                  error: err => {
                    this.userDays = [];
                  }
                });

    }
  }


  getNumberOfDaysInMonth(month: number, year: number) : number {

    var selectedmonth = new Date(year, month, 1);
    var nextMonth = new Date(selectedmonth.setMonth(selectedmonth.getMonth() + 1, 1));
    return new Date(nextMonth.getFullYear(), nextMonth.getMonth(), 0).getDate();
  }

}

