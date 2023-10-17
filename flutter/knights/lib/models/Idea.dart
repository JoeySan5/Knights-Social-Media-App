class Idea{
  final int mId;
  final String mContent;
  final int mLikeCount;

  const Idea({
    required this.mId,
    required this.mContent,
    required this.mLikeCount,
  });

  //this is a factory constructor that creates an idea instance
  // from json
  factory Idea.fromJson(Map<String, dynamic> json){
    return Idea(
      mId: json['mData']['mId'],
      mContent: json['mData']['mContent'],
      mLikeCount: json['mData']['mLikeCount']
    );
  }

  //converts idea object into json object (map)
  Map<String, dynamic> toJson() =>{
    'mId' : mId,
    'mContent' : mContent,
    'mLikeCount': mLikeCount
  };



}