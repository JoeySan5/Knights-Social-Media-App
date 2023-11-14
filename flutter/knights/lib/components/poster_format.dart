import 'package:flutter/material.dart';

import 'package:knights/net/web_requests.dart';
import 'package:knights/models/User.dart';
import 'package:knights/pages/poster_user_profile_page.dart';
import 'package:knights/models/Poster.dart';

/// this class is format to  display current userinformation
///
/// component includers user id, username, email, note, SO, GI
class PosterFormat extends StatefulWidget {
  final String userId;
  final String sessionKey;
  const PosterFormat({super.key, required this.userId, required this.sessionKey});
  @override
  State<PosterFormat> createState() => _PosterFormat();
}

class _PosterFormat extends State<PosterFormat> {
  late Future<Poster> _futurePosterFormat;
  // controller for allowing text to be editable
  TextEditingController textController = TextEditingController();

  /// now we want to read in data from dokku using get and parse json into user_format objects
  ///
  /// initState called to initalize data that depends on a build,
  /// once the widget is inserted inside the widgettree. In this case the
  /// widget is the list of user_formats
  @override
  void initState() {
    super.initState();
    print('user format sessionKey: ${widget.sessionKey}');
    _futurePosterFormat = fetchPoster(widget.userId,widget.sessionKey);
  }

  void retry() {
    setState(() {
      _futurePosterFormat = fetchPoster(widget.userId,widget.sessionKey);
    });
  }

  @override
  Widget build(BuildContext context) {
    return Center(
      child: Container(
        width: 700,
        height: 500,
        child: Padding(
          padding: const EdgeInsets.only(
              left: 24.0, top: 100.0, right: 24.0, bottom: 140),
          child: Container(
            decoration: BoxDecoration(
              border: Border.all(
                color: Colors.green,
                width: 1.5,
              ),
              borderRadius: const BorderRadius.all(Radius.circular(10)),
            ),
            child: FutureBuilder<Poster>(
              future: _futurePosterFormat,
              builder: (BuildContext context, AsyncSnapshot<Poster> snapshot) {
                if (snapshot.hasData) {
                  return Column(
                    mainAxisSize: MainAxisSize.min,
                    crossAxisAlignment: CrossAxisAlignment.center,
                    children: [
                      Padding(
                        padding: const EdgeInsets.all(8.0),
                        child: Text(
                          'Username: ${snapshot.data!.mUsername}',
                          style: const TextStyle(
                              fontSize: 20,
                              color: Colors.white,
                              fontFamily: 'roboto'),
                        ),
                      ),
                      Padding(
                        padding: const EdgeInsets.all(8.0),
                        child: Text(
                          'User Email: ${snapshot.data!.mEmail}',
                          style: const TextStyle(
                              fontSize: 20,
                              color: Colors.white,
                              fontFamily: 'roboto'),
                        ),
                      ),
                      Padding(
                        padding: const EdgeInsets.all(8.0),
                        child: Text(
                          'User Note: ${snapshot.data!.mNote}',
                          style: const TextStyle(
                              fontSize: 20,
                              color: Colors.white,
                              fontFamily: 'roboto'),
                        ),
                      ),
                                                // Other TextFormField widgets with similar structure
                          ElevatedButton(
                            style: const ButtonStyle(
                              backgroundColor:
                                  MaterialStatePropertyAll(Colors.green),
                            ),
                            onPressed: () {
                              // Validate and save the form
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
    );
  }
}
