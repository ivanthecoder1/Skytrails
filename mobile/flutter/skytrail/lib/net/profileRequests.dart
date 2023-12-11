import '../models/User.dart';
import '../models/UserAuth.dart';
import 'dart:developer' as developer;
import 'dart:convert';
import 'package:http/http.dart' as http;

// Used for backend connection
// Replace this ip address with your own ip address
// Ip address will change every few days, so make sure to update this url when it does
const String backendURL = 'http://10.0.2.2:8000';

// Clean session key and create a query with it
String rawSessionKey = UserAuth.sessionKey!.replaceAll('"', '');
final String sessionKeyQuery = '?sessionKey=${rawSessionKey}';

// Store username as a query
final String? usernameQuery = UserAuth.username;

// User routes
// Function to fetch a requested user and see if they exist
Future<bool> getUser(String? requestedUsername) async {
  final url =
      '$backendURL/users/$requestedUsername$sessionKeyQuery'; // Define the API endpoint

  final response = await http.get(Uri.parse(url));

  if (response.statusCode == 200) {
    print('User found');
    return true;
  } else {
    print('Error. Status code: ${response.statusCode}');
    return false;
  }
}

// Function to create a user if they do not already exist
Future<void> createUser(String? newUsername, String? newEmail) async {
  
  final url = '$backendURL/users$sessionKeyQuery'; // Define the API endpoint

  // Sets everything except username and email (derived from Oauth) to default
  final response = await http.post(
    // Send an HTTP POST request with the new title and message.
    Uri.parse(url),
    body: json.encode({
      'uUsername': newUsername,
      'uFirstname': "Default first name",
      'uLastname': "Default last name",
      'uEmail': newEmail,
      'uGender': "Default gender",
      'uSexorientation': "Default Sex Orientation",
      'uNote': "Default note",
    }),
    headers: {
      'Content-Type': 'application/json; charset=UTF-8',
    },
  );

  // Send status depending on if the operation succeeded or not
  if (response.statusCode == 201) {
    print('User created successfully');
    print('Response body: ${response.body}');
  } else if (response.statusCode == 500) {
    print('User already exists');
    print('Response body: ${response.body}');
  } 
  else {
    print('Error. Status code: ${response.statusCode}');
    print('Response body: ${response.body}');
  }
}

// Function to fetch a specific user and map their information to a defined user class from models
Future<List<User>> fetchUser(String? requestedUser) async {
  // Send an HTTP GET request to the API's endpoint.
  final response = await http.get(Uri.parse('$backendURL/users/$requestedUser$sessionKeyQuery'));

  print('Response body: ${response.body}'); // Log the response body for debugging purposes.

  // If user exists, map all the fields from the response body to a user class that we defined
  // So we can store a list of the user's username, gender, etc
  if (response.statusCode == 200) {
    // If the response status code is 200 (OK), parse the JSON data.
    final List<User> returnData = [];
    var res = jsonDecode(response.body);
    res = res['mData'];
    if (res is List) {
      // If the response is a List, map it to a list of 'Idea' objects.
      returnData.addAll((res as List).map((x) => User.fromJson(x)));
    } else if (res is Map) {
      // If the response is a Map, parse it as a single 'Idea' object.
      returnData.add(User.fromJson(res as Map<String, dynamic>));
    } else {
      developer
          .log('ERROR: Unexpected JSON response type (was not a List or Map).');
    }
    print(returnData);
    return returnData; // Return the list even if it's empty
  } else {
    developer.log('Error: ${response.statusCode}');
    return []; // Return an empty list if the request was not successful
  }
}

// Function that takes in updated parameter arguments and update the requested user's profile page
Future<void> updateUser(
    String? requestedUser,
    String newFirstName,
    String newLastName,
    String? requestedEmail,
    String newGender,
    String newSexOrientation,
    String newNote) async {
  
  final url = '$backendURL/users/$requestedUser$sessionKeyQuery'; // Define the API endpoint
  print(url);

  // Use provided arguments to construct the user in the post request
  final response = await http.put(
    // Send an HTTP POST request with the new title and message.
    Uri.parse(url),
    body: json.encode({
      'uUsername': requestedUser,
      'uFirstname': newFirstName,
      'uLastname': newLastName,
      'uEmail': requestedEmail,
      'uGender': newGender,
      'uSexorientation': newSexOrientation,
      'uNote': newNote,
    }),
    headers: {
      'Content-Type': 'application/json; charset=UTF-8',
    },
  );

  // Send status depending on if the operation succeeded or not
  if (response.statusCode == 200) {
    print('User updated successfully');
    print('Response body: ${response.body}');
  } else {
    print('Error. Status code: ${response.statusCode}');
    print('Response body: ${response.body}');
  }
}
