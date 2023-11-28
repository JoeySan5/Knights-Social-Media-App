import { Component } from '@angular/core';
import { IdeaListComponent } from '../idea-list/idea-list.component';
import { Router } from '@angular/router';

var newEntryForm: IdeaSubmission;
var mainList: IdeaListComponent;
const backendUrl = "https://team-knights.dokku.cse.lehigh.edu";
const sessionKey = localStorage.getItem('sessionKey');


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
    constructor(private router: Router)
    {
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
    
    selectedFile: File | undefined;
    fileData: string | undefined;
  
    onFileSelected(event: any): void {
      const file: File = event.target.files[0];
      if (file) {
        this.selectedFile = file;
        const reader = new FileReader();
        reader.readAsDataURL(file);
        reader.onload = () => {
          const base64 = reader.result as string;
          const fileData = {
            fileName: file.name,
            fileType: file.type,
            base64File: base64.split(',')[1]
          };
          this.fileData = JSON.stringify(fileData, null, 2);
          console.log(this.fileData); // console json
        };
        reader.onerror = (error) => {
          console.error('Error reading file:', error);
        };
      }
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
        let link: string | null = (<HTMLInputElement>document.getElementById("newLink")).value;
        if (link === "") {
            link = null;
        }
        // interface FileData {
        //     mfileType: string | null;
        //     base64: string | null;
        //     mfileName: string | null;
        // }
        // let file: FileData | null = null;
              
        // let base64 = (<HTMLInputElement>document.getElementById("newBase64")).value || null;

        // if (base64) { // if base64 is null, then file is null
        //     let fileType = (<HTMLInputElement>document.getElementById("newFileType")).value || null;
        //     let fileName = (<HTMLInputElement>document.getElementById("newFileName")).value || null;
        
        //     file = {
        //         mfileType: fileType,
        //         base64: base64,
        //         mfileName: fileName
        //     };
        // }

        console.log(idea);

        // set up an AJAX POST. 
        // When the server replies, the result will go to onSubmitResponse
        const doAjax = async () => {
            await fetch(`${backendUrl}/ideas`, {
                method: 'POST',
                body: JSON.stringify({
                    mContent: idea,
                    sessionKey: sessionKey,
                    // link: link,
                    // file: file
              }),
                headers: {
                    'Content-type': 'application/json; charset=UTF-8'
                }
            }).then((response) => {
                // If we get an "ok" message, return the json
                if (response.ok) {
                    this.router.navigate(['/home-page']);
                    return Promise.resolve(response.json());
                    
                }
                // Otherwise, handle server errors with a detailed popup message
                else {
                    window.alert(`The server replied not ok: ${response.status}\n` + response.statusText);
                }
                return Promise.reject(response);
            }).then((data) => {
                console.log('this is data: ', data);

                //newEntryForm.onSubmitResponse(data);
            }).catch((error) => {
                console.warn('Something went wrong with POST.', error);
                window.alert("Unspecified error, in fetch for submitForm, in NewEntryForm");
            });
        }

        // make the AJAX post and output value or error message to console
        doAjax().then(console.log).catch(console.log);
    }


}
