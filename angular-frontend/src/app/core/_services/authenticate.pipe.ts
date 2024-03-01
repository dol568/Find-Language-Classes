import {ChangeDetectorRef, inject, Injectable, NgZone, OnDestroy, Pipe, PipeTransform} from '@angular/core';
import {HttpClient, HttpResponse} from "@angular/common/http";
import {DomSanitizer, SafeUrl} from "@angular/platform-browser";
import {BehaviorSubject, distinctUntilChanged, filter, map, Observable, Subscription, switchMap, tap} from "rxjs";

@Pipe({
    name: 'authenticate',
    pure: false,
    standalone: true
})
export class AuthenticatePipe implements PipeTransform, OnDestroy {
    #http = inject(HttpClient)
    #sanitizer = inject(DomSanitizer)
    #cdr = inject(ChangeDetectorRef)
    private subscription = new Subscription();
    private transformValue = new BehaviorSubject<string>('');
    private latestValue!: string | SafeUrl;

    constructor() {
        this.setUpSubscription();
    }

    transform(imagePath: string): string | SafeUrl {
        this.transformValue.next(imagePath)
        return  this.latestValue
    }

    ngOnDestroy(): void {
        this.subscription.unsubscribe()
    }

    private setUpSubscription(): void {
        const transformSubscription = this.transformValue
            .asObservable()
            .pipe(
                filter((v): v is string => !!v),
                distinctUntilChanged(),
                // we use switchMap, so the previous subscription gets torn down
                switchMap((imagePath: string) => this.#http
                    // we get the imagePath, observing the response and getting it as a 'blob'
                    .get(imagePath, { observe: 'response', responseType: 'blob' })
                    .pipe(
                        // we map our blob into an ObjectURL
                        map((response: HttpResponse<Blob>) => URL.createObjectURL(response.body)),
                        // we bypass Angular's security mechanisms
                        map((unsafeBlobUrl: string) => this.#sanitizer.bypassSecurityTrustUrl(unsafeBlobUrl)),
                        // we trigger it only when there is a change in the result
                        filter((blobUrl) => blobUrl !== this.latestValue),
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

    // transform(url): Observable<SafeUrl> {
    //     return this.#http.get(url, {responseType: 'blob'}).pipe(
    //         map(val => this.#sanitizer.bypassSecurityTrustUrl(URL.createObjectURL(val))));
    // }
}
