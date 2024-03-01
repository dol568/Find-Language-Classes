import {Routes} from "@angular/router";
import {FormComponent} from "../form/form.component";
import {TrainingClassDetailComponent} from "./training-class-detail/training-class-detail.component";
import {TrainingClassesComponent} from "./training-classes.component";

export const TRAINING_CLASSES_ROUTES: Routes = [
  { path: '', component: TrainingClassesComponent},
  { path: ':id', component: TrainingClassDetailComponent },
  { path: ':id' +'/edit', component: FormComponent }
]
