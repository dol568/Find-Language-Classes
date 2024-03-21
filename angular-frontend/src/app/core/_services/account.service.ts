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

@Injectable({
  providedIn: 'root',
})
export class AccountService {
  #http = inject(HttpClient);
  #domSanitizer = inject(DomSanitizer);
  #router = inject(Router);
  #baseUrl: string = _api_default;
  #followUrl: string = this.#baseUrl + 'follow';
  #profileUrl: string = this.#baseUrl + 'profiles';

  #currentUser: WritableSignal<IUser> = signal<IUser | null>(null);
  #profile: WritableSignal<IProfile> = signal<IProfile | undefined>(undefined);
  #photo: WritableSignal<SafeUrl> = signal<SafeUrl>(undefined);
  #isAuthenticated: WritableSignal<boolean> = signal<boolean>(false);

  currentUser: Signal<IUser> = computed(this.#currentUser);
  profile: Signal<IProfile> = computed(this.#profile);
  photo: Signal<SafeUrl> = computed(this.#photo);

  constructor() {
    this.loadCurrentUser();
  }

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
        this.#currentUser.set(copy);
        this.#profile.update((value) => ({ ...value, photoUrl: response }));
      })
    );
  }

  public loadPhoto(photoUrl: string): Observable<SafeUrl> {
    return this.#http.get(photoUrl, { responseType: 'blob' }).pipe(
      map((blob) => this.#domSanitizer.bypassSecurityTrustUrl(URL.createObjectURL(blob))),
      tap((response) => {
        this.#photo.set(response);
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
}
