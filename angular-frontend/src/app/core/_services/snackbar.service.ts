import {Injectable, NgZone} from '@angular/core';
import {MatSnackBar, MatSnackBarConfig} from "@angular/material/snack-bar";

@Injectable({
  providedIn: 'root'
})
export class SnackbarService {

  constructor(public snackbar: MatSnackBar, private zone: NgZone) { }

  success(message: string) {
    const config = new MatSnackBarConfig();
    config.panelClass = ['success-snackbar'];
    config.duration = 3000;
    this.zone.run(() => {
      this.snackbar.open(message,'', config);
    });
  }

  error(message: string) {
    const config = new MatSnackBarConfig();
    config.panelClass = ['error-snackbar'];
    config.duration = 3000;
    this.zone.run(() => {
      this.snackbar.open(message,'', config);
    });
  }
}
