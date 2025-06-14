import {Component, OnDestroy, OnInit} from '@angular/core';
import { ApiService } from "../../../shared/api.service";
import { Router } from "@angular/router";
import { AuthGuard } from "../../../auth/auth.guard";

@Component({
  selector: 'app-list',
  templateUrl: './list.component.html',
  styleUrls: ['./list.component.css'],
})
export class ListComponent implements OnInit, OnDestroy {
  orders: {
    id: number;
    status: string;
    createdBy: {
      firstName: string;
      lastName: string;
    };
    items: { name: string }[];
    scheduledFor: string;
    createdDate: string;
  }[] = [];

  canCancelOrder: boolean = false;
  canTrackOrder: boolean = false;

  searchCriteria = {
    status: '',
    dateFrom: '',
    dateTo: '',
    userId: null,
  };

  isAdmin: boolean = false;
  pollingInterval: any;

  currentPage: number = 0;
  pageSize: number = 10;
  totalPages: number = 0;

  constructor(private apiService: ApiService, private router: Router) {}

  ngOnInit(): void {
    this.loadOrders();
    this.startPolling();
    this.checkPermissions();
    this.checkAdminStatus();
  }

  ngOnDestroy(): void {
    this.stopPolling();
  }

  startPolling(): void {
    this.pollingInterval = setInterval(() => {
      this.refreshOrders();
    }, 3000);
  }

  refreshOrders(): void {
    this.orders.forEach(order => {
      this.apiService.trackOrder(order.id).subscribe({
        next: (response: { status: string }) => {
          order.status = response.status;
        },
        error: () => console.error(`Failed to refresh status for order ${order.id}`),
      });
    });
  }

  stopPolling(): void {
    if (this.pollingInterval) {
      clearInterval(this.pollingInterval);
    }
  }

  trackOrder(orderId: number): void {
    this.apiService.trackOrder(orderId).subscribe({
      next: (res) => {
        const order = this.orders.find(o => o.id === orderId);
        if (order) {
          order.status = res.status;
        }
        alert(`Order ${orderId} status: ${res.status}`);
      },
      error: (err) => {
        if (err.status === 403) {
          alert('You are not authorized to track this order.');
        } else {
          alert(`Failed to track order ${orderId}`);
        }
      },
    });
  }

  checkPermissions() {
    const permissions = AuthGuard.getPermissions();
    this.canCancelOrder = permissions.includes('CAN_CANCEL_ORDER');
    this.canTrackOrder = permissions.includes('CAN_TRACK_ORDER');
  }

  createOrder(): void {
    this.router.navigate(['/orders/create']);
  }

  loadOrders(): void {
    this.apiService.getOrdersPaginated(this.currentPage, this.pageSize).subscribe({
      next: (data) => {
        this.orders = data.content;
        this.totalPages = data.totalPages;
      },
      error: (err) => {
        console.error('Failed to load paginated orders:', err);
        alert('Failed to load orders');
      },
    });
  }

  onPageChange(page: number): void {
    if (page >= 0 && page < this.totalPages) {
      this.currentPage = page;
      this.loadOrders();
    }
  }

  cancelOrder(orderId: number): void {
    if (!confirm('Are you sure you want to cancel this order?')) {
      return;
    }

    this.apiService.cancelOrder(orderId).subscribe({
      next: () => {
        alert('Order canceled successfully');
        this.loadOrders();
      },
      error: (err) => {
        console.error('Failed to cancel order:', err);
        alert('Failed to cancel order');
      },
    });
  }

  private checkAdminStatus(): void {
    try {
      const token = localStorage.getItem('token');
      if (!token) {
        console.warn('No token found in local storage.');
        this.isAdmin = false;
        return;
      }

      const payload = JSON.parse(atob(token.split('.')[1]));
      this.isAdmin = payload.isAdmin || false;
    } catch (e) {
      console.error('Failed to parse token:', e);
      this.isAdmin = false;
    }
  }

  onSearch(): void {
    const params: any = {
      status: this.searchCriteria.status || null,
      dateFrom: this.searchCriteria.dateFrom ? new Date(this.searchCriteria.dateFrom).toISOString() : null,
      dateTo: this.searchCriteria.dateTo ? new Date(this.searchCriteria.dateTo).toISOString() : null,
    };

    if (this.isAdmin && this.searchCriteria.userId) {
      params.userId = this.searchCriteria.userId;
    }

    this.apiService.searchOrders(params).subscribe({
      next: (data: any[]) => (this.orders = data),
      error: (error) => console.error('Failed to search orders:', error),
    });
  }

  isValidDate(date: any): boolean {
    return date && !isNaN(Date.parse(date));
  }

}
