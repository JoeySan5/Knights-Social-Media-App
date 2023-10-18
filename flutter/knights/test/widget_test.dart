import 'package:knights/pages/HomePage.dart';
import 'package:knights/pages/MessagePage.dart';
import 'package:mockito/mockito.dart';
// This is a basic Flutter widget test.
//
// To perform an interaction with a widget in your test, use the WidgetTester
// utility in the flutter_test package. For example, you can send tap and scroll
// gestures. You can also use WidgetTester to find child widgets in the widget
// tree, read text, and verify that the values of widget properties are correct.

import 'package:flutter/material.dart';
import 'package:flutter_test/flutter_test.dart';

import 'package:knights/main.dart';

//creating mock NavigatorObserver
class MockNavigatorObserver extends Mock implements NavigatorObserver{}
void main() {
  
  group('post message navigator', () {
    testWidgets('Say your piece button is present', (WidgetTester tester) async{
    final mockObserver = MockNavigatorObserver();
    //builds widget
    await tester.pumpWidget(
      const MyApp()
    );

    expect(find.byType(ElevatedButton), findsOneWidget);
    
  });

  // testWidgets('MessagePage appears after clicking button', (WidgetTester tester) async{
  //   final mockObserver = MockNavigatorObserver();
  //   //builds widget
  //   await tester.pumpWidget(
  //     MyApp()
  //   );
  //   await tester.tap(find.byType(ElevatedButton));
  //   await tester.pumpAndSettle();

  //   expect(find.text('Go Back Home'), findsOneWidget);
    
  // });
 
});

  // Define a test. The TestWidgets function also provides a WidgetTester
  // to work with. The WidgetTester allows you to build and interact
  // with widgets in the test environment.
  // testWidgets('Say your piece button is present and triggers navigation after tapped', (WidgetTester tester) async{
  //   final mockObserver = MockNavigatorObserver();
  //   //builds widget
  //   await tester.pumpWidget(
  //     MaterialApp(
  //       home: MyHomePage(),
  //       navigatorObservers: [mockObserver],
  //     ),
  //   );
  //   expect(find.byType(ElevatedButton), findsOneWidget);
  //   await tester.tap(find.byType(ElevatedButton));
  //   await tester.pumpAndSettle();

  //   /// Verify that a push event happened
  //   verify(mockObserver.didPush(any, any));

  //   /// You'd also want to be sure that your page is now
  //   /// present in the screen.
  //   expect(find.byType(MessagePage), findsOneWidget);
  // });


  // testWidgets('Counter increments smoke test', (WidgetTester tester) async {
  //   // Build our app and trigger a frame.
  //   await tester.pumpWidget(const MyApp());

  //   // Verify that our counter starts at 0.
  //   expect(find.text('0'), findsOneWidget);
  //   expect(find.text('1'), findsNothing);

  //   // Tap the '+' icon and trigger a frame.
  //   await tester.tap(find.byIcon(Icons.add));
  //   await tester.pump();

  //   // Verify that our counter has incremented.
  //   expect(find.text('0'), findsNothing);
  //   expect(find.text('1'), findsOneWidget);
  // });
}
