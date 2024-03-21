import { inject, Injectable } from '@angular/core';
import { MatSnackBar, MatSnackBarConfig } from '@angular/material/snack-bar';

@Injectable({
  providedIn: 'root',
})
export class SnackbarService {
  #snackbar = inject(MatSnackBar);

  public success(message: string): void {
    this.#getSnack(message, 'success-snackbar');
  }

  public error(message: string): void {
    this.#getSnack(message, 'error-snackbar');
  }

  #getSnack(message: string, panelClass: string) {
    const config = new MatSnackBarConfig();
    config.panelClass = [`${panelClass}`];
    config.duration = 3000;
    this.#snackbar.open(message, '', config);
  }
}
