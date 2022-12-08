import { DatePipe } from '@angular/common';
import { HttpClient } from '@angular/common/http';
import { Component, OnInit } from '@angular/core';
import { FormBuilder, FormControl, FormGroup, Validators } from '@angular/forms';
import { catchError, timeout } from 'rxjs/operators';
import { ISchedule } from 'src/app/_models/Schedule';
import { IUserActivation } from 'src/app/_models/UserActivation';
import { IUserRole } from 'src/app/_models/UserRole';
import { TokenStorageService } from 'src/app/_services/token-storage.service';
import { UserService } from '../../_services/user.service';

export interface SelectedMonth {
  value: Date;
  viewValue: string | null;
}

@Component({
  selector: 'app-board-admin',
  templateUrl: './board-admin.component.html',
  styleUrls: ['./board-admin.component.css'],
  providers: [DatePipe]
})
export class BoardAdminComponent implements OnInit {

  monthNames: string[] = ['Siječanj', 'Veljača', 'Ožujak', 'Travanj', 'Svibanj', 'Lipanj', 'Srpanj', 'Kolovoz', 'Rujan', 'Listopad', 'Studeni', 'Prosinac'];

  isLoggedIn = false;
  userId = 0;
  nextMonth = new Date(new Date().setMonth(new Date().getMonth() + 1, 1));
  selectedMonth = this.nextMonth;
  monthName = "";
  loading = false;

  username: string = '';
  requestedUser: IUserRole | null = null;

  form: FormGroup = this.fb.group({
    month: [new Date(), Validators.required]
  });

  monthList: SelectedMonth[] = [];
  schedule: ISchedule = {
    id: 0,
    valid: false,
    month: this.nextMonth,
    generatedDateTime: new Date(),
    generatedByUser: "",
    userDuties: []
  };

  userActivations: IUserActivation[] = [];

  constructor(private tokenStorageService: TokenStorageService, private http: HttpClient, private datePipe: DatePipe, private fb: FormBuilder) { }

  ngOnInit(): void {
    this.isLoggedIn = !!this.tokenStorageService.getToken();
    
    if (this.isLoggedIn) {
      this.userId = this.tokenStorageService.getUser().id;

      for (let i = 1; i >= -10; i--) {
        this.monthList.push({value: new Date(new Date().setMonth(new Date().getMonth() + i, 1)), viewValue: this.monthNames[new Date(new Date().setMonth(new Date().getMonth() + i, 1)).getMonth()] + " " + this.datePipe.transform(new Date(new Date().setMonth(new Date().getMonth() + i, 1)), 'yyyy') } );
      }
      
      this.form.patchValue({month: this.nextMonth});

      this.getDataForMonth();
    }
  }
    
    
  getDataForMonth(){  
    this.selectedMonth = this.form.value.month;

    let requestedMonth = this.datePipe.transform(this.selectedMonth, 'yyyy-MM-dd'); 
    this.monthName = this.monthNames[Number(this.datePipe.transform(this.selectedMonth, 'MM')) - 1];

    //console.log(requestedMonth);

    this.http.get("http://localhost:180/api/test/schedule/schedule/" + requestedMonth, { responseType: 'text' })
               .subscribe({
                  next: data => {
                    this.schedule = JSON.parse(data);

                  },
                  error: err => {
                    
                  }
                });

    this.http.get("http://localhost:180/api/test/schedule/usersforactivation/" + requestedMonth, { responseType: 'text' })
                .subscribe({
                   next: data => {
                     this.userActivations = JSON.parse(data);
 
                   },
                   error: err => {
                     
                   }
                 });
  }
  
  generateSchedule() : void {
    this.loading = true;
    let requestedMonth = this.datePipe.transform(this.selectedMonth, 'yyyy-MM-dd'); 
    
    this.http.get("http://localhost:180/api/test/schedule/generateSchedule/" + requestedMonth + "/" + this.userId, { responseType: 'text' })
                .pipe(
                  timeout(3600000)
                )            
                .subscribe({
                   next: data => {
                    let res : Boolean = JSON.parse(data);

                    if(res == true)
                    {
                      alert("Raspored je generiran");
                      this.getDataForMonth();
                    }
                    else alert("Pogreška !");
                    this.loading = false;
                   },
                   error: err => {
                    alert("Pogreška !");
                    this.loading = false;
                   }
                 });

    console.log(requestedMonth);

  }

  saveUsers() : void {
    let requestedMonth = this.datePipe.transform(this.selectedMonth, 'yyyy-MM-dd'); 

    this.http.post("http://localhost:180/api/test/schedule/saveUserActivesInMonth/" + requestedMonth + "/" + this.userId, this.userActivations, { responseType: 'text' })
               .subscribe({
                  next: data => {
                    let res : Boolean = JSON.parse(data);

                    if(res == true)
                    {
                      alert("Popis aktivnih korisnika je spremljen");
                    }
                    else alert("Pogreška !");
                  },
                  error: err => {
                    alert("Pogreška !");
                  }
                });

  }

  getUserRole() : void {
    if(this.username.length == 0) return;

    this.http.get("http://localhost:180/api/test/schedule/getUserRole/" + this.username, { responseType: 'text' })
                .subscribe({
                   next: data => {
                     this.requestedUser = JSON.parse(data);
                   },
                   error: err => {
                     
                   }
                 });
  }

  setAdmin() : void {
    this.http.post("http://localhost:180/api/test/schedule/setUserRole/", this.requestedUser, { responseType: 'text' })
    .subscribe({
       next: data => {
         let res : Boolean = JSON.parse(data);

         if(res == true)
         {
           alert("Korisnik je spremljen");
           this.requestedUser = null;
           this.username = '';
         }
         else alert("Pogreška !");
       },
       error: err => {
         alert("Pogreška !");
       }
     });
  }

}