import 'package:flutter/material.dart';
import 'package:skytrail/models/Idea.dart';
import 'net/webRequests.dart';
import 'net/profileRequests.dart';
import '../models/UserAuth.dart';
import 'pages/homePage.dart';
import 'pages/loginPage.dart';
import 'pages/userProfile.dart';
import 'dart:io';

// Added to handle the handshake error, fromat was found in Stack Overflow
class MyHttpOverrides extends HttpOverrides {
  @override
  HttpClient createHttpClient(SecurityContext? context) {
    return super.createHttpClient(context)
      ..badCertificateCallback =
          (X509Certificate cert, String host, int port) => true;
  } // Creates a way to handle bad certificates
}

void main() {
  HttpOverrides.global =
      MyHttpOverrides(); // Set custom HTTP overrides using the MyHttpOverrides
  runApp(MyApp()); // Run the Flutter app starting with the MyApp widget
}

class MyApp extends StatelessWidget {
  const MyApp({super.key});

  // This widget is the root of your application.
  @override
  Widget build(BuildContext context) {
    return MaterialApp(
      title: 'SkyTrail',
      theme: ThemeData(
        // Define the theme for the entire application, including color scheme
        colorScheme: ColorScheme.fromSeed(seedColor: const Color.fromRGBO(2, 119, 189, 1)),
        useMaterial3: true,
      ),
      // Set the initial route to the login page widget (user won't be able to access home or profile until they login)
      initialRoute: '/login',
      routes: <String, WidgetBuilder> { 
        '/login': (context) => const LoginPage(title: "SkyTrail Login"),
        '/home': (context) => const MyHomePage(title: "SkyTrail Home"),
        '/profile': (context) => UserProfile(title: "SkyTrail User Profile", requestedUser: UserAuth.username, isCurrentUser: true,),
      },
    );
  }
}

