import { Component, OnInit } from '@angular/core';
import { Router } from '@angular/router';
import { ApiService } from '../../shared/api.service';

@Component({
  selector: 'app-add',
  templateUrl: './add.component.html',
  styleUrls: ['./add.component.css']
})
export class AddComponent implements OnInit {
  user: any = {
    firstName: '',
    lastName: '',
    email: '',
    password: '',
    permissions: [] as string[],
    isAdmin: false,
  };

  availablePermissions: string[] = [];

  constructor(private apiService: ApiService, private router: Router) {}

  ngOnInit(): void {
    this.loadPermissions();
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

  onPermissionChange(permission: string, event: Event): void {
    const isChecked = (event.target as HTMLInputElement).checked;
    if (isChecked) {
      this.user.permissions.push(permission);
    } else {
      this.user.permissions = this.user.permissions.filter((p: string) => p !== permission);
    }
  }

  onSubmit() {
    this.apiService.addUser(this.user).subscribe({
      next: () => {
        alert('User created successfully');
        this.router.navigate(['/users']);
      },
      error: () => {
        alert('Failed to create user');
      },
    });
  }
}
