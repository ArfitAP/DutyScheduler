import { DatePipe } from '@angular/common';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Component, OnInit } from '@angular/core';
import { IUserApplication } from 'src/app/_models/UserApplication';
import { TokenStorageService } from 'src/app/_services/token-storage.service';

const httpOptions = {
  headers: new HttpHeaders({ 'Content-Type': 'application/json' })
};

@Component({
  selector: 'app-board-user',
  templateUrl: './board-user.component.html',
  styleUrls: ['./board-user.component.css'],
  providers: [DatePipe]
})
export class BoardUserComponent implements OnInit {

  dayNames: string[] = ['Ponedjeljak', 'Utorak', 'Srijeda', 'Četvrtak', 'Petak', 'Subota', 'Nedjelja'];
  monthNames: string[] = ['Siječanj', 'Veljača', 'Ožujak', 'Travanj', 'Svibanj', 'Lipanj', 'Srpanj', 'Kolovoz', 'Rujan', 'Listopad', 'Studeni', 'Prosinac'];

  isLoggedIn = false;
  userId = 0;
  nextMonth = new Date(new Date().setMonth(new Date().getMonth() + 1, 1));

  userApplication: IUserApplication = {
    id: 0,
    active: true,
    month: this.nextMonth,
    grouped: false,
    user_id: 0,
    applicationDays: []
  };
  userDays: Date[] = [];

  nextMonthWeeks: any[] = [];
  Holydays: any[] = [];

  constructor(private tokenStorageService: TokenStorageService, private http: HttpClient, private datePipe: DatePipe) { }

  ngOnInit(): void {
    this.isLoggedIn = !!this.tokenStorageService.getToken();
    
    if (this.isLoggedIn) {
      this.userId = this.tokenStorageService.getUser().id;

      this.userApplication.user_id = this.userId;
      this.userApplication.month = this.nextMonth;

      const numOfDays = this.getNumberOfDaysInMonth(this.nextMonth.getMonth(), this.nextMonth.getFullYear());

      var dayofweekfirstday = this.nextMonth.getDay();
      if(dayofweekfirstday == 0) dayofweekfirstday = 7;
      var week = [];
      var currentDay = 1;

      for (let i = 1; i < dayofweekfirstday; i++) {
        week.push({
          dayNo: currentDay++,
          hidden: true,
          date: null,
          selected: false,
          holyday: false
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
          selected: false,
          holyday: false
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
            holyday: false
          });
        }

        this.nextMonthWeeks.push(week);
      }

      //console.log(this.nextMonthWeeks);

      this.http.get("http://localhost:180/api/schedule/userapplications/" + this.userId + '/' + this.datePipe.transform(this.nextMonth, 'yyyy-MM-dd'), { responseType: 'text' })
               .subscribe({
                  next: data => {
                    this.userApplication = JSON.parse(data);

                    try {
                      this.userDays = this.userApplication.applicationDays.map((item: { day: Date; }) => item.day);

                      this.nextMonthWeeks.forEach( (value) => {
                        value.forEach( (dayinweek: { dayNo: number; hidden: boolean; date: any; selected: boolean; holyday: boolean; }) => {
                          if(this.userDays.includes(dayinweek.date))
                          {
                            dayinweek.selected = true;
                          }
                        });
                      }); 

                      
                    } catch (error) {
                      this.userDays = [];
                    }

                  },
                  error: err => {
                    this.userDays = [];
                  }
                });

      this.http.get("http://localhost:180/api/schedule/getHolydaysForMonth/" + this.datePipe.transform(this.nextMonth, 'yyyy-MM-dd'), { responseType: 'text' })
                .subscribe({
                   next: data => {

                    this.Holydays = JSON.parse(data).map((item: { day: Date; }) => item.day);;  

                    this.nextMonthWeeks.forEach( (value) => {
                      value.forEach( (dayinweek: { dayNo: number; hidden: boolean; date: any; selected: boolean; holyday: boolean; }) => {

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
  }

  save() : void {
    console.log(this.userDays);

    this.userApplication.applicationDays =  this.userDays.map((item) => { return { id: 0, day: this.datePipe.transform(item, 'yyyy-MM-dd') }; });

    this.http.post("http://localhost:180/api/schedule/adduserapplications/", this.userApplication, { responseType: 'text' })
               .subscribe({
                  next: data => {
                    let res : Boolean = JSON.parse(data);

                    if(res == true)
                    {
                      alert("Vaš izbor je spremljen");
                    }
                    else alert("Pogreška !");
                  },
                  error: err => {
                    alert("Pogreška !");
                  }
                });
  }

  dayClick(date: string) : void {
    this.nextMonthWeeks.forEach( (value) => {
      value.forEach( (dayinweek: { dayNo: number; hidden: boolean; date: any; selected: boolean; }) => {
        if(dayinweek.date == date)
        {
          dayinweek.selected = !dayinweek.selected;

          if(this.userDays.includes(dayinweek.date))
          {
            this.userDays = this.userDays.filter(obj => obj !== dayinweek.date);
          }
          else
          {
            this.userDays.push(dayinweek.date);
          }
        }
      });
    }); 
  }

  getNumberOfDaysInMonth(month: number, year: number) : number {

    var selectedmonth = new Date(year, month, 1);
    var nextMonth = new Date(selectedmonth.setMonth(selectedmonth.getMonth() + 1, 1));
    return new Date(nextMonth.getFullYear(), nextMonth.getMonth(), 0).getDate();
  }

}

