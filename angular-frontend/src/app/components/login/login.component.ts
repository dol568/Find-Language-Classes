import { Component, OnDestroy, Signal, inject } from '@angular/core';
import { Router, RouterModule } from '@angular/router';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { AccountService } from '../../core/_services/account.service';
import {
  _client_home,
  _client_signup,
  _client_language_classes,
} from '../../shared/_constVars/_client_consts';
import { IUser } from '../../shared/_models/IUser';
import { Subject, takeUntil } from 'rxjs';

@Component({
  selector: 'app-login',
  standalone: true,
  imports: [RouterModule, CommonModule, FormsModule],
  templateUrl: './login.component.html',
  styleUrl: './login.component.scss',
})
export class LoginComponent implements OnDestroy {
  #destroySubject$: Subject<void> = new Subject<void>();
  #router = inject(Router);
  #accountService = inject(AccountService);
  currentUser: Signal<IUser> = this.#accountService.currentUser;
  language_classes: string = _client_language_classes;
  signup: string = _client_signup;

  ngOnDestroy(): void {
    this.#destroySubject$.next();
    this.#destroySubject$.complete();
  }

  public isValidateTextFalse(data: any): boolean {
    return !!(data.touched && data.invalid);
  }

  public submitFunc(data: any, event: Event): void {
    event.preventDefault();
    this.#accountService
      .login(data.value)
      .pipe(takeUntil(this.#destroySubject$))
      .subscribe({
        next: () => this.#router.navigate([_client_home]),
        error: (err) => console.error(err),
      });
  }
}
