import { HttpClient } from '@angular/common/http';
import { Pipe, PipeTransform } from '@angular/core';
import { DomSanitizer, SafeUrl } from '@angular/platform-browser';
import { Observable, map, of, tap } from 'rxjs';

@Pipe({
  name: 'imageCache',
  standalone: true
})
export class ImageCachePipe implements PipeTransform {
  private cachedImages: {[url: string]: SafeUrl} = {}

  constructor(private dom: DomSanitizer, private http: HttpClient) {}

  transform(url: string): Observable<SafeUrl> {
    if (url in this.cachedImages) {
      return of(this.cachedImages[url])
    } else {
      return this.http.get(url, { responseType : 'blob' }).pipe(
        map((blob) => this.dom.bypassSecurityTrustUrl(URL.createObjectURL(blob))),
        tap(safeurl => this.cachedImages[url] = safeurl)
      )
    }
  }

}
