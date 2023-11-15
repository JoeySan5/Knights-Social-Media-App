import { NgModule } from '@angular/core';
import { BrowserModule } from '@angular/platform-browser';

import { AppRoutingModule } from './app-routing.module';
import { Router, RouterModule, Routes } from '@angular/router';

import { AppComponent } from './app.component';
import { IdeaSubmission } from './idea-submission/idea-submission.component';
import { IdeaListComponent } from './idea-list/idea-list.component';
import { ProfilepageComponent } from './profilepage/profilepage.component';


const routes: Routes = [
  { path: 'idea-list', component: IdeaListComponent, title: "ideaList" },
  { path: 'idea-submission', component: IdeaSubmission },
  {  path: 'profile-page', component: ProfilepageComponent},
  
]; // sets up routes constant where you define your routes

// This file contains the modules of all custom classes that are being used. 
@NgModule({
  declarations: [
    AppComponent,
    IdeaSubmission,
    IdeaListComponent,
    ProfilepageComponent
  ],
  imports: [
    BrowserModule,
    AppRoutingModule,
    RouterModule.forRoot(routes)
  ],
  providers: [],
  bootstrap: [AppComponent]
})
export class AppModule { }
