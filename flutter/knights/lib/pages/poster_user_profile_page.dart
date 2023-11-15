import 'package:flutter/material.dart';
import 'package:knights/components/poster_format.dart';



///Profilepage for other user 
///seen when username is clicked on post
///displays similar information to current user's profile page except that 
///the GI and SO are not displayed
///Calls the PosterFormat component to display info
class PosterProfilePage extends StatelessWidget{
final String userId;
final String sessionKey;
  const PosterProfilePage({super.key,required this.userId, required this.sessionKey});
  
  @override
  Widget build(BuildContext context) {
    return  Scaffold(
      body:Center(
        child: Column( 
          children: <Widget>[
            /// component displays poster's information
            PosterFormat(userId:userId, sessionKey: sessionKey),
          ],
        ),
      )
    );
  }
}

