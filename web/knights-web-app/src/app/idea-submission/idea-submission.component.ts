import { Component } from '@angular/core';
import { IdeaListComponent } from '../idea-list/idea-list.component';

var newEntryForm: IdeaSubmission;
var mainList: IdeaListComponent;
const backendUrl = "https://team-knights.dokku.cse.lehigh.edu";
const sehyounID = "101136375578726959533";

@Component({
    selector: 'idea-submission',
    templateUrl: './idea-submission.component.html',
    styleUrls: ['./idea-submission.component.css']
  })

  export class IdeaSubmission {
    /**
     * To initialize the object, we say what method of NewEntryForm should be
     * run in response to each of the form's buttons being clicked.
     */
    constructor() {
      // event listeners for adding a post and canceling
    //   document.getElementById("addCancel")?.addEventListener("click", (e) => { newEntryForm.clearForm(); });
    //   document.getElementById("addButton")?.addEventListener("click", (e) => { newEntryForm.submitForm(); });

  }

  /**
   * Clear the form's input fields
   */
  clearForm() {
      (<HTMLInputElement>document.getElementById("newIdea")).value = "";

      // reset the UI
      (<HTMLElement>document.getElementById("addElement")).style.display = "none";
      (<HTMLElement>document.getElementById("ideaList")).style.display = "block";
  }

  /**
   * Check if the input fields are both valid, and if so, do an AJAX call.
   */
  submitForm() {
      // get the values of the idea field, force them to be strings, and check 
      // that neither is empty
      let idea = "" + (<HTMLInputElement>document.getElementById("newIdea")).value;
      if (idea === "") {
          window.alert("Error: idea is not valid");
          return;
      }

      console.log(idea);

      // set up an AJAX POST. 
      // When the server replies, the result will go to onSubmitResponse
      const doAjax = async () => {
          await fetch(`${backendUrl}/ideas`, {
              method: 'POST',
              body: JSON.stringify({
                  mContent: idea,
                  mUserId: sehyounID
              }),
              headers: {
                  'Content-type': 'application/json; charset=UTF-8'
              }
          }).then((response) => {
              // If we get an "ok" message, return the json
              if (response.ok) {
                  return Promise.resolve(response.json());
              }
              // Otherwise, handle server errors with a detailed popup message
              else {
                  window.alert(`The server replied not ok: ${response.status}\n` + response.statusText);
              }
              return Promise.reject(response);
          }).then((data) => {
            console.log('this is data: ', data);

              newEntryForm.onSubmitResponse(data);
          }).catch((error) => {
              console.warn('Something went wrong with POST.', error);
              window.alert("Unspecified error, in fetch for submitForm, in NewEntryForm");
          });
      }

      // make the AJAX post and output value or error message to console
      doAjax().then(console.log).catch(console.log);
  }

  /**
   * onSubmitResponse runs when the AJAX call in submitForm() returns a 
   * result.
   * 
   * @param data The object returned by the server
   */
  private onSubmitResponse(data: any) {
      // If we get an "ok" message, clear the form
      if (data.mStatus === "ok") {
          //window.alert("onSubmitResponse = ok" + data);
          newEntryForm.clearForm();
          mainList.refresh();
      }
      // Handle explicit errors with a detailed popup message
      else if (data.mStatus === "error") {
          window.alert("The server replied with an error:\n" + data.ideas);
      }
      // Handle other errors with a less-detailed popup message
      else {
          window.alert("Unspecified error returned, in onSubmitResponse, in NewEntryForm");
      }
  }
  }
  