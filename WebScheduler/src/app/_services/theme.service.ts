import { Injectable } from '@angular/core';
import { BehaviorSubject, Observable } from 'rxjs';

export type ThemeMode = 'light' | 'dark' | 'system';

const THEME_KEY = 'app-theme';
const DATA_ATTR = 'data-theme';

@Injectable({
  providedIn: 'root'
})
export class ThemeService {

  private readonly mediaQuery: MediaQueryList | null =
    typeof window !== 'undefined' && window.matchMedia
      ? window.matchMedia('(prefers-color-scheme: dark)')
      : null;

  private readonly modeSubject = new BehaviorSubject<ThemeMode>(this.readStoredMode());
  readonly mode$: Observable<ThemeMode> = this.modeSubject.asObservable();

  private readonly resolvedSubject = new BehaviorSubject<'light' | 'dark'>('light');
  readonly resolved$: Observable<'light' | 'dark'> = this.resolvedSubject.asObservable();

  constructor() {
    // Re-evaluate when system preference changes while mode === 'system'
    if (this.mediaQuery) {
      const listener = () => {
        if (this.modeSubject.value === 'system') {
          this.apply('system');
        }
      };
      if (this.mediaQuery.addEventListener) {
        this.mediaQuery.addEventListener('change', listener);
      } else if ((this.mediaQuery as any).addListener) {
        (this.mediaQuery as any).addListener(listener);
      }
    }

    this.apply(this.modeSubject.value);
  }

  get mode(): ThemeMode {
    return this.modeSubject.value;
  }

  get resolved(): 'light' | 'dark' {
    return this.resolvedSubject.value;
  }

  setMode(mode: ThemeMode): void {
    this.modeSubject.next(mode);
    if (mode === 'system') {
      localStorage.removeItem(THEME_KEY);
    } else {
      localStorage.setItem(THEME_KEY, mode);
    }
    this.apply(mode);
  }

  /** Cycle through light → dark → system → light. */
  cycle(): void {
    const order: ThemeMode[] = ['light', 'dark', 'system'];
    const idx = order.indexOf(this.modeSubject.value);
    const next = order[(idx + 1) % order.length];
    this.setMode(next);
  }

  /** Toggle binary: if currently dark → light, otherwise → dark. */
  toggle(): void {
    this.setMode(this.resolvedSubject.value === 'dark' ? 'light' : 'dark');
  }

  private apply(mode: ThemeMode): void {
    const effective: 'light' | 'dark' =
      mode === 'system'
        ? (this.mediaQuery?.matches ? 'dark' : 'light')
        : mode;

    const html = document.documentElement;
    html.setAttribute(DATA_ATTR, effective);

    // Keep <meta name="theme-color"> in sync for mobile browsers
    const meta = document.querySelector('meta[name="theme-color"]');
    if (meta) {
      meta.setAttribute('content', effective === 'dark' ? '#0b1220' : '#6366f1');
    }

    this.resolvedSubject.next(effective);
  }

  private readStoredMode(): ThemeMode {
    try {
      const saved = localStorage.getItem(THEME_KEY);
      if (saved === 'light' || saved === 'dark') return saved;
    } catch {
      /* SSR / restricted storage — fall through */
    }
    return 'system';
  }
}
