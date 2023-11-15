import { Component, OnInit } from '@angular/core';
import { DetailedPostInfoService } from '../detailed-post-info.service';
import { Router } from '@angular/router';


const backendUrl = "https://team-knights.dokku.cse.lehigh.edu";
const sessionKey = localStorage.getItem('sessionKey');

@Component({
  selector: 'add-comment-page',
  templateUrl: './add-comment-page.component.html',
  styleUrls: ['./add-comment-page.component.css']
})
export class AddCommentPageComponent implements OnInit{

  data: any;

  constructor(private detailedPostInfoService: DetailedPostInfoService, private router: Router){
  }

  ngOnInit(): void {
      this.getData();
  }

  getData(): any{
    this.data = this.detailedPostInfoService.getData();
    console.log("here is data in comment submission component:", this.data);
  }

  // /ideas/:id/comments POST '{"mContent": "Hello This is comment written by Sehyoun", "sessionKey": "String", "mIdeaId": 10}'
  onSubmit(){
    // get the values of the idea field, force them to be strings, and check 
      // that neither is empty
      let comment = "" + (<HTMLInputElement>document.getElementById("newComment")).value;
      if (comment === "") {
          window.alert("Error: comment is not valid");
          return;
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
                  mIdeaId: this.data.mId
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
            this.router.navigate(["/home-page"])

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
