import { computed, inject, Injectable, Signal, signal, WritableSignal } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { _api_default, _api_language_classes } from '../../shared/_constVars/_api_consts';
import { concatMap, forkJoin, from, map, Observable, tap, toArray } from 'rxjs';
import { CommentDto, IComment, ILanguageClass } from '../../shared/_models/ILanguageClass';
import { IApiResponse } from '../../shared/_models/IApiResponse';
import { AttendanceType, Params } from '../../shared/_models/Params';
import { timeToNumber } from '../../shared/_helper/_languageClass';
import { AccountService } from './account.service';
import { ShopParams } from '../../shared/_models/ShopParams';
import { IApiResponsePage } from '../../shared/_models/IApiResponsePage';
import { IPage } from '../../shared/_models/IPage';
import { _paramsAppend } from '../../shared/_helper/_paramsAppend';
import { toSignal } from '@angular/core/rxjs-interop';
import { ImageService } from './image.service';

@Injectable({
  providedIn: 'root',
})
export class LanguageClassesService {
  #accountService = inject(AccountService);
  #http = inject(HttpClient);
  img = inject(ImageService);
  #baseUrl = _api_default + _api_language_classes;
  #displayedHours: WritableSignal<number[]> = signal<number[]>([]);
  #languageClasses: WritableSignal<ILanguageClass[]> = signal<ILanguageClass[]>([]);
  #comments: WritableSignal<IPage<IComment>> = signal<IPage<IComment> | undefined>(undefined);
  comments: Signal<IPage<IComment>> = computed(this.#comments);
  currentUser = this.#accountService.currentUser;

  displayedHours: Signal<number[]> = computed(this.#displayedHours);
  languageClasses: Signal<ILanguageClass[]> = computed(this.#languageClasses);

  public comments$ = function (
    id: number,
    params: ShopParams
  ): Observable<IApiResponsePage<IPage<IComment>>> {
    const options = {
      params: _paramsAppend(params),
      observe: 'response' as 'body',
      responseType: 'json',
    };

    return this.#http
      .get(`${this.#baseUrl}/${id}/comment`, options)
      .pipe(map((response) => response['body']));
  };

  public postComment(data: CommentDto, id: number): Observable<IApiResponse<IComment>> {
    return this.#http.post<IApiResponse<IComment>>(`${this.#baseUrl}/${id}/comment`, data);
  }

  public getLanguageClasses(params: Params): ILanguageClass[] {
    this.#filterAndSort(params).forEach((languageClass) => {
      this.#evalTime(languageClass.time);
    });
    return this.#filterAndSort(params);
  }

  public languageClasses$: Observable<ILanguageClass[]> = this.#http
  .get<IApiResponse<ILanguageClass[]>>(this.#baseUrl)
  .pipe(
    map((response) => response.data),
    map((languageClasses) =>
      languageClasses.map((languageClass) => ({
        ...languageClass,
        isHost: languageClass.userLanguageClasses.some(
          (x) => x.userName === this.currentUser()?.userName && x.host
        ),
        isGoing: languageClass.userLanguageClasses.some(
          (x) => x.userName === this.currentUser()?.userName && !x.host
        ),
      }))
    ),
    concatMap((languageClasses) =>
      from(languageClasses).pipe(
        concatMap((languageClass) =>
          this.processLanguageClass(languageClass)
        ),
        toArray()
      )
    ),
    tap(languageClasses => this.#languageClasses.set(languageClasses))
  );

  private processLanguageClass(languageClass: ILanguageClass): Observable<ILanguageClass> {
    return new Observable<ILanguageClass>((observer) => {
      this.img.getImage(languageClass.hostImage).subscribe((hostImage) => {
        languageClass.hostImage = hostImage as string;
        if (languageClass.comments) {
          const commentImageObservables = languageClass.comments.map((comment) =>
            this.img.getImage(comment.image).pipe(
              tap((commentImage) => (comment.image = commentImage as string))
            )
          );
          forkJoin(commentImageObservables).subscribe(() => {
            if (languageClass.userLanguageClasses) {
              const userLanguageClassImageObservables = languageClass.userLanguageClasses.map((ulc) =>
                this.img.getImage(ulc.image).pipe(
                  tap((ulcImage) => (ulc.image = ulcImage as string))
                )
              );
              forkJoin(userLanguageClassImageObservables).subscribe(() => {
                observer.next(languageClass);
                observer.complete();
              });
            } else {
              observer.next(languageClass);
              observer.complete();
            }
          });
        } else if (languageClass.userLanguageClasses) {
          const userLanguageClassImageObservables = languageClass.userLanguageClasses.map((ulc) =>
            this.img.getImage(ulc.image).pipe(
              tap((ulcImage) => (ulc.image = ulcImage as string))
            )
          );
          forkJoin(userLanguageClassImageObservables).subscribe(() => {
            observer.next(languageClass);
            observer.complete();
          });
        } else {
          observer.next(languageClass);
          observer.complete();
        }
      });
    });
  }

  languageClass = (id: number) => {
    const foundClass = this.languageClasses().find((languageClass) => languageClass.id === id);
    if (foundClass) {
      return computed(() => foundClass);
    }
    return toSignal(this.languageClass$(id));
  };

  public languageClass$ = (id: number): Observable<ILanguageClass> =>
    this.#http.get<IApiResponse<ILanguageClass>>(this.#baseUrl + '/' + id).pipe(
      map((response) => response.data),
      concatMap((languageClass) =>
          this.processLanguageClass(languageClass)
        ),

    );

  #filterAndSort(params: Params) {
    return this.languageClasses()
      .filter(
        (languageClass) =>
          (languageClass.isHost && params.attend === AttendanceType.HOST) ||
          (languageClass.isGoing && params.attend === AttendanceType.GOING) ||
          params.attend === null
      )
      .filter(
        (languageClass) =>
          params.time === null || timeToNumber(languageClass.time) >= timeToNumber(params.time)
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

  public removeLanguageClass(languageClassId: number): Observable<IApiResponse<void>> {
    return this.#http.delete<IApiResponse<any>>(this.#baseUrl + '/' + languageClassId).pipe(
      tap(() => {
        const updatedClasses = this.languageClasses().filter(
          (value) => value.id !== languageClassId
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
          response.data.isGoing = true;
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
        this.img.getImage(response.hostImage).subscribe((image) => {
          response.hostImage = image as string;
        });
        if (response.userLanguageClasses) {
          response.userLanguageClasses.forEach((ulc) => {
            this.img.getImage(ulc.image).subscribe((image) => (ulc.image = image as string));
          });
        }
        if (response.comments) {
          response.comments.forEach((comment) => {
            this.img
              .getImage(comment.image)
              .subscribe((image) => (comment.image = image as string));
          });
        }
        updatedClasses[index] = response;
        return updatedClasses;
      });
    }
  }
}
