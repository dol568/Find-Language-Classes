import {ApplicationConfig, ErrorHandler} from '@angular/core';
import { provideRouter } from '@angular/router';

import { routes } from './app.routes';
import {provideAnimations} from "@angular/platform-browser/animations";
import {provideHttpClient, withInterceptors} from "@angular/common/http";
import {jwtInterceptor} from "./core/interceptors/jwt.interceptor";
import {loadingInterceptor} from "./core/interceptors/loading.interceptor";
import {CustomErrorHandler} from "./core/_services/custom-error-handler.service";
import {errorInterceptor} from "./core/interceptors/error.interceptor";
import {AuthenticatePipe} from "./core/_services/authenticate.pipe";

export const appConfig: ApplicationConfig = {
  providers: [
    provideRouter(routes),
    provideAnimations(),
    provideHttpClient(withInterceptors(
      [jwtInterceptor, loadingInterceptor, errorInterceptor]
    )),
      // { provide: ErrorHandler, useClass: CustomErrorHandler }
  ]
};
