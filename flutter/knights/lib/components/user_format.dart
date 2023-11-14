import 'package:flutter/material.dart';

import 'package:knights/net/web_requests.dart';
import 'package:knights/models/User.dart';
import 'package:knights/pages/home_page.dart';

/// this class is format to  display current userinformation
///
/// component includers user id, username, email, note, SO, GI
class UserFormat extends StatefulWidget {
  final String sessionKey;
  const UserFormat({super.key, required this.sessionKey});

  @override
  State<UserFormat> createState() => _UserFormat();
}

class _UserFormat extends State<UserFormat> {
  late Future<User> _futureUserFormat;
  // controller for allowing text to be editable
  final GlobalKey<FormState> _formKey = GlobalKey<FormState>();

  /// now we want to read in data from dokku using get and parse json into user_format objects
  ///
  /// initState called to initalize data that depends on a build,
  /// once the widget is inserted inside the widgettree. In this case the
  /// widget is the list of user_formats
  @override
  void initState() {
    super.initState();
    print('user format sessionKey: ${widget.sessionKey}');
    _futureUserFormat = fetchUsers(widget.sessionKey);
  }

  void retry() {
    setState(() {
      _futureUserFormat = fetchUsers(widget.sessionKey);
    });
  }

  @override
  Widget build(BuildContext context) {
    String mUsername = "";
    String mEmail = "";
    String mNote = "";
    String mGI = "";
    String mSO = "";
    return Form(
      key:
          _formKey, // Create a GlobalKey<FormState> _formKey in your State class
      child: Column(
        children: [
          Container(
            width: 700,
            height: 700,
            child: Padding(
              padding: const EdgeInsets.only(
                  left: 24.0, top: 100.0, right: 24.0, bottom: 0),
              child: Container(
                decoration: BoxDecoration(
                  border: Border.all(
                    color: Colors.green,
                    width: 1.5,
                  ),
                  borderRadius: const BorderRadius.all(Radius.circular(10)),
                ),
                child: FutureBuilder<User>(
                  future: _futureUserFormat,
                  builder:
                      (BuildContext context, AsyncSnapshot<User> snapshot) {
                    if (snapshot.hasData) {
                      return Column(
                        mainAxisSize: MainAxisSize.min,
                        crossAxisAlignment: CrossAxisAlignment.center,
                        children: [
                          Padding(
                            padding: const EdgeInsets.all(8.0),
                            child: TextFormField(
                              initialValue:
                                  '${snapshot.data!.mUsername}',
                              style: const TextStyle(
                                  fontSize: 20,
                                  color: Colors.white,
                                  fontFamily: 'roboto'),
                              decoration: InputDecoration(
                                icon: Icon(Icons.person),
                                hintText: '${snapshot.data!.mUsername}',
                                labelText: 'Username',
                                hintStyle: const TextStyle(
                                    color: Colors.white,
                                    fontFamily: 'roboto',
                                    fontSize: 20),
                              ),
                              onSaved: (String? value) {
                                // Save the data when the form is saved
                                if (value != null) {
                                  mUsername = value;
                                } else {
                                  mUsername = "";
                                }
                                // The key parameter should be unique for each TextFormField
                                print('Username: $value');
                              },
                            ),
                          ),
                          Padding(
                            padding: const EdgeInsets.all(8.0),
                            child: TextFormField(
                              initialValue: '${snapshot.data!.mEmail}',
                              style: const TextStyle(
                                  fontSize: 20,
                                  color: Colors.white,
                                  fontFamily: 'roboto'),
                              decoration: InputDecoration(
                                icon: Icon(Icons.person),
                                hintText: '${snapshot.data!.mEmail}',
                                labelText: 'Email',
                                hintStyle: const TextStyle(
                                    color: Colors.white,
                                    fontFamily: 'roboto',
                                    fontSize: 20),
                              ),
                              onSaved: (String? value) {
                                // Save the data when the form is saved
                                if (value != null) {
                                  mEmail = value;
                                } else {
                                  mEmail = "";
                                }
                                // The key parameter should be unique for each TextFormField
                                print('Email: $value');
                              },
                            ),
                          ),
                          Padding(
                            padding: const EdgeInsets.all(8.0),
                            child: TextFormField(
                              initialValue: '${snapshot.data!.mNote}',
                              style: const TextStyle(
                                  fontSize: 20,
                                  color: Colors.white,
                                  fontFamily: 'roboto'),
                              decoration: InputDecoration(
                                icon: Icon(Icons.person),
                                hintText: '${snapshot.data!.mNote}',
                                labelText: 'Note',
                                hintStyle: const TextStyle(
                                    color: Colors.white,
                                    fontFamily: 'roboto',
                                    fontSize: 20),
                              ),
                              onSaved: (String? value) {
                                // Save the data when the form is saved
                                if (value != null) {
                                  mNote = value;
                                } else {
                                  mNote = "";
                                }
                                // The key parameter should be unique for each TextFormField
                                print('Note: $value');
                              },
                            ),
                          ),
                          Padding(
                            padding: const EdgeInsets.all(8.0),
                            child: TextFormField(
                              initialValue: '${snapshot.data!.mGI}',
                              style: const TextStyle(
                                  fontSize: 20,
                                  color: Colors.white,
                                  fontFamily: 'roboto'),
                              decoration: InputDecoration(
                                icon: Icon(Icons.person),
                                hintText: '${snapshot.data!.mGI}',
                                labelText: 'GI',
                                hintStyle: const TextStyle(
                                    color: Colors.white,
                                    fontFamily: 'roboto',
                                    fontSize: 20),
                              ),
                              onSaved: (String? value) {
                                // Save the data when the form is saved
                                if (value != null) {
                                  mGI = value;
                                } else {
                                  mGI = "";
                                }
                                // The key parameter should be unique for each TextFormField
                                print('GI: $value');
                              },
                            ),
                          ),
                          Padding(
                            padding: const EdgeInsets.all(8.0),
                            child: TextFormField(
                              initialValue: '${snapshot.data!.mSO}',
                              style: const TextStyle(
                                  fontSize: 20,
                                  color: Colors.white,
                                  fontFamily: 'roboto'),
                              decoration: InputDecoration(
                                icon: Icon(Icons.person),
                                hintText: '${snapshot.data!.mSO}',
                                labelText: 'SO',
                                hintStyle: const TextStyle(
                                    color: Colors.white,
                                    fontFamily: 'roboto',
                                    fontSize: 20),
                              ),
                              onSaved: (String? value) {
                                // Save the data when the form is saved
                                if (value != null) {
                                  mSO = value;
                                } else {
                                  mSO = "";
                                }
                                // The key parameter should be unique for each TextFormField
                                print('SO: $value');
                              },
                            ),
                          ),
                          // Other TextFormField widgets with similar structure
                          ElevatedButton(
                            style: const ButtonStyle(
                              backgroundColor:
                                  MaterialStatePropertyAll(Colors.green),
                            ),
                            onPressed: () {
                              // Validate and save the form
                              if (_formKey.currentState!.validate()) {
                                _formKey.currentState!.save();
                                // Do something with the collected data
                                print('Form saved!');
                                updateUserProfile(widget.sessionKey, mUsername , mEmail, mSO, mGI, mNote);
                                print('updated user profile!');
                              }
                            },
                            child: const Text('Submit'),
                          ),
                                                    // Other TextFormField widgets with similar structure
                          ElevatedButton(
                            style: const ButtonStyle(
                              backgroundColor:
                                  MaterialStatePropertyAll(Colors.green),
                            ),
                            onPressed: () {
                              // Validate and save the form
                              Navigator.pop(context);
                            },
                            child: const Text('Home Page'),
                          ),
                        ],
                        
                      );
                    } else if (snapshot.hasError) {
                      return Text('${snapshot.error}');
                    } else {
                      return CircularProgressIndicator();
                    }
                  },
                ),
              ),
            ),
          ),
        ],
      ),
    );
  }

