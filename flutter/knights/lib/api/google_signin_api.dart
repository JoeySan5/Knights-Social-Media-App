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
import 'dart:developer' as developer;

class GoogleSignInApi {
  static final _googleSignIn = GoogleSignIn();
  static Future login() => _googleSignIn.signIn();
  // static Future<GoogleSignInAccount?> login() async {
  //   try {
  //     final GoogleSignInAccount? account = await _googleSignIn.signIn();
  //     if (account != null) {
  //       final GoogleSignInAuthentication googleAuth = await account.authentication;
  //       // correctly gets token from google OAuth
  //       print('login method token:  ${googleAuth.accessToken}');
  //       await _sendTokenToServer(googleAuth.idToken);

  //       return account;
  //     }
  //   } catch (error) {
  //     print('Google Sign-In error: $error');
  //   }
  //   return null;
  // }

  // /// having problem of sending token to backend within this method
  // static Future<void> _sendTokenToServer(String? token) async {
  // var backendUrl = Uri.parse('http://10.0.2.2:8998/login');
  // var headers = {"Accept": "application/json"};
  // var body = {'token': token};

  // //var response = await  http.post(backendUrl, headers:headers, body: jsonEncode(body));

  //    try {
  //       final response = await  http.post(backendUrl, headers:headers, body: jsonEncode(body));
  //     if (response.statusCode == 200) {
  //       print('Token sent to server successfully');
  //     } else {
  //       throw Exception('error: $Error');
  //       print('Failed to send token to server: ${response.body}');
  //       print('error printing toke: ${token}');  
  //     }
  //    } catch (error) {
  //      print('error printing toke: ${token}');
  //      print('Sending token to server failed: $error');
  //    }
  // }
}


/**
 *       var headers = {"Accept": "application/json"};
      var body = {'mContent': userText};

      var response = await http.post(url, headers: headers, body: jsonEncode(body));

            if (response.statusCode == 200){
        developer.log('response headers: ${response.headers}');
        developer.log('response body: ${response.body}');

      return userText;
        
      }
      else{
        ///If the server did not return a 200 OK response,
        ///then throw an exception.
        throw Exception('Did not receive success status(200) code from request.');
      }
 */

/// json.encode({
      //   'token': token,
      // }),








