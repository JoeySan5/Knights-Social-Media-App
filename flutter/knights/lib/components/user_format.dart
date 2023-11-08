import 'package:flutter/material.dart';

import 'package:knights/net/web_requests.dart';
import 'package:knights/models/User.dart';
import 'package:knights/pages/home_page.dart';


/// this class is format to  display current userinformation
///
/// component includers user id, username, email, note, SO, GI
class UserFormat extends StatefulWidget {


  const UserFormat(userId, sessionKey, {super.key});
  //const UserFormat({super.key, required this.mId, required this.mUsername, required this.mEmail, required this.mNote, required this.GI, required this.SO});
  @override
  State<UserFormat> createState() => _UserFormat();
}

class _UserFormat extends State<UserFormat> {
  late Future<User> _futureUserFormat;

  /// now we want to read in data from dokku using get and parse json into user_format objects
  ///
  /// initState called to initalize data that depends on a build,
  /// once the widget is inserted inside the widgettree. In this case the
  /// widget is the list of user_formats
  @override
  void initState() {
    super.initState();
    _futureUserFormat = fetchUsers(userId, sessionKey);
    
  }

  void retry() {
    setState(() {
      _futureUserFormat = fetchUsers(userId, sessionKey);
    });
  }

  

  

  
  @override
  Widget build(BuildContext context) {
    var fb = FutureBuilder<User>(
      future: _futureUserFormat,
      builder: (BuildContext context, AsyncSnapshot<User> snapshot) {
        Widget child;


        
        if (snapshot.hasData) {
          return Container(
              margin: const EdgeInsets.all(15.0),
              //decoration is to make borders look different
              decoration: BoxDecoration(
                border: Border.all(
                  color: Colors.green,
                  width: 1.5,
                ),
                borderRadius: const BorderRadius.all(Radius.circular(10)),
              ),
              child: Column(
                children: [
                  ///Sets up the user message.
                  Padding(
                      padding: const EdgeInsets.all(8.0),
                      child: Text(
                        'User ID: ${snapshot.data!.mId}',
                        style: const TextStyle(fontSize: 40, color: Colors.white, fontFamily: 'roboto'),
                      )),
                  Padding(
                      padding: const EdgeInsets.all(8.0),
                      child: Text(
                        'Username: ${snapshot.data!.mUsername}',
                        style: const TextStyle(fontSize: 40, color: Colors.white, fontFamily: 'roboto'),
                      )),
                  Padding(
                      padding: const EdgeInsets.all(8.0),
                      child: Text(
                        'User Email: ${snapshot.data!.mEmail}',
                        style: const TextStyle(fontSize: 40, color: Colors.white, fontFamily: 'roboto'),
                      )),
                  Padding(
                      padding: const EdgeInsets.all(8.0),
                      child: Text(
                        'User Note: ${snapshot.data!.mNote}',
                        style: const TextStyle(fontSize: 40, color: Colors.white, fontFamily: 'roboto'),
                      )),
                ],
              ));
        } else if (snapshot.hasError) {
          child = Text('${snapshot.error}');
        } else {
          ///This awaits snapshot data, displaying a loading spinner
          child = const CircularProgressIndicator();
        }
        
        return child;
      },
      
    );
    return fb;
  
  } // end of widget
}
