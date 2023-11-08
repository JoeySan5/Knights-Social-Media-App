import 'package:flutter/material.dart';
import 'package:knights/components/user_format.dart';
import 'package:knights/components/user_list.dart';


///This class is the Message Page for the app.
///
///Features a centered column with an Ideas Form and a button to 
///navigate back home (pop current message_page)
class ProfilePage extends StatelessWidget{
//final BigInt userId = 112569610817039937158 as BigInt;
final String userId = '1234567890abcdef1234567890abcdef';
final String sessionKey = 'lGaJjDO8kdNq'; // hardcoded session key for Tommy
  const ProfilePage(userId,sessionKey,{super.key});

  @override
  Widget build(BuildContext context) {
    return  Scaffold(
      body:Center(
        child: Column( 
          children: <Widget>[
            //UserList(userId, sessionKey), // used to get and display user information
            //UserFormat(mId: mId, mUsername: mUsername, mEmail: mEmail, mNote: mNote, GI: GI, SO: SO)
            UserFormat(userId, sessionKey),
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


