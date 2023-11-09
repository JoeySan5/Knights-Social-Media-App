import 'package:flutter/material.dart';
import 'package:knights/components/detailed_post_format.dart';



class DetailedPostPage extends StatelessWidget{
  final String userId = '1234567890abcdef1234567890abcdef';
  final String sessionKey = 'lGaJjDO8kdNq'; // hardcoded session key for Tommy  
  int mId = 0;

  DetailedPostPage(mId, {super.key});

  @override
  Widget build(BuildContext context) {
    return  Scaffold(
      body:Center(
        child: Column( 
          children: <Widget>[
            //UserList(userId, sessionKey), // used to get and display user information
            //UserFormat(mId: mId, mUsername: mUsername, mEmail: mEmail, mNote: mNote, GI: GI, SO: SO)
            DetailedPostFormat(mId),
            ElevatedButton(
              style: const ButtonStyle(
                  backgroundColor: MaterialStatePropertyAll(Colors.green),
                ),
              onPressed: (){
                Navigator.pop(context);
              },
              child: const Text('Edit Profile.')
              ),
             
          ],
        ),
      )
    );
  }

}