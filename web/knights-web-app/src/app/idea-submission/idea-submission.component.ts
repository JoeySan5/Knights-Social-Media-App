import { Component } from '@angular/core';
import { IdeaListComponent } from '../idea-list/idea-list.component';
import { Router } from '@angular/router';

var newEntryForm: IdeaSubmission;
var mainList: IdeaListComponent;

const backendUrl = "https://team-knights.dokku.cse.lehigh.edu";
// const backendUrl = "http://localhost:8998";
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
    constructor(private router: Router) {
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

    // Declaring a variable to hold the selected file, it can be of type File or undefined
    selectedFile: File | undefined;

    // Declaring a variable to store the file data as a string, it can also be undefined
    fileData: string | undefined;

    /**
     * This method is triggered when a file is selected in the input field.
     * It processes the selected file and converts it to a Base64 encoded string.
     * @param event - The event triggered on file selection, containing the file data.
     */
    onFileSelected(event: any): void {
        // Extracting the first file from the file input event
        const file: File = event.target.files[0];
        if (file) {
            // Storing the selected file in the component's state
            this.selectedFile = file;

            // Creating a FileReader to read the file content
            const reader = new FileReader();

            // Reading the file as a data URL (which will result in a Base64 encoded string)
            reader.readAsDataURL(file);

            // This function is called once the FileReader finishes reading the file
            reader.onload = () => {
                const mBase64 = reader.result as string;
                // Creating an object with the file's name, type, and the Base64 encoded content
                const fileData = {
                    mFileName: file.name,
                    mFileType: file.type,
                    mBase64: mBase64.split(',')[1] // Splitting the result to get only the Base64 part
                };
                // Converting the file data object to a JSON string for further processing
                this.fileData = JSON.stringify(fileData, null, 2);
                console.log(this.fileData); // Logging the JSON string to the console
            };

            // Error handling for the FileReader
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
            window.alert("Error: you must enter an idea");
            return;
        }
        // get the values of the link field, force them to be strings, and check
        // But if the link is empty, set it to null
        let link: string | null = (<HTMLInputElement>document.getElementById("newLink")).value;
        if (link === "") {
            link = null;
        }

        // Defining an interface for the structure of file data to be sent to the server.
        interface ServerFileData {
            mFileType: string;  // Type of the file, e.g., 'image/jpeg'
            mBase64: string;     // Base64 encoded string of the file content
            mFileName: string;  // Name of the file
        }

        // Declaring a variable to store the file data for the server.
        // Initially, it's set to null to handle cases where no file is selected.
        let serverFileData: ServerFileData | null = null;
        
        // Check if there is any file data present (this.fileData is not undefined)
        if (this.fileData) {
            // Parsing the JSON string to an object to easily access its properties
            const fileDataObject = JSON.parse(this.fileData);

            // Assigning the parsed data to the serverFileData with the correct structure
            serverFileData = {
                mFileType: fileDataObject.mFileType, // Assigning file type
                mBase64: fileDataObject.mBase64,  // Assigning base64 encoded content
                mFileName: fileDataObject.mFileName  // Assigning file name
            };
        } 
        // else{
        //     serverFileData = {
        //         mFileType: "nullType", // Assigning file type
        //         mBase64: "nullBase64",  // Assigning base64 encoded content
        //         mFileName: "nullName"  // Assigning file name
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
                    mLink: link,
                    mFile: serverFileData
                }),
                headers: {
                    'Content-type': 'application/json; charset=UTF-8'
                }
            }).then((response) => {
                console.log('this is response: ', response); 
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