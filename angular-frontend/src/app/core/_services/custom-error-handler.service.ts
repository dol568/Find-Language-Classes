import {ErrorHandler, inject, Injectable, NgZone} from '@angular/core';
import {SnackbarService} from "./snackbar.service";
import {HttpErrorResponse} from "@angular/common/http";
import {Router} from "@angular/router";
import {_client_notfound} from "../../shared/_constVars/_client_consts";

@Injectable({
    providedIn: 'root'
})
export class CustomErrorHandler implements ErrorHandler {
    #snackbar = inject(SnackbarService);
    #router = inject(Router);
    zone = inject(NgZone);


    handleError = (error: Error | HttpErrorResponse): void => {
        if (error instanceof HttpErrorResponse) {
            if (error.status == 404) {
                this.zone.run(() => {
                    this.#router.navigate([`/${_client_notfound}`]).then()
                })
            }
            console.error(`An error occurred: ${error.error.message}`)
            this.#snackbar.error(`An error occurred: ${error.error.message}`)
        }
    }
}
