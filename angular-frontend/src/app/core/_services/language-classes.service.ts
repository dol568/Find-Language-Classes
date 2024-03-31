import { computed, inject, Injectable, Signal, signal, WritableSignal } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { _api_default, _api_language_classes } from '../../shared/_constVars/_api_consts';
import { forkJoin, map, Observable, switchMap, tap } from 'rxjs';
import { CommentDto, IComment, ILanguageClass } from '../../shared/_models/ILanguageClass';
import { IApiResponse } from '../../shared/_models/IApiResponse';
import { IUser } from '../../shared/_models/IUser';
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
  acc = inject(AccountService);
  #http = inject(HttpClient);
  img = inject(ImageService);
  #baseUrl = _api_default + _api_language_classes;
  #displayedHours: WritableSignal<number[]> = signal<number[]>([]);
  #languageClasses: WritableSignal<ILanguageClass[]> = signal<ILanguageClass[]>([]);
  #languageClass: WritableSignal<ILanguageClass> = signal<ILanguageClass>(undefined);
  #comments: WritableSignal<IPage<IComment>> = signal<IPage<IComment> | undefined>(undefined);
  comments: Signal<IPage<IComment>> = computed(this.#comments);
  currentUser = this.acc.currentUser;

  displayedHours: Signal<number[]> = computed(this.#displayedHours);
  languageClasses: Signal<ILanguageClass[]> = computed(this.#languageClasses);
  languageClass: Signal<ILanguageClass> = computed(this.#languageClass);

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

  // public getLanguageClasses(user: IUser, params: Params): ILanguageClass[] {
  public getLanguageClasses(user: IUser, params: Params) {
    this.#filterAndSort(user, params).forEach((languageClass) => {
      console.log(languageClass);
      this.#evalTime(languageClass.time);
    });
    return this.#filterAndSort(user, params);

    // ...languageClass,
    // hostImage: this.img
    // .getImage(languageClass.hostImage)
    // .subscribe((image) => {
    //   (languageClass.hostImage = image as string)
    // })
    // // console.log(languageClass.hostImage)
    // });
    // })
  }

  public languageClasses$: Observable<ILanguageClass[]> = this.#http
    .get<IApiResponse<ILanguageClass[]>>(this.#baseUrl)
    .pipe(
      map((response) => {
        return response.data
      }),
      map(resp => resp.map(r => {
        return({...r,
          isHost: r.userLanguageClasses.some((x) => x.userName === this.currentUser()?.userName && x.host),
          isGoing: r.userLanguageClasses.some((x) => x.userName === this.currentUser()?.userName && !x.host),})
      })),
      tap((languageClasses) => {
        languageClasses.forEach((languageClass) => {
         

          // if (languageClass.hostImage) {
            this.img
              .getImage(languageClass.hostImage)
              .subscribe((image) => (languageClass.hostImage = image as string));
          // }
          if (languageClass.comments) {
            languageClass.comments.forEach((comment) => {
              this.img
                .getImage(comment.image)
                .subscribe((image) => (comment.image = image as string));
            });
          }
          if (languageClass.userLanguageClasses) {
            languageClass.userLanguageClasses.forEach((ulc) => {
              this.img.getImage(ulc.image).subscribe((image) => (ulc.image = image as string));
            });
          }
        });
        console.log(languageClasses)
        this.#languageClasses.set(languageClasses);
      })
    );

  langClassSignal = (id: number) => {
    const found = this.languageClasses().find((cl) => cl.id === id);
    if (found) {
      return computed(() => found);
    }
    return toSignal(this.languageClass$(id));
  };

  public languageClass$ = (id: number): Observable<ILanguageClass> =>
    this.#http.get<IApiResponse<ILanguageClass>>(this.#baseUrl + '/' + id).pipe(
      map((response) => response.data),
      tap((languageClass) => {

        if (languageClass.hostImage) {
        this.img.getImage(languageClass.hostImage).subscribe((image) => {
          languageClass.hostImage = image as string;
        });
        }
        if (languageClass.comments) {
          languageClass.comments.forEach((comment) => {
            this.img
              .getImage(comment.image)
              .subscribe((image) => (comment.image = image as string));
          });
        }
        if (languageClass.userLanguageClasses) {
          languageClass.userLanguageClasses.forEach((ulc) => {
            this.img.getImage(ulc.image).subscribe((image) => (ulc.image = image as string));
          });
        }
      })
    );

  #filterAndSort(user: IUser, params: Params) {
    console.log(this.languageClasses());

    //
    const langs =  this.languageClasses()
    // .map((tc) => ({
    //   ...tc,
    //   isHost: tc.userLanguageClasses.some((x) => x.userName === user?.userName && x.host),
    //   isGoing: tc.userLanguageClasses.some((x) => x.userName === user?.userName && !x.host),
    // }))
    .filter(
      (tc) =>
        (tc.isHost && params.attend === AttendanceType.HOST) ||
        (tc.isGoing && params.attend === AttendanceType.GOING) ||
        params.attend === null
    )
    .filter((tc) => params.time === null || timeToNumber(tc.time) >= timeToNumber(params.time))
    console.log(langs)
    return langs
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
        // if (response.hostImage) {
          this.img.getImage(response.hostImage).subscribe((image) => {
            response.hostImage = image as string;
          });
        // }
        if (response.userLanguageClasses) {
          response.userLanguageClasses.forEach((ulc) => {
            this.img.getImage(ulc.image).subscribe((image) => (ulc.image = image as string));
          });
        }
        if (response.comments) {
          response.comments.forEach((comment) => {
            // if (comment.image) {
            this.img
              .getImage(comment.image)
              .subscribe((image) => (comment.image = image as string));
            // }
          });
        }
        updatedClasses[index] = response;
        return updatedClasses;
      });
    }
  }
}