  // @override
  // Widget build(BuildContext context) {
  //   return Column(children: [
  //     Container(
  //       width: 700,
  //       height: 700,
  //       child: Padding(
  //         padding: const EdgeInsets.only(
  //             left: 24.0, top: 100.0, right: 24.0, bottom: 0),
  //         child: Container(
  //           decoration: BoxDecoration(
  //             border: Border.all(
  //               color: Colors.green,
  //               width: 1.5,
  //             ),
  //             borderRadius: const BorderRadius.all(Radius.circular(10)),
  //           ),
  //           child: FutureBuilder<User>(
  //             future: _futureUserFormat,
  //             builder: (BuildContext context, AsyncSnapshot<User> snapshot) {
  //               if (snapshot.hasData) {
  //                 return Column(
  //                   mainAxisSize: MainAxisSize.min,
  //                   crossAxisAlignment: CrossAxisAlignment.center,
  //                   children: [
  //                     Padding(
  //                       padding: const EdgeInsets.all(8.0),
  //                       child: TextFormField(
  //                         initialValue: 'Username: ${snapshot.data!.mUsername}',
  //                         style: const TextStyle(
  //                             fontSize: 20,
  //                             color: Colors.white,
  //                             fontFamily: 'roboto'),
  //                         decoration: InputDecoration(
  //                           icon: Icon(Icons.person),
  //                           hintText: '${snapshot.data!.mUsername}',
  //                           labelText: 'Username',
  //                           hintStyle: const TextStyle(
  //                             color: Colors.white,
  //                             fontFamily: 'roboto',
  //                             fontSize: 20
  //                           ),
  //                         ),
  //                         onSaved: (String? value) {
  //                           print(value);
  //                         },
  //                       ),
  //                     ),
  //                     Padding(
  //                       padding: const EdgeInsets.all(8.0),
  //                       child: TextFormField(
  //                         initialValue: 'Email: ${snapshot.data!.mEmail}',
  //                         style: const TextStyle(
  //                             fontSize: 20,
  //                             color: Colors.white,
  //                             fontFamily: 'roboto'),
  //                         decoration: InputDecoration(
  //                           icon: Icon(Icons.person),
  //                           hintText: '${snapshot.data!.mEmail}',
  //                           labelText: 'Email',
  //                           hintStyle: const TextStyle(
  //                             color: Colors.white,
  //                             fontFamily: 'roboto',
  //                             fontSize: 20
  //                           ),
  //                         ),
  //                         onSaved: (String? value) {
  //                           print(value);
  //                         },
  //                       ),
  //                     ),
  //                     Padding(
  //                       padding: const EdgeInsets.all(8.0),
  //                       child: TextFormField(
  //                         initialValue: 'Note: ${snapshot.data!.mNote}',
  //                         style: const TextStyle(
  //                             fontSize: 20,
  //                             color: Colors.white,
  //                             fontFamily: 'roboto'),
  //                         decoration: InputDecoration(
  //                           icon: Icon(Icons.person),
  //                           hintText: '${snapshot.data!.mNote}',
  //                           labelText: 'Note',
  //                           hintStyle: const TextStyle(
  //                             color: Colors.white,
  //                             fontFamily: 'roboto',
  //                             fontSize: 20
  //                           ),
  //                         ),
  //                         onSaved: (String? value) {
  //                           print(value);
  //                         },
  //                       ),
  //                     ),
  //                     Padding(
  //                       padding: const EdgeInsets.all(8.0),
  //                       child: TextFormField(
  //                         initialValue: 'GI: ${snapshot.data!.mGI}',
  //                         style: const TextStyle(
  //                             fontSize: 20,
  //                             color: Colors.white,
  //                             fontFamily: 'roboto'),
  //                         decoration: InputDecoration(
  //                           icon: Icon(Icons.person),
  //                           hintText: '${snapshot.data!.mGI}',
  //                           labelText: 'GI',
  //                           hintStyle: const TextStyle(
  //                             color: Colors.white,
  //                             fontFamily: 'roboto',
  //                             fontSize: 20
  //                           ),
  //                         ),
  //                         onSaved: (String? value) {
  //                           print(value);
  //                         },
  //                       ),
  //                     ),
  //                     Padding(
  //                       padding: const EdgeInsets.all(8.0),
  //                       child: TextFormField(
  //                         initialValue: 'SO: ${snapshot.data!.mSO}',
  //                         style: const TextStyle(
  //                             fontSize: 20,
  //                             color: Colors.white,
  //                             fontFamily: 'roboto'),
  //                         decoration: InputDecoration(
  //                           icon: Icon(Icons.person),
  //                           hintText: '${snapshot.data!.mSO}',
  //                           labelText: 'SO',
  //                           hintStyle: const TextStyle(
  //                             color: Colors.white,
  //                             fontFamily: 'roboto',
  //                             fontSize: 20
  //                           ),
  //                         ),
  //                         onSaved: (String? value) {
  //                           print(value);
  //                         },
  //                       ),
  //                     ),
  //                     ElevatedButton(
  //                         style: const ButtonStyle(
  //                           backgroundColor:
  //                               MaterialStatePropertyAll(Colors.green),
  //                         ),
  //                         //the arrow function ()=> allows for postIdeas to return a future
  //                         //this is done because onPressed accepts only voids, not future
  //                         onPressed: () => {
  //                               print(),
  //                               Navigator.pop(context)
  //                             },
  //                         child: const Text('Submit')),
  //                   ],
  //                 );
  //               } else if (snapshot.hasError) {
  //                 return Text('${snapshot.error}');
  //               } else {
  //                 return CircularProgressIndicator();
  //               }
  //             },
  //           ),
  //         ),
  //       ),
  //     ),
  //   ]);
  // }
}
