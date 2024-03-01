import {Component, inject, OnDestroy, OnInit} from '@angular/core';
import {TrainingClassesService} from "../../core/_services/training-classes.service";
import {Router, RouterModule} from "@angular/router";
import {AccountService} from "../../core/_services/account.service";
import {FormControl, FormGroup, ReactiveFormsModule, Validators} from "@angular/forms";
import {IUser} from "../../shared/_models/IUser";
import {ITrainingClass} from "../../shared/_models/ITrainingClass";
import {AttendanceType, Params} from "../../shared/_models/Params";
import {_client_training_classes} from "../../shared/_constVars/_client_consts";
import {getDaysOfWeekWords} from "../../shared/_constVars/_days";
import {CommonModule} from "@angular/common";
import {NgxMaterialTimepickerModule} from "ngx-material-timepicker";
import {AuthenticatePipe} from "../../core/_services/authenticate.pipe";
import {HandleImageErrorDirective} from "../../core/_services/handle-image-error.directive";
import {SnackbarService} from "../../core/_services/snackbar.service";
import {MatDialog} from "@angular/material/dialog";
import {ConfirmationDialogComponent} from "../confirmation-dialog/confirmation-dialog.component";
import {timeToNumber} from "../../shared/_helper/_trainingClass";
import {catchError, EMPTY, Observable, Subscription} from "rxjs";

@Component({
    selector: 'app-training-classes',
    standalone: true,
    imports: [RouterModule, CommonModule, ReactiveFormsModule, NgxMaterialTimepickerModule, AuthenticatePipe, HandleImageErrorDirective],
    templateUrl: './training-classes.component.html',
    styleUrl: './training-classes.component.scss'
})
export class TrainingClassesComponent implements OnInit, OnDestroy {
    #snackBarService = inject(SnackbarService);
    #dialog = inject(MatDialog);
    #trainingClassesService = inject(TrainingClassesService);
    #accountService = inject(AccountService);
    #router = inject(Router);
    trainingClasses$: Observable<ITrainingClass[]>;
    currentUser: IUser;
    timeForm: FormGroup;
    displayedHours: any;
    _classesParams = new Params();
    protected readonly _client_training_classes = _client_training_classes;
    protected readonly getDaysOfWeekWords = getDaysOfWeekWords;
    protected readonly AttendanceType = AttendanceType;
    subscriptions: Subscription[] = [];
    subscription1$: Subscription;
    subscription2$: Subscription;
    subscription3$: Subscription;
    subscription4$: Subscription;

    ngOnInit(): void {
        const token = sessionStorage.getItem('token');
        if (token) {
            this.subscription1$ = this.#accountService.loadCurrentUser(token).subscribe({
                next: (user) => {
                    this.currentUser = user;
                    this.displayedHours = this.#trainingClassesService.displayedHours;
                    this.#loadClasses();
                },
                error: err => console.error(err)
            });
        }
        this.timeForm = new FormGroup({
            time: new FormControl('', [Validators.required])
        });
        this.subscriptions.push(this.subscription1$);
    }

    ngOnDestroy(): void {
        this.subscriptions.forEach((subscription) => subscription.unsubscribe());
    }

    #loadClasses() {
        this.trainingClasses$ = this.#trainingClassesService.filterAndSort(this.currentUser, this._classesParams)
            .pipe(catchError(err =>  {
                console.error(err);
                return EMPTY;
            }));
    }

    getClassesParams(): Params {
        return this._classesParams;
    }

    setClassesParams(value: Params) {
        this._classesParams = value;
    }

    filterAttendance(param: AttendanceType) {
        let params = this.getClassesParams();
        params.attend = param;
        this.setClassesParams(params);
        this.#loadClasses();
    }

    submitFunc() {
        let params = this.getClassesParams();
        params.time = this.timeForm.value.time;
        this.setClassesParams(params);
        this.#loadClasses();
    }

    formatTime(time: string) {
        if (timeToNumber(time) >= 900 && timeToNumber(time) < 1200) return '9:00';
        else if (timeToNumber(time) >= 1200 && timeToNumber(time) < 1500) return '12:00';
        else if (timeToNumber(time) >= 1500 && timeToNumber(time) < 1800) return '15:00';
        else return '18:00';
    }

    isButtonVisible(time: string): boolean {
        const timeNumber = timeToNumber(time);
        return this.displayedHours().includes(timeNumber);
    }

    goToProfile(username: string) {
        this.#router.navigate([`/profiles/${username}`]);
    }

    joinClass(id: number) {
        this.subscription2$ = this.#trainingClassesService.attendTrainingClass(id)
            .subscribe({
                next: () => {
                    this.#loadClasses();
                    this.#snackBarService.success('Successfully sign up for the class');
                },
                error: err => console.error(err)
            });
        this.subscriptions.push(this.subscription2$);
    }

    abandonClass(id: number) {
        this.subscription3$ = this.#trainingClassesService.abandonTrainingClass(id)
            .subscribe({
                next: () => {
                    this.#loadClasses();
                    this.#snackBarService.success('Successfully quit the class');
                },
                error: err => console.error(err)
            });
        this.subscriptions.push(this.subscription3$);
    }

    deleteTrainingClass(id: number) {
        const dialogRef = this.#dialog.open(ConfirmationDialogComponent, {
            data: {
                message: 'Are you sure want to delete this class?',
                buttonText: {
                    ok: 'Yes',
                    cancel: 'No'
                }
            },
            panelClass: 'custom-panel-class',
        });

        dialogRef.afterClosed().subscribe((confirmed: boolean) => {
            if (confirmed) {
                this.subscription4$ = this.#trainingClassesService.removeTrainingClass(id)
                    .subscribe({
                        next: () => {
                            this.#loadClasses();
                            this.#snackBarService.success('Language class has been deleted');
                        },
                        error: err => console.error(err)
                    });
                this.subscriptions.push(this.subscription4$);
            }
        });
    }
}
