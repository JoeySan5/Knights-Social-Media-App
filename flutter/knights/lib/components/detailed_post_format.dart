import 'package:flutter/material.dart';
import 'package:knights/models/DetailedPost.dart';

import 'package:knights/net/web_requests.dart';
import 'package:knights/models/User.dart';
import 'package:knights/net/web_requests.dart';
import 'package:knights/pages/home_page.dart';
import 'package:knights/components/idea_format.dart';

/// this class is to format detailed post view
///
/// component contains postid, like count,content, posteruserName, current user

class DetailedPostFormat extends StatefulWidget {
  /// postId
  final int mId;
  final String sessionKey;
  const DetailedPostFormat(
      {super.key, required this.mId, required this.sessionKey});

  @override
  State<DetailedPostFormat> createState() => _DetailedPostFormat();
}

class _DetailedPostFormat extends State<DetailedPostFormat> {
  late Future<DetailedPost> _futureDetailedPostFormat;
  final GlobalKey<FormState> _formKey = GlobalKey<FormState>();

  @override
  void initState() {
    super.initState();
    _futureDetailedPostFormat =
        fetchDetailedPost(widget.mId, widget.sessionKey);
  }

  @override
  void retry() {
    super.initState();
    _futureDetailedPostFormat =
        fetchDetailedPost(widget.mId, widget.sessionKey);
  }

  /// now we want to read in data from dokku using get and parse json into detailed_post objects
  ///
  /// initState called to initalize data that depends on a build,
  /// once the widget is inserted inside the widgettree. In this case the
  /// widget is the list of detailed_post
  ///

  @override
  Widget build(BuildContext context) {
    String newComment = "";
    return Form(
        key: _formKey,
        child: Center(
          child: Container(
            width: 800,
            height: 783,
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
                child: FutureBuilder<DetailedPost>(
                  future: _futureDetailedPostFormat,
                  builder: (BuildContext context,
                      AsyncSnapshot<DetailedPost> snapshot) {
                    if (snapshot.hasData) {
                      return Column(
                        mainAxisSize: MainAxisSize.min,
                        crossAxisAlignment: CrossAxisAlignment.center,
                        children: [
                          Padding(
                            padding: const EdgeInsets.all(8.0),
                            child: Text(
                              'Poster Username: ${snapshot.data!.mPosterUsername}',
                              style: const TextStyle(
                                  fontSize: 20,
                                  color: Colors.white,
                                  fontFamily: 'roboto'),
                            ),
                          ),
                          Padding(
                            padding: const EdgeInsets.all(8.0),
                            child: Text(
                              'Post: ${snapshot.data!.mContent}',
                              style: const TextStyle(
                                  fontSize: 20,
                                  color: Colors.white,
                                  fontFamily: 'roboto'),
                            ),
                          ),
                          Padding(
                            padding: const EdgeInsets.all(8.0),
                            child: Text(
                              'Likes: ${snapshot.data!.mLikeCount}',
                              style: const TextStyle(
                                  fontSize: 20,
                                  color: Colors.white,
                                  fontFamily: 'roboto'),
                            ),
                          ),
                          // Create a widget for each comment using ListView.builder
                          Padding(
                            padding: const EdgeInsets.all(8.0),
                            ///builds a list dependongon how many comment objects there are
                            child: ListView.builder(
                              shrinkWrap: true,
                              scrollDirection: Axis.vertical,
                              itemCount: snapshot.data!.mComments.length,
                              itemBuilder: (BuildContext context, int index) {
                                return Padding(
                                  padding: const EdgeInsets.all(8.0),
                                  child: Text(
                                    'Comment ${index + 1}: ${snapshot.data!.mComments[index].mContent}',
                                    style: const TextStyle(
                                        fontSize: 20,
                                        color: Colors.white,
                                        fontFamily: 'roboto'),
                                  ),
                                );
                              },
                            ),
                          ),
                          Padding(
                            padding: const EdgeInsets.all(8.0),
                            /// make this form editable because we can add comments
                            child: TextFormField(
                              
                              style: const TextStyle(
                                  fontSize: 20,
                              
                                  color: Colors.white,
                                  fontFamily: 'roboto'),
                              decoration: const InputDecoration(
                                hintText: 'Comment...',
                                labelText: 'Comment...',
                                labelStyle: TextStyle(
                                    color: Colors.white,
                                    fontFamily: 'roboto',
                                    fontSize: 20),
                                hintStyle: TextStyle(
                                    backgroundColor: Colors.grey,
                                    color: Colors.white,
                                    fontFamily: 'roboto',
                                    fontSize: 20),
                              ),
                              onSaved: (String? value) {
                                // Save the data when the form is saved
                                if (value != null) {
                                  newComment = value;
                                } else {
                                  newComment = "";
                                }
                                // The key parameter should be unique for each TextFormField
                                print('new Comment: $value');
                              },
                            ),
                          ),
                          ElevatedButton(
                            style: const ButtonStyle(
                              backgroundColor:
                                  MaterialStatePropertyAll(Colors.green),
                            ),
                            onPressed: () {
                              // Validate and save the form
                              if (_formKey.currentState!.validate()) {
                                _formKey.currentState!.save();
                                
                                print('Form saved!');
                                /// posts comment using sessionkeyand the text entered and ideaId
                                postComment(newComment, widget.sessionKey,
                                    snapshot.data!.mId);
                                print('updated user profile!');
                              }
                            },
                            child: const Text('Post Comment'),
                          ),
                          
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
        ));
  }
}
