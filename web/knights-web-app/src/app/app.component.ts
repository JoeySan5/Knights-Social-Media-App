import { Component, OnInit } from '@angular/core';
import { IdeaListComponent } from './idea-list/idea-list.component';
import { IdeaSubmission } from './idea-submission/idea-submission.component';

var mainList: IdeaListComponent;
var newEntryForm: IdeaSubmission;




@Component({
  selector: 'app-root',
  templateUrl: './app.component.html',
  styleUrls: ['./app.component.css']
})

export class AppComponent implements OnInit{
  onProfile = false;

  profileClick(){
    this.onProfile = !this.onProfile;

  }
  constructor(){
    // // display idea lists, block add element
    // (<HTMLElement>document.getElementById("addElement")).style.display = "none";
    // (<HTMLElement>document.getElementById("ideaList")).style.display = "block";
    // // having add feature show when clicked, block all ideas list
    // document.getElementById("addButtonFooter")?.addEventListener("click", (e) => {
    //     (<HTMLElement>document.getElementById("addElement")).style.display = "block";
    //     (<HTMLElement>document.getElementById("ideaList")).style.display = "none";
    // });
    // // Create the object that controls the "New Entry" form
    // newEntryForm = new IdeaSubmission();
    // // Create the object for the main data list, and populate it with data from the server
    // mainList = new IdeaListComponent();
    // // Create the object that controls the "Edit Entry" form
    // mainList.refresh();
  }

  //angular's lifecycle hook ngOnInit
  ngOnInit(){
      /**
 * configurations for when the page loads
 * initially shows idea list and does not make add element visible
 * controls when add post6 is clicked reverses intialy configuiration
 * 
 */
  //     const addElement = document.getElementById("addElement");
  //     const ideaList = document.getElementById("ideaList");
  //     const addButtonFooter = document.getElementById("addButtonFooter");
  // // display idea lists, block add element
  // (<HTMLElement>document.getElementById("addElement")).style.display = "none";
  // (<HTMLElement>document.getElementById("ideaList")).style.display = "block";
  // // having add feature show when clicked, block all ideas list
  // document.getElementById("addButtonFooter")?.addEventListener("click", (e) => {
  //     (<HTMLElement>document.getElementById("addElement")).style.display = "block";
  //     (<HTMLElement>document.getElementById("ideaList")).style.display = "none";
  // });
  // // Create the object that controls the "New Entry" form
  // newEntryForm = new IdeaSubmission();
  // Create the object for the main data list, and populate it with data from the server
  // mainList = new IdeaListComponent();
  // // Create the object that controls the "Edit Entry" form
  // mainList.refresh();

  }
  
}
