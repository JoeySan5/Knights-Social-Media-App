import { Component, AfterViewInit } from '@angular/core';
import { RouterLink } from '@angular/router';

// const backendUrl = "https://team-knights.dokku.cse.lehigh.edu";
const backendUrl = "http://localhost:8998";

// Async/Await makes it easier to write promises. The keyword 'async' before a
// function makes the function return a promise, always. And 
//the keyword await is used inside async functions, which makes the 
//program wait until the Promise resolves.


@Component({
  selector: 'login-page',
  templateUrl: './login-page.component.html',
  styleUrls: ['./login-page.component.css']
})

export class LoginPageComponent implements AfterViewInit{

  signedIn = false;
  ngAfterViewInit(){
    // @ts-ignore
    google.accounts.id.initialize({
    client_id: "1019349198762-463i1tt2naq9ipll3f9ade5u7nli7gju.apps.googleusercontent.com",
    callback: this.handleCredentialResponse.bind(this),
    auto_select: false,
    cancel_on_tap_outside: true,

  });
  // @ts-ignore
  google.accounts.id.renderButton(
  // @ts-ignore
  document.getElementById("google-button"),
    { theme: "outline", size: "large", width: "100%" }
  );
  }
  // This function is called when sign in is tapped, CredentialResponse is passed in parameters
 async handleCredentialResponse(response: { credential: string; }) {
  // Log the JWT ID token to the console
  console.log("Encoded JWT ID token: " + response.credential);
  
  // Send the ID token to your backend for verification
  // Here we use the Fetch API to post to our backend
  await fetch(`${backendUrl}/login`, {
    method: 'POST',
    headers: {
      'Content-Type': 'application/json; charset=UTF-8',
      'Access-Control-Allow-Origin': '{backendUrl}/login'
    },
    body: JSON.stringify({credential: response.credential})
  })
  .then(response => {
    //console.log(response);
    return response.json()})
  .then(data => {
    console.log(data);
    //data is available once response has been parsed into JSON
    // Check if the backend response is successful, then redirect to main page
    if (data.mStatus === "ok") {
      console.log(data.mMessage);
      this.signedIn=true;
//       You can use either Session storage or Local storage to store the data temporarily.
// Session storage will be available for specific tab where as we can use Local storage through out the browser
      localStorage.setItem('sessionKey', data.mData);
      //sessionkey += data.m
      
      window.location.href = "http://localhost:4200/home-page"; // Redirect to the main page

    } else {
      // Handle login failure
      console.error("Login failed: " + data.message);
    }
  })
  .catch(error => {
    // Handle errors during the login process
    console.error('Error during login:', error);
  });
}
}
