import {inject, Injectable} from '@angular/core';
import {NgxSpinnerService} from "ngx-spinner";

@Injectable({
  providedIn: 'root'
})
export class BusyService {
  #spinnerService = inject(NgxSpinnerService);
  busyRequestCount = 0;

  constructor() { }

  busy() {
    this.busyRequestCount++;
    this.#spinnerService.show(undefined, {
      type: 'ball-clip-rotate',
      bdColor: 'rgba(0 ,0, 0, 0.8)',
      color: '#fff'
    });
  }

  idle() {
    this.busyRequestCount--;
    if (this.busyRequestCount <= 0) {
      this.busyRequestCount = 0;
      this.#spinnerService.hide();
    }
  }
}
