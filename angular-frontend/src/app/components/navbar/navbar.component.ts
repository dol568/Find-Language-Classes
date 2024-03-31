import { Component, inject, OnDestroy, Signal} from '@angular/core';
import { Router, RouterModule } from '@angular/router';
import { AccountService } from '../../core/_services/account.service';
import {
  _client_add_class,
  _client_language_classes,
  _client_profiles,
} from '../../shared/_constVars/_client_consts';
import { CommonModule } from '@angular/common';
import { Subject } from 'rxjs';
import { IUser } from '../../shared/_models/IUser';

@Component({
    selector: 'app-navbar',
    standalone: true,
    templateUrl: './navbar.component.html',
    styleUrl: './navbar.component.scss',
    imports: [RouterModule, CommonModule]
})
export class NavbarComponent implements OnDestroy {
  #destroySubject$: Subject<void> = new Subject<void>();
  #router = inject(Router);
  #accountService = inject(AccountService);
  currentUser: Signal<IUser> = this.#accountService.currentUser;
  profile = this.#accountService.profile;
  add_class: string = _client_add_class;
  photo = this.#accountService.loggedInUserPhoto;

  ngOnDestroy(): void {
    this.#destroySubject$.next();
    this.#destroySubject$.complete();
  }

  public logout(): void {
    this.#accountService.logout();
  }

  public goToClasses(): void {
    this.#router.navigate([_client_language_classes]);
  }

  public goToProfile(): void {
    this.#router.navigate([_client_profiles + '/' + this.currentUser()?.userName]);
  }
}
