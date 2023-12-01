import { NgModule } from '@angular/core';
import { BrowserModule } from '@angular/platform-browser';
import { Base64DecodePipe } from './pipes/base64Decode.pipe';
import { AppRoutingModule } from './app-routing.module';
import { Router, RouterModule, Routes } from '@angular/router';

import { AppComponent } from './app.component';
import { IdeaSubmission } from './idea-submission/idea-submission.component';
import { IdeaListComponent } from './idea-list/idea-list.component';
import { ProfilepageComponent } from './profilepage/profilepage.component';
import { LoginPageComponent } from './login-page/login-page.component';
import { HomePageComponent } from './home-page/home-page.component';
import { DetailedPostComponent } from './detailed-post/detailed-post.component';
import { DetailedPostInfoService } from './detailed-post-info.service';
import { AddCommentPageComponent } from './add-comment-page/add-comment-page.component';
import { OtherProfileComponent } from './other-profile/other-profile.component';


//Here we write all possible routes to components. This is useful when you want to 
//load a component to the user with router
const routes: Routes = [
  { path: 'idea-list', component: IdeaListComponent, title: "ideaList" },
  { path: 'idea-submission', component: IdeaSubmission },
  {  path: 'profile-page', component: ProfilepageComponent},
  {  path: '', component: LoginPageComponent},
  {  path: 'home-page', component: HomePageComponent},
  {  path: 'detailed-post', component: DetailedPostComponent},
  {  path: 'add-comment-page', component: AddCommentPageComponent},
  {  path: 'other-profile', component: OtherProfileComponent},

  
]; // sets up routes constant where you define your routes

// This file contains the modules of all custom classes that are being used. 
@NgModule({
  declarations: [
    AppComponent,
    IdeaSubmission,
    IdeaListComponent,
    ProfilepageComponent,
    LoginPageComponent,
    HomePageComponent,
    DetailedPostComponent,
    AddCommentPageComponent,
    OtherProfileComponent,
    Base64DecodePipe
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
