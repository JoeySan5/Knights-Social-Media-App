/// Model class for Comments
/// 
/// Comments must contain content, commentId, userId, commenterUsername

class Comments{
  String mContent;
  int mCommentId;
  String mUserId;
  String mCommenterUsername;

  Comments({
    required this.mCommentId,
    required this.mContent,
    required this.mUserId,
    required this.mCommenterUsername
  });

  factory Comments.fromJson(Map<String, dynamic>json){
    return Comments(
      mCommentId: json['mCommentId'],
      mContent: json['mContent'],
      mUserId: json['mUserId'],
      mCommenterUsername: json['mCommenterUsername']
    );
  }

  Map<String, dynamic> toJson()=>{
    'mCommentId': mCommentId,
    'mContent': mContent,
    'mUserId': mUserId,
    'mCommenterUsername': mCommenterUsername
  };

}