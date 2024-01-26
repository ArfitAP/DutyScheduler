import { Injectable } from "@angular/core";

const colors: string[] = [
                          //darkslategray
                          '#2f4f4f',
                          //seagreen
                          '#2e8b57',
                          //maroon2
                          '#7f0000',
                          //midnightblue
                          '#191970',
                          //olive
                          '#808000',
                          //red
                          '#ff0000',
                          //darkorange
                          '#ff8c00',
                          //gold
                          '#ffd700',
                          //mediumblue
                          '#0000cd',
                          //mediumorchid
                          '#ba55d3',
                          //springgreen
                          '#00ff7f',
                          //darksalmon
                          '#e9967a',
                          //greenyellow
                          '#adff2f',
                          //fuchsia
                          '#ff00ff',
                          //dodgerblue
                          '#1e90ff',
                          //palegoldenrod
                          '#eee8aa',
                          //plum
                          '#dda0dd',
                          //deeppink
                          '#ff1493',
                          //lightskyblue
                          '#87cefa',
                          //aquamarine
                          '#7fffd4',
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
  