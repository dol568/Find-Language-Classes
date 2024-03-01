import {inject, Injectable} from '@angular/core';
import {HttpClient} from "@angular/common/http";
import {_api_default} from "../../shared/_constVars/_api_consts";

@Injectable({
  providedIn: 'root'
})
export class FollowService {
  #http = inject(HttpClient);
  baseUrl = _api_default + 'follow';

  followUser(userName: string) {
    return this.#http.post(this.baseUrl + '/' + userName, null);
  }

  unFollowUser(userName: string) {
    return this.#http.delete(this.baseUrl + '/' + userName);
  }
}
