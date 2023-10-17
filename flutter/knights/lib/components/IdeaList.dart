import 'package:flutter/material.dart';
import 'dart:developer' as developer;


import 'package:knights/models/Idea.dart';
import 'package:knights/components/IdeaFormat.dart';
import 'package:knights/net/webRequests.dart';


class IdeaList extends StatefulWidget{
    
    const IdeaList({super.key});

      @override
      State<IdeaList> createState() => _IdeaList();
  }


  //now we want to read in data from dokku using get (fetchdata()), and then parse the data into idea objects.
  //with the idea object we will get a list similar to below, 
  class _IdeaList extends State<IdeaList>{
    // final List<Idea> _ideas = [
    //   Idea(mId: 4, mContent: 'helloworld', mLikeCount: 4),
    //   Idea(mId: 5, mContent: 'byeworld', mLikeCount: -7),
    // ];
    late Future<List<Idea>> _future_list_ideas;

    //initState called to initialize data that depends on a build
    //once the widget is inserted inside the widget tree
    @override
    void initState(){
      _future_list_ideas = fetchIdeas();
    }

    void retry(){
      setState(() {
        _future_list_ideas = fetchIdeas();
      });
    }


    @override
    Widget build(BuildContext context) {
        var fb = FutureBuilder<List<Idea>>(
        future: _future_list_ideas, 
        //context keeps track of each widget in the widgetTree
        //snapshot is what is currently available 
        builder: (BuildContext context, AsyncSnapshot<List<Idea>> snapshot){
          Widget child;
          
          if (snapshot.hasData){

            child = ListView.builder(
            
            
            scrollDirection: Axis.vertical,
            shrinkWrap: true,
            itemCount: snapshot.data!.length,
            itemBuilder: (context, index) {
              developer.log('building with context & $index');

              return IdeaFormat(mId: snapshot.data![index].mId, mContent:snapshot.data![index].mContent, mLikeCount: snapshot.data![index].mLikeCount);
            },
            );
          }
          else if(snapshot.hasError){
            child = Text('${snapshot.error}');
          } else{
            //this awaits snapshot data, displaying a loading spinner
            child = const CircularProgressIndicator();
          }
          return child; 
        },
      );

      return fb;
    }
      
    
  }