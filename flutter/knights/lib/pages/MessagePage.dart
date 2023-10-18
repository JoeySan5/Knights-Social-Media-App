import 'package:flutter/material.dart';

import 'package:knights/components/IdeasForm.dart';


class MessagePage extends StatelessWidget{
  const MessagePage({super.key});

  @override
  Widget build(BuildContext context) {
    return  Scaffold(
      body:Center(
        child: Column(
          children: <Widget>[
            IdeasForm(),
            
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
