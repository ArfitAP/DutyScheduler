import { Injectable } from "@angular/core";

const colors: string[] = ['#CD6155', 
                          '#AF7AC5', 
                          '#1D8348',   
                          '#76448A',
                          '#A93226',
                          '#D68910',
                          '#F7DC6F',                        
                          '#5499C7',
                          '#EB984E',
                          '#5D6D7E',
                          '#E6B0AA',                         
                          '#48C9B0',
                          '#D4AC0D',
                          '#2471A3',
                          '#148F77',                                                                       
                          '#99A3A4',
                          '#BA4A00',
                          '#979A9A',
                          '#EDBB99',                         
                          '#AEB6BF',                         
                        ];

@Injectable({
    providedIn: 'root'
  })
  export class ColorService {
    constructor() { }

    startIndex: number = 0;
  
    getNextColor(): string {
        var color: string = colors[this.startIndex];
        this.startIndex = (this.startIndex + 1) % colors.length;
        return color;
    }

    resetIndex(): void {
        this.startIndex = Math.floor(Math.random() * (colors.length - 1));
    }
  
  }
  