import { HttpClient } from '@angular/common/http';
import { Injectable, inject } from '@angular/core';
import { DomSanitizer, SafeUrl } from '@angular/platform-browser';
import { map, of, tap } from 'rxjs';
import { _img_default, _img_flag } from '../../shared/_constVars/_client_consts';

@Injectable({
  providedIn: 'root',
})
export class ImageService {
  private _cacheUrls: string[] = [];
  public _cachedImages: {[url: string]: SafeUrl} = {};
  #domSanitizer = inject(DomSanitizer);

  set cacheUrls(urls: string[]) {
    this._cacheUrls = [...urls];
  }
  get cacheUrls(): string[] {
    return this._cacheUrls;
  }

  constructor(private http: HttpClient) {}

  getImage(url: string) {
    if (!url || url.trim() === '') { 
      return of(this.#domSanitizer.bypassSecurityTrustUrl(`${_img_default}/user.jpg`));
    } else if (url.toLowerCase() === 'french' 
    || url.toLowerCase() === 'english' || url .toLowerCase()=== 'german' 
    || url.toLowerCase() === 'italian' || url.toLowerCase() === 'polish' || url.toLowerCase() === 'spanish') {
      console.log(`${_img_flag}/${url?.toLowerCase()}.jpg`)
      return of(this.#domSanitizer.bypassSecurityTrustUrl(`${_img_flag}/${url?.toLowerCase()}.jpg`));
    }
    console.log(this._cachedImages)
    if (url in this._cachedImages) {
      console.log('hi')
      return of(this._cachedImages[url]);
    } else {

      return this.http
        .get(url, { responseType: 'blob' })
        .pipe(
          map(
          (blob) => this.#domSanitizer.bypassSecurityTrustUrl(URL.createObjectURL(blob))
        ),
          
          tap((blob) => {
            console.log(blob)
            this._cachedImages[url] = blob
            console.log(this._cachedImages)
    }))
  }
  }

}
