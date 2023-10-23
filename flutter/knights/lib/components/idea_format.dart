import 'package:flutter/material.dart';

import 'package:like_button/like_button.dart';
import 'package:knights/net/web_requests.dart';

  ///This class is the format to display an idea post.
  ///
  ///Component includes user message, like counter, like button (increment counter), and dislike button (decrement counter).
  class IdeaFormat extends StatefulWidget{
      final int mId;
      final String mContent;
      final int mLikeCount;
      const IdeaFormat({super.key, required this.mId, required this.mContent, required this.mLikeCount});

      @override
      State<IdeaFormat> createState() => _IdeaFormat();

  }

  //Set state to increment.

  class _IdeaFormat extends State<IdeaFormat> {
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
              borderRadius: const BorderRadius.all(
                Radius.circular(10)
              ),
            ),
          
          child:  Column(
            children: [
              ///Sets up the user message.
              Padding(
                padding: const EdgeInsets.all(8.0),
                child:Text(
                  widget.mContent,
                  style: const TextStyle(color: Colors.white),
              
                )
              ),
              ///Sets up the like counter.
              Row(
                mainAxisAlignment: MainAxisAlignment.spaceEvenly,
                children: [
                  Text(
                  widget.mLikeCount.toString(),
                  style: const TextStyle(color: Colors.white),
                  ),
                  ///Sets up the like button.
                  LikeButton(
                    //likeCount: widget.mLikeCount,
                    onTap: (isLiked) {
                      return onLikeButtonTapped(widget.mId);
                    },
                    likeBuilder:(bool isLiked){
                      return const Icon(
                          Icons.thumb_up,
                          color: Colors.white
                      );
                    },
                  ),
                  ///Sets up the dislike button.
                  LikeButton(
                    onTap: (isLiked) {
                      return onDislikeButtonTapped(widget.mId);
                    },
                    likeBuilder:(bool isLiked){
                      return const Icon(
                          Icons.thumb_down,
                          color: Colors.white
                      );
                    },
                  ),
              
                ],
              )
            ],
          )
        );

      }
  }