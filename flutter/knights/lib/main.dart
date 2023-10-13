import 'package:flutter/material.dart';
import 'package:like_button/like_button.dart';


//everytime like button is cliked it should be a new request, that either 
//increments or decrements like counter
void main() {
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
            IdeaFormat(),
          ],
        )
      )
    );
      }
    }

    //This class is the format to display an idea,
    //includes like counter, like button (increment counter), and dislike button (decrement counter)
    class IdeaFormat extends StatelessWidget{
      const IdeaFormat({super.key});
        
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
                  LikeButton(
                    likeBuilder:(bool isLiked){
                      return const Icon(
                          Icons.thumb_up,
                          color: Colors.white
                      );
                    },
                  ),
                  LikeButton(
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

