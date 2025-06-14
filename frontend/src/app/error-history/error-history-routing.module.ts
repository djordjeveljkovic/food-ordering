import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { ErrorHistoryComponent } from './components/error-history/error-history.component';

const routes: Routes = [
  { path: '', component: ErrorHistoryComponent },
];

@NgModule({
  imports: [RouterModule.forChild(routes)],
  exports: [RouterModule],
})
export class ErrorHistoryRoutingModule {}
