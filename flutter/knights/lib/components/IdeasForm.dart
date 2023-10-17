import 'package:flutter/material.dart';

import 'package:knights/net/webRequests.dart';


class IdeasForm extends StatefulWidget{
  const IdeasForm({super.key});

  @override
  State<IdeasForm> createState() => _IdeasForm();
}

class _IdeasForm extends State<IdeasForm>{
  //text controller used to retreive current value of the TextField
  final myController = TextEditingController();

  @override
  void dispose(){
    //clean up controller when the widget is disposed
    myController.dispose();
    super.dispose();
  }
  @override
  Widget build(BuildContext context) {
    return Column(
      children: [
            
            Padding(
              padding: EdgeInsets.only(top: 30),
              child: SizedBox( 
              width: 300,
              height: 300,
              child: TextField(
                maxLines: null,
                expands: true,
              
              controller: myController,
              decoration: const InputDecoration(
              border: OutlineInputBorder(),
              hintText: 'Please write your idea here...',
              hintStyle: TextStyle(color: Colors.white),
              contentPadding: EdgeInsets.all(10)
            ),
            style: TextStyle(color: Colors.white),
            )
            )
            ),
            
        ElevatedButton(
              style: const ButtonStyle(
                  backgroundColor: MaterialStatePropertyAll(Colors.green),
                ),
                //the arrow function ()=> allows for postIdeas to return a future
                //this is done because onPressed accepts only voids, not future
              onPressed:() => postIdeas(myController.text),
              child: const Text('Submit')
              ),
      ],
    );
  }
}