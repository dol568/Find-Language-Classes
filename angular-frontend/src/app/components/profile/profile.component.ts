import {
  Component,
  effect,
  inject,
  Injector,
  Input,
  OnInit,
  runInInjectionContext,
  Signal,
  signal,
  WritableSignal,
} from '@angular/core';
import { IProfile, IProfileEdit, ProfileInfo } from '../../shared/_models/IProfile';
import { DomSanitizer, SafeUrl } from '@angular/platform-browser';
import { ProfileHeaderComponent } from './profile-header/profile-header.component';
import { ProfileListComponent } from './profile-list/profile-list.component';
import { ProfileDetailComponent } from './profile-detail/profile-detail.component';
import { EMPTY, Subject, switchMap, takeUntil } from 'rxjs';
import { AccountService } from '../../core/_services/account.service';
import { ActivatedRoute } from '@angular/router';
import { CommonModule } from '@angular/common';
import { SnackbarService } from '../../core/_services/snackbar.service';
import { IUser } from '../../shared/_models/IUser';

@Component({
  selector: 'app-profile',
  standalone: true,
  imports: [ProfileHeaderComponent, ProfileListComponent, ProfileDetailComponent, CommonModule],
  templateUrl: './profile.component.html',
  styleUrl: './profile.component.scss',
})
export class ProfileComponent implements OnInit {
  #destroySubject$: Subject<void> = new Subject<void>();
  #injector = inject(Injector);
  #accountService = inject(AccountService);
  #activatedRoute = inject(ActivatedRoute);
  #snackBar = inject(SnackbarService);
  #domSanitizer = inject(DomSanitizer);

  profile: Signal<IProfile> = this.#accountService.profile;
  photo: Signal<SafeUrl> = this.#accountService.photo;
  currentUser: Signal<IUser> = this.#accountService.currentUser;
  safeCroppedImage: WritableSignal<SafeUrl> = signal<SafeUrl>('');
  listInfo: WritableSignal<ProfileInfo> = signal<ProfileInfo>(ProfileInfo.ABOUT);

  @Input() userName = '';

  ngOnInit(): void {
    runInInjectionContext(this.#injector, () => {
      effect(() => {
        this.#activatedRoute.paramMap
          .pipe(
            switchMap((params) => {
              const userName = params.get('userName');
              if (userName) {
                return this.#accountService.profile$(userName).pipe(
                  switchMap((response) => {
                    const photo = response.data?.photoUrl;
                    if (photo || photo?.length === 0) {
                      return this.#accountService.loadPhoto(photo);
                    } else { 
                      return this.#accountService.loadPhoto('');
                    }
                  })
                );
              } else {
                return EMPTY;
              }
            }),
            takeUntil(this.#destroySubject$)
          )
          .subscribe({
            error: (err) => console.error(err),
          });
      });
    });
  }

  public follow(username: string): void {
    this.#accountService
      .followUser(username)
      .pipe(takeUntil(this.#destroySubject$))
      .subscribe({
        next: () => {
          this.#snackBar.success(`User '${this.profile().fullName}' has been followed`);
        },
        error: (err) => console.error(err),
      });
  }

  public unfollow(username: string): void {
    this.#accountService
      .unFollowUser(username)
      .pipe(takeUntil(this.#destroySubject$))
      .subscribe({
        next: () => {
          this.#snackBar.success(`User '${this.profile().fullName}' has been unfollowed`);
        },
        error: (err) => console.error(err),
      });
  }

  public updateProfile(data: IProfileEdit): void {
    this.#accountService
      .updateProfile(this.profile()?.username, data)
      .pipe(takeUntil(this.#destroySubject$))
      .subscribe({
        next: () => {
          this.#snackBar.success(`User info updated`);
        },
        error: (err) => console.error(err),
      });
  }

  public uploadPhoto(data: FormData): void {
    this.#accountService
      .uploadPhoto(data)
      .pipe(takeUntil(this.#destroySubject$))
      .subscribe({
        next: () => {
          this.#snackBar.success(`User image updated`);
        },
        error: (err) => console.error(err),
      });
  }

  public getListInfo(profileInfo: ProfileInfo): void {
    this.listInfo.set(profileInfo);
  }

  public getUnsafeCroppedImage(unSafeUrl: string): void {
    this.safeCroppedImage.set(this.#domSanitizer.bypassSecurityTrustUrl(unSafeUrl));
  }
}
