import 'package:flutter/material.dart';
import 'package:knights/components/detailed_post_format.dart';


/// page that shows the detailed view of a post
/// calls the component DetailedPostFormat which needs the Id of the post and the session key
class DetailedPostPage extends StatelessWidget {
  /// post ID given by Idea when clicked
  final int mId;
  /// sessionKey
  final String sessionKey;
  DetailedPostPage({super.key, required this.mId, required this.sessionKey});

  @override
  Widget build(BuildContext context) {
    return Scaffold(
        body: Center(
      child: Column(
        children: <Widget>[
          // calls compnent to display detailed view of individual idea
          DetailedPostFormat(mId: mId, sessionKey: sessionKey),
        ],
      ),
    ));
  }
}
