import { Component } from '@angular/core';

const backendUrl = "https://team-knights.dokku.cse.lehigh.edu";
const sessionKey = "k0kyOGwPlod5";
const userId= "107106171889739877350";
@Component({
  selector: 'profile-page',
  templateUrl: './profilepage.component.html',
  styleUrls: ['./profilepage.component.css']
})

export class ProfilepageComponent {

  getUsers(){
  // Issue an AJAX GET and then pass the result to update(). 
  const doAjax = async () => {
    await fetch(`${backendUrl}/users/${userId}?sessionKey=${sessionKey}`, {
        method: 'GET',
        headers: {
            'Content-type': 'application/json; charset=UTF-8'
        }
    }).then((response) => {
        // If we get an "ok" idea, clear the form
        if (response.ok) {
            console.log("recieved response from server for get");
            return Promise.resolve(response.json());

        }
        // Otherwise, handle server errors with a detailed popup idea
        else {
            window.alert(`The server replied not ok: ${response.status}\n` + response.statusText);
        }
        return Promise.reject(response);
    }).then((data) => {
        console.log('here is data:', data);
    }).catch((error) => {
        console.warn('Something went wrong with GET.', error);
        console.log("Unspecified error with refresh()");
    });
  }
   // make the AJAX post and output value or error message to console
   doAjax().then(console.log).catch(console.log);
}
  // Future<User> fetchUsers(String userId, String sessionKey) async{
  //   developer.log('Making web request for user data...');
  //   var url = Uri.parse('https://team-knights.dokku.cse.lehigh.edu/users/$userId?sessionKey=$sessionKey');
  //   var headers = {"Accept":"application/json"};
  //   // garbage user that gets returned if sopmething goes wrong
  //   User garb = User(mId: "", mUsername: 'garbage', mEmail: 'nonExistent', mNote: 'garbage');
  //   var response = await http.get(url, headers: headers);
  //   if(response.statusCode == 200){
  //     final User returnData;
  //     var res = jsonDecode(response.body);
  //     developer.log('json decode: $res');
  //     developer.log('resmdata: ${res['mData']}');
  //     if(res['mData'] is Map){
  //       returnData = User.fromJson(res['mData'] as Map<String,dynamic>);
  //     } else {
  //       developer.log('ERROR: Unexpected json response type (was not user). Using garb');
  //       returnData = garb;
  //     }
  //         developer.log('$returnData');
  //         return returnData;
  //   } else{
  //     throw Exception('Did not receive success status code from request.');
  //   }
  // }
}
