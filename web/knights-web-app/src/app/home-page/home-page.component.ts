import { Component, OnInit } from '@angular/core';
import { IdeaListComponent } from '../idea-list/idea-list.component';
import { IdeaSubmission } from '../idea-submission/idea-submission.component';

var mainList: IdeaListComponent;
var newEntryForm: IdeaSubmission;

@Component({
  selector: 'home-page',
  templateUrl: './home-page.component.html',
  styleUrls: ['./home-page.component.css']
})

export class HomePageComponent {
  onProfile = false;

  profileClick(){
    this.onProfile = !this.onProfile;

  }
}
