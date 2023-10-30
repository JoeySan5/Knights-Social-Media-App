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

///Creating mock NavigatorObserver
class MockNavigatorObserver extends Mock implements NavigatorObserver{}

///The main function tests and verifies that the elevated button is present 
///on the home page when the app is initialized.
void main() {
  
  group('post message navigator', () {
    testWidgets('Say your piece button is present', (WidgetTester tester) async{
    //final mockObserver = MockNavigatorObserver();
    //builds widget
    await tester.pumpWidget(
      const MyApp()
    );

    expect(find.byType(ElevatedButton), findsOneWidget);
    
  });

  //the test below will be updated to test the navigation of the app

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


}
