import {Routes} from '@angular/router';
import {
  _client_add_class, _client_notfound, _client_profiles, _client_servererror,
  _client_signin,
  _client_signup,
  _client_training_classes
} from "./shared/_constVars/_client_consts";
import {authGuard} from "./core/_guard/auth.guard";

export const routes: Routes = [
  {
    path: '',
    loadComponent: () => import('./components/login/login.component')
      .then(m => m.LoginComponent)
  },
  {
    path: _client_signup,
    loadComponent: () => import('./components/register/register.component')
      .then(m => m.RegisterComponent)
  },
  {
    path: _client_add_class,
    canActivate: [authGuard],
    loadComponent: () => import('./components/form/form.component')
      .then(m => m.FormComponent)
  },
  {
    path: _client_training_classes,
    canActivate: [authGuard],
    loadChildren: () => import('./components/training-classes/training-classes.routes')
      .then(r => r.TRAINING_CLASSES_ROUTES)
  },
  {
    path: _client_profiles + '/:userName',
    canActivate: [authGuard],
    loadComponent: () => import('./components/profile/profile.component')
      .then(m => m.ProfileComponent)
  },
  {
    path: _client_servererror,
    loadComponent: () => import('./components/server-error/server-error.component')
      .then(m => m.ServerErrorComponent)
  },
  {
    path: _client_notfound,
    loadComponent: () => import('./components/notfound/notfound.component')
      .then(m => m.NotfoundComponent)
  },
  {path: '**', redirectTo: 'not-found', pathMatch: 'full'}
];
