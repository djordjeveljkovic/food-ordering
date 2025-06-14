import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule } from '@angular/router';
import { FormsModule } from '@angular/forms';
import { ErrorHistoryComponent } from './components/error-history/error-history.component';
import { ErrorHistoryRoutingModule } from './error-history-routing.module';

@NgModule({
  declarations: [ErrorHistoryComponent],
  imports: [
    CommonModule,
    RouterModule,
    FormsModule,
    ErrorHistoryRoutingModule,
  ],
})
export class ErrorHistoryModule {}
