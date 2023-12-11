import 'package:flutter/material.dart';
import 'package:google_sign_in/google_sign_in.dart';
import '../api/googleAPI.dart';

class LoginPage extends StatefulWidget {
  const LoginPage(
      {super.key, required this.title}); // Constructor for Login Page
  final String title; // A required property, Title of the page

  @override
  State<LoginPage> createState() => _LoginPageState();
}

class _LoginPageState extends State<LoginPage> {
  // Instantiate instance of our googleSignInAPI so that we can use the sign in function
  final googleSignInApi = GoogleSignInApi();

  @override
  
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(
        backgroundColor: Theme.of(context).colorScheme.primary,
        leading: Image.asset(
            'lib/assets/images/skytrailLogo.png', 
            width: 40, // Adjust the width as needed
            height: 40, // Adjust the height as needed
        ),
        automaticallyImplyLeading: false, // Set this to false to hide the back arrow
        title: Text(
          widget.title,
          style: const TextStyle(
            color: Colors.white, 
          ),
        ),
      ),
      body: Center(
        child: Column(
          mainAxisAlignment: MainAxisAlignment.center,
          children: <Widget>[
            ElevatedButton(
              // Calls google sign in Oauth function when user clicks on login
              onPressed: () async {
                await googleSignInApi.signinWithGoogle(context);
              },
              child: Text('Login with Google'),
            ),
          ],
        ),
      ),
    );
  }
}
