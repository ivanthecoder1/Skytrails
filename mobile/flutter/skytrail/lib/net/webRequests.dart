import '../models/Idea.dart';
import '../models/User.dart';
import '../models/Comment.dart'; 
import 'dart:developer' as developer;
import 'dart:convert';
import 'package:http/http.dart' as http;
import '../models/UserAuth.dart';



// Used for backend connection
// Replace this ip address with your own ip address
// Ip address will change every few days, so make sure to update this url when it does
const String backendURL = 'http://10.0.2.2:8000';

// Clean session key and create a query with it
String rawSessionKey = UserAuth.sessionKey!.replaceAll('"', '');
final String sessionKeyQuery = '?sessionKey=${rawSessionKey}';

// Store username as a query
final String? usernameQuery = UserAuth.username;

// Idea routes
// Function to fetch a list of ideas from a remote API.
Future<List<Idea>> fetchIdeas() async {
  // Send an HTTP GET request to the API's endpoint.
  final response = await http.get(Uri.parse('$backendURL/messages'));
  print(
      'Response body: ${response.body}'); // Log the response body for debugging purposes.

  if (response.statusCode == 200) {
    // If the response status code is 200 (OK), parse the JSON data.
    final List<Idea> returnData = [];

    var res = jsonDecode(response.body);
    res = res['mData'];
    if (res is List) {
      // If the response is a List, map it to a list of 'Idea' objects.
      returnData.addAll((res as List).map((x) => Idea.fromJson(x)));
    } else if (res is Map) {
      // If the response is a Map, parse it as a single 'Idea' object.
      returnData.add(Idea.fromJson(res as Map<String, dynamic>));
    } else {
      developer
          .log('ERROR: Unexpected JSON response type (was not a List or Map).');
    }
    return returnData; // Return the list even if it's empty
  } else {
    developer.log('Error: ${response.statusCode}');
    return []; // Return an empty list if the request was not successful
  }
}

// Function to post a new idea to the remote API.
Future<void> postIdeas(
    String newTitle, String newMessage, String? userName, String? link, String? base64String) async {
  final url = '$backendURL/messages$sessionKeyQuery'; // Define the API endpoint
  print(userName);

  final response = await http.post(
    // Send an HTTP POST request with the new title and message.
    Uri.parse(url),
    body: json.encode({
      'mTitle': newTitle,
      'mMessage': newMessage,
      'mUserName': userName,
      'mLink': link,
      'mBase64String': base64String
    }),
    headers: {
      'Content-Type': 'application/json; charset=UTF-8',
    },
  );

  if (response.statusCode == 200) {
    print('Idea posted successfully');
    // Refresh the list of ideas should be handled separately.
  } else {
    print('Failed to post idea. Status code: ${response.statusCode}');
    print('Response body: ${response.body}');
  }
}

// Function to increment the like count of an idea.
Future<void> likeIdeas(int messageId, int currentLikes) async {
  final url =
      '$backendURL/messages/$messageId/like$sessionKeyQuery&username=$usernameQuery'; //Define API endpoint
  print(sessionKeyQuery);
  print(url);

  // Increment the currentLikes by 1.
  final int newLikes = currentLikes + 1;

  // Define the new data to be sent in the request body, updating 'likes'.
  final response = await http.post(
    Uri.parse(url),
    body: json.encode({
      'mLikes': newLikes,
    }), // Convert the data to JSON string
    headers: {
      'Content-Type': 'application/json', // Set the content type to JSON
    },
  );

  if (response.statusCode == 200) {
    print(
        'Likes for message $messageId updated successfully. New likes: $newLikes');
  } else {
    print(
        'Failed to update likes for message $messageId. Status code: ${response.statusCode}');
    print('Response body: ${response.body}');
  }
}

// Function to decrement the like count of an idea.
Future<void> dislike(int messageId, int currentLikes) async {
  final url = '$backendURL/messages/$messageId/dislike$sessionKeyQuery&username=$usernameQuery'; //Define API endpoint
  print(url);
  // Increment the currentLikes by 1.
  final int newLikes = currentLikes - 1;

  // Define the new data to be sent in the request body, updating 'likes'.
  final response = await http.post(
    Uri.parse(url),
    body: json.encode({
      'mLikes': newLikes,
    }), // Convert the data to JSON string
    headers: {
      'Content-Type': 'application/json', // Set the content type to JSON
    },
  );

  if (response.statusCode == 200) {
    print(
        'Likes (dislike) for message $messageId updated successfully. New likes: $newLikes');
  } else {
    print(
        'Failed to update likes for message $messageId. Status code: ${response.statusCode}');
    print('Response body: ${response.body}');
  }
}



