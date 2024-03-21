import { CanActivateFn, Router } from '@angular/router';
import { inject } from '@angular/core';
import { AccountService } from '../_services/account.service';

export const authGuard: CanActivateFn = (route, state) => {
  const accountService = inject(AccountService);
  const router = inject(Router);

  if (accountService.isAuthenticatedUser() || sessionStorage.getItem('token')) {
    return true;
  } else {
    router.navigate(['/']);
    return false;
  }
};
