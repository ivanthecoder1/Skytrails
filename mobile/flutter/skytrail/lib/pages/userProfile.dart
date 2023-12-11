import 'package:flutter/material.dart';
import '../net/webRequests.dart';
import '../api/googleAPI.dart';
import '../models/UserAuth.dart';
import '../models/User.dart';
import '../net/profileRequests.dart';

class UserProfile extends StatefulWidget {
  const UserProfile(
      {Key? key,
      required this.title,
      required this.requestedUser,
      required this.isCurrentUser})
      : super(key: key);

  // These fields are required when making an instance of a user profile
  final String title; // Title of the page
  final String? requestedUser; // Used to fetch the requested user
  final bool
      isCurrentUser; // Used to check if the requested user is the user currently logged in

  @override
  State<UserProfile> createState() => _UserProfileState();
}

class _UserProfileState extends State<UserProfile> {
  // These variables will contain the values that the user inputs when they edit their own profile
  String newFirstName = '';
  String newLastName = '';
  String newGender = '';
  String newSexOrientation = '';
  String newNote = '';

  // Store the contents of user
  late Future<List<User>> futureUser;

  @override
  void initState() {
    super.initState();
    // Fetch user based on requested user argument
    futureUser = fetchUser(widget.requestedUser);
  }

  // Refresh page after a change is made
  void refreshPage() {
    setState(() {
      futureUser = fetchUser(widget.requestedUser);
    });
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      // Navigation bar
      appBar: AppBar(
        backgroundColor: Theme.of(context).colorScheme.primary,
        automaticallyImplyLeading: false,
        leading: Image.asset(
          'lib/assets/images/skytrailLogo.png',
          width: 40,
          height: 40,
        ),
        title: Text(
          widget.title,
          style: const TextStyle(
            color: Colors.white,
          ),
        ),
        actions: [
          // Sends you to home page
          IconButton(
            icon: const Icon(Icons.home),
            color: Colors.white,
            onPressed: () {
              Navigator.of(context).pushReplacementNamed('/home');
            },
          ),
          // Logs you out and send you back to login page
          IconButton(
            icon: const Icon(Icons.logout),
            color: Colors.white,
            onPressed: () async {
              showLogoutConfirmationDialog(context);
            },
          ),
        ],
      ),
      body: Center(
        // Grab all the information from the current user login (gender, name, etc)
        child: FutureBuilder<List<User>>(
          future: futureUser,
          builder: (context, snapshot) {
            if (snapshot.connectionState == ConnectionState.waiting) {
              // Display a loading indicator while waiting for data.
              return const Center(child: CircularProgressIndicator());
            } else if (snapshot.hasError) {
              // Display an error message
              return Center(child: Text('Error: ${snapshot.error}'));
            } else if (!snapshot.hasData || snapshot.data!.isEmpty) {
              // Display a message if there are no ideas available
              return const Center(child: Text('No ideas available.'));
            } else {
              User user = snapshot.data!.first;

              return Container(
                padding: EdgeInsets.all(16.0),
                decoration: BoxDecoration(
                  border: Border.all(color: Colors.grey),
                  borderRadius: BorderRadius.circular(10.0),
                ),
                child: Column(
                  mainAxisSize: MainAxisSize.min,
                  children: [
                    ListTile(
                      title: Text('Profile Page'),
                      subtitle: Column(
                        crossAxisAlignment: CrossAxisAlignment.start,
                        children: [
                          // Displays user information
                          // If the user profile is not the user who is logged in, then they cannot view so, gi
                          // They cannot edit as well
                          if (widget.isCurrentUser)
                            Text('Username: ${UserAuth.username}'),

                          if (widget.isCurrentUser == false)
                            Text('Username: ${user.username}'),

                          Text('First Name: ${user.firstName}'),
                          Text('Last Name: ${user.lastName}'),
                          Text('Email: ${user.email}'),

                          if (widget.isCurrentUser)
                            Text('Gender Identity: ${user.gender}'),

                          if (widget.isCurrentUser)
                            Text('Sexual Orientation: ${user.sexOrientation}'),

                          Text('Note: ${user.note}'),

                          // Edit button
                          if (widget.isCurrentUser)
                            ElevatedButton(
                              onPressed: () {
                                showDialog(
                                  context: context,
                                  builder: (BuildContext context) {
                                    return AlertDialog(
                                      title: Text('Edit Profile'),
                                      content: SingleChildScrollView(
                                        child: Column(
                                          children: [
                                            // Fields to edit user profile info
                                            TextField(
                                              decoration: InputDecoration(
                                                  labelText: 'First Name'),
                                              onChanged: (value) {
                                                newFirstName = value;
                                              },
                                            ),
                                            TextField(
                                              decoration: InputDecoration(
                                                  labelText: 'Last Name'),
                                              onChanged: (value) {
                                                newLastName = value;
                                              },
                                            ),
                                            TextField(
                                              decoration: InputDecoration(
                                                  labelText: 'Gender'),
                                              onChanged: (value) {
                                                newGender = value;
                                              },
                                            ),
                                            TextField(
                                              decoration: InputDecoration(
                                                  labelText:
                                                      'Sexual Orientation'),
                                              onChanged: (value) {
                                                newSexOrientation = value;
                                              },
                                            ),
                                            TextField(
                                              decoration: InputDecoration(
                                                  labelText: 'Note'),
                                              onChanged: (value) {
                                                newNote = value;
                                              },
                                            ),
                                            ElevatedButton(
                                              onPressed: () {
                                                updateUser(
                                                  UserAuth.username,
                                                  // If values are empty, then keep original values,
                                                  // otherwise update to new values from fields
                                                  newFirstName.isNotEmpty
                                                      ? newFirstName
                                                      : user.firstName,
                                                  newLastName.isNotEmpty
                                                      ? newLastName
                                                      : user.lastName,
                                                  UserAuth.email,
                                                  newGender.isNotEmpty
                                                      ? newGender
                                                      : user.gender,
                                                  newSexOrientation.isNotEmpty
                                                      ? newSexOrientation
                                                      : user.sexOrientation,
                                                  newNote.isNotEmpty
                                                      ? newNote
                                                      : user.note,
                                                );

                                                // Trigger the refresh after updating
                                                refreshPage();

                                                Navigator.of(context).pop();
                                              },
                                              child: Text('Save Changes'),
                                            ),
                                            TextButton(
                                              onPressed: () {
                                                Navigator.of(context)
                                                    .pop(); // Close the dialog without saving changes
                                              },
                                              child: Text('Cancel'),
                                            ),
                                          ],
                                        ),
                                      ),
                                    );
                                  },
                                );
                              },
                              child: Center(
                                child: Text('Edit Profile'),
                              ),
                            ),
                        ],
                      ),
                    ),
                  ],
                ),
              );
            }
          },
        ),
      ),
    );
  }
}
