import 'package:http/http.dart' as http;
import 'package:knights/models/Comments.dart';
import 'dart:convert';
import 'dart:developer' as developer;

import 'package:knights/models/Idea.dart';
import 'package:knights/models/User.dart';
import 'package:knights/models/DetailedPost.dart';
import 'package:knights/models/Poster.dart';

// web function to get user profile data
// View User
// /users/:id
// GET
// View a User’s Profile Page using sessionKey
Future<User> fetchUsers(String sessionKey) async {
  developer.log('Making web request for user data...');
  var url = Uri.parse(
      'https://team-knights.dokku.cse.lehigh.edu/users?sessionKey=$sessionKey');
  var headers = {"Accept": "application/json"};
  // garbage user that gets returned if sopmething goes wrong
  User garb = User(
      mId: "",
      mUsername: 'garbage',
      mEmail: 'nonExistent',
      mNote: 'garbage',
      mGI: '',
      mSO: '');

  var response = await http.get(url, headers: headers);

  if (response.statusCode == 200) {
    final User returnData;

    var res = jsonDecode(response.body);
    developer.log('json decode: $res');
    developer.log('resmdata: ${res['mData']}');
    if (res['mData'] is Map) {
      /// user data
      returnData = User.fromJson(res['mData'] as Map<String, dynamic>);
    } else {
      developer.log(
          'ERROR: Unexpected json response type (was not user). Using garb');
      returnData = garb;
    }

    developer.log('$returnData');
    return returnData;
  } else {
    throw Exception('Did not receive success status code from request.');
  }
}

/// FetchPoster which gets another users account using their userId and current user sessionKey
/// does not returns  GI and SO
/// GET
/// very similar to fetchUser above
Future<Poster> fetchPoster(String id, String sessionKey) async {
  developer.log('Making web request for user data...');
  var url = Uri.parse(
      'https://team-knights.dokku.cse.lehigh.edu/users/$id?sessionKey=$sessionKey');
  var headers = {"Accept": "application/json"};
  // garbage user that gets returned if something goes wrong
  Poster garb = Poster(
      mId: "", mUsername: 'garbage', mEmail: 'nonExistent', mNote: 'garbage');

  var response = await http.get(url, headers: headers);

  if (response.statusCode == 200) {
    final Poster returnData;

    var res = jsonDecode(response.body);
    developer.log('json decode: $res');
    developer.log('resmdata: ${res['mData']}');
    if (res['mData'] is Map) {
      // otheruser data
      returnData = Poster.fromJson(res['mData'] as Map<String, dynamic>);
    } else {
      developer.log(
          'ERROR: Unexpected json response type (was not user). Using garb');
      returnData = garb;
    }

    developer.log('$returnData');
    return returnData;
  } else {
    throw Exception('Did not receive success status code from request.');
  }
}

/// Edit current user profile data
/// PUT
/// takes in sessionKey, and other parameters that may or may not have beenupdated
/// doesnot return anything but database is updated after this is sucessful
Future updateUserProfile(String sessionKey, String mUsername, String mEmail,
    String mSO, String mGI, String mNote) async {
  developer.log('Making web request...');
  var url = Uri.parse('https://team-knights.dokku.cse.lehigh.edu/users');
  var headers = {"Accept": "application/json"};
  var body = {
    'sessionKey': sessionKey,
    'mUsername': mUsername,
    'mEmail': mEmail,
    'mGI': mGI,
    'mSO': mSO,
    'mNote': mNote
  };

  /// put request for updating user
  var response = await http.put(url, headers: headers, body: jsonEncode(body));
  print('updating user profile: ${response.body}');
  if (response.statusCode == 200) {
    developer.log('response headers: ${response.headers}');
    developer.log('response body: ${response.body}');
  } else {
    ///If the server did not return a 200 OK response,
    ///then throw an exception.
    throw Exception('Did not receive success status(200) code from request.');
  }
}

