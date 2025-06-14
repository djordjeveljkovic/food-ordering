import { Component, OnInit } from '@angular/core';
import {ApiService} from "../../../shared/api.service";

@Component({
  selector: 'app-error-history',
  templateUrl: './error-history.component.html',
  styleUrls: ['./error-history.component.css'],
})
export class ErrorHistoryComponent implements OnInit {
  errors: any[] = [];
  currentPage = 0;
  totalPages = 0;
  pageSize = 10;

  constructor(private apiService: ApiService,) {}

  ngOnInit(): void {
    this.loadErrors();
  }

  loadErrors(): void {
    this.apiService.getErrors(this.currentPage, this.pageSize).subscribe({
      next: (data: { content: any[]; totalPages: number }) => {
        this.errors = data.content;
        this.totalPages = data.totalPages;
      },
      error: (err: any) => {
        console.error('Error fetching error history:', err);
      },
    });
  }

  changePage(page: number): void {
    this.currentPage = page;
    this.loadErrors();
  }
}
