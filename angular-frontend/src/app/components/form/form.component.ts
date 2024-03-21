import {
  Component,
  effect,
  inject,
  Injector,
  OnDestroy,
  OnInit,
  runInInjectionContext,
  signal,
  Signal,
  ViewChild,
  WritableSignal,
} from '@angular/core';
import { FormControl, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { LanguageClassesService } from '../../core/_services/language-classes.service';
import { ActivatedRoute, Router } from '@angular/router';
import { getDaysOfWeekNumbers, getDaysOfWeekWords } from '../../shared/_constVars/_days';
import { CommonModule, Location } from '@angular/common';
import { NgxMaterialTimepickerModule } from 'ngx-material-timepicker';
import { SnackbarService } from '../../core/_services/snackbar.service';
import { EMPTY, Subject, switchMap, takeUntil } from 'rxjs';
import { ILanguageClass } from '../../shared/_models/ILanguageClass';

@Component({
  selector: 'app-form',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule, NgxMaterialTimepickerModule],
  templateUrl: './form.component.html',
  styleUrl: './form.component.scss',
})
export class FormComponent implements OnInit, OnDestroy {
  #destroySubject$: Subject<void> = new Subject<void>();
  #injector = inject(Injector);
  #languageClassesService = inject(LanguageClassesService);
  #activatedRoute = inject(ActivatedRoute);
  #router = inject(Router);
  #snackBar = inject(SnackbarService);
  #location = inject(Location);
  @ViewChild('myPickerRef') myTimePicker;
  addClassForm: FormGroup;

  id: WritableSignal<number> = signal<number>(undefined);
  isEditMode: WritableSignal<boolean> = signal<boolean>(false);
  languageClass: Signal<ILanguageClass> = this.#languageClassesService.languageClass;

  ngOnInit(): void {
    this.addClassForm = new FormGroup({
      title: new FormControl('', [Validators.required]),
      category: new FormControl('', [Validators.required]),
      city: new FormControl('', [Validators.required]),
      country: new FormControl('', [Validators.required]),
      province: new FormControl('', [Validators.required]),
      address: new FormControl('', [Validators.required]),
      postalCode: new FormControl('', [Validators.required]),
      dayOfWeek: new FormControl('', [Validators.required]),
      description: new FormControl('', [Validators.required]),
      time: new FormControl('', [Validators.required]),
      totalSpots: new FormControl('', [Validators.required]),
    });

    this.#activatedRoute.url
      .pipe(
        switchMap((urlSegments) => {
          if (urlSegments.find((urlSegment) => urlSegment.path.includes('edit'))) {
            this.isEditMode.set(true);
            return this.#activatedRoute.paramMap.pipe(
              switchMap((params) => {
                const id = Number(params.get('id'));
                if (this.id !== null) {
                  this.id.set(id);
                  return this.#languageClassesService.languageClass$(this.id());
                } else {
                  return EMPTY;
                }
              })
            );
          } else {
            return EMPTY;
          }
        }),
        takeUntil(this.#destroySubject$)
      )
      .subscribe({
        error: (err) => console.error(err),
      });

    if (this.isEditMode()) {
      runInInjectionContext(this.#injector, () => {
        effect(() => {
          if (!!this.languageClass()) {
            this.#populateForm();
          }
        });
      });
    }
  }

  ngOnDestroy(): void {
    this.#destroySubject$.next();
    this.#destroySubject$.complete();
  }

  public submitFunc(): void {
    this.addClassForm.value.dayOfWeek = getDaysOfWeekNumbers(this.addClassForm.value.dayOfWeek);

    if (!this.isEditMode()) {
      this.#languageClassesService
        .addLanguageClass(this.addClassForm.value)
        .pipe(takeUntil(this.#destroySubject$))
        .subscribe({
          next: () => {
            this.#router
              .navigate(['/language-classes'])
              .then(() => this.#snackBar.success('New language class has been added'));
          },
          error: (err) => console.error(err),
        });
    } else {
      this.#languageClassesService
        .editLanguageClass(this.id(), this.addClassForm.value)
        .pipe(takeUntil(this.#destroySubject$))
        .subscribe({
          next: () => {
            this.#location.back();
            this.#snackBar.success('Language class has been updated');
          },
          error: (err) => console.error(err),
        });
    }
  }

  #populateForm(): void {
    this.addClassForm.patchValue({
      title: this.languageClass()?.title,
      category: this.languageClass()?.category,
      city: this.languageClass()?.city,
      country: this.languageClass()?.country,
      province: this.languageClass()?.province,
      address: this.languageClass()?.address,
      postalCode: this.languageClass()?.postalCode,
      dayOfWeek: getDaysOfWeekWords(this.languageClass()?.dayOfWeek),
      description: this.languageClass()?.description,
      time: this.languageClass()?.time,
      totalSpots: this.languageClass()?.totalSpots,
    });
  }

  public cancel(): void {
    this.#location.back();
  }
}
