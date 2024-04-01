import { HttpClient } from '@angular/common/http';
import { Injectable, inject } from '@angular/core';
import { DomSanitizer } from '@angular/platform-browser';
import { Observable, map, of } from 'rxjs';
import { _img_default, _img_flag } from '../../shared/_constVars/_client_consts';

@Injectable({
  providedIn: 'root',
})
export class ImageService {
  #domSanitizer = inject(DomSanitizer);
  #http = inject(HttpClient);

  #cachedImages: { [url: string]: string } = {
    '': `${_img_default}/user.jpg`,
    french: `${_img_flag}/french.jpg`,
    english: `${_img_flag}/english.jpg`,
    german: `${_img_flag}/german.jpg`,
    italian: `${_img_flag}/italian.jpg`,
    polish: `${_img_flag}/polish.jpg`,
    spanish: `${_img_flag}/spanish.jpg`,
  };

  setCachedImage(photoUrl: string, safeUrl: string): void {
    this.#cachedImages[photoUrl] = safeUrl;
  }

  getCachedImage(key: string): string | undefined {
    return this.#cachedImages[key];
  }

  getImage(url: string): Observable<string> {
    const lowerCaseUrl = url ? url.toLowerCase() : '';
    const cachedImage = this.#cachedImages[lowerCaseUrl];

    if (cachedImage) {
      console.log('saved')
      return of(cachedImage);
    }

    return this.#http.get(url, { responseType: 'blob' }).pipe(
      map((blob) => {
        console.log('http')
        const safeUrl = this.#domSanitizer.bypassSecurityTrustUrl(
          URL.createObjectURL(blob)
        ) as string;
        this.#cachedImages[lowerCaseUrl] = safeUrl;
        return safeUrl;
      })
    );
  }
}
