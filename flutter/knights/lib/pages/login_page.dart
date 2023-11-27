import 'dart:convert';
import 'dart:io';

import 'package:flutter/material.dart';
import 'package:google_sign_in/google_sign_in.dart';
import 'package:google_sign_in/google_sign_in.dart';
import 'package:http/http.dart' as http;
import 'package:knights/components/idea_list.dart';
import 'package:knights/pages/home_page.dart';
import 'package:knights/pages/message_page.dart';

import 'package:knights/api/google_signin_api.dart';

/**
 * This is the login page for the app
 */

class MyLoginPage extends StatelessWidget {
  const MyLoginPage({super.key});

  @override
  Widget build(BuildContext context) {
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
<<<<<<< HEAD
                onPressed: signIn,
=======
                onPressed: () => {
                      if (signIn != null)
                        {
                          Navigator.push(
                              context,
                              MaterialPageRoute(
                                  builder: (context) => const MyHomePage()))
                        }
                    },
>>>>>>> web
                child: const Text('Sign Up with Google')))
      ],
    ))));
  }

  Future signIn() async {
<<<<<<< HEAD
    final user = await GoogleSignInApi.login();
  }

=======
    final GoogleSignInAccount? user = await GoogleSignInApi.login();
    //final GoogleSignInAccount? account = await _googleSignIn.signIn();
    if (user != null) {
      final GoogleSignInAuthentication googleAuth = await user.authentication;
      // correctly gets token from google OAuth
      print('user id: ${user.id}');
      print('login method token:  ${googleAuth.idToken}');
      await _sendTokenToServer(googleAuth.idToken);
      return user;
    } else {
      print('user is null!!');
      return null;
    }
  }

  /// having problem of sending token to backend within this method
  static Future<void> _sendTokenToServer(String? token) async {
    var backendUrl =
        Uri.parse('https://team-knights.dokku.cse.lehigh.edu/login');
    var headers = {"Accept": "application/json"};
    var body = {'credential': token};
    try {
      final response =
          await http.post(backendUrl, headers: headers, body: jsonEncode(body));
      if (response.statusCode == 200) {
        print('Token sent to server successfully'); 
      } else {
        throw Exception('error: $Error');
        print('Failed to send token to server: ${response.body}');
        print('error printing toke: ${token}');
      }
    } catch (error) {
      print('error printing toke: ${token}');
      print('Sending token to server failed: $error');
    }
  }
>>>>>>> web
}
