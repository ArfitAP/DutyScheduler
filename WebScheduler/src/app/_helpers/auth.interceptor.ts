import { HTTP_INTERCEPTORS, HttpEvent } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { HttpInterceptor, HttpHandler, HttpRequest } from '@angular/common/http';

import { TokenStorageService } from '../_services/token-storage.service';
import { Observable } from 'rxjs';

const TOKEN_HEADER_KEY = 'Authorization';       // for Spring Boot back-end
const LANGUAGE_KEY = 'app-language';

@Injectable()
export class AuthInterceptor implements HttpInterceptor {
  constructor(private token: TokenStorageService) { }

  intercept(req: HttpRequest<any>, next: HttpHandler): Observable<HttpEvent<any>> {
    let headers = req.headers;

    const token = this.token.getToken();
    if (token != null) {
      headers = headers.set(TOKEN_HEADER_KEY, 'Bearer ' + token);
    }

    const lang = localStorage.getItem(LANGUAGE_KEY) || 'en';
    headers = headers.set('Accept-Language', lang);

    const authReq = req.clone({ headers });
    return next.handle(authReq);
  }
}

export const authInterceptorProviders = [
  { provide: HTTP_INTERCEPTORS, useClass: AuthInterceptor, multi: true }
];
