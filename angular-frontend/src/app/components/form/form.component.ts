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
import { ActivatedRoute, ParamMap, Router } from '@angular/router';
import { getDaysOfWeekNumbers, getDaysOfWeekWords } from '../../shared/_constVars/_days';
import { CommonModule, Location } from '@angular/common';
import { NgxMaterialTimepickerModule } from 'ngx-material-timepicker';
import { SnackbarService } from '../../core/_services/snackbar.service';
import { EMPTY, Subject, switchMap, takeUntil } from 'rxjs';
import { ILanguageClass } from '../../shared/_models/ILanguageClass';
import { toSignal } from '@angular/core/rxjs-interop';

@Component({
  selector: 'app-form',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule, NgxMaterialTimepickerModule],
  templateUrl: './form.component.html',
  styleUrl: './form.component.scss',
})
export class FormComponent implements OnInit, OnDestroy {
  #destroySubject$: Subject<void> = new Subject<void>();
  #languageClassesService = inject(LanguageClassesService);
  #activatedRoute = inject(ActivatedRoute);
  #router = inject(Router);
  #snackBar = inject(SnackbarService);
  #location = inject(Location);
  @ViewChild('myPickerRef') myTimePicker;
  addClassForm: FormGroup;

  paramsUrl = toSignal(this.#activatedRoute.url);
  isEditMode = this.paramsUrl().findIndex((urlSegment) => urlSegment.path.includes('edit')) > -1;

  params: Signal<ParamMap> = toSignal(this.#activatedRoute.paramMap);
  id = Number(this.params().get('id'));

  languageClass = this.isEditMode ? this.#languageClassesService.langClassSignal(this.id): null;

  constructor() {
    console.log('form')
    console.log(this.languageClass)
    console.log(this.isEditMode)
    if (this.isEditMode !== null && this.languageClass !== null) {
        effect(() => {
          // if (!!this.languageClass()) {
            this.#populateForm();
          // }
      });
    }
  }

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
  }

  ngOnDestroy(): void {
    this.#destroySubject$.next();
    this.#destroySubject$.complete();
  }

  public submitFunc(): void {
    this.addClassForm.value.dayOfWeek = getDaysOfWeekNumbers(this.addClassForm.value.dayOfWeek);

    if (!this.isEditMode) {
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
        .editLanguageClass(this.id, this.addClassForm.value)
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