/// request for detailed view of post
/// GET
/// returns a DetailedPost which has likes, comments, id, posterUserName, and PosterId
/// comments are list of its own kind and its ownobject (see under models/Comments)
///
Future<DetailedPost> fetchDetailedPost(int mId, String sessionKey) async {
  developer.log('Making web request for detailed post data...');
  var url = Uri.parse(
      'https://team-knights.dokku.cse.lehigh.edu/ideas/$mId?sessionKey=$sessionKey');
  var headers = {"Accept": "application/json"};
  // garbage DetailedPost that gets returned if something goes wrong
  List<Comments> garb = [];

  var response = await http.get(url, headers: headers);
  print(response.body);

  /// upon sucess, returnData gets populated withproper data
  if (response.statusCode == 200) {
    DetailedPost returnData;
    var res = jsonDecode(response.body);
    print('json decode: $res');
    print('resmdata: ${res['mData']}');
    if (res['mData'] is Map) {
      print('returning detailed post');
      print(res['mData']);

      /// making mData equal to the response so that list of comments can be picked out
      Map<String, dynamic> mData = res['mData'];

      /// storing comments
      List<Comments> comments = [];
      // returns true
      if (mData.containsKey('mComments') && mData['mComments'] is List) {
        // Process each comment in the 'mComments' list
        print('made it here');
        List<dynamic> mCommentsList = mData['mComments'];
        print(mCommentsList);

        /// loops through each comment in comments and adds to comments[]
        for (dynamic commentData in mCommentsList) {
          if (commentData is Map<String, dynamic>) {
            try {
              Comments comment = Comments.fromJson(commentData);
              comments.add(comment);
            } catch (e) {
              print('Error processing comment: $e');
            }
          }
        }

        /// adds other information first then adds comments and returns the detailed post
        returnData =
            DetailedPost.fromJson(res['mData'] as Map<String, dynamic>);
        returnData.mComments = comments;
        print(returnData);
        return returnData;
      }

      returnData = DetailedPost.fromJson(mData);
    } else {
      developer.log(
          'ERROR: Unexpected json response type (was not ideas/id). Check web_requests and that proper data is passed.');

      /// garbage detailed post that is returned if does not work
      returnData = DetailedPost(
          mId: -1,
          mContent: "error",
          mLikeCount: 0,
          mUserId: "notValid123",
          mPosterUsername: "DNE123",
          mComments: garb);
    }

    developer.log('$returnData');
    print(returnData);
    return returnData;
  } else {
    throw Exception('Did not receive success status code from request.');
  }
}

/// post a comment method
/// POST
/// uses the content, sessionKey, and ideaId
Future postComment(String mContent, String sessionKey, int mIdeaId) async {
  developer.log('making web request...');
  var url = Uri.parse('https://team-knights.dokku.cse.lehigh.edu/comments');
  var headers = {"Accept": "application/json"};
  var body = {
    'sessionKey': sessionKey,
    'mContent': mContent,
    'mIdeaId': mIdeaId
  };
  var response = await http.post(url, headers: headers, body: jsonEncode(body));

  if (response.statusCode == 200) {
    print("sucessful comment posted");
  } else {
    print('error posting comment');
  }
}

///Web Function to send put request to respective idea ID, and *decrement* mLikeCount
///PUT
///backend  handles logic
Future<bool> onDislikeButtonTapped(int id, String sessionKey) async {
  ///Can safely ignore isLiked
  bool isLiked = true;

  developer.log('Making web request...');
  var url =
      Uri.parse('https://team-knights.dokku.cse.lehigh.edu/ideas/$id/likes');
  var headers = {"Accept": "application/json"};
  var body = {'sessionKey': sessionKey, 'value': -1};

  developer.log('json encode body: $sessionKey');

  var response = await http.put(url, headers: headers, body: jsonEncode(body));

  return !isLiked;
}

///Web Function to send put request to respective idea ID, and *increment* mLikeCount
///PUT
///backend handles logic
Future<bool> onLikeButtonTapped(int id, String sessionKey) async {
  bool isLiked = true;

  developer.log('Making web request...');
  var url =
      Uri.parse('https://team-knights.dokku.cse.lehigh.edu/ideas/$id/likes');
  var headers = {"Accept": "application/json"};
  var body = {'sessionKey': sessionKey, 'value': 1};

  var response = await http.put(url, headers: headers, body: jsonEncode(body));
  print(response.body);
  developer.log(response.body);

  return !isLiked;
}

