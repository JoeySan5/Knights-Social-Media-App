import 'package:flutter/material.dart';
import 'package:google_sign_in/google_sign_in.dart';

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
                onPressed: signIn,
                child: const Text('Sign Up with Google')))
      ],
    ))));
  }

  Future signIn() async {
    final user = await GoogleSignInApi.login();
  }

}
