import {Component, inject, OnInit, ViewChild} from '@angular/core';
import {FormControl, FormGroup, ReactiveFormsModule, Validators} from "@angular/forms";
import {ITrainingClass} from "../../shared/_models/ITrainingClass";
import {TrainingClassesService} from "../../core/_services/training-classes.service";
import {ActivatedRoute, Router} from "@angular/router";
import {getDaysOfWeekNumbers, getDaysOfWeekWords} from "../../shared/_constVars/_days";
import {CommonModule, Location} from "@angular/common";
import {NgxMaterialTimepickerModule} from "ngx-material-timepicker";
import {SnackbarService} from "../../core/_services/snackbar.service";

@Component({
    selector: 'app-form',
    standalone: true,
    imports: [CommonModule, ReactiveFormsModule, NgxMaterialTimepickerModule],
    templateUrl: './form.component.html',
    styleUrl: './form.component.scss'
})
export class FormComponent implements OnInit {
    #trainingClassesService = inject(TrainingClassesService);
    #activatedRoute = inject(ActivatedRoute);
    #router = inject(Router);
    #location = inject(Location);
    #snackBarService = inject(SnackbarService);
    @ViewChild('myPickerRef') myTimePicker;
    addClassForm: FormGroup;
    isEditMode: boolean = false;
    trainingClass: ITrainingClass;

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

        this.#activatedRoute.url.subscribe({
            next: (urlSegments) => {
                this.isEditMode = urlSegments.some(segment => segment.path === 'edit');
            },
            error: err => console.error(err)
        });
        if (this.isEditMode) this.populateForm();
    }

    submitFunc() {
        this.addClassForm.value.dayOfWeek = getDaysOfWeekNumbers(this.addClassForm.value.dayOfWeek);

        if (!this.isEditMode) {
            this.#trainingClassesService.addTrainingClass(this.addClassForm.value)
                .subscribe({
                    next: () => {
                        this.#router.navigate(['/training-classes'])
                            .then(() => this.#snackBarService.success('New language class has been added')
                            );
                    },
                    error: err => console.error(err)
                });
        } else {
            this.#trainingClassesService.editTrainingClass(this.#activatedRoute.snapshot.params['id'], this.addClassForm.value)
                .subscribe({
                    next: () => {
                        this.#router.navigate(['/training-classes']).then(
                            () => this.#snackBarService.success('Language class has been updated')
                        );
                    },
                    error: err => console.error(err)
                });
        }
    }

    populateForm() {
        const trainingClassId = this.#activatedRoute.snapshot.params['id'];

        this.#trainingClassesService.trainingClass$(trainingClassId)
            .subscribe({
                next: (trainingClass) => {
                    this.trainingClass = trainingClass;
                    this.addClassForm.patchValue({
                        title: this.trainingClass.title,
                        category: this.trainingClass.category,
                        city: this.trainingClass.city,
                        country: this.trainingClass.country,
                        province: this.trainingClass.province,
                        address: this.trainingClass.address,
                        postalCode: this.trainingClass.postalCode,
                        dayOfWeek: getDaysOfWeekWords(this.trainingClass.dayOfWeek),
                        description: this.trainingClass.description,
                        time: this.trainingClass.time,
                        totalSpots: this.trainingClass.totalSpots,
                    });
                },
                error: err => console.error(err)
            });
    }

    cancel() {
        this.#location.back();
    }
}
