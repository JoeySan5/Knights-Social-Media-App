import { Component } from '@angular/core';

const backendUrl = "https://team-knights.dokku.cse.lehigh.edu";


@Component({
  selector: 'login-page',
  templateUrl: './login-page.component.html',
  styleUrls: ['./login-page.component.css']
})
export class LoginPageComponent {

  signedIn = false;
  ngOnInit(){
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
  fetch(`${backendUrl}/login`, {
    method: 'POST',
    headers: {
      'Content-Type': 'application/json; charset=UTF-8',
      'Access-Control-Allow-Origin': '{backendUrl}/login'
    },
    body: JSON.stringify({credential: response.credential
                          })
  })
  .then(response => response.json())
  .then(data => {
    console.log(data);
    //data is available once response has been parsed into JSON
    // Check if the backend response is successful, then redirect to main page
    if (data.mStatus === "ok") {
      console.log(data.mMessage);
      this.signedIn=true;
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
