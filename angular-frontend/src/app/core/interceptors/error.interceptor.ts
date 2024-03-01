import { HttpInterceptorFn } from '@angular/common/http';
import {inject} from "@angular/core";
import {catchError, throwError} from "rxjs";
import {Router} from "@angular/router";
import {SnackbarService} from "../_services/snackbar.service";
import {_client_notfound, _client_servererror} from "../../shared/_constVars/_client_consts";

export const errorInterceptor: HttpInterceptorFn = (req, next) => {
    const router = inject(Router);
    const snackbar = inject(SnackbarService);
    let errorMessage = '';
    return next(req).pipe(
        catchError((error) => {
            if (error) {
                if (error.status === 404) {
                            router.navigate([`/${_client_notfound}`])
                }
                if (error.status === 500) {
                    router.navigate([`/${_client_servererror}`]);
                }
                errorMessage = `An error occurred: ${error.error.message}`;
                snackbar.error(errorMessage)
            }
            return throwError(() => errorMessage);
        })
    );
};
