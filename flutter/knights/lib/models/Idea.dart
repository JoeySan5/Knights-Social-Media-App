///This is the model class for an Idea.
///
///Idea's must contain mID, mContent, and mLikeCount.
///Must matchup with JSOn returned from backend
class Idea {
  final int mId;
  final String mContent;
  final String? mLink;
  final int mLikeCount;
  final String mPosterUsername;
  final String mUserId;
  // final String? mFilePath; // New field for file path

  const Idea(
      {required this.mId,
      required this.mContent,
      this.mLink,
      required this.mLikeCount,
      required this.mPosterUsername,
      required this.mUserId
      // this.mFilePath, // Initialize the new field
      });

  ///This is a factory constructor that creates an idea instance from json objects.
  factory Idea.fromJson(Map<String, dynamic> json) {
    return Idea(
        mId: json['mId'],
        mContent: json['mContent'],
        mLink: json['mLink'],
        mLikeCount: json['mLikeCount'],
        mPosterUsername: json['mPosterUsername'],
        mUserId: json['mUserId'],
        // mFilePath: json['mFilePath'], // Extract from JSON
    );
  }

  ///Converts idea object into json object (map).
  Map<String, dynamic> toJson() => {
        'mId': mId,
        'mContent': mContent,
        'mLikeCount': mLikeCount,
        'mPosterUsername': mPosterUsername,
        'mUserId': mUserId,
        'mLink': mLink
        // 'mFilePath': mFilePath, // Convert to JSON
      };
}
