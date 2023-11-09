import 'package:flutter/material.dart';

import 'package:knights/components/idea_list.dart';
import 'package:knights/pages/message_page.dart';
import 'package:knights/pages/profile_page.dart';

//import 'package:bignum/bignum.dart';

// global variable to test out profile page features
              //112569610817039937158
// BigInt userId = 3431212323615612355 as BigInt;

// const String userId = '101136375578726959533';
// const String sessionKey = 'ocm8tRf3Kabd';
///This class is the Home Page for the app.
///
///Features a centered column with a title, sub title, idea list,
///and a button that navigates you to message_page (push message_page onto stack).
class MyHomePage extends StatelessWidget{
  final userId;
  final sessionKey;
  MyHomePage({super.key, required this.userId, this.sessionKey});
    
    @override
    Widget build(BuildContext context) {
      print('home page sessionKey: $userId');  
      return   Scaffold(
        // app bar for the profile icon that takes you to profile
        appBar: AppBar(
          title: Text('Profile'),
          actions: <Widget>[
            IconButton(
              icon: Icon(Icons.account_circle),
              // when pressed will go to profile page
              // currently hardcoded
              // TODO: get rid of hard coded access token
              onPressed: (){
                Navigator.push(
                    context, 
                    MaterialPageRoute(
                      builder: (context) => ProfilePage(userId: userId, sessionKey: sessionKey)
                      )
                  );
              },
            )
          ]
        ),
        body:Center(
          
          child: SingleChildScrollView(
            child: Column(
            
          
            children: <Widget>[
              const Padding(padding: EdgeInsets.only(top: 40.0),
              child: Text(
                'Knights',
                
                style: TextStyle(
                
                  fontSize: 40,
                  color: Color.fromARGB(221, 255, 255, 255),
                  fontFamily: 'roboto'
                ),
              )
              ),

              const Padding( padding: EdgeInsets.only(top: 30.0, bottom: 30.0),
              child: Text('Here\'s what people have been saying:',
              textDirection: TextDirection.ltr,
                style: TextStyle(
                  fontSize: 20,
                  color: Colors.white,
                  fontFamily: 'roboto',
                
                ),
                ),
              ),
              const SizedBox(
                height: 400,
                child: IdeaList(),
              ),
              Padding(
                padding: const EdgeInsets.only(top: 35.0),
                child: ElevatedButton(
                style: const ButtonStyle(
                  backgroundColor: MaterialStatePropertyAll(Colors.green),
                ),
                onPressed: () {
                  Navigator.push(
                    context, 
                    MaterialPageRoute(
                      builder: (context) => const MessagePage()
                      )
                  );
                }, 
                child: const Text('Say your Piece')
                )
              )
            ],
          )
        )
        )
      );
    }
  }