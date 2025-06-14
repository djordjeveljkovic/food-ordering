import { Injectable } from '@angular/core';
import {HttpClient, HttpHeaders, HttpParams} from '@angular/common/http';
import { Observable } from 'rxjs';

@Injectable({
  providedIn: 'root'
})
export class ApiService {
  private apiUrl = 'http://localhost:8081';

  constructor(private http: HttpClient) {}

  login(email: string, password: string): Observable<any> {
    return this.http.post(`${this.apiUrl}/auth/login`, { email, password });
  }

  getUsers(): Observable<any> {
    return this.http.get(`${this.apiUrl}/users`);
  }

  addUser(user: any): Observable<any> {
    return this.http.post(`${this.apiUrl}/users`, user, { responseType: 'text' });
  }

  updateUser(id: number, user: any): Observable<string> {
    return this.http.put(`${this.apiUrl}/users/${id}`, user, { responseType: 'text' });
  }

  deleteUser(id: number): Observable<any> {
    return this.http.delete(`${this.apiUrl}/users/${id}`, { responseType: 'text' });
  }

  getPermissions(): Observable<string[]> {
    return this.http.get<string[]>(`${this.apiUrl}/permissions`);
  }

  getAllOrders(): Observable<any[]> {
    return this.http.get<any[]>(`${this.apiUrl}/orders`);
  }

  getOrdersPaginated(page: number, size: number): Observable<any> {
    return this.http.get<any>(`${this.apiUrl}/orders?page=${page}&size=${size}`);
  }

  createOrder(payload: { itemIds: number[] }) {
    return this.http.post(`${this.apiUrl}/orders`, payload);
  }

  getAllDishes(): Observable<any[]> {
    return this.http.get<any[]>(`${this.apiUrl}/dishes`);
  }

  cancelOrder(orderId: number): Observable<void> {
    return this.http.put<void>(`${this.apiUrl}/orders/cancel/${orderId}`, {});
  }

  searchOrders(params: any): Observable<any[]> {
    return this.http.get<any[]>(`${this.apiUrl}/orders/search`, { params });
  }

  trackOrder(orderId: number): Observable<{ status: string }> {
    return this.http.get<{ status: string }>(`${this.apiUrl}/orders/track/${orderId}`);
  }

  getErrorsByOrderId(orderId: number): Observable<any[]> {
    return this.http.get<any[]>(`${this.apiUrl}/errors/order/${orderId}`);
  }

  scheduleOrder(payload: { itemIds: number[]; scheduleDate: string | null }) {
    return this.http.post(`${this.apiUrl}/orders/schedule`, payload);
  }

  getErrors(page: number, size: number): Observable<any> {
    const params = new HttpParams()
      .set('page', page.toString())
      .set('size', size.toString());

    return this.http.get<any>(`${this.apiUrl}/errors/history`, { params });
  }
}
