import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { ListComponent } from './list/list.component';
import { AddComponent } from './add/add.component';
import { EditComponent } from './edit/edit.component';
import { AuthGuard } from '../auth/auth.guard';

const routes: Routes = [
  {
    path: '',
    component: ListComponent,
    canActivate: [AuthGuard],
    data: { permissions: ['CAN_READ_USERS'] }
  },
  {
    path: 'add',
    component: AddComponent,
    canActivate: [AuthGuard],
    data: { permissions: ['CAN_CREATE_USERS'] }
  },
  {
    path: 'edit/:id',
    component: EditComponent,
    canActivate: [AuthGuard],
    data: { permissions: ['CAN_UPDATE_USERS'] }
  },
];

@NgModule({
  imports: [RouterModule.forChild(routes)],
  exports: [RouterModule]
})
export class UsersRoutingModule {}
