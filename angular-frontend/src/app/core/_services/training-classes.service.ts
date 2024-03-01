import {inject, Injectable, signal} from '@angular/core';
import {HttpClient} from "@angular/common/http";
import {_api_default, _api_training_classes} from "../../shared/_constVars/_api_consts";
import {catchError, map, Observable, of} from "rxjs";
import {ITrainingClass} from "../../shared/_models/ITrainingClass";
import {IApiResponse} from "../../shared/_models/IApiResponse";
import {toSignal} from '@angular/core/rxjs-interop';
import {IUser} from "../../shared/_models/IUser";
import {AttendanceType, Params} from "../../shared/_models/Params";
import {timeToNumber} from "../../shared/_helper/_trainingClass";

@Injectable({
    providedIn: 'root'
})
export class TrainingClassesService {
    #http = inject(HttpClient);
    baseUrl = _api_default + _api_training_classes;
    trainingClasses: ITrainingClass[] = [];
    displayedHours = signal([])

    private trainingClasses$ = this.#http.get<IApiResponse<ITrainingClass[]>>(this.baseUrl).pipe(
        map((response) => {
            this.trainingClasses = response.data;
            return response.data
        })
    );

    filterAndSort(user: IUser, params: Params) {
        return this.trainingClasses$.pipe(map((response) => {
            const filteredAndSorted = response
                .map(tc => ({
                    ...tc,
                    isHost: tc.userTrainingClasses.some(x =>
                        (x.userName === user.userName) && x.host),
                    isGoing: tc.userTrainingClasses.some(x =>
                        (x.userName === user.userName) && !x.host)
                }))
                .sort((a, b) => +timeToNumber(a.time) - +timeToNumber(b.time))
                .filter(tc => tc.isHost && params.attend === AttendanceType.HOST
                    || tc.isGoing && params.attend === AttendanceType.GOING
                    || params.attend === null)
                .filter(tc => params.time === null
                    || (timeToNumber(tc.time) >= timeToNumber(params.time)))

            filteredAndSorted.forEach(trClass => this.evalTime(trClass.time));
            return filteredAndSorted;

        }))
    }

    evalTime(time: string) {
        const timeNumber = timeToNumber(time);
        if (!this.displayedHours().includes(timeNumber)) {
            if (timeNumber >= 900 && timeNumber < 1200) this.displayedHours().push(900);
            else if (timeNumber >= 1200 && timeNumber < 1500) this.displayedHours().push(1200);
            else if (timeNumber >= 1500 && timeNumber < 1800) this.displayedHours().push(1500);
            else if (timeNumber >= 1800 && timeNumber < 2100) this.displayedHours().push(1800);
        }
    }

    trainingClass$(id: number): Observable<ITrainingClass> {
        const trainingClass = this.trainingClasses.find(trClass => trClass.id === id);
        if (trainingClass) return of(trainingClass);

        return this.#http.get<IApiResponse<ITrainingClass>>(this.baseUrl + '/' + id).pipe(
            map(response => response.data)
        );
    }

    addTrainingClass(data: ITrainingClass) {
        return this.#http.post(this.baseUrl, data).pipe(
            map((response: IApiResponse<ITrainingClass[]>) => response.data)
        );
    }

    editTrainingClass(id: number, data: ITrainingClass) {
        return this.#http.put(this.baseUrl + `/${id}`, data).pipe(
            map((response: IApiResponse<ITrainingClass[]>) => response.data)
        );
    }

    removeTrainingClass(itemId: number) {
        return this.#http.delete(this.baseUrl + '/' + itemId).pipe(
            map((response: IApiResponse<ITrainingClass[]>) => response.data)
        );
    }

    attendTrainingClass(id: number) {
        return this.#http.post(`${this.baseUrl}/${id}/attend`, null).pipe(
            map((response: IApiResponse<ITrainingClass[]>) => response.data)
        );
    }

    abandonTrainingClass(id: number) {
        return this.#http.delete(`${this.baseUrl}/${id}/abandon`).pipe(
            map((response: IApiResponse<ITrainingClass[]>) => response.data)
        );
    }
}
