

/// model class for a poster user
/// 
/// User must contain mUserId, username, email, note
class Poster{
  final String mId;
  final String mUsername;
  final String mEmail;
  final String mNote;

Poster({required this.mId, required this.mUsername, required this.mEmail, required this.mNote});

factory Poster.fromJson(Map<String, dynamic> json){
  return Poster(
    mId: json['mId'],
    mUsername: json['mUsername'],
    mEmail: json['mEmail'],
    mNote: json['mNote'],
  );
}

///Converts user object into jsopn object (map)
Map<String, dynamic> toJson() =>{
  'mId': mId,
  'mUsername': mUsername,
  'mEmail': mEmail,
  'mNote': mNote,
};

}