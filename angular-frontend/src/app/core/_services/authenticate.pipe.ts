import {
  ChangeDetectorRef,
  inject,
  OnDestroy,
  Pipe,
  PipeTransform,
} from '@angular/core';
import { HttpClient, HttpResponse } from '@angular/common/http';
import { DomSanitizer, SafeUrl } from '@angular/platform-browser';
import {
  BehaviorSubject,
  distinctUntilChanged,
  filter,
  map,
  Subscription,
  switchMap,
  tap,
} from 'rxjs';

@Pipe({
  name: 'authenticate',
  pure: false,
  standalone: true,
})
export class AuthenticatePipe implements PipeTransform, OnDestroy {
  #http = inject(HttpClient);
  #sanitizer = inject(DomSanitizer);
  #cdr = inject(ChangeDetectorRef);
  
  private subscription = new Subscription();
  private transformValue = new BehaviorSubject<string>('');
  private latestValue!: string | SafeUrl;

  constructor() {
    this.setUpSubscription();
  }

  transform(imagePath: string): string | SafeUrl {
    this.transformValue.next(imagePath);
    return this.latestValue;
  }

  ngOnDestroy(): void {
    this.subscription.unsubscribe();
  }

  private setUpSubscription(): void {
    const transformSubscription = this.transformValue
      .asObservable()
      .pipe(
        filter((v): v is string => !!v),
        distinctUntilChanged(),
        switchMap((imagePath: string) =>
          this.#http
            .get(imagePath, { observe: 'response', responseType: 'blob' })
            .pipe(
              map((response: HttpResponse<Blob>) =>
                URL.createObjectURL(response.body)
              ),
              map((unsafeBlobUrl: string) =>
                this.#sanitizer.bypassSecurityTrustUrl(unsafeBlobUrl)
              ),
              // filter((blobUrl) => blobUrl !== this.latestValue)
            )
        ),
        tap((imagePath: string | SafeUrl) => {
          this.latestValue = imagePath;
          this.#cdr.markForCheck();
        })
      )
      .subscribe();
    this.subscription.add(transformSubscription);
  }
}
