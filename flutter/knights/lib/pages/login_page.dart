import 'dart:convert';
import 'dart:io';
import 'dart:developer' as developer;

import 'package:flutter/material.dart';
import 'package:google_sign_in/google_sign_in.dart';

import 'package:http/http.dart' as http;

import 'package:knights/pages/home_page.dart';

import 'package:knights/api/google_signin_api.dart';

/// login page
/// has single login button
/// calls google_signin_api.dart which kickstarts signin process
/// that returns a GoogleSignInAccount object which allows the authentication process to begin
/// after that it gets validated on Google's end and returns a key, email, and userId info
/// this gets passed to our backend to get validated and upon success, a session key for current session is returned
/// uerId and sessionKey are stored in a map and passed to homepage which then distrubutes it to other pages for use
class MyLoginPage extends StatelessWidget {
  const MyLoginPage({super.key});

  @override
  Widget build(BuildContext context) {
    Map<String, dynamic> myMap;
    String userId;
    String sessionKey;
    return Scaffold(
        body: Center(
            child: SingleChildScrollView(
                child: Column(
      children: <Widget>[
        const Padding(
            padding: EdgeInsets.only(top: 40.0),
            child: Text(
              'Knights',
              style: TextStyle(
                  fontSize: 40,
                  color: Color.fromARGB(221, 255, 255, 255),
                  fontFamily: 'roboto'),
            )),
        const Padding(
          padding: EdgeInsets.only(top: 30.0, bottom: 30.0),
          child: Text(
            'Welcome back. Please log in to continue:',
            textDirection: TextDirection.ltr,
            style: TextStyle(
              fontSize: 20,
              color: Colors.white,
              fontFamily: 'roboto',
            ),
          ),
        ),
        Padding(
            padding: const EdgeInsets.only(top: 35.0),
            child: ElevatedButton(
                style: const ButtonStyle(
                  backgroundColor: MaterialStatePropertyAll(Colors.green),
                ),
                // sends user to home page after sucessfully signing in
                onPressed: () async => {
                      myMap = await signIn(),
                      if (myMap['userId'] != "")
                        {
                          userId = myMap['userId'],
                          sessionKey = myMap['sessionKey'],
                          print('myMap sessionKey: $sessionKey'),
                          Navigator.push(
                              context,
                              MaterialPageRoute(
                                  builder: (context) => MyHomePage(
                                      userId: userId, sessionKey: sessionKey)))
                        }
                    },
                child: const Text('Log in with Google')))
      ],
    ))));
  }

  /// takes GoogleSignInAccount object from api and uses it to authenitcate on googles end
  /// then it passes that to the backend through the _sendTokenToServer method and gets the session key in return
  /// then passes back a map of session key and userId
  Future<Map<String, dynamic>> signIn() async {
    final user = await GoogleSignInApi.login();
    // map to hold userId and session key
    Map<String, dynamic> userSeshInfo = {'userId': "", 'sessionKey': ""};
    if (user != null) {
      print(user.id);
      final GoogleSignInAuthentication googleAuth = await user.authentication;
      // correctly gets token from google OAuth
      print('user id: ${user.id}');
      print('login method token:  ${googleAuth.idToken}');
      var sessionKey = await _sendTokenToServer(googleAuth.idToken);
      userSeshInfo = {'userId': user.id, 'sessionKey': sessionKey};
      return userSeshInfo;
    } else {
      print('user is null!!');
      return userSeshInfo;
    }
  }

  /// gets SignInAuthentication and sends to backend to get credential or token which is session key
  Future<String> _sendTokenToServer(String? token) async {
    var backendUrl =
        Uri.parse('https://team-knights.dokku.cse.lehigh.edu/login');
    //Uri.parse("http://10.0.2.2:8998/login");
    var headers = {"Accept": "application/json"};
    var body = {'credential': token};
    try {
      final response =
          await http.post(backendUrl, headers: headers, body: jsonEncode(body));
      if (response.statusCode == 200) {
        print('\nresponse: ${response.body}\n');
        print('Token sent to server successfully');
        var res = json.decode(response.body);
        String sessionKey = res['mData'];
        print(sessionKey);
        return sessionKey;
      } else {
        print('Failed to send token to server: ${response.body}');
        print('error printing token: ${token}');
        return "";
      }
    } catch (error) {
      print('Sending token to server failed: $error');
      print('error printing token: ${token}');
      return "";
    }
  }
} // end of class