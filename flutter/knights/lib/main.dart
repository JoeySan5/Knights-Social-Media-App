import 'package:flutter/material.dart';
import 'package:flutter/services.dart';
import 'package:flutter/widgets.dart';
import 'dart:io';

import 'package:knights/pages/home_page.dart';
import 'package:knights/pages/login_page.dart';


///The main functions starts the app by calling MyHomePage
///
///Also holds the process to get app certification to communicate with other sites.
void main() async {
  WidgetsFlutterBinding.ensureInitialized();

  ByteData data = await PlatformAssetBundle().load('assets/ca/lets-encrypt-r3.pem');
  SecurityContext.defaultContext.setTrustedCertificatesBytes(data.buffer.asUint8List());
  runApp(const MyApp());
}

class MyApp extends StatelessWidget{
  const MyApp({super.key});
  @override
  Widget build(BuildContext context) {
    return  MaterialApp(
      title: 'Knights App',
      theme: ThemeData(
        scaffoldBackgroundColor: const Color.fromARGB(255, 5, 5, 5),
      ),
      home:  const Directionality(
        textDirection: TextDirection.ltr, 
        child: MyLoginPage(),
        ),

    );
  }
}
