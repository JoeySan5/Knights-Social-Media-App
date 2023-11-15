import 'package:flutter/material.dart';

import 'package:knights/components/ideas_form.dart';

///This class is the Message Page for the app.
///
///Features a centered column with an Ideas Form and a button to 
///navigate back home (pop current message_page)
class MessagePage extends StatelessWidget{
  final String sessionKey;
  const MessagePage({super.key, required this.sessionKey});

  @override
  Widget build(BuildContext context) {
    return  Scaffold(
      body:Center(
        child: Column(
          children: <Widget>[
            /// component that allows userto input new idea  and submit
            IdeasForm(sessionKey: sessionKey),
            /// button takes you back home
            ElevatedButton(
              style: const ButtonStyle(
                  backgroundColor: MaterialStatePropertyAll(Colors.green),
                ),
              onPressed: (){
                Navigator.pop(context);
              },
              child: const Text('Go Back Home')
              ),
          ],
        ),
      )
    );
  }
}
