import 'dart:convert';
import 'dart:io';
import 'dart:typed_data';

import 'package:file_picker/file_picker.dart';
import 'package:flutter/material.dart';
import 'package:knights/models/DetailedPost.dart';
import 'package:knights/net/web_requests.dart';
// import 'package:url_launcher/url_launcher.dart';

// import 'package:knights/models/User.dart';
// import 'package:knights/net/web_requests.dart';
// import 'package:knights/pages/home_page.dart';
// import 'package:knights/components/idea_format.dart';
import 'package:url_launcher/url_launcher_string.dart';

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

  Future<void> _launchUrl(String url) async {
    if (await canLaunchUrlString(url)) {
      await launchUrlString(url);
    } else {
      ScaffoldMessenger.of(context).showSnackBar(
        const SnackBar(content: Text('Cannot open the link')),
      );
    }
  }

  @override
  void retry() {
    super.initState();
    _futureDetailedPostFormat =
        fetchDetailedPost(widget.mId, widget.sessionKey);
  }

  File? _selectedFile;
  Future<void> pickFile() async {
    FilePickerResult? result = await FilePicker.platform.pickFiles();

    if (result != null) {
      setState(() {
        _selectedFile = File(result.files.single.path!);
        String fileName = result.files.first.name;
        print('selected fileName: $fileName');
        Uint8List? fileBytes = result.files.first.bytes;
        String base64 = base64Encode(fileBytes!);
        print('base64 file bytes: $base64');
      });
    } else {
      // User canceled the picker
      ScaffoldMessenger.of(context).showSnackBar(
        const SnackBar(content: Text('No file selected')),
      );
    }
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
    String link = "";
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
                            child: snapshot.data!.mLink != null &&
                                    snapshot.data!.mLink!.isNotEmpty
                                ? InkWell(
                                    child: Text(
                                      'Open Link: ${snapshot.data!.mLink}',
                                      style: TextStyle(
                                        color: Colors.blue,
                                        decoration: TextDecoration.underline,
                                      ),
                                    ),
                                    onTap: () =>
                                        _launchUrl(snapshot.data!.mLink!),
                                    //onTap: () => launchUrlString(snapshot.data!.mLink!),
                                  )
                                : Container(), // Empty container when there's no link
                            // Text(
                            //   'Poster Username: ${snapshot.data!.mPosterUsername}',
                            //   style: const TextStyle(
                            //       fontSize: 20,
                            //       color: Colors.white,
                            //       fontFamily: 'roboto'),
                            // ),
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

                          // add a new comment
                          Padding(
                            padding: const EdgeInsets.all(8.0),

                            /// make this form editable because we can add comments
                            child: TextFormField(
                              maxLines: 3,
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

                          // links in comments
                          Padding(
                              padding: const EdgeInsets.all(8.0),
                              child: TextFormField(
                                maxLines: 2, // reduced max lines
                                decoration: const InputDecoration(
                                    border: OutlineInputBorder(),
                                    hintText: 'Please write your link here...',
                                    labelText: 'Please write your link here...',
                                    labelStyle: TextStyle(
                                        color: Colors.white,
                                        fontFamily: 'roboto',
                                        fontSize: 20),
                                    hintStyle: TextStyle(color: Colors.white),
                                    contentPadding: EdgeInsets.all(10)),
                                onSaved: (String? value) {
                                  // Save the data when the form is saved
                                  if (value != null) {
                                    link = value;
                                  } else {
                                    link = "";
                                  }
                                  // The key parameter should be unique for each TextFormField
                                  print('content: $value');
                                },
                                style: const TextStyle(color: Colors.white),
                              )),

                          // Attach file button
                          ElevatedButton(
                            onPressed: pickFile,
                            child: const Text('Attach File'),
                          ),

                          // Optional: Display the selected file name
                          if (_selectedFile != null)
                            Padding(
                              padding: const EdgeInsets.all(8.0),
                              child: Text(
                                  "Selected File: ${_selectedFile!.path.split('/').last}"),
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
                            child: const Text('Add Comment'),
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
