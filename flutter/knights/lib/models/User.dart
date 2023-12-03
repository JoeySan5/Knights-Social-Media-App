

/// model class for a User
/// 
/// User must contain mUserId, username, email, note, GI, SO
/// Must matchup with JSOn coming in from backend
class User{
  final String mId;
  final String mUsername;
  final String mEmail;
  final String mNote;
  final String mGI;
  final String mSO;

User({required this.mId, required this.mUsername, required this.mEmail, required this.mNote, required this.mGI, required this.mSO});

// what is assigned from the JSOn returned from backend
factory User.fromJson(Map<String, dynamic> json){
  return User(
    mId: json['mId'],
    mUsername: json['mUsername'],
    mEmail: json['mEmail'],
    mNote: json['mNote'],
    mGI: json['mGI'],
    mSO: json['mSO']
  );
}

///Converts user object into jsopn object (map)
Map<String, dynamic> toJson() =>{
  'mId': mId,
  'mUsername': mUsername,
  'mEmail': mEmail,
  'mNote': mNote,
  'mGI': mGI,
  'mSO': mSO
};

}