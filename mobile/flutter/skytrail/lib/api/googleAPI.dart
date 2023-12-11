import 'package:google_sign_in/google_sign_in.dart';
import "dart:convert";
import 'package:http/http.dart' as http;
import 'dart:io';
import '../pages/userProfile.dart';
import 'package:flutter/material.dart';
import '../models/UserAuth.dart';
import '../net/webRequests.dart';
import '../net/profileRequests.dart';

// This class is responsible for all functionalities relating to GoogleOauth (login, sign out, etc)
class GoogleSignInApi {
  static final GoogleSignIn googleSignIn = GoogleSignIn(
      scopes: [
        'https://www.googleapis.com/auth/userinfo.email',
        'openid',
        'https://www.googleapis.com/auth/userinfo.profile',
      ],
  );

  // Login functionality: extract token id, username, and email
  Future<void> signinWithGoogle(BuildContext context) async {
    try {
      // Google Oauth login process
      final googleUserAccount = await googleSignIn.signIn();
      final googleAuth = await googleUserAccount?.authentication;

      if (googleAuth != null) {
        // Extract username and email from googleAuth info
        String? username = googleUserAccount?.displayName;
        String? email = googleUserAccount?.email;

        // Used to set info of a user class instance - to be used for user profile
        UserAuth.email = email;
        UserAuth.username = username;

        // Send token ID to backend in exchange for a session key
        googleSignUpBackend(googleAuth.idToken);

        // Create new user with provided information if user is not already created
        // Other fields will be filled with default, and the user can edit it themselves later
        createUser(username, email);

        // Navigate to the home page after user logins
        Navigator.of(context).pushReplacementNamed('/home');
        
      }
    } catch (error) {
      print("Error during Google sign-in: $error");
      // Handle the error
    }
  }


  // Function to send tokenID to backend in exchange for session key
  Future<String?> googleSignUpBackend(String? googleIdToken) async {
    // Invalid google ID token
    if (googleIdToken == null) {
      print('Invalid googleIdToken');
      return null; 
    }

    // Exchange ID token with backend for session key
    final response = await http.post(Uri.parse('http://10.0.2.2:8000/login'),
      body: googleIdToken,
      headers: <String, String>{
        'Content-Type': 'application/json; charset=UTF-8',
      },
    );

    // Store session key to user auth model so that we can use it when we make HTTPS requests
    if (response.statusCode == 201 || response.statusCode == 200) {
      String sessionKey = response.body;
      UserAuth.sessionKey = response.body;
      print("Session key: " + response.body);
    }
    // Error
    else {
      print('Failed to send TokenID. Status code: ${response.statusCode}');
      print('Response body: ${response.body}');
    }
  }

  // Logout current user and clears session (new account can login)
  static Future logout() => googleSignIn.disconnect();
}

// Logout button: Ask user if they want to logout or not
Future<void> showLogoutConfirmationDialog(BuildContext context) async {
  return showDialog<void>(
    context: context,
    builder: (BuildContext context) {
      return AlertDialog(
        title: Text('Sign Out'),
        content: Text('Are you sure you want to sign out?'),
        actions: <Widget>[
          TextButton(
            child: Text('Cancel'),
            onPressed: () {
              Navigator.of(context).pop();
            },
          ),
          TextButton(
            child: Text('Sign Out'),
            onPressed: () {
              // Call the logout method
              GoogleSignInApi.logout().then((_) {
                Navigator.of(context).pop();
                // Navigate back to login page
                Navigator.of(context).pushReplacementNamed('/login');
              });
            },
          ),
        ],
      );
    },
  );
}
