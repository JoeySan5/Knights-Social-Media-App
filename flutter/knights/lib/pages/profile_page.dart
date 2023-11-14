import 'package:flutter/material.dart';
import 'package:knights/components/user_format.dart';



///This class is the Message Page for the app.
///
///Features a centered column with an Ideas Form and a button to 
///navigate back home (pop current message_page)
class ProfilePage extends StatelessWidget{
final String sessionKey;
// final String sessionKey = 'ocm8tRf3Kabd'; // hardcoded session key for Tommy
  const ProfilePage({super.key, required this.sessionKey});
  
  @override
  Widget build(BuildContext context) {
    return  Scaffold(
      body:Center(
        child: Column( 
          children: <Widget>[
            //UserList(userId, sessionKey), // used to get and display user information
            //UserFormat(mId: mId, mUsername: mUsername, mEmail: mEmail, mNote: mNote, GI: GI, SO: SO)
            UserFormat(sessionKey: sessionKey),
            // ElevatedButton(
            //   style: const ButtonStyle(
            //       backgroundColor: MaterialStatePropertyAll(Colors.green),
            //     ),
            //   onPressed: (){
            //     Navigator.pop(context);
            //   },
            //   child: const Text('Edit Profile')
            //   ),
            //              ElevatedButton(
            //   style: const ButtonStyle(
            //       backgroundColor: MaterialStatePropertyAll(Colors.green),
            //     ),
            //   onPressed: (){
            //     Navigator.pop(context);
            //   },
            //   child: const Text('Home Page')
            //   ),
          ],
        ),
      )
    );
  }
}


