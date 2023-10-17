import 'package:flutter/material.dart';
import 'dart:developer' as developer;
import 'package:flutter/services.dart';
import 'dart:io';

import 'package:knights/models/Idea.dart';
import 'package:knights/components/IdeaFormat.dart';
import 'package:knights/net/webRequests.dart';
import 'package:knights/components/IdeaList.dart';


//everytime like button is cliked it should be a new request, that either 
//increments or decrements like counter
void main() async {
  WidgetsFlutterBinding.ensureInitialized();

  ByteData data = await PlatformAssetBundle().load('assets/ca/lets-encrypt-r3.pem');
  SecurityContext.defaultContext.setTrustedCertificatesBytes(data.buffer.asUint8List());

  runApp(const MyApp());
}

class MyApp extends StatelessWidget{
  const MyApp({super.key});
  @override
  Widget build(BuildContext context) {
    return  MaterialApp(
      title: 'Knights App',
      theme: ThemeData(
        scaffoldBackgroundColor: const Color.fromARGB(255, 5, 5, 5),
      ),
      home:  const Directionality(
        textDirection: TextDirection.ltr, 
        child: MyHomePage(),
        ),
        
    );
  }
}

class MessagePage extends StatelessWidget{
  const MessagePage({super.key});

  @override
  Widget build(BuildContext context) {
    return  Scaffold(
      body:Center(
        child: Column(
          children: <Widget>[
            ElevatedButton(
              onPressed: (){
                
              },
              child: Text('Submit')
              ),
            
            ElevatedButton(
              onPressed: (){
                Navigator.pop(context);
              },
              child: Text('Go Back Home')
              ),
             
          ],
        ),
      )
    );
  }
}


class MyHomePage extends StatelessWidget{
  const MyHomePage({super.key});

    @override
    Widget build(BuildContext context) {
      
      return   Scaffold(
        
        body:Center(
          child: Column(
          
            children: <Widget>[
              Padding(padding: EdgeInsets.only(top: 40.0),
              child: Text(
                'Knights',
                
                style: TextStyle(
                
                  fontSize: 40,
                  color: Color.fromARGB(221, 255, 255, 255),
                  fontFamily: 'roboto'
                ),
              )
              ),

              Padding( padding: EdgeInsets.only(top: 30.0, bottom: 30.0),
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
                child: IdeaList(),
              ),
              Padding(
                padding: EdgeInsets.only(top: 35.0),
                child: ElevatedButton(
                style: ButtonStyle(
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
                child: Text('Say your Piece')
                )
              )
            ],
          )
        )
      );
    }
  }



  



    // This class is for writing an "idea", with a message
    // class Idea extends StatelessWidget {
    //   const Idea({super.key});

    //   @override
    //   Widget build(BuildContext context) {
    //     return(
    //         const Padding(
    //         padding: EdgeInsets.only(top: 10.0),
    //         child: TextField(
    //           decoration: InputDecoration(
    //           border: OutlineInputBorder(),
    //           hintText: 'Please write your idea here...',
    //           hintStyle: TextStyle(color: Colors.green),
    //         ),
    //         style: TextStyle(color: Colors.white),
    //         ),
    //         )
    //     );
    //   }

    // }

