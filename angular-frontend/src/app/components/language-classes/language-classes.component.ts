import {
  Component,
  computed,
  inject,
  OnDestroy,
  Signal,
  signal,
  WritableSignal,
} from '@angular/core';
import { LanguageClassesService } from '../../core/_services/language-classes.service';
import { Router, RouterModule } from '@angular/router';
import { AccountService } from '../../core/_services/account.service';
import { FormControl, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { AttendanceType, Params } from '../../shared/_models/Params';
import { _client_language_classes } from '../../shared/_constVars/_client_consts';
import { CommonModule } from '@angular/common';
import { NgxMaterialTimepickerModule } from 'ngx-material-timepicker';
import { AuthenticatePipe } from '../../core/_services/authenticate.pipe';
import { HandleImageErrorDirective } from '../../core/_services/handle-image-error.directive';
import { SnackbarService } from '../../core/_services/snackbar.service';
import { MatDialog } from '@angular/material/dialog';
import { ConfirmationDialogComponent } from '../confirmation-dialog/confirmation-dialog.component';
import { timeToNumber } from '../../shared/_helper/_languageClass';
import { Subject, takeUntil } from 'rxjs';
import { LanguageClassItemComponent } from './language-class-item/language-class-item.component';
import { IUser } from '../../shared/_models/IUser';
import { ILanguageClass } from '../../shared/_models/ILanguageClass';

@Component({
  selector: 'app-Language-classes',
  standalone: true,
  imports: [
    RouterModule,
    CommonModule,
    ReactiveFormsModule,
    NgxMaterialTimepickerModule,
    AuthenticatePipe,
    HandleImageErrorDirective,
    LanguageClassItemComponent,
  ],
  templateUrl: './language-classes.component.html',
  styleUrl: './language-classes.component.scss',
})
export class LanguageClassesComponent implements OnDestroy {
  #destroySubject$: Subject<void> = new Subject<void>();
  #snackBar = inject(SnackbarService);
  #dialog = inject(MatDialog);
  #languageClassesService = inject(LanguageClassesService);
  #accountService = inject(AccountService);
  #router = inject(Router);
  timeForm: FormGroup;
  displayedHours: Signal<number[]> = this.#languageClassesService.displayedHours;
  protected readonly AttendanceType = AttendanceType;
  classesParams: WritableSignal<Params | undefined> = signal<Params | undefined>(new Params());
  currentUser: Signal<IUser> = this.#accountService.currentUser;
  languageClasses: Signal<ILanguageClass[]> = computed(() =>
    this.#languageClassesService.getLanguageClasses(this.currentUser(), this.classesParams())
  );

  ngOnInit(): void {
    this.#languageClassesService.languageClasses$.pipe(takeUntil(this.#destroySubject$)).subscribe({
      next: () => this.#snackBar.success('Language classes retrieved'),
      error: (err) => console.error(err),
    });

    this.timeForm = new FormGroup({
      time: new FormControl('', [Validators.required]),
    });
  }

  ngOnDestroy(): void {
    this.#destroySubject$.next();
    this.#destroySubject$.complete();
  }

  public filterAttendance(param: AttendanceType): void {
    this.classesParams.update((value) => {
      return { ...value, attend: param };
    });
  }

  public submitFunc(): void {
    this.classesParams.update((value) => {
      return { ...value, time: this.timeForm.value.time };
    });
  }

  public formatTime(time: string): string {
    if (timeToNumber(time) >= 900 && timeToNumber(time) < 1200) return '9:00';
    else if (timeToNumber(time) >= 1200 && timeToNumber(time) < 1500) return '12:00';
    else if (timeToNumber(time) >= 1500 && timeToNumber(time) < 1800) return '15:00';
    else return '18:00';
  }

  public isButtonVisible(time: string): boolean {
    const timeNumber = timeToNumber(time);
    return this.displayedHours().includes(timeNumber);
  }

  public goToProfile(username: string): void {
    this.#router.navigate([`/profiles/${username}`]);
  }

  public joinClass(id: number): void {
    this.#languageClassesService
      .attendLanguageClass(id)
      .pipe(takeUntil(this.#destroySubject$))
      .subscribe({
        next: () => this.#snackBar.success('Successfully sign up for the class'),
        error: (err) => console.error(err),
      });
  }

  public quitClass(id: number): void {
    this.#languageClassesService
      .abandonLanguageClass(id)
      .pipe(takeUntil(this.#destroySubject$))
      .subscribe({
        next: () => this.#snackBar.success('Successfully quit the class'),
        error: (err) => console.error(err),
      });
  }

  public deleteClass(id: number): void {
    const dialogRef = this.#dialog.open(ConfirmationDialogComponent, {
      data: {
        message: 'Are you sure want to delete this class?',
        buttonText: {
          ok: 'Yes',
          cancel: 'No',
        },
      },
      panelClass: 'custom-panel-class',
    });

    dialogRef
      .afterClosed()
      .pipe(takeUntil(this.#destroySubject$))
      .subscribe((confirmed: boolean) => {
        if (confirmed) {
          this.#languageClassesService
            .removeLanguageClass(id)
            .pipe(takeUntil(this.#destroySubject$))
            .subscribe({
              next: () => this.#snackBar.success('Language class has been deleted'),
              error: (err) => console.error(err),
            });
        }
      });
  }
}
