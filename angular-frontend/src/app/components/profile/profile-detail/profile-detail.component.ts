import {
  Component,
  effect,
  EventEmitter,
  input,
  InputSignal,
  OnDestroy,
  OnInit,
  Output,
  signal,
  WritableSignal,
} from '@angular/core';
import {
  IProfile,
  IProfileEdit,
  ProfileInfo,
} from '../../../shared/_models/IProfile';
import { getDaysOfWeekWords } from '../../../shared/_constVars/_days';
import { CommonModule } from '@angular/common';
import {
  FormControl,
  FormGroup,
  ReactiveFormsModule,
  Validators,
} from '@angular/forms';
import { Subject } from 'rxjs';
import { SafeUrl } from '@angular/platform-browser';
import { ImageCroppedEvent, ImageCropperModule } from 'ngx-image-cropper';
import { IUser } from '../../../shared/_models/IUser';

@Component({
  selector: 'app-profile-detail',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule, ImageCropperModule],
  templateUrl: './profile-detail.component.html',
  styleUrl: './profile-detail.component.scss',
})
export class ProfileDetailComponent implements OnInit, OnDestroy {
  #destroySubject$: Subject<void> = new Subject<void>();

  @Output() followUser = new EventEmitter<string>();
  @Output() unFollowUser = new EventEmitter<string>();
  @Output() updateProfile = new EventEmitter<IProfileEdit>();
  @Output() uploadPhoto = new EventEmitter<FormData>();
  @Output() unsafeCroppedImage = new EventEmitter<string>();

  safecroppedImage: InputSignal<SafeUrl> = input.required<SafeUrl>();
  currentUser: InputSignal<IUser> = input.required<IUser>();
  profile: InputSignal<IProfile> = input.required<IProfile>();
  listInfo: InputSignal<ProfileInfo> = input.required<ProfileInfo>();

  isEditMode: WritableSignal<boolean> = signal<boolean>(false);
  imageChangedEvent: WritableSignal<string> = signal<string>('');
  crop: WritableSignal<Blob> = signal<Blob>(null);

  protected readonly ProfileInfo = ProfileInfo;
  protected readonly getDaysOfWeekWords = getDaysOfWeekWords;
  editProfileForm: FormGroup;

  isFollowMode: boolean = false;

  constructor() {
    effect(() => {
      if (!!this.profile()) {
        this.isFollowMode = !this.profile()?.followers.some((follower) => {
          return follower.username === this.currentUser().userName;
        });
        this.editProfileForm.patchValue({
          fullName: this.profile()?.fullName,
          bio: this.profile()?.bio,
        });
      }
    });
  }

  ngOnInit(): void {
    this.editProfileForm = new FormGroup({
      fullName: new FormControl('', [
        Validators.required,
        Validators.minLength(2),
      ]),
      bio: new FormControl('', [
        Validators.required,
        Validators.maxLength(1000),
      ]),
    });
  }

  ngOnDestroy(): void {
    this.#destroySubject$.next();
    this.#destroySubject$.complete();
  }

  get fullName() {
    return this.editProfileForm.get('fullName');
  }

  get bio() {
    return this.editProfileForm.get('bio');
  }

  public follow(username: string): void {
    this.followUser.emit(username);
    this.isFollowMode = true;
  }

  public unfollow(username: string): void {
    this.unFollowUser.emit(username);
    this.isFollowMode = false;
  }

  public changeMode(): void {
    this.isEditMode.set(this.isEditMode() === false);
  }

  public submitFunc(): void {
    this.updateProfile.emit(this.editProfileForm.value);
    this.isEditMode.set(false);
  }

  fileChangeEvent(event: any): void {
    this.imageChangedEvent.set(event);
  }

  cancelPhotoSelection() {
    this.imageChangedEvent.set(null);
  }

  imageCropped(event: ImageCroppedEvent) {
    this.unsafeCroppedImage.emit(event.objectUrl);
    this.crop.set(event.blob);
  }

  public uploadFile(): void {
    if (this.crop() && this.profile()?.id) {
      const formData = new FormData();
      formData.append('file', this.crop());
      formData.append('id', this.profile().id);
      this.uploadPhoto.emit(formData);
      this.imageChangedEvent.set(null);
    }
  }
}
