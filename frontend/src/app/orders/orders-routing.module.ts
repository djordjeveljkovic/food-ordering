import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { ListComponent } from './pages/list/list.component';
import { FormComponent } from './pages/form/form.component';
import {AuthGuard} from "../auth/auth.guard";

const routes: Routes = [
  { path: '',
    component: ListComponent,
    canActivate: [AuthGuard],
    data: { permissions: ['CAN_SEARCH_ORDER'] },
  },
  {
    path: 'create',
    component: FormComponent,
    canActivate: [AuthGuard],
    data: {permissions: ['CAN_PLACE_ORDER']},
  },
];

@NgModule({
  imports: [RouterModule.forChild(routes)],
  exports: [RouterModule],
})
export class OrdersRoutingModule {}
