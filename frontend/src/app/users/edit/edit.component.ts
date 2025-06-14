import { Component, OnInit } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { ApiService } from '../../shared/api.service';

@Component({
  selector: 'app-edit',
  templateUrl: './edit.component.html',
  styleUrls: ['./edit.component.css']
})
export class EditComponent implements OnInit {
  user: any = {
    id: null,
    firstName: '',
    lastName: '',
    email: '',
    permissions: [] as string[],
    password: '',
    isAdmin: false,
  };

  availablePermissions: string[] = [];

  constructor(
    private route: ActivatedRoute,
    private apiService: ApiService,
    private router: Router
  ) {}

  ngOnInit(): void {
    this.loadPermissions();

    const userId = this.route.snapshot.paramMap.get('id');
    if (userId) {
      this.loadUser(Number(userId));
    } else {
      alert('Invalid user ID');
      this.router.navigate(['/users']);
    }
  }

  loadPermissions() {
    this.apiService.getPermissions().subscribe({
      next: (data: string[]) => {
        this.availablePermissions = data;
      },
      error: () => {
        alert('Failed to load permissions');
      },
    });
  }

  loadUser(id: number) {
    this.apiService.getUsers().subscribe({
      next: (users) => {
        const user = users.find((u: any) => u.id === id);
        if (user) {
          this.user = {
            ...user,
            password: '',
            permissions: [...(user.permissions || [])],
          };
        } else {
          alert('User not found');
          this.router.navigate(['/users']);
        }
      },
      error: () => {
        alert('Failed to load user');
      },
    });
  }
  onPermissionChange(permission: string, event: Event): void {
    const isChecked = (event.target as HTMLInputElement).checked;
    if (isChecked) {
      this.user.permissions.push(permission);
    } else {
      this.user.permissions = this.user.permissions.filter((p: string) => p !== permission);
    }
  }

  onSubmit() {
    this.apiService.updateUser(this.user.id, this.user).subscribe({
      next: (newToken: string) => {
        if (newToken !== 'User updated successfully') {
          localStorage.setItem('token', newToken);
          alert('Your permissions have been updated.');
          this.router.navigate(['/users']);
        } else {
          alert('User updated successfully.');
          this.router.navigate(['/users']);
        }
      },
      error: () => alert('Failed to update user'),
    });
  }

}
