import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { IdeaSubmission } from './idea-submission/idea-submission.component';
import { IdeaListComponent } from './idea-list/idea-list.component';

const routes: Routes = [];

@NgModule({
  imports: [RouterModule.forRoot(routes)],
  exports: [RouterModule]
})
export class AppRoutingModule { }
