/// model class for detailed  IDea
/// 
/// ideas must contain mID, mContent, mLikeCount, mUserId, mPosterName, mComments
import 'package:knights/models/Comments.dart';
class DetailedPost{
  final int mId;
  final String mContent;
  final int mLikeCount;
  final String mUserId;
  final String mPosterUsername;
  /// comments object
  //final Comments mComments;
  DetailedPost({required this.mId, required this.mContent, required this.mLikeCount, required this.mUserId, required this.mPosterUsername});


factory DetailedPost.fromJson(Map<String, dynamic> json){
  return DetailedPost(
    mId: json['mId'],
    mContent: json['mContent'],
    mLikeCount: json['mLikeCount'],
    mUserId: json['mUserId'],
    mPosterUsername: json['mPosterUsername'],
    //mComments: json['mComments']
  );
}

Map<String, dynamic>toJson() =>{
  'mId': mId,
  'mContent': mContent,
  'mLikeCount': mLikeCount,
  'mUserId': mUserId,
  'mPosterUserName': mPosterUsername,
  //'mComments': mComments
};
}