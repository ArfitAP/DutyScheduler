import { DatePipe } from '@angular/common';
import { HttpClient } from '@angular/common/http';
import { Component, OnInit } from '@angular/core';
import { FormBuilder, FormControl, FormGroup, Validators } from '@angular/forms';
import { ISchedule } from 'src/app/_models/Schedule';
import { IUserActivation } from 'src/app/_models/UserActivation';
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

  isLoggedIn = false;
  userId = 0;
  nextMonth = new Date(new Date().setMonth(new Date().getMonth() + 1, 1));
  selectedMonth = this.nextMonth;

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
        this.monthList.push({value: new Date(new Date().setMonth(new Date().getMonth() + i, 1)), viewValue: this.datePipe.transform(new Date(new Date().setMonth(new Date().getMonth() + i, 1)), 'yyyy-MM') } );
      }
      
      this.form.patchValue({month: this.nextMonth});

      this.getDataForMonth();
    }
  }
    
    
  getDataForMonth(){  
    this.selectedMonth = this.form.value.month;

    let requestedMonth = this.datePipe.transform(this.selectedMonth, 'yyyy-MM-dd'); 

    console.log(requestedMonth);

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
    let requestedMonth = this.datePipe.transform(this.selectedMonth, 'yyyy-MM-dd'); 

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

}