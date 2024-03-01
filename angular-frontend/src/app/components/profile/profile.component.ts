import {Component, inject, OnDestroy, OnInit} from '@angular/core';
import {AccountService} from "../../core/_services/account.service";
import {ActivatedRoute, Router} from "@angular/router";
import {IProfile, ProfileInfo} from "../../shared/_models/IProfile";
import {Observable, Subscription} from "rxjs";
import {IUser} from "../../shared/_models/IUser";
import {FormControl, FormGroup, ReactiveFormsModule, Validators} from "@angular/forms";
import {ProfileService} from "../../core/_services/profile.service";
import {FollowService} from "../../core/_services/follow.service";
import {CommonModule} from "@angular/common";
import {getDaysOfWeekWords} from "../../shared/_constVars/_days";
import {AuthenticatePipe} from "../../core/_services/authenticate.pipe";
import {HandleImageErrorDirective} from "../../core/_services/handle-image-error.directive";
import {DomSanitizer} from "@angular/platform-browser";
import {ImageCroppedEvent, ImageCropperModule} from "ngx-image-cropper";
import {SnackbarService} from "../../core/_services/snackbar.service";

@Component({
    selector: 'app-profile',
    standalone: true,
    imports: [CommonModule, ReactiveFormsModule, AuthenticatePipe, HandleImageErrorDirective, ImageCropperModule],
    templateUrl: './profile.component.html',
    styleUrl: './profile.component.scss'
})
export class ProfileComponent implements OnInit {
    #profileService = inject(ProfileService);
    #followService = inject(FollowService);
    #accountService = inject(AccountService);
    #activatedRoute = inject(ActivatedRoute);
    #snackBar = inject(SnackbarService);
    #domSanitizer = inject(DomSanitizer);
    #router = inject(Router);
    imageChangedEvent: any = '';
    croppedImage: any = '';
    crop: any = '';
    profile: IProfile | null = null;
    currentUser$: Observable<IUser>;
    editProfileForm: FormGroup;
    isEditMode: boolean = false;
    username: string | null = null;
    info: ProfileInfo = ProfileInfo.ABOUT;
    subscription: Subscription;

    protected readonly ProfileInfo = ProfileInfo;
    protected readonly getDaysOfWeekWords = getDaysOfWeekWords;

    ngOnInit(): void {
        this.currentUser$ = this.#accountService.currentUser$;
        this.username = this.#activatedRoute.snapshot.params['userName'];
        this.#activatedRoute.url.subscribe(() => this.#loadProfile());
        this.editProfileForm = new FormGroup({
            fullName: new FormControl('', [Validators.required]),
            bio: new FormControl('', [Validators.required])
        })
    }

    #loadProfile() {
        if (this.username) {
            this.#profileService.profile$(this.username).subscribe({
                next: (profile) => {
                    this.profile = profile;
                    this.editProfileForm.patchValue({
                        fullName: this.profile.fullName,
                        bio: this.profile.bio
                    });
                },
                error: err => console.error(err)
            });
        }
    }

    follow(username: string) {
        if (this.username) {

            this.#followService.followUser(username).subscribe({
                next: () => {
                    this.#loadProfile()
                    this.#snackBar.success(`User '${username}' has been followed`)
                },
                error: err => console.error(err)
            });
        }
    }

    unfollow(username: string) {
        if (this.username) {
            this.#followService.unFollowUser(username).subscribe({
                next: () => {
                    this.#loadProfile()
                    this.#snackBar.success(`User '${username}' has been unfollowed`)
                },
                error: err => console.error(err)
            });
        }
    }

    setInfo(value: ProfileInfo) {
        this.info = value;
    }

    changeMode() {
        this.isEditMode = !this.isEditMode;
    }

    submitFunc() {
        if (this.username) {
            this.#profileService.updateProfile((this.username), this.editProfileForm.value)
                .subscribe({
                    next: () => {
                        this.#router.navigateByUrl(`profiles/${this.profile.username}`, {replaceUrl: true})
                            .then(() => this.#router.navigate([this.#router.url]));
                        this.#snackBar.success(`User info updated`);
                        this.#accountService.triggerRefresh();
                    },
                    error: err => console.error(err)
                });
        }
    }

    shouldShowFollowButton(profile: IProfile, currentUser: IUser): boolean {
        return !profile.followers.some((follower) => follower.username === currentUser.userName)
            && (profile.username !== currentUser.userName);
    }

    shouldShowUnFollowButton(profile: IProfile, currentUser: IUser): boolean {
        return profile.followers.some((follower) => follower.username === currentUser.userName)
            && (profile.username !== currentUser.userName);
    }

    fileChangeEvent(event: any): void {
        this.imageChangedEvent = event;
    }

    cancelPhotoSelection() {
        this.imageChangedEvent = null;
    }

    imageCropped(event: ImageCroppedEvent) {
        this.croppedImage = this.#domSanitizer.bypassSecurityTrustUrl(event.objectUrl);
        this.crop = event.blob;
    }

    uploadFile() {
        if (this.crop && this.profile?.id) {
            const formData = new FormData();
            formData.append('file', this.crop);
            formData.append('id', this.profile.id);
            this.#profileService.uploadPhoto(formData).subscribe({
                    next: () => {
                        this.#router.navigateByUrl(`profiles/${this.profile.username}`, {replaceUrl: true})
                            .then(() => this.#router.navigate([this.#router.url]));
                        this.#snackBar.success(`User image updated`);
                        this.#accountService.triggerRefresh();
                    },
                    error: err => console.error(err)
                }
            )
        }
    }
}
