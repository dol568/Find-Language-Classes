import { Component, effect, inject, OnDestroy, Signal, computed } from '@angular/core';
import { Router, RouterModule } from '@angular/router';
import { AccountService } from '../../core/_services/account.service';
import {
  _client_add_class,
  _client_language_classes,
  _client_profiles,
} from '../../shared/_constVars/_client_consts';
import { CommonModule } from '@angular/common';
import { AuthenticatePipe } from '../../core/_services/authenticate.pipe';
import { HandleImageErrorDirective } from '../../core/_services/handle-image-error.directive';
import { SafeUrl } from '@angular/platform-browser';
import { EMPTY, Subject, switchMap, takeUntil, tap } from 'rxjs';
import { IUser } from '../../shared/_models/IUser';

@Component({
  selector: 'app-navbar',
  standalone: true,
  imports: [RouterModule, CommonModule, AuthenticatePipe, HandleImageErrorDirective],
  templateUrl: './navbar.component.html',
  styleUrl: './navbar.component.scss',
})
export class NavbarComponent implements OnDestroy {
  #destroySubject$: Subject<void> = new Subject<void>();
  #router = inject(Router);
  #accountService = inject(AccountService);
  currentUser: Signal<IUser> = this.#accountService.currentUser;
  newSignal = computed(() => this.#accountService.currentUser());
  profile = this.#accountService.profile;
  add_class: string = _client_add_class;
  // photo: SafeUrl | string = '';
  photo = this.#accountService.photo;


  constructor() {
    effect(() => console.log(this.currentUser()))
    // effect(() => {
    //   if (!!this.currentUser()) {
    //     this.#accountService
    //       .profile$(this.currentUser()?.userName)
    //       .pipe(
    //         switchMap((response) => {
    //           const photo = response.data?.photoUrl;
    //           if (photo) {
    //             return this.#accountService
    //               .loadPhoto(photo)
    //               .pipe(tap((safeUrl) => (this.photo = safeUrl)));
    //           } else {
    //             return EMPTY;
    //           }
    //         }),
    //         takeUntil(this.#destroySubject$)
    //       )
    //       .subscribe({
    //         error: (err) => console.error(err),
    //       });
    //   }
    // });
  }

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
