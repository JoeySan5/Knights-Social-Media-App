///This is the model class for an Idea.
///
///Idea's must contain mID, mContent, and mLikeCount.
class Idea{
  final int mId;
  final String mContent;
  final int mLikeCount;

  const Idea({
    required this.mId,
    required this.mContent,
    required this.mLikeCount,
  });

  ///This is a factory constructor that creates an idea instance from json objects.
  factory Idea.fromJson(Map<String, dynamic> json){
    return Idea(
      mId: json['mId'],
      mContent: json['mContent'],
      mLikeCount: json['mLikeCount']
    );
  }

  ///Converts idea object into json object (map).
  Map<String, dynamic> toJson() =>{
    'mId' : mId,
    'mContent' : mContent,
    'mLikeCount': mLikeCount
  };



}