/// model class for detailed  IDea
/// 
/// ideas must contain mID, mContent, mLikeCount, mUserId, mPosterName, mComments
/// must match up with what is returned from backend
import 'package:knights/models/Comments.dart';
class DetailedPost{
  final int mId;
  final String mContent;
  final int mLikeCount;
  final String mUserId;
  final String mPosterUsername;
  List<Comments> mComments;
  DetailedPost({required this.mId, required this.mContent, required this.mLikeCount, required this.mUserId, required this.mPosterUsername, required this.mComments});

/// determines what is assigned to variables upon sucessful JSOn request
factory DetailedPost.fromJson(Map<String, dynamic> json){
  return DetailedPost(
    mId: json['mId'],
    mContent: json['mContent'],
    mLikeCount: json['mLikeCount'],
    mUserId: json['mUserId'],
    mPosterUsername: json['mPosterUsername'],
    mComments: (json['mComments'] as List<dynamic>)
          .map((commentJson) => Comments.fromJson(commentJson))
          .toList(),
  );
}
/// maps values to JSON to be sent over to backend
Map<String, dynamic>toJson() =>{
  'mId': mId,
  'mContent': mContent,
  'mLikeCount': mLikeCount,
  'mUserId': mUserId,
  'mPosterUserName': mPosterUsername,
  'mComments': mComments
};

}