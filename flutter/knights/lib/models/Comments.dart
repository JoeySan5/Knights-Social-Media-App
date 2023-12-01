/// Model class for Comments
///
/// Comments must contain content, commentId, userId, commenterUsername, ideaId

class Comments {
  String mContent;
  int mId;
  String mUserId;
  String mCommenterUsername;
  int mIdeaId;
  String? mLink;

  Comments(
      {required this.mId,
      required this.mContent,
      required this.mUserId,
      required this.mCommenterUsername,
      required this.mIdeaId,
      this.mLink,
      });

  ///This is a factory constructor that creates an Comment instance from json objects.
  factory Comments.fromJson(Map<String, dynamic> json) {
    return Comments(
        mId: json['mId'],
        mContent: json['mContent'],
        mUserId: json['mUserId'],
        mCommenterUsername: json['mCommenterUsername'],
        mIdeaId: json['mIdeaId'],
        mLink: json['mLink'],);
  }

  ///Converts idea object into json object (map).
  Map<String, dynamic> toJson() => {
        'mId': mId,
        'mContent': mContent,
        'mUserId': mUserId,
        'mCommenterUsername': mCommenterUsername,
        'mIdeaId': mIdeaId,
        'mLink': mLink,
      };
}
