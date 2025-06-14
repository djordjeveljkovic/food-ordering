import { Component, OnInit } from '@angular/core';
import { ApiService } from "../../../shared/api.service";
import { FormsModule } from "@angular/forms";
import { CommonModule } from "@angular/common";
import { Router } from "@angular/router";

@Component({
  selector: 'app-order-form',
  templateUrl: './form.component.html',
  standalone: true,
  imports: [
    CommonModule,
    FormsModule
  ]
})
export class FormComponent implements OnInit {
  order = {
    itemIds: [] as number[],
    scheduleDate: '',
  };

  availableDishes: any[] = [];
  minDateTime: string;

  constructor(private apiService: ApiService, private router: Router) {
    this.minDateTime = new Date().toISOString().slice(0, 16);
  }

  ngOnInit() {
    this.loadDishes();
  }

  loadDishes() {
    this.apiService.getAllDishes().subscribe({
      next: (dishes) => (this.availableDishes = dishes),
      error: () => alert('Failed to load dishes'),
    });
  }

  onDishChange(dishId: number, event: Event) {
    const isChecked = (event.target as HTMLInputElement).checked;
    if (isChecked) {
      this.order.itemIds.push(dishId);
    } else {
      this.order.itemIds = this.order.itemIds.filter((id) => id !== dishId);
    }
  }

  onSubmit() {
    if (this.order.itemIds.length === 0) {
      alert('You must select at least one dish!');
      return;
    }

    const payload = {
      itemIds: this.order.itemIds,
      scheduleDate: this.order.scheduleDate || null,
    };

    if (payload.scheduleDate) {
      this.apiService.scheduleOrder(payload).subscribe({
        next: () => {
          this.router.navigate(['/orders']);
          alert('Order scheduled successfully');
        },
        error: (err) => {
          alert('Failed to schedule order');
        },
      });
    } else {
      this.apiService.createOrder(payload).subscribe({
        next: () => {
          this.router.navigate(['/orders']);
          alert('Order created successfully');
        },
        error: (err) => {
          alert('Failed to create order : ' + err.error);
        },
      });
    }
  }
}
