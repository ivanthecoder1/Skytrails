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

// Comment routes  
// Function to request a list of comments corresponding to a message id
Future<List<Comment>> fetchComments(int messageId) async {
  final response = await http.get(Uri.parse('$backendURL/messages/$messageId/comments$sessionKeyQuery'));
  print('Response body: ${response.body}');

  if (response.statusCode == 200) {
    final List<Comment> returnData = [];

    var res = jsonDecode(response.body);

    if (res is List) {
      // If the response is a List, map it to a list of 'Comment' objects.
      returnData.addAll((res as List).map((comment) => Comment.fromJson(comment)));
    } else {
      developer.log('ERROR: Unexpected JSON response type (was not a List).');
    }
    print("Return data: $res");
    return returnData;
  } else {
    developer.log('Error: ${response.statusCode}');
    return [];
  }
}

// Function to post a comment to a specific message
Future<void> postComment(int messageID, String? username, String comment, String? link, String? base64String) async {
  
  final url = '$backendURL/messages/$messageID/comment$sessionKeyQuery'; // Define the API endpoint

  // Use provided arguments to construct the user in the post request
  final response = await http.post(
    // Send an HTTP POST request with the new title and message.
    Uri.parse(url),
    body: json.encode({
      "cContent": comment,
      "cUsername": username,
      "cMessageID": messageID,
      'cLink': link,
      'cBase64String': base64String

    }),
    headers: {
      'Content-Type': 'application/json; charset=UTF-8',
    },
  );

  // Send status depending on if the operation succeeded or not
  if (response.statusCode == 200) {
    print('Commented created successfully');
    print('Response body: ${response.body}');
  } else {
    print('Error. Status code: ${response.statusCode}');
    print('Response body: ${response.body}');
  }
}

// Add route for editing comment 
Future<void> updateComment(int messageID, int commentID, String newComment) async {
  
  final url = '$backendURL/messages/$messageID/comments/$commentID$sessionKeyQuery'; // Define the API endpoint
  print(url);

  final response = await http.put(
    // Send an HTTP PUT request with the new comment
    Uri.parse(url),
    body: json.encode({
      'cContent': newComment,
    }),
    headers: {
      'Content-Type': 'application/json; charset=UTF-8',
    },
  );

  // Send status depending on if the operation succeeded or not
  if (response.statusCode == 200) {
    print('Comment updated successfully');
    print('Response body: ${response.body}');
  } else {
    print('Error with updating comment. Status code: ${response.statusCode}');
    print('Response body: ${response.body}');
  }
}