import { Injectable } from '@angular/core';
import { TranslateService } from '@ngx-translate/core';

const LANGUAGE_KEY = 'app-language';

@Injectable({
  providedIn: 'root'
})
export class LanguageService {

  readonly supportedLanguages = [
    { code: 'en', label: 'English' },
    { code: 'de', label: 'Deutsch' },
    { code: 'hr', label: 'Hrvatski' }
  ];

  constructor(private translate: TranslateService) {
    this.translate.addLangs(this.supportedLanguages.map(l => l.code));
    this.translate.setDefaultLang('en');

    const saved = localStorage.getItem(LANGUAGE_KEY);
    const lang = saved && this.supportedLanguages.some(l => l.code === saved) ? saved : 'en';
    this.translate.use(lang);
  }

  get currentLang(): string {
    return this.translate.currentLang || 'en';
  }

  setLanguage(lang: string): void {
    this.translate.use(lang);
    localStorage.setItem(LANGUAGE_KEY, lang);
  }

  getMonthNames(): string[] {
    const keys = [
      'MONTHS.JANUARY', 'MONTHS.FEBRUARY', 'MONTHS.MARCH', 'MONTHS.APRIL',
      'MONTHS.MAY', 'MONTHS.JUNE', 'MONTHS.JULY', 'MONTHS.AUGUST',
      'MONTHS.SEPTEMBER', 'MONTHS.OCTOBER', 'MONTHS.NOVEMBER', 'MONTHS.DECEMBER'
    ];
    const result: string[] = [];
    for (const key of keys) {
      result.push(this.translate.instant(key));
    }
    return result;
  }

  getDayNames(): string[] {
    const keys = [
      'DAYS.MONDAY', 'DAYS.TUESDAY', 'DAYS.WEDNESDAY', 'DAYS.THURSDAY',
      'DAYS.FRIDAY', 'DAYS.SATURDAY', 'DAYS.SUNDAY'
    ];
    return keys.map(k => this.translate.instant(k));
  }

  getDayNamesShort(): string[] {
    const keys = [
      'DAYS_SHORT.MON', 'DAYS_SHORT.TUE', 'DAYS_SHORT.WED', 'DAYS_SHORT.THU',
      'DAYS_SHORT.FRI', 'DAYS_SHORT.SAT', 'DAYS_SHORT.SUN'
    ];
    return keys.map(k => this.translate.instant(k));
  }

  // Sunday-first order for dayOfWeek (0=Sun, 1=Mon, ..., 6=Sat)
  getDayNamesSundayFirst(): string[] {
    const keys = [
      'DAYS_SHORT.SUN', 'DAYS_SHORT.MON', 'DAYS_SHORT.TUE', 'DAYS_SHORT.WED',
      'DAYS_SHORT.THU', 'DAYS_SHORT.FRI', 'DAYS_SHORT.SAT'
    ];
    return keys.map(k => this.translate.instant(k));
  }
}
