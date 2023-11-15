/// model class for detailed  IDea
/// 
/// ideas must contain mID, mContent, mLikeCount, mUserId, mPosterName, mComments
class DetailedPost{
  final int mId;
  final String mContent;
  final int mLikeCount;
  final String mUserId;
  final String mPosterUserName;
  // TODO: add comments model
//  final String mCommentsX
  DetailedPost({required this.mId, required this.mContent, required this.mLikeCount, required this.mUserId, required this.mPosterUserName});


factory DetailedPost.fromJson(Map<String, dynamic> json){
  return DetailedPost(
    mId: json['mId'],
    mContent: json['mContent'],
    mLikeCount: json['mLikeCount'],
    mUserId: json['mUserId'],
    mPosterUserName: json['mPosterUserName']
  );
}

Map<String, dynamic>toJson() =>{
  'mId': mId,
  'mContent': mContent,
  'mLikeCount': mLikeCount,
  'mUserId': mUserId,
  'mPosterUserName': mPosterUserName,
};
}