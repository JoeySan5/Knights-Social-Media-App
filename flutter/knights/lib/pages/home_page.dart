import 'package:flutter/material.dart';

import 'package:knights/components/idea_list.dart';
import 'package:knights/pages/message_page.dart';
import 'package:knights/pages/profile_page.dart';

///This class is the Home Page for the app.
///
///Features a centered column with a title, sub title, idea list,
///and a button that navigates you to message_page (push message_page onto stack).
class MyHomePage extends StatelessWidget{
  final userId;
  final sessionKey;
  /// parameters are userId and sessionKey to be used for later pages
  MyHomePage({super.key, required this.userId, this.sessionKey});
    
    @override
    Widget build(BuildContext context) {  
      return   Scaffold(
        /// app bar for the profile icon that takes you to profile when clicked
        appBar: AppBar(
          title: Text(''),
          actions: <Widget>[
            IconButton(
              /// profile 
              icon: Icon(Icons.account_circle),
              /// goes to profile page when profile icon is tapped
              /// passes through 
              onPressed: (){
                Navigator.push(
                    context, 
                    MaterialPageRoute(
                      /// takes user to profile page and passed the sessionKey 
                      builder: (context) => ProfilePage(sessionKey: sessionKey)
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
              SizedBox(
                height: 400,
                /// IdeaList displays all ideas in neat list format and uses sessionKey
                child: IdeaList(sessionKey: sessionKey),
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
                      /// Allows user to post a message, uses sessionKey
                      builder: (context) => MessagePage(sessionKey: sessionKey)
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