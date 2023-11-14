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

class DetailedPostFormat extends StatefulWidget{
  /// postId
  final int mId;
  final String sessionKey;
  const DetailedPostFormat({super.key, required this.mId, required this.sessionKey});

  @override
  State<DetailedPostFormat> createState() => _DetailedPostFormat();
}

class _DetailedPostFormat extends State<DetailedPostFormat>{
  late Future<DetailedPost> _futureDetailedPostFormat;

    /// now we want to read in data from dokku using get and parse json into detailed_post objects
  ///
  /// initState called to initalize data that depends on a build,
  /// once the widget is inserted inside the widgettree. In this case the
  /// widget is the list of detailed_post
  @override
  void initState(){
    super.initState();
    _futureDetailedPostFormat = fetchDetailedPost(widget.mId, widget.sessionKey);
  }

    @override
  void retry(){
    super.initState();
    _futureDetailedPostFormat = fetchDetailedPost(widget.mId, widget.sessionKey);
  }

  @override
  Widget build(BuildContext context){
    return Center(
      child: Container(
        width: 800,
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
            child: FutureBuilder<DetailedPost>(
              future: _futureDetailedPostFormat,
              builder: (BuildContext context, AsyncSnapshot<DetailedPost> snapshot) {
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
      )
    );
  }




//   @override
// Widget build(BuildContext context) {
//     var fb = FutureBuilder<DetailedPost>(
//       future: _futureDetailedPostFormat,
//       builder: (BuildContext context, AsyncSnapshot<DetailedPost> snapshot) {
//         Widget child;


        
//         if (snapshot.hasData) {
//           print(snapshot.data!.mUserId);
//           print(snapshot.data!.mContent);
//           return Container(
//               margin: const EdgeInsets.all(15.0),
//               //decoration is to make borders look different
//               decoration: BoxDecoration(
//                 border: Border.all(
//                   color: Colors.green,
//                   width: 1.5,
//                 ),
//                 borderRadius: const BorderRadius.all(Radius.circular(10)),
//               ),
//               child: Row(
//                 children: [
//                   ///Sets up the user message.
//                   Padding(
//                       padding: const EdgeInsets.all(8.0),
//                       child: Text(
//                         'Post: ${snapshot.data!.mContent}',
//                         style: const TextStyle(fontSize: 40, color: Colors.white, fontFamily: 'roboto'),
//                       )),
//                   Padding(
//                       padding: const EdgeInsets.all(8.0),
//                       child: Text(
//                         'Post Id: ${snapshot.data!.mId}',
//                         style: const TextStyle(fontSize: 40, color: Colors.white, fontFamily: 'roboto'),
//                       )),
//                   Padding(
//                       padding: const EdgeInsets.all(8.0),
//                       child: Text(
//                         'Likes: ${snapshot.data!.mLikeCount}',
//                         style: const TextStyle(fontSize: 40, color: Colors.white, fontFamily: 'roboto'),
//                       )),
//                   Padding(
//                       padding: const EdgeInsets.all(8.0),
//                       child: Text(
//                         'Poster Username: ${snapshot.data!.mPosterUsername}',
//                         style: const TextStyle(fontSize: 40, color: Colors.white, fontFamily: 'roboto'),
//                       )),
//                 ],
//               ));
//         } else if (snapshot.hasError) {
//           child = Text('${snapshot.error}');
//         } else {
//           ///This awaits snapshot data, displaying a loading spinner
//           child = const CircularProgressIndicator();
//         }
        
//         return child;
//       },
      
//     );
//     return fb;
  
//   } // end of widget
}