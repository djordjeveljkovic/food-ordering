import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';

import { UsersRoutingModule } from './users-routing.module';
import { ListComponent } from './list/list.component';
import { AddComponent } from './add/add.component';
import { EditComponent } from './edit/edit.component';
import {FormsModule} from "@angular/forms";


@NgModule({
  declarations: [
    ListComponent,
    AddComponent,
    EditComponent
  ],
    imports: [
        CommonModule,
        UsersRoutingModule,
        FormsModule
    ]
})
export class UsersModule { }
