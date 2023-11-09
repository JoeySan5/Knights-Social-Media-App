import 'package:flutter/material.dart';
import 'dart:developer' as developer;
import 'package:knights/models/idea.dart';
import 'package:knights/components/idea_format.dart';
import 'package:knights/net/web_requests.dart';

///This component creates a list view for all Idea Posts(Formats)
///
///Fetches Ideas from dokku and converts those ideas into Idea Format objects.
///This then is added into a list view which is formatted within the home_page view.
class IdeaList extends StatefulWidget{
    
    const IdeaList({super.key});

      @override
      State<IdeaList> createState() => _IdeaList();
  }


  
  class _IdeaList extends State<IdeaList>{
    
    late Future<List<Idea>> _futureListIdeas;

    ///Now we want to read in data from dokku using get and parse the json into idea_format objects.
    ///
    ///initState called to initialize data that depends on a build,
    ///once the widget is inserted inside the widget tree. In this case the 
    ///widget is the list of ideas_formats.
    @override
    void initState(){
      super.initState();
      _futureListIdeas = fetchIdeas();
    }

    void retry(){
      setState(() {
        _futureListIdeas = fetchIdeas();
      });
    }


    @override
    Widget build(BuildContext context) {
      
        var fb = FutureBuilder<List<Idea>>(
        future: _futureListIdeas, 
        ///Context keeps track of each widget in the widgetTree.
        ///Snapshot is what is currently available of widget.
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
            ///This awaits snapshot data, displaying a loading spinner.
            child = const CircularProgressIndicator();
          }
          return child; 
        },
      );

      return fb;
    }
      
    
  }