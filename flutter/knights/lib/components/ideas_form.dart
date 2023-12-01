import 'package:flutter/material.dart';

import 'package:knights/net/web_requests.dart';

///Component for Ideas Form (Submission).
///
///Contains the a text field for the user to write into. Creates a controller to
///keep track of what is being written inside of the text field. Lastly, contains a
///submit button to post onto backend-dokku.
class IdeasForm extends StatefulWidget {
  final String sessionKey;
  const IdeasForm({super.key, required this.sessionKey});

  @override
  State<IdeasForm> createState() => _IdeasForm();
}

class _IdeasForm extends State<IdeasForm> {
  ///text controller used to retreive current value of the TextField
  final myController = TextEditingController();
  final GlobalKey<FormState> _formKey = GlobalKey<FormState>();

  @override
  void dispose() {
    //clean up controller when the widget is disposed
    myController.dispose();
    super.dispose();
  }

  @override
  Widget build(BuildContext context) {
    String content = "";
    String link = "";
    return Form(
      key: _formKey,
      child: Column(
        children: [
          Padding(
              padding: const EdgeInsets.only(top: 30),
              child: SizedBox(
                  width: 300,
                  height: 300,
                  child: TextFormField(
                    maxLines: null,
                    expands: true,

                    //controller: myController,
                    decoration: const InputDecoration(
                        border: OutlineInputBorder(),
                        hintText: 'Please write your idea here...',
                        labelText: 'Please write your idea here...',
                        labelStyle: TextStyle(
                            color: Colors.white,
                            fontFamily: 'roboto',
                            fontSize: 20),
                        hintStyle: TextStyle(color: Colors.white),
                        contentPadding: EdgeInsets.all(10)),
                    onSaved: (String? value) {
                      // Save the data when the form is saved
                      if (value != null) {
                        content = value;
                      } else {
                        content = "";
                      }
                      // The key parameter should be unique for each TextFormField
                      print('content: $value');
                    },
                    style: const TextStyle(color: Colors.white),
                  ))),
          Padding(
              padding: const EdgeInsets.only(top: 30),
              // SECOND BOX FOR LINKS
              child: SizedBox(
                  width: 300,
                  height: 300,
                  child: TextFormField(
                    maxLines: null,
                    expands: true,

                    //controller: myController,
                    decoration: const InputDecoration(
                        border: OutlineInputBorder(),
                        hintText: 'Please write your link here...',
                        labelText: 'Please write your link here...',
                        labelStyle: TextStyle(
                            color: Colors.white,
                            fontFamily: 'roboto',
                            fontSize: 20),
                        hintStyle: TextStyle(color: Colors.white),
                        contentPadding: EdgeInsets.all(10)),
                    onSaved: (String? value) {
                      // Save the data when the form is saved
                      if (value != null) {
                        link = value;
                      } else {
                        link = "";
                      }
                      // The key parameter should be unique for each TextFormField
                      print('content: $value');
                    },
                    style: const TextStyle(color: Colors.white),
                  ))),
          ElevatedButton(
              style: const ButtonStyle(
                backgroundColor: MaterialStatePropertyAll(Colors.green),
              ),
              //the arrow function ()=> allows for postIdeas to return a future
              //this is done because onPressed accepts only voids, not future
              onPressed: () => {
                                if (_formKey.currentState!.validate()) {
                                _formKey.currentState!.save(),
                                print('Form saved!'),
                                /// update user profile if information is valid
                    postIdeas(content, link, widget.sessionKey),
                                print('posted idea sucessfully!'),
                              },

                    Navigator.pop(context)
                  },
              child: const Text('Submit')),
        ],
      ),
    );
    // return Column(
    //   children: [

    //         Padding(
    //           padding: const EdgeInsets.only(top: 30),
    //           child: SizedBox(
    //           width: 300,
    //           height: 300,
    //           child: TextField(
    //             maxLines: null,
    //             expands: true,

    //           controller: myController,
    //           decoration: const InputDecoration(
    //           border: OutlineInputBorder(),
    //           hintText: 'Please write your idea here...',
    //           hintStyle: TextStyle(color: Colors.white),
    //           contentPadding: EdgeInsets.all(10)
    //         ),
    //         style: const TextStyle(color: Colors.white),
    //         )
    //         )
    //         ),

    //     ElevatedButton(
    //           style: const ButtonStyle(
    //               backgroundColor: MaterialStatePropertyAll(Colors.green),
    //             ),
    //             //the arrow function ()=> allows for postIdeas to return a future
    //             //this is done because onPressed accepts only voids, not future
    //           onPressed:() => {
    //             postIdeas(myController.text, widget.sessionKey),
    //             Navigator.pop(context)
    //           },
    //           child: const Text('Submit')

    //           ),
    //   ],
    // );
  }
}
