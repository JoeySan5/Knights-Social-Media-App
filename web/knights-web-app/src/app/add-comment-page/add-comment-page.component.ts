import { Component, OnInit } from '@angular/core';
import { DetailedPostInfoService } from '../detailed-post-info.service';
import { Router } from '@angular/router';


const backendUrl = "https://team-knights.dokku.cse.lehigh.edu";
// const backendUrl = "http://localhost:8998";

const sessionKey = localStorage.getItem('sessionKey');

@Component({
  selector: 'add-comment-page',
  templateUrl: './add-comment-page.component.html',
  styleUrls: ['./add-comment-page.component.css']
})
export class AddCommentPageComponent implements OnInit {

  data: any;

  constructor(private detailedPostInfoService: DetailedPostInfoService, private router: Router) {
  }

  ngOnInit(): void {
    this.getData();
  }

  getData(): any {
    this.data = this.detailedPostInfoService.getData();
    console.log("here is data in comment submission component:", this.data);
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

  // /ideas/:id/comments POST '{"mContent": "Hello This is comment written by Sehyoun", "sessionKey": "String", "mIdeaId": 10}'
  onSubmit() {
    // get the values of the idea field, force them to be strings, and check 
    // that neither is empty
    let comment = "" + (<HTMLInputElement>document.getElementById("newComment")).value;
    if (comment === "") {
      window.alert("Error: comment is not valid");
      return;
    }

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
    console.log(comment);

    // set up an AJAX POST. 
    // When the server replies, the result will go to onSubmitResponse
    const doAjax = async () => {
      await fetch(`${backendUrl}/comments`, {
        method: 'POST',
        body: JSON.stringify({
          mContent: comment,
          sessionKey: sessionKey,
          mIdeaId: this.data.mId,
          mLink: link,
          mFile: serverFileData
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
        this.router.navigate(['home-page/']);

        // newEntryForm.onSubmitResponse(data);
      }).catch((error) => {
        console.warn('Something went wrong with POST.', error);
        window.alert("Unspecified error, in fetch for onSubmit for comments");
      });
    }

    // make the AJAX post and output value or error message to console
    doAjax().then(console.log).catch(console.log);
  }
}
