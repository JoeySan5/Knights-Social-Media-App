import 'package:flutter/material.dart';
import 'package:knights/components/user_format.dart';



///This class is the current user'sProfile Page for the app.
///
///Features a centered column that calls the UserFormat component
/// which populates and displays the user's data 
///navigate back home (pop current message_page)
class ProfilePage extends StatelessWidget{
final String sessionKey;
  const ProfilePage({super.key, required this.sessionKey});
  
  @override
  Widget build(BuildContext context) {
    return  Scaffold(
      body:Center(
        child: Column( 
          children: <Widget>[
            /// component to display current user's data
            UserFormat(sessionKey: sessionKey),
          ],
        ),
      )
    );
  }
}


