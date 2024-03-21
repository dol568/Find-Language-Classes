import { Routes } from '@angular/router';
import { FormComponent } from '../form/form.component';
import { LanguageClassDetailComponent } from './language-class-detail/language-class-detail.component';
import { LanguageClassesComponent } from './language-classes.component';

export const LANGUAGE_CLASSES_ROUTES: Routes = [
  { path: '', component: LanguageClassesComponent },
  { path: ':id', component: LanguageClassDetailComponent },
  { path: ':id' + '/edit', component: FormComponent },
];
