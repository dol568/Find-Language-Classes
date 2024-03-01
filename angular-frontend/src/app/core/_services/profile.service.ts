import {inject, Injectable} from '@angular/core';
import {HttpClient} from "@angular/common/http";
import {map, Observable} from "rxjs";
import {_api_default} from "../../shared/_constVars/_api_consts";
import {IProfile, IProfileEdit} from "../../shared/_models/IProfile";
import {IApiResponse} from "../../shared/_models/IApiResponse";
import {AccountService} from "./account.service";

@Injectable({
  providedIn: 'root'
})
export class ProfileService {
  #http = inject(HttpClient);
  baseUrl = _api_default + 'profiles';

  profile$(userName: string): Observable<IProfile> {
    return this.#http.get<IApiResponse<IProfile>>(this.baseUrl + '/' + userName).pipe(
      map(response => response.data)
    );
  }

  updateProfile(userName: string, data: IProfileEdit): Observable<IProfile> {
    return this.#http.put<IApiResponse<IProfile>>(this.baseUrl + '/' + userName, data).pipe(
      map(response => response.data)
    );
  }

  uploadPhoto(data: any) {
    return this.#http.put<IApiResponse<string>>(this.baseUrl + '/photo', data);
  }
}
