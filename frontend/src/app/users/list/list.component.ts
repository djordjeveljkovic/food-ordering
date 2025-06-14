import { Component, OnInit } from '@angular/core';
import { ApiService } from '../../shared/api.service';
import {Router} from "@angular/router";
import {AuthGuard} from "../../auth/auth.guard";

@Component({
  selector: 'app-list',
  templateUrl: './list.component.html',
  styleUrls: ['./list.component.css']
})
export class ListComponent implements OnInit {
  users: any[] = [];
  canCreateUsers: boolean = false;
  canDeleteUsers: boolean = false;
  canEditUsers: boolean = false;

  constructor(private apiService: ApiService, private router: Router) {}

  ngOnInit(): void {
    this.checkPermissions();
    this.loadUsers();
  }

  checkPermissions() {
    const permissions = AuthGuard.getPermissions();
    this.canCreateUsers = permissions.includes('CAN_CREATE_USERS');
    this.canDeleteUsers = permissions.includes('CAN_DELETE_USERS');
    this.canEditUsers = permissions.includes('CAN_UPDATE_USERS');
  }

  loadUsers() {
    this.apiService.getUsers().subscribe({
      next: (data) => {
        this.users = data;
      },
      error: (error) => {
        alert('Failed to load users');
      },
    });
  }

  addUser() {
    this.router.navigate(['/users/add',]);
  }

  editUser(id: number) {
    this.router.navigate(['/users/edit', id]);
  }

  deleteUser(id: number) {
    this.apiService.deleteUser(id).subscribe({
      next: (response) => {
        console.log('Server response:', response);
        alert('User deleted');
        this.loadUsers();
      },
      error: (err) => {
        console.error('Failed to delete user:', err);
        alert('Failed to delete user');
      },
    });
  }
}
