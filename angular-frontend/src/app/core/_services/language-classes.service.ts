import { computed, inject, Injectable, Signal, signal, WritableSignal } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { _api_default, _api_language_classes } from '../../shared/_constVars/_api_consts';
import { concatMap, forkJoin, map, Observable, switchMap, tap, toArray } from 'rxjs';
import { ILanguageClass } from '../../shared/_models/ILanguageClass';
import { IApiResponse } from '../../shared/_models/IApiResponse';
import { IUser } from '../../shared/_models/IUser';
import { AttendanceType, Params } from '../../shared/_models/Params';
import { timeToNumber } from '../../shared/_helper/_languageClass';
import { AccountService } from './account.service';
import { DomSanitizer, SafeUrl } from '@angular/platform-browser';

@Injectable({
  providedIn: 'root',
})
export class LanguageClassesService {
  #domSanitizer = inject(DomSanitizer);
  acc = inject(AccountService)
  #http = inject(HttpClient);
  #baseUrl = _api_default + _api_language_classes;
  #displayedHours: WritableSignal<number[]> = signal<number[]>([]);
  #languageClasses: WritableSignal<ILanguageClass[]> = signal<ILanguageClass[]>([]);
  #languageClass: WritableSignal<ILanguageClass> = signal<ILanguageClass>(undefined);

  displayedHours: Signal<number[]> = computed(this.#displayedHours);
  languageClasses: Signal<ILanguageClass[]> = computed(this.#languageClasses);
  languageClass: Signal<ILanguageClass> = computed(this.#languageClass);

  public getLanguageClasses(user: IUser, params: Params): ILanguageClass[] {
    this.#filterAndSort(user, params)().forEach((languageClass) =>
      this.#evalTime(languageClass.time)
    );
    return this.#filterAndSort(user, params)();
  }

  // public languageClasses$: Observable<ILanguageClass[]> = this.#http
  //   .get<IApiResponse<ILanguageClass[]>>(this.#baseUrl)
  //   .pipe(
  //     map((response) => response.data),
  //     tap(res => this.#languageClasses.set(res))
  //   );

  public languageClasses$: Observable<ILanguageClass[]> = this.#http
  .get<IApiResponse<ILanguageClass[]>>(this.#baseUrl)
  .pipe(
    map((response) => response.data),
    concatMap((classes) =>
      forkJoin(
        classes.map((languageClass) =>
          forkJoin(
            this.acc.loadPhoto2(languageClass.hostImage),
            forkJoin(
              languageClass.userLanguageClasses.map((userLanguageClass) =>
                this.acc.loadPhoto2(userLanguageClass.image)
              )
            )
          ).pipe(
            map(([hostImageBlob, userImagesBlobs]) => ({
              ...languageClass,
              hostImage: this.#domSanitizer.bypassSecurityTrustUrl(
                URL.createObjectURL(hostImageBlob)
              ) as string,
              userLanguageClasses: languageClass.userLanguageClasses.map((userLanguageClass, index) => ({
                ...userLanguageClass,
                image: this.#domSanitizer.bypassSecurityTrustUrl(
                  URL.createObjectURL(userImagesBlobs[index])
                ) as string
              }))
            }))
          )
        )
      )
    ),
    tap(res => this.#languageClasses.set(res))
  );

      
     
  


  public languageClass$ = (id: number): Observable<ILanguageClass> =>
    this.#http.get<IApiResponse<ILanguageClass>>(this.#baseUrl + '/' + id).pipe(
      map((response) => response.data),
      tap((response) => {
        this.#languageClass.set(response);
      })
    );

  #filterAndSort(user: IUser, params: Params): Signal<ILanguageClass[]> {
    return computed(() =>
      this.languageClasses()
        .map((tc) => ({
          ...tc,
          isHost: tc.userLanguageClasses.some((x) => x.userName === user?.userName && x.host),
          isGoing: tc.userLanguageClasses.some((x) => x.userName === user?.userName && !x.host),
        }))
        .filter(
          (tc) =>
            (tc.isHost && params.attend === AttendanceType.HOST) ||
            (tc.isGoing && params.attend === AttendanceType.GOING) ||
            params.attend === null
        )
        .filter((tc) => params.time === null || timeToNumber(tc.time) >= timeToNumber(params.time))
    );
  }

  #evalTime(time: string): void {
    const timeNumber = timeToNumber(time);
    if (!this.#displayedHours().includes(timeNumber)) {
      if (timeNumber >= 900 && timeNumber < 1200) this.displayedHours().push(900);
      else if (timeNumber >= 1200 && timeNumber < 1500) this.displayedHours().push(1200);
      else if (timeNumber >= 1500 && timeNumber < 1800) this.displayedHours().push(1500);
      else if (timeNumber >= 1800 && timeNumber < 2100) this.displayedHours().push(1800);
    }
  }

  public addLanguageClass(data: ILanguageClass): Observable<IApiResponse<ILanguageClass>> {
    return this.#http.post<IApiResponse<ILanguageClass>>(this.#baseUrl, data).pipe(
      tap((response) => {
        this.#languageClasses.update((value) => [...value, response.data]);
      })
    );
  }

  public editLanguageClass(
    id: number,
    data: ILanguageClass
  ): Observable<IApiResponse<ILanguageClass>> {
    return this.#http.put<IApiResponse<ILanguageClass>>(this.#baseUrl + `/${id}`, data).pipe(
      tap((response) => {
        this.#updateLanguageClassesSignal(id, response.data);
      })
    );
  }

  public removeLanguageClass(LanguageClassId: number): Observable<IApiResponse<void>> {
    return this.#http.delete<IApiResponse<any>>(this.#baseUrl + '/' + LanguageClassId).pipe(
      tap(() => {
        const updatedClasses = this.languageClasses().filter(
          (value) => value.id !== LanguageClassId
        );
        this.#languageClasses.set(updatedClasses);
      })
    );
  }

  public attendLanguageClass(id: number): Observable<IApiResponse<ILanguageClass>> {
    return this.#http
      .post<IApiResponse<ILanguageClass>>(`${this.#baseUrl}/${id}/attend`, null)
      .pipe(
        tap((response) => {
          this.#updateLanguageClassesSignal(id, response.data);
        })
      );
  }

  public abandonLanguageClass(id: number): Observable<IApiResponse<ILanguageClass>> {
    return this.#http.delete<IApiResponse<ILanguageClass>>(`${this.#baseUrl}/${id}/abandon`).pipe(
      tap((response) => {
        this.#updateLanguageClassesSignal(id, response.data);
      })
    );
  }

  #updateLanguageClassesSignal(languageClassId: number, response: ILanguageClass): void {
    const index = this.languageClasses().findIndex(
      (languageClass) => languageClass.id === languageClassId
    );
    if (index !== -1) {
      this.#languageClasses.update((values) => {
        const updatedClasses = [...values];
        updatedClasses[index] = response;
        return updatedClasses;
      });
    }
  }
}
