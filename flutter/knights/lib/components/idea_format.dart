import 'package:flutter/material.dart';

import 'package:like_button/like_button.dart';
import 'package:knights/net/web_requests.dart';
import 'package:knights/pages/detailed_post_page.dart';
import 'package:knights/pages/poster_user_profile_page.dart';

///This class is the format to display an idea post.
///
///Component includes user message, like counter, like button (increment counter), and dislike button (decrement counter).
class IdeaFormat extends StatefulWidget {
  final int mId;
  final String mContent;
  final int mLikeCount;
  final String sessionKey;
  final String mPosterUsername;
  final String mUserId;
  const IdeaFormat(
      {super.key,
      required this.sessionKey,
      required this.mId,
      required this.mContent,
      required this.mLikeCount,
      required this.mPosterUsername,
      required this.mUserId});

  @override
  State<IdeaFormat> createState() => _IdeaFormat();
}

//Set state to increment.

class _IdeaFormat extends State<IdeaFormat> {
  ///This is to keep track of the like count
  int likeCount = 0;

  @override
  void initState() {
    super.initState();

    ///likecount is first initialized based on the respective
    ///idea mLikeCount
    likeCount = widget.mLikeCount;
  }

  @override
  Widget build(BuildContext context) {
    return Container(
        margin: const EdgeInsets.all(15.0),
        //decoration is to make borders look different
        decoration: BoxDecoration(
          border: Border.all(
            color: Colors.green,
            width: 1.5,
          ),
          borderRadius: const BorderRadius.all(Radius.circular(10)),
        ),
        child: Column(
          children: [
            GestureDetector(
              onTap: () {
                print('Displaying posters profile');
                Navigator.push(
                  context,
                  MaterialPageRoute(
                      builder: (context) => PosterProfilePage(
                          userId: widget.mUserId,
                          sessionKey: widget.sessionKey)),
                );
              },
              child: Text(
                widget.mPosterUsername,
                style: const TextStyle(color: Colors.white),
              ),
            ),
            GestureDetector(
                onTap: () {
                  print('Displaying detailed post view');
                  Navigator.push(
                    context,
                    MaterialPageRoute(
                        builder: (context) => DetailedPostPage(
                            mId: widget.mId, sessionKey: widget.sessionKey)),
                  );
                },
                child: Text(
                  widget.mContent,
                  style: const TextStyle(color: Colors.white),
                )),

            ///Sets up the like counter.
            Row(
              mainAxisAlignment: MainAxisAlignment.spaceEvenly,
              children: [
                Text(
                  likeCount.toString(),
                  style: const TextStyle(color: Colors.white),
                ),

                ///Sets up the like button.
                LikeButton(
                  //likeCount: widget.mLikeCount,
                  onTap: (isLiked) {
                    setState(() {
                      likeCount++;
                    });
                    return onLikeButtonTapped(widget.mId, widget.sessionKey);
                  },
                  likeBuilder: (bool isLiked) {
                    return const Icon(Icons.thumb_up, color: Colors.white);
                  },
                ),

                ///Sets up the dislike button.
                LikeButton(
                  onTap: (isLiked) {
                    setState(() {
                      likeCount--;
                    });
                    return onDislikeButtonTapped(widget.mId, widget.sessionKey);
                  },
                  likeBuilder: (bool isLiked) {
                    return const Icon(Icons.thumb_down, color: Colors.white);
                  },
                ),
              ],
            )
          ],
        ));
  }
}
