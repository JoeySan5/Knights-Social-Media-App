import 'package:flutter/material.dart';
import 'package:like_button/like_button.dart';
import 'package:knights/net/webRequests.dart';

//This class is the format to display an idea,
    //includes user massage, like counter, like button (increment counter), and dislike button (decrement counter)
  class IdeaFormat extends StatefulWidget{
      final int mId;
      final String mContent;
      final int mLikeCount;
      const IdeaFormat({super.key, required this.mId, required this.mContent, required this.mLikeCount});

      @override
      State<IdeaFormat> createState() => _IdeaFormat();

  }

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
          
          height: 70,
          child:  Column(
            // mainAxisAlignment: MainAxisAlignment.spaceEvenly,
            children: [
               Padding(
                padding: EdgeInsets.all(8.0),
                child:Text(
              widget.mContent,
              style: const TextStyle(color: Colors.white),
              )),
              Row(
                mainAxisAlignment: MainAxisAlignment.spaceEvenly,
                children: [
                  Text(
                  widget.mLikeCount.toString(),
                  style: const TextStyle(color: Colors.white),
                  ),
                  //this is thumbs-up icon
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
                  //this is thumbs-down icon
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