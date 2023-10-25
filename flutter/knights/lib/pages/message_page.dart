import 'package:flutter/material.dart';

import 'package:knights/components/ideas_form.dart';

///This class is the Message Page for the app.
///
///Features a centered column with an Ideas Form and a button to 
///navigate back home (pop current message_page)
class MessagePage extends StatelessWidget{
  const MessagePage({super.key});

  @override
  Widget build(BuildContext context) {
    return  Scaffold(
      body:Center(
        child: Column(
          children: <Widget>[
            const IdeasForm(),
            
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
