import { NgModule } from '@angular/core';
import { BrowserModule } from '@angular/platform-browser';
import { BrowserAnimationsModule } from '@angular/platform-browser/animations';
import { provideHttpClient, withInterceptorsFromDi } from '@angular/common/http';
import { AppRoutingModule } from './app-routing.module';
import { AppComponent } from './app.component';
import { TranslateModule } from '@ngx-translate/core';
import { provideTranslateHttpLoader } from '@ngx-translate/http-loader';

import { authInterceptorProviders } from './_helpers/auth.interceptor';
import { AuthModule } from './auth/auth.module';
import { SchedulerModule } from './scheduler/scheduler.module';

@NgModule({
    declarations: [
        AppComponent
    ],
    bootstrap: [AppComponent],
    imports: [
        BrowserModule,
        BrowserAnimationsModule,
        AppRoutingModule,
        AuthModule,
        SchedulerModule,
        TranslateModule.forRoot()
    ],
    providers: [
        authInterceptorProviders,
        provideHttpClient(withInterceptorsFromDi()),
        provideTranslateHttpLoader({ prefix: './assets/i18n/', suffix: '.json' })
    ]
})
export class AppModule { }
