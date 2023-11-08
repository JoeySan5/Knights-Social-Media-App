import 'package:flutter/material.dart';
import 'dart:developer' as developer;

import 'package:knights/models/User.dart';
import 'package:knights/components/user_format.dart';
import 'package:knights/net/web_requests.dart';
import 'package:knights/pages/home_page.dart';

const String userId = '1234567890abcdef1234567890abcdef';
const String sessionKey = 'lGaJjDO8kdNq'; // hardcoded session key for Tommy
/// this component creates a view for the users profile content
/// 
/// fetches profile info from fokku and converts to User formast objects
/// this is then displayed within the profile_page view

class UserList extends StatefulWidget{
//  final int userId;
  const UserList(String userId, String sessionKey, {super.key});

    @override
    State<UserList> createState() => _UserList();
}

class _UserList extends State<UserList>{
  late Future<User> _futureUserList;

  /// now we want to read in data from dokku using get and parse json into user_format objects
  /// 
  /// initState called to initalize data that depends on a build,
  /// once the widget is inserted inside the widgettree. In this case the
  /// widget is the list of user_formats
  @override
  void initState(){
    super.initState();
    _futureUserList = fetchUsers(userId, sessionKey);
  }

  void retry(){
    setState((){
      _futureUserList = fetchUsers(userId, sessionKey);
    });
  }

  @override
  Widget build(BuildContext context){
    var fb = FutureBuilder<User>(
      future: _futureUserList,
      builder: (BuildContext context, AsyncSnapshot<User> snapshot){
        Widget child;
        if(snapshot.hasData){
          developer.log(snapshot.data!.mId+'text only');
          child = Text(
            snapshot.data!.mId,
            style: const TextStyle(color: Colors.white),
          );
          child = ListView.builder(
            scrollDirection: Axis.vertical,
            shrinkWrap: true,
            itemBuilder: (context, index){
              developer.log('building with context and $index');
              return UserFormat(userId, sessionKey);
              //return UserFormat(mId: snapshot.data!.mId, mUsername: snapshot.data!.mUsername, mEmail: snapshot.data!.mEmail, mNote: snapshot.data!.mNote, GI: snapshot.data!.GI, SO: snapshot.data!.SO);

            },
            );
        } else if(snapshot.hasError){
          child = Text('${snapshot.error}');
        } else {
          ///This awaits snapshot data, displaying a loading spinner
          child = const CircularProgressIndicator();
        }
        return child;
      },
    );
    return fb;
  }


  
}
