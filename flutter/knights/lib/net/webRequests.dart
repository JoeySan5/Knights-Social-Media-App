import 'package:http/http.dart' as http;
import 'dart:convert';
import 'dart:developer' as developer;
import 'package:knights/components/IdeaFormat.dart';
import 'package:knights/models/Idea.dart';

Future<bool> onDislikeButtonTapped(int id) async{
  bool isLiked = true;
    /// send your request here
    // final bool success= await sendRequest();

    /// if failed, you can do nothing
    // return success? !isLiked:isLiked;

    developer.log('Making web request...');
    var url = Uri.parse('https://team-knights.dokku.cse.lehigh.edu/ideas/$id');
    var headers = {"Accept": "application/json"};
    var body = {'mLikeIncrement': '-1'};

    var response = await http.put(url, headers: headers, body: jsonEncode(body));

    developer.log('Response status: ${response.statusCode}');
    developer.log('Response headers: ${response.headers}');
    developer.log('Response body: ${response.body}');
    developer.log(await http.read(url));

    // var res = jsonDecode(response.body);
    // developer.log('json decode: $res');

    // var idea = Idea.fromJson(res);

    // developer.log('idea content: ${idea.mContent} ');

    return !isLiked;
  }

    //put to url/ideas/2
    //mLikeIncrement: +1 or -1
    Future<bool> onLikeButtonTapped(int id) async{
        bool isLiked = true;

    /// send your request here
    // final bool success= await sendRequest();

    /// if failed, you can do nothing
    // return success? !isLiked:isLiked;

    developer.log('Making web request...');
    var url = Uri.parse('https://team-knights.dokku.cse.lehigh.edu/ideas/$id');
    var headers = {"Accept": "application/json"};
    var body = {'mLikeIncrement': '1'};

    var response = await http.put(url, headers: headers, body: jsonEncode(body));

    developer.log('Response status: ${response.statusCode}');
    developer.log('Response headers: ${response.headers}');
    developer.log('Response body: ${response.body}');
    developer.log(await http.read(url));

    // var res = jsonDecode(response.body);
    // developer.log('json decode: $res');

    // var idea = Idea.fromJson(res);

    // developer.log('idea content: ${idea.mContent} ');

    return !isLiked;
  }

    //this method fetches the json from dokku, and then 
    //seperates each json object into an Idea(.dart) object
    Future<List<Idea>> fetchIdeas() async{
      developer.log('Making web request...');
      var url = Uri.parse('https://team-knights.dokku.cse.lehigh.edu/ideas');
      var headers = {"Accept": "application/json"};

      var response = await http.get(url, headers: headers);

      

      if (response.statusCode == 200){
        final List<Idea> returnData;
        
        var res = jsonDecode(response.body);
        print('json decode: $res');
        print('resmdata: ${res['mData']}');
        
        if(res['mData'] is List){
          //dynamic allows for a types to be inferred during runtime, and can be changed to different types
          returnData = (res['mData'] as List<dynamic>).map((x) => Idea.fromJson(x)).toList();
        }
        else if(res is Map){
          returnData = <Idea>[Idea.fromJson(res['mData'] as Map<String,dynamic>)];
        }else{
          developer.log('ERROR: Unexpected json response type (was not a List or Map).');
          returnData = List.empty();
        }

        print(returnData);
        return returnData;
      } 
      else{
        // If the server did not return a 200 OK response,
        // then throw an exception.
        throw Exception('Did not receive success status code from request.');
      }

    }