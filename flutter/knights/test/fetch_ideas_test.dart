import 'package:flutter_test/flutter_test.dart';
import 'package:http/http.dart' as http;

import 'package:knights/models/idea.dart';
import 'package:knights/net/web_requests.dart';

import 'package:mockito/annotations.dart';
import 'package:mockito/mockito.dart';

import 'fetch_ideas_test.mocks.dart';

///Generate a MockClient using the Mockito package.
///Create new instances of this class in each test.
@GenerateMocks([http.Client])
void main() {
  group('fetchIdeas', () {

    test('returns a list of Ideas if the http call completes succesfully', () async {

    final client = MockClient();

    var url = Uri.parse('https://team-knights.dokku.cse.lehigh.edu/ideas');
    var headers = {"Accept": "application/json"};
    
    //Use mockito to return a succesful response when it calls the provided http.client.

    when(client
    .get(url, headers: headers))
    .thenAnswer((_) async =>
    http.Response('{"mStatus":"ok","mData":{"mId":2,"mContent":"this is a test","mLikeCount":0}}',200));

    expect(await fetchIdeasTest(client), isA<List<Idea>>());
   });
  });
}