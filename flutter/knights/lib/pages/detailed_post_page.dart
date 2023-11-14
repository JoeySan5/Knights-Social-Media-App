import 'package:flutter/material.dart';
import 'package:knights/components/detailed_post_format.dart';

class DetailedPostPage extends StatelessWidget {
  final int mId;
  final String sessionKey;
  DetailedPostPage({super.key, required this.mId, required this.sessionKey});

  @override
  Widget build(BuildContext context) {
    return Scaffold(
        body: Center(
      child: Column(
        children: <Widget>[
          DetailedPostFormat(mId: mId, sessionKey: sessionKey),
          ElevatedButton(
              style: const ButtonStyle(
                backgroundColor: MaterialStatePropertyAll(Colors.green),
              ),
              onPressed: () {
                Navigator.pop(context);
              },
              child: const Text('Comment')),
          ElevatedButton(
              style: const ButtonStyle(
                backgroundColor: MaterialStatePropertyAll(Colors.green),
              ),
              onPressed: () {
                Navigator.pop(context);
              },
              child: const Text('Home Page')),
        ],
      ),
    ));
  }
}
