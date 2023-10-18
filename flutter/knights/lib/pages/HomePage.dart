import 'package:flutter/material.dart';

import 'package:knights/components/IdeaList.dart';
import 'package:knights/pages/MessagePage.dart';
class MyHomePage extends StatelessWidget{
  const MyHomePage({super.key});

    @override
    Widget build(BuildContext context) {
      
      return   Scaffold(
        
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
                padding: EdgeInsets.only(top: 35.0),
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