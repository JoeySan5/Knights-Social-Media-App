import 'package:flutter/material.dart';
import 'package:knights/components/poster_format.dart';
import 'package:knights/components/user_format.dart';

class PosterProfilePage extends StatelessWidget{
final String userId;
final String sessionKey;
// final String sessionKey = 'ocm8tRf3Kabd'; // hardcoded session key for Tommy
  const PosterProfilePage({super.key,required this.userId, required this.sessionKey});
  
  @override
  Widget build(BuildContext context) {
    return  Scaffold(
      body:Center(
        child: Column( 
          children: <Widget>[
            //UserList(userId, sessionKey), // used to get and display user information
            //UserFormat(mId: mId, mUsername: mUsername, mEmail: mEmail, mNote: mNote, GI: GI, SO: SO)
            PosterFormat(userId:userId, sessionKey: sessionKey),
                         ElevatedButton(
              style: const ButtonStyle(
                  backgroundColor: MaterialStatePropertyAll(Colors.green),
                ),
              onPressed: (){
                Navigator.pop(context);
              },
              child: const Text('Home Page')
              ),
          ],
        ),
      )
    );
  }
}

