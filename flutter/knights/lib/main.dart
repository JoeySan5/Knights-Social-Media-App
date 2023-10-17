import 'package:flutter/material.dart';
import 'package:like_button/like_button.dart';
import 'package:http/http.dart' as http;
import 'dart:convert';
import 'dart:developer' as developer;
import 'package:flutter/services.dart';
import 'dart:io';
import 'package:knights/models/Idea.dart';


//everytime like button is cliked it should be a new request, that either 
//increments or decrements like counter
void main() async {
  WidgetsFlutterBinding.ensureInitialized();

  ByteData data = await PlatformAssetBundle().load('assets/ca/lets-encrypt-r3.pem');
  SecurityContext.defaultContext.setTrustedCertificatesBytes(data.buffer.asUint8List());

  runApp(const MyApp());
}

class MyApp extends StatelessWidget{
  const MyApp({super.key});
  @override
  Widget build(BuildContext context) {
    return  MaterialApp(
      title: 'Knights App',
      theme: ThemeData(
        scaffoldBackgroundColor: const Color.fromARGB(255, 5, 5, 5),
      ),
      home:  const Directionality(
        textDirection: TextDirection.ltr, 
        child: MyHomePage(),
        ),
        
    );
  }
}


class MyHomePage extends StatelessWidget{
  const MyHomePage({super.key});

    @override
    Widget build(BuildContext context) {
      
      return  const Scaffold(
        body:Center(
          child: Column(
          
            children: <Widget>[
              Padding(padding: EdgeInsets.only(top: 40.0),
              child: Text(
                'Knights',
                
                style: TextStyle(
                
                  fontSize: 40,
                  color: Color.fromARGB(221, 255, 255, 255),
                  fontFamily: 'roboto'
                ),
              )
              ),

              Padding( padding: EdgeInsets.only(top: 30.0),
              child: Text('Here\'s what people have been saying:',
              textDirection: TextDirection.ltr,
                style: TextStyle(
                  fontSize: 20,
                  color: Colors.white,
                  fontFamily: 'roboto',
                
                ),
                ),
              ),
              IdeaList(),
            ],
          )
        )
      );
    }
  }

  class IdeaList extends StatefulWidget{
    const IdeaList({super.key});

      @override
      State<IdeaList> createState() => _IdeaList();
  }

  class _IdeaList extends State<IdeaList>{
    final List<Idea> _ideas = [
      Idea(mId: 4, mContent: 'helloworld', mLikeCount: 4),
      Idea(mId: 5, mContent: 'byeworld', mLikeCount: -7),
    ];

    @override
    Widget build(BuildContext context) {
      return ListView.builder(
        scrollDirection: Axis.vertical,
        shrinkWrap: true,
        itemCount: _ideas.length,
        itemBuilder: (context, index) {
          developer.log('building with context & $index');

          return IdeaFormat();
        },
        );
    } 
  }

    //This class is the format to display an idea,
    //includes user massage, like counter, like button (increment counter), and dislike button (decrement counter)
  class IdeaFormat extends StatefulWidget{
      const IdeaFormat({super.key});

      @override
      State<IdeaFormat> createState() => _IdeaFormat();

  }

  class _IdeaFormat extends State<IdeaFormat> {
      @override
      Widget build(BuildContext context) {
        return Container(
          margin: const EdgeInsets.all(15),
          //decoration is to make borders look different
          decoration: BoxDecoration(
            border: Border.all(
              color: Colors.green,
              width: 1.5,
              
              ),
              borderRadius: const BorderRadius.all(
                Radius.circular(10)
              ),
            ),
          
          height: 50,
          child:  Column(
            // mainAxisAlignment: MainAxisAlignment.spaceEvenly,
            children: [
              const Text(
              'Random Message',
              style: TextStyle(color: Colors.white),
              ),
              Row(
                mainAxisAlignment: MainAxisAlignment.spaceEvenly,
                children: [
                  const Text('0',
                  style: TextStyle(color: Colors.white),
                  ),
                  //this is thumbs-up icon
                  LikeButton(
                    onTap: onLikeButtonTapped,
                    likeBuilder:(bool isLiked){
                      return const Icon(
                          Icons.thumb_up,
                          color: Colors.white
                      );
                    },
                  ),
                  //this is thumbs-down icon
                  LikeButton(
                    onTap: onDislikeButtonTapped,
                    likeBuilder:(bool isLiked){
                      return const Icon(
                          Icons.thumb_down,
                          color: Colors.white
                      );
                    },
                  ),
              
                ],
              )
            ],
          )
        );

      }
  }

    //this method fetches the json from dokku, and then 
    //seperates each json object into an Idea(.dart) object
    Future<List<Idea>> fetchIdeas() async{
      developer.log('Making web request...');
      var url = Uri.parse('https://team-knights.dokku.cse.lehigh.edu/ideas/7');
      var headers = {"Accept": "application/json"};

      var response = await http.get(url, headers: headers);

      

      if (response.statusCode == 200){
        final List<Idea> returnData;
        
        var res = jsonDecode(response.body);
        
        if(res['mData'] is List){
          //dynamic allows for a types to be inferred during runtime, and can be changed to different types
          returnData = (res as List<dynamic>).map((x) => Idea.fromJson(x)).toList();
        }
        else if(res is Map){
          returnData = <Idea>[Idea.fromJson(res as Map<String,dynamic>)];
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

    Future<bool> onDislikeButtonTapped(bool isLiked) async{
    /// send your request here
    // final bool success= await sendRequest();

    /// if failed, you can do nothing
    // return success? !isLiked:isLiked;

    developer.log('Making web request...');
    var url = Uri.parse('https://team-knights.dokku.cse.lehigh.edu/ideas/7');
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
    Future<bool> onLikeButtonTapped(bool isLiked) async{
    /// send your request here
    // final bool success= await sendRequest();

    /// if failed, you can do nothing
    // return success? !isLiked:isLiked;

    developer.log('Making web request...');
    var url = Uri.parse('https://team-knights.dokku.cse.lehigh.edu/ideas/7');
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

    // This class is for writing an "idea", with a message
    // class Idea extends StatelessWidget {
    //   const Idea({super.key});

    //   @override
    //   Widget build(BuildContext context) {
    //     return(
    //         const Padding(
    //         padding: EdgeInsets.only(top: 10.0),
    //         child: TextField(
    //           decoration: InputDecoration(
    //           border: OutlineInputBorder(),
    //           hintText: 'Please write your idea here...',
    //           hintStyle: TextStyle(color: Colors.green),
    //         ),
    //         style: TextStyle(color: Colors.white),
    //         ),
    //         )
    //     );
    //   }

    // }

