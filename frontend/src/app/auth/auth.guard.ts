import { Injectable } from '@angular/core';
import { CanActivate, Router, ActivatedRouteSnapshot, RouterStateSnapshot } from '@angular/router';

@Injectable({
  providedIn: 'root',
})
export class AuthGuard implements CanActivate {
  private static permissions: string[] = [];

  constructor(private router: Router) {}

  canActivate(route: ActivatedRouteSnapshot, state: RouterStateSnapshot): boolean {
    const token = localStorage.getItem('token');
    const adminOnly = route.data['adminOnly'] || false;

    if (token) {
      const userPermissions = this.getPermissionsFromToken(token);
      AuthGuard.permissions = userPermissions;
      const isAdmin = this.getIsAdminFromToken(token);

      const requiredPermissions = route.data['permissions'] as string[];

      if (adminOnly && !isAdmin) {
        alert('Access denied. Admins only.');
        this.router.navigate(['/']);
        return false;
      }

      if (!requiredPermissions || requiredPermissions.every((p) => userPermissions.includes(p))) {
        return true;
      }

      alert('Access denied: insufficient permissions');
    }

    this.router.navigate(['/auth/login']);
    return false;
  }

  private getPermissionsFromToken(token: string): string[] {
    try {
      const payload = JSON.parse(atob(token.split('.')[1]));
      return payload.permissions || [];
    } catch (e) {
      console.error('Failed to parse token:', e);
      return [];
    }
  }

  static getPermissions(): string[] {
    return this.permissions;
  }

  private getIsAdminFromToken(token: string): boolean {
    try {
      const payload = JSON.parse(atob(token.split('.')[1]));
      return payload.isAdmin || false;
    } catch (e) {
      console.error('Failed to parse token:', e);
      return false;
    }
  }
}
