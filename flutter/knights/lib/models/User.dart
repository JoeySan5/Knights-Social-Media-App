

/// model class for a User
/// 
/// User must contain mUserId, username, email, note, GI, SO
class User{
  final String mId;
  final String mUsername;
  final String mEmail;
  final String mNote;
  String GI = "";
  String SO = "";

User({required this.mId, required this.mUsername, required this.mEmail, required this.mNote});

factory User.fromJson(Map<String, dynamic> json){
  return User(
    mId: json['mId'],
    mUsername: json['mUsername'],
    mEmail: json['mEmail'],
    mNote: json['mNote'],
    //GI: json['GI'],
    //SO: json['SO']
  );
}

///Converts user object into jsopn object (map)
Map<String, dynamic> toJson() =>{
  'mId': mId,
  'mUsername': mUsername,
  'mEmail': mEmail,
  'mNote': mNote,
  //'GI': GI,
  //'SO': SO
};

}