import { inject } from '@angular/core';
import { CanActivateFn, Router } from '@angular/router';
import { UserRole } from '../../model/enum/user-role.enum';
import { AuthService } from '../service/auth.service';

export const authGuard: CanActivateFn = (route, state) => {
  const authService = inject(AuthService);
  const router = inject(Router);

  const isLoggedIn = authService.isLoggedIn();

  const userRole = authService.getRole();
  const allowedRoles = route.data?.['roles'] as UserRole[] | undefined;

  if (!isLoggedIn) {
    return router.createUrlTree(['/login']);
  }

  if (allowedRoles && (!userRole || !allowedRoles.includes(userRole))) {
    return router.createUrlTree(['/']);
  }

  return true;
};
