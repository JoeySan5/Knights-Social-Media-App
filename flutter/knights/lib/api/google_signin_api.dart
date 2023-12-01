// import 'package:google_sign_in/google_sign_in.dart';

// class GoogleSignInApi{
//   static final ex = GoogleSignIn()
  
//   static final _googleSignIn = GoogleSignIn();
  
//   static Future<GoogleSignInAccount?> login() => _googleSignIn.signIn();
  
// }
import 'package:flutter/material.dart';
import 'package:google_sign_in/google_sign_in.dart';
import 'package:http/http.dart' as http;
import 'dart:convert';

class GoogleSignInApi {
  static final GoogleSignIn _googleSignIn = GoogleSignIn();
  static Future<GoogleSignInAccount?> login() async {
    try {
      final GoogleSignInAccount? account = await _googleSignIn.signIn();
      if (account != null) {
        final GoogleSignInAuthentication googleAuth = await account.authentication;
        await _sendTokenToServer(googleAuth.accessToken);
        return account;
      }
    } catch (error) {
      print('Google Sign-In error: $error');
    }
    return null;
  }
  static Future<void> _sendTokenToServer(String? token) async {
    const String backendUrl = 'http://localhost:8998/login';
    try {
      final response = await http.post(
        Uri.parse(backendUrl),
        headers: {
          'Content-Type': 'application/json',
        },
      body: json.encode({
        'token': token,
      }),
      );
      if (response.statusCode == 200) {
        print('Token sent to server successfully');
      } else {
        print('Failed to send token to server: ${response.body}');
      }
    } catch (error) {
      print('Sending token to server failed: $error');
    }
  }
}









