import { computed, inject, Injectable, Signal, signal, WritableSignal } from '@angular/core';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { map, Observable, tap } from 'rxjs';
import { Router } from '@angular/router';
import {
  _api_account,
  _api_default,
  _api_login,
  _api_signup,
} from '../../shared/_constVars/_api_consts';
import { _authSecretKey, _client_home } from '../../shared/_constVars/_client_consts';
import { IUser, IUserFormValues } from '../../shared/_models/IUser';
import { IApiResponse } from '../../shared/_models/IApiResponse';
import { IProfile, IProfileEdit } from '../../shared/_models/IProfile';
import { DomSanitizer, SafeUrl } from '@angular/platform-browser';
import { ImageService } from './image.service';

@Injectable({
  providedIn: 'root',
})
export class AccountService {
  #http = inject(HttpClient);
  #domSanitizer = inject(DomSanitizer);
  #imageService = inject(ImageService);
  #router = inject(Router);
  #baseUrl: string = _api_default;
  #followUrl: string = this.#baseUrl + 'follow';
  #profileUrl: string = this.#baseUrl + 'profiles';

  #currentUser: WritableSignal<IUser> = signal<IUser | null>(null);
  #profile: WritableSignal<IProfile> = signal<IProfile | undefined>(undefined);
  // #photo: WritableSignal<string> = signal<string>(undefined);
  #photo: WritableSignal<SafeUrl> = signal<SafeUrl>(undefined);
  // #loggedInUserPhoto: WritableSignal<string> = signal<string>(undefined);
  #loggedInUserPhoto: WritableSignal<SafeUrl> = signal<SafeUrl>(undefined);
  #isAuthenticated: WritableSignal<boolean> = signal<boolean>(false);

  currentUser: Signal<IUser> = computed(this.#currentUser);
  profile: Signal<IProfile> = computed(this.#profile);
  // photo: Signal<string> = computed(this.#photo);
  photo: Signal<SafeUrl> = computed(this.#photo);
  // loggedInUserPhoto: Signal<string> = computed(this.#loggedInUserPhoto);
  loggedInUserPhoto: Signal<SafeUrl> = computed(this.#loggedInUserPhoto);

  public loadCurrentUser(): Observable<void> {
    const token = sessionStorage.getItem(_authSecretKey);
    if (token === null) {
      this.#currentUser.set(null);
      this.#isAuthenticated.set(false);
      return null;
    }
    let headers = new HttpHeaders();
    headers = headers.set('Authorization', `Bearer ${token}`);
    return this.#http.get<IApiResponse<IUser>>(this.#baseUrl + _api_account, { headers }).pipe(
      map((response) => {
        const user = response.data;
        if (user) {
          sessionStorage.setItem(_authSecretKey, user.token);
          this.#currentUser.set(user);
          this.#imageService.getImage(user?.photoUrl).subscribe((photo) => {
            this.#loggedInUserPhoto.set(photo);
      
          })
          // this.#loadPhoto(user?.photoUrl).subscribe((photo) => {
          //   this.#loggedInUserPhoto.set(photo);
          // });
          this.#isAuthenticated.set(true);
        } else {
          this.#isAuthenticated.set(false);
          this.#currentUser.set(null);
        }
      })
    );
  }

  public isAuthenticatedUser(): boolean {
    return this.#isAuthenticated();
  }

  public login(values: IUserFormValues): Observable<IApiResponse<IUser>> {
    return this.#http.post<IApiResponse<IUser>>(this.#baseUrl + _api_login, values).pipe(
      tap((response) => {
        const user = response.data;
        if (user) {
          sessionStorage.setItem(_authSecretKey, user.token);
          this.#currentUser.set(user);
          this.#isAuthenticated.set(true);
        }
      })
    );
  }

  public register(values: IUserFormValues): Observable<IApiResponse<IUser>> {
    return this.#http.post<IApiResponse<IUser>>(this.#baseUrl + _api_signup, values).pipe(
      tap((response) => {
        const user = response.data;
        if (user) {
          sessionStorage.setItem(_authSecretKey, user.token);
          this.#currentUser.set(user);
          this.#isAuthenticated.set(true);
        }
      })
    );
  }

  public logout(): void {
    sessionStorage.removeItem(_authSecretKey);
    this.#currentUser.set(null);
    this.#isAuthenticated.set(false);
    this.#router.navigate([_client_home]);
  }

  public profile$(userName: string): Observable<IApiResponse<IProfile>> {
    return this.#http.get<IApiResponse<IProfile>>(this.#profileUrl + '/' + userName).pipe(
      tap((response) => {
        this.#imageService.getImage(response.data.photoUrl).subscribe((photo) => {
          this.#photo.set(photo);
    
        })
        // this.#loadPhoto(response.data.photoUrl).subscribe((photo) => {
        //   this.#photo.set(photo);
        // });
        this.#profile.set(response.data);
      })
    );
  }

  public updateProfile(userName: string, data: IProfileEdit): Observable<IProfile> {
    return this.#http.put<IApiResponse<IProfile>>(this.#profileUrl + '/' + userName, data).pipe(
      map((response) => response.data),
      tap((response) => {
        this.#currentUser.update((value) => ({
          ...value,
          fullName: response.fullName,
        }));
        this.#profile.set(response);
      })
    );
  }

  public uploadPhoto(data: any): Observable<string> {
    return this.#http.put<IApiResponse<string>>(this.#profileUrl + '/photo', data).pipe(
      map((response) => response.data),
      tap((response) => {
        const copy = { ...this.currentUser() };
        copy.photoUrl = response;
        // this.#imageService.getImage(response).subscribe((photo) => {
        //   // console.log(photo)
        //   this.#photo.set(photo);
        //   this.#loggedInUserPhoto.set(photo);
        // })
        this.#loadPhoto(response).subscribe((photo) => {
          this.#photo.set(photo);
          this.#loggedInUserPhoto.set(photo);
        });
        this.#currentUser.set(copy);
        this.#profile.update((value) => ({ ...value, photoUrl: response }));
      })
    );
  }

  public followUser(userName: string): Observable<IProfile> {
    return this.#http.post<IApiResponse<IProfile>>(this.#followUrl + '/' + userName, null).pipe(
      map((response) => response.data),
      tap((response) => {
        this.#profile.set(response);
      })
    );
  }

  public unFollowUser(userName: string): Observable<IProfile> {
    return this.#http.delete<IApiResponse<IProfile>>(this.#followUrl + '/' + userName).pipe(
      map((response) => response.data),
      tap((response) => {
        this.#profile.set(response);
      })
    );
  }

  #loadPhoto(photoUrl: string): Observable<SafeUrl> {
    return this.#http
      .get(photoUrl, { responseType: 'blob' })
      .pipe(
        map(
          (blob) => this.#domSanitizer.bypassSecurityTrustUrl(URL.createObjectURL(blob))
        ),
          
        tap((blob) => {
          console.log(blob)
          this.#imageService._cachedImages[photoUrl] = blob
          // console.log(this._cachedImages)
  }))
      
  }
}
