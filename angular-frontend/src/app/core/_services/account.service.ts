import {inject, Injectable} from '@angular/core';
import {HttpClient, HttpErrorResponse, HttpHeaders} from "@angular/common/http";
import {BehaviorSubject, catchError, map, Observable, Subject, throwError} from "rxjs";
import {Router} from "@angular/router";
import {_api_account, _api_default, _api_login, _api_signup} from "../../shared/_constVars/_api_consts";
import {_client_home} from "../../shared/_constVars/_client_consts";
import {IUser, IUserFormValues} from "../../shared/_models/IUser";
import {IApiResponse} from "../../shared/_models/IApiResponse";

@Injectable({
  providedIn: 'root'
})
export class AccountService {
  #http = inject(HttpClient);
  #router = inject(Router);
  baseUrl = _api_default;
  private currentUserSource = new BehaviorSubject<IUser>(null);
  currentUser$ = this.currentUserSource.asObservable();
    private refreshSubject = new Subject<void>();

    refresh$ = this.refreshSubject.asObservable();

    triggerRefresh() {
        this.refreshSubject.next();
    }

  loadCurrentUser(token: string) {
    if (token === null) {
      this.currentUserSource.next(null);
      return null;
    }
    let headers = new HttpHeaders();
    headers = headers.set('Authorization', `Bearer ${token}`);
    return this.#http.get(this.baseUrl + _api_account, {headers}).pipe(
      map((response: IApiResponse<IUser>) => {
          let user = response.data;
          if (user) {
            sessionStorage.setItem(`token`, user.token);
            this.currentUserSource.next(user);
            return user
          } else {
              return null;
          }
        }
      ));
  }

  login(values: IUserFormValues) {
    return this.#http.post(this.baseUrl + _api_login, values).pipe(
      map((response: IApiResponse<IUser>) => {
        let user = response.data;
        if (user) {
          sessionStorage.setItem(`token`, user.token);
          this.currentUserSource.next(user);
        }
      }
        ))
  }

  register(values: IUserFormValues) {
    return this.#http.post(this.baseUrl + _api_signup, values).pipe(
      map((response: IApiResponse<IUser>) => {
        let user = response.data;
        if (user) {
          sessionStorage.setItem(`token`, user.token);
          this.currentUserSource.next(user);
        }
      })
    );
  }

  logout() {
      sessionStorage.removeItem(`token`);
      this.currentUserSource.next(null);
      this.#router.navigate([_client_home]);
  }
}
