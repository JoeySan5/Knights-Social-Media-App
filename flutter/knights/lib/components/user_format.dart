import 'package:flutter/material.dart';

import 'package:knights/net/web_requests.dart';
import 'package:knights/models/User.dart';

/// this class is format to  display current userinformation
///
/// component includers user id, username, email, note, SO, GI
/// Uses sessionKey as parameter to make TTP GET request
class UserFormat extends StatefulWidget {
  final String sessionKey;
  const UserFormat({super.key, required this.sessionKey});

  @override
  State<UserFormat> createState() => _UserFormat();
}

/// what is created when _UserFormat is called after createState()  method
class _UserFormat extends State<UserFormat> {
  /// future object for async calls
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

/// widget that displays profile
  @override
  Widget build(BuildContext context) {
    String mUsername = "";
    String mEmail = "";
    String mNote = "";
    String mGI = "";
    String mSO = "";
    /// form is to allow the informationto be editable
    return Form(
      key:
          _formKey, // Create a GlobalKey<FormState> _formKey in your State class
      child: Column(
        /// all children are in this container
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
                        /// if data is returned, construct and fill column and children
                    if (snapshot.hasData) {
                      return Column(
                        mainAxisSize: MainAxisSize.min,
                        crossAxisAlignment: CrossAxisAlignment.center,
                        children: [
                          Padding(
                            padding: const EdgeInsets.all(8.0),
                            /// TextFormField allows for data to be editable
                            child: TextFormField(
                              initialValue: '${snapshot.data!.mUsername}',
                              style: const TextStyle(
                                  fontSize: 20,
                                  color: Colors.white,
                                  fontFamily: 'roboto'),
                              decoration: InputDecoration(
                                icon: Icon(Icons.person),
                                hintText: '${snapshot.data!.mUsername}',
                                labelText: 'Username',
                                labelStyle: TextStyle(
                                    color: Colors.white,
                                    fontFamily: 'roboto',
                                    fontSize: 20),
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
                            /// TextFormField allows for data to be editable
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
                                labelStyle: TextStyle(
                                    color: Colors.white,
                                    fontFamily: 'roboto',
                                    fontSize: 20),
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
                            /// TextFormField allows for data to be editable
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
                                labelStyle: TextStyle(
                                    color: Colors.white,
                                    fontFamily: 'roboto',
                                    fontSize: 20),
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
                            /// TextFormField allows for data to be editable
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
                                labelStyle: TextStyle(
                                    color: Colors.white,
                                    fontFamily: 'roboto',
                                    fontSize: 20),
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
                            /// TextFormField allows for data to be editable
                            child: TextFormField(
                              initialValue: '${snapshot.data!.mSO}',
                              style: const TextStyle(
                                  fontSize: 20,
                                  color: Colors.white,
                                  fontFamily: 'roboto'),
                              decoration: InputDecoration(
                                icon: Icon(Icons.person),
                                hintText: 'SO: ${snapshot.data!.mSO}',
                                labelText: 'SO',
                                labelStyle: TextStyle(
                                    color: Colors.white,
                                    fontFamily: 'roboto',
                                    fontSize: 20),
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
                          ElevatedButton(
                            style: const ButtonStyle(
                              backgroundColor:
                                  MaterialStatePropertyAll(Colors.green),
                            ),
                            onPressed: () {
                              // Validate and save the form
                              if (_formKey.currentState!.validate()) {
                                _formKey.currentState!.save();
                                print('Form saved!');
                                /// update user profile if information is valid
                                updateUserProfile(widget.sessionKey, mUsername,
                                    mEmail, mSO, mGI, mNote);
                                print('updated user profile!');
                              }
                            },
                            child: const Text('Submit'),
                          ),
                          ElevatedButton(
                            style: const ButtonStyle(
                              backgroundColor:
                                  MaterialStatePropertyAll(Colors.green),
                            ),
                            onPressed: () {
                              // send user back to homepage
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
}
