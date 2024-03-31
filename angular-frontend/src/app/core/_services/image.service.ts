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
  public _cachedImages: {[url: string]: SafeUrl} = {
    '':`${_img_default}/user.jpg`,
    'french':`${_img_flag}/french.jpg`,
    'english':`${_img_flag}/english.jpg`,
    'german':`${_img_flag}/german.jpg`,
    'italian':`${_img_flag}/italian.jpg`,
    'polish':`${_img_flag}/polish.jpg`,
    'spanish':`${_img_flag}/spanish.jpg`,
  };
  #domSanitizer = inject(DomSanitizer);

  set cacheUrls(urls: string[]) {
    this._cacheUrls = [...urls];
  }
  get cacheUrls(): string[] {
    return this._cacheUrls;
  }

  constructor(private http: HttpClient) {}

  getImage(url: string) {
    console.log(url)
    url = url.toLowerCase()
    // if (!url || url.trim() === '') { 
    //   return of(`${_img_default}/user.jpg`);
    // } 
    
    // else if (url.toLowerCase() === 'french' 
    // if (url.toLowerCase() === 'french' 
    // || url.toLowerCase() === 'english' || url .toLowerCase()=== 'german' 
    // || url.toLowerCase() === 'italian' || url.toLowerCase() === 'polish' || url.toLowerCase() === 'spanish') {
    //   return of(`${_img_flag}/${url?.toLowerCase()}.jpg`);
    // }
    if (url in this._cachedImages) {
      console.log('inside cache images')
      return of(this._cachedImages[url]);
    } else {

      return this.http
        .get(url, { responseType: 'blob' })
        .pipe(
          map(
          (blob) => this.#domSanitizer.bypassSecurityTrustUrl(URL.createObjectURL(blob))
        ),
          
          tap((blob) => {
            console.log('http call images')
            this._cachedImages[url] = blob
    }))
  }
  }

}