///Web Function to send post request to dokku backend. Creates idea with userText
///POST
///takes in userText and sessionKey to determine which user is making post
Future<String> postIdeas(
    String userText, String link, String sessionKey, String fileName, String base64) async {
  developer.log('Making web request...');
  var url = Uri.parse('https://team-knights.dokku.cse.lehigh.edu/ideas');
  var headers = {"Accept": "application/json"};
  var mFile = {'mFileName': fileName, 'mFileType': fileName.split(".").last, 'mBase64': base64};
  var body = {'mContent': userText, 'mLink': link, 'sessionKey': sessionKey, 'mFile': mFile};
  print(sessionKey);
  print(userText);
  print(link);
  var response = await http.post(url, headers: headers, body: jsonEncode(body));
  print(response.body);
  if (response.statusCode == 200) {
    developer.log('response headers: ${response.headers}');
    developer.log('response body: ${response.body}');

    return userText;
  } else {
    ///If the server did not return a 200 OK response,
    ///then throw an exception.
    throw Exception('Did not receive success status(200) code from request.');
  }
}

///This web function fetches json data from dokku, and then
///parses each json object into an idea object(model)
/// GET
/// returns all ideas assuming sessionKey is valid
Future<List<Idea>> fetchIdeas(String sessionKey) async {
  developer.log('Making web request...');
  var url = Uri.parse(
      'https://team-knights.dokku.cse.lehigh.edu/ideas?sessionKey=$sessionKey');
  var headers = {"Accept": "application/json"};

  var response = await http.get(url, headers: headers);

  if (response.statusCode == 200) {
    final List<Idea> returnData;

    var res = jsonDecode(response.body);
    developer.log('json decode: $res');
    developer.log('resmdata: ${res['mData']}');

    if (res['mData'] is List) {
      //dynamic allows for a types to be inferred during runtime, and can be changed to different types
      returnData =
          (res['mData'] as List<dynamic>).map((x) => Idea.fromJson(x)).toList();
    } else if (res is Map) {
      returnData = <Idea>[Idea.fromJson(res['mData'] as Map<String, dynamic>)];
    } else {
      developer
          .log('ERROR: Unexpected json response type (was not a List or Map).');
      returnData = List.empty();
    }
    developer.log('$returnData');
    return returnData;
  } else {
    ///If the server did not return a 200 OK response, then throw an exception.
    throw Exception('Did not receive success status code from request.');
  }
}

///This web function is similar to fetchIdeas, except it is modified to
///accept a http.Client as a parameter. This is done so we can test the web function
///with a mock http request as done in message_page_test.dart
///GET
Future<List<Idea>> fetchIdeasTest(http.Client client) async {
  developer.log('Making web request...');
  var url = Uri.parse('https://team-knights.dokku.cse.lehigh.edu/ideas');
  var headers = {"Accept": "application/json"};

  var response = await client.get(url, headers: headers);

  if (response.statusCode == 200) {
    final List<Idea> returnData;

    var res = jsonDecode(response.body);
    developer.log('json decode: $res');
    developer.log('resmdata: ${res['mData']}');

    if (res['mData'] is List) {
      //dynamic allows for a types to be inferred during runtime, and can be changed to different types
      returnData =
          (res['mData'] as List<dynamic>).map((x) => Idea.fromJson(x)).toList();
    } else if (res is Map) {
      returnData = <Idea>[Idea.fromJson(res['mData'] as Map<String, dynamic>)];
    } else {
      developer
          .log('ERROR: Unexpected json response type (was not a List or Map).');
      returnData = List.empty();
    }

    developer.log('$returnData');
    return returnData;
  } else {
    ///If the server did not return a 200 OK response,
    throw Exception('Did not receive success status code from request.');
  }
}
