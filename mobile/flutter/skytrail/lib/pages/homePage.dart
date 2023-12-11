import 'dart:typed_data';

import 'package:flutter/material.dart';
import 'package:skytrail/models/Idea.dart';
import 'package:url_launcher/url_launcher_string.dart';
import '../net/webRequests.dart';
import '../net/commentRequests.dart';
import '../api/googleAPI.dart';
import '../models/UserAuth.dart';
import '../models/Comment.dart';
import '../pages/userProfile.dart';
import 'package:flutter/gestures.dart';
import 'package:image_picker/image_picker.dart';
import 'dart:convert';
import 'dart:io';
import 'package:flutter/services.dart';
import 'package:flutter/gestures.dart';
import 'package:url_launcher/url_launcher.dart';
import 'package:flutter_cache_manager/flutter_cache_manager.dart';


class MyHomePage extends StatefulWidget {
  const MyHomePage(
      {super.key, required this.title}); // Constructor for MyHomePage
  final String title; // A required property, Title of the page

  @override
  State<MyHomePage> createState() => _MyHomePageState();
}

class _MyHomePageState extends State<MyHomePage> {
  // A Future that will hold a list of 'Idea' objects.
  late Future<List<Idea>> futureIdeas;

  // A Future that will hold a list of 'Comment' objects.
  late Future<List<Comment>> futureComments;

  // Track if comment button is clicked or not (initially set to false)
  bool commentPressed = false;

  // Map to store comments for each idea
  Map<int, List<String>> ideaComments = {};
  String newComment = '';

  @override
  void initState() {
    super.initState();
    // Initialize 'futureIdeas' by calling the 'fetchIdeas' function.
    futureIdeas = fetchIdeas();
  }

  // Refresh the page by pulling down on screen
   Future<void> _refreshData() async {
    setState(() {
      futureIdeas = fetchIdeas();
    });
  }

  // // Function to open the camera and pick an image
  // Future<void> _pickImage() async {
  //   final ImagePicker _picker = ImagePicker();
  //   pickedImage = await _picker.pickImage(source: ImageSource.camera);
  //   setState(() {});
  // }
  File? _imageFile;
  String? _base64 = '';
  final ImagePicker _picker = ImagePicker();
  void _pickImageBase64() async{
    final XFile? image = await _picker.pickImage(source: ImageSource.gallery);
    if(image == null) return;
    Uint8List imagebyte = await image!.readAsBytes();
    _base64 = base64.encode(imagebyte);
  print('Base64 String: $_base64'); // Add this line to print the base64 string

    final imagetemppath = File(image.path);
    DefaultCacheManager().downloadFile(imagetemppath.uri.toString());

    setState(() {
      this._imageFile = imagetemppath;
    });
    
  }



  // Function to show a dialog for posting a new idea.
  void _showPostIdeaDialog() {
    String subject = '';
    String idea = '';
    String link = '';

    showDialog(
      context: context,
      builder: (context) {
        return AlertDialog(
          title: const Text('Post New Idea'),
          content: Column(
            mainAxisSize: MainAxisSize.min,
            children: [
              // TextFields to input the subject and idea for the new post.
              Expanded(
              child: TextField(
                onChanged: (value) {
                  setState(() {
                    subject = value;
                  });
                },
                decoration: const InputDecoration(labelText: 'Subject'),
              ),
              ),
              Expanded(
              child: TextField(
                onChanged: (value) {
                  setState(() {
                    idea = value;
                  });
                },
                decoration: const InputDecoration(labelText: 'Idea'),
              ),
              ),
              Expanded(
               child: TextField(
                onChanged: (value) {
                  setState(() {
                    link = value;
                  });
                },
                decoration: const InputDecoration(labelText: 'Link'),
              ),
              ),
               ElevatedButton(
                onPressed: _pickImageBase64,
                child: const Text('Add Photo'),
              ),
             // _imageFile == null ? Container() : Image.file(_imageFile!),

              
              
            ],
          ),
          actions: [
            ElevatedButton(
              // Button to post the idea.
              onPressed: () {
                if (subject.isNotEmpty) {
                  // Call 'postIdeas' to post the new idea.
                  print(_base64);
                  content: Text(_base64!);
                  postIdeas(subject, idea, UserAuth.username, link, _base64);
                  // Refresh the list of ideas by calling 'fetchIdeas'.
                  futureIdeas = fetchIdeas();
                  // Close the dialog.
                  Navigator.of(context).pop();
                } else {
                  ScaffoldMessenger.of(context).showSnackBar(
                    // Show a snackbar if the subject is empty.
                    const SnackBar(
                      content: Text('Post New Idea.'),
                    ),
                  );
                }
              },
              child: const Text('Post'),
            ),
            TextButton(
              onPressed: () {
                Navigator.of(context).pop();
              },
              child: const Text('Cancel'), // Button to cancel the dialog.
            ),
          ],
        );
      },
    );
  }

  // Function to show a dialog for comments
  void _showCommentDialog(int messageID) {
    // Fetch comments for the current idea
    futureComments = fetchComments(messageID);

    // Local state for managing comments in the dialog
    String newComment = '';
    String newLink = '';
    List<Comment> comments = [];

    showDialog(
      context: context,
      builder: (context) {
        return AlertDialog(
          title: const Text('Comments'),
          content: SizedBox(
            height: 300,
            child: SingleChildScrollView(
              child: Column(
                children: [
                  // Display comments
                  FutureBuilder<List<Comment>>(
                    future: futureComments,
                    builder: (context, snapshot) {
                      if (snapshot.connectionState == ConnectionState.waiting) {
                        // Display a loading indicator while waiting for comments.
                        return const Center(child: CircularProgressIndicator());
                      } else if (snapshot.hasError) {
                        // Display an error message
                        return Center(child: Text('Error: ${snapshot.error}'));
                      } else if (!snapshot.hasData || snapshot.data!.isEmpty) {
                        // Display a message if there are no comments available
                        return const Center(
                            child: Text('No comments available.'));
                      } else {
                        comments = snapshot.data!;
                        // Display a list of comments using a ListView
                        return Column(
                          children: [
                            for (var comment in comments)
                              Container(
                                decoration: BoxDecoration(
                                  border: Border.all(
                                    color: Colors.black,
                                    width: 1.0,
                                  ),
                                ),
                                child: ListTile(
                                  title: RichText(
                                    text: TextSpan(
                                      children: [
                                        // Allows username in comments to be clickable
                                        TextSpan(
                                          text: '${comment.username}: ',
                                          style: TextStyle(
                                            color: Colors.blue,
                                            decoration:
                                                TextDecoration.underline,
                                          ),
                                          recognizer: TapGestureRecognizer()
                                            ..onTap = () {
                                              // Your onPressed logic for the username
                                              print(
                                                  'Username tapped: ${comment.username}');

                                              // Clicking on a user will take you to their profile
                                              // If the user is the currently logged in user, then it will display gi and so
                                              // Otherwise, you cannot see it
                                              if (comment.username ==
                                                  UserAuth.username) {
                                                Navigator.of(context).push(
                                                  MaterialPageRoute(
                                                    builder: (context) =>
                                                        UserProfile(
                                                            title:
                                                                "Skytrail Home",
                                                            requestedUser:
                                                                UserAuth
                                                                    .username,
                                                            isCurrentUser:
                                                                true),
                                                  ),
                                                );
                                              } else {
                                                Navigator.of(context).push(
                                                  MaterialPageRoute(
                                                    builder: (context) =>
                                                        UserProfile(
                                                            title:
                                                                "Skytrail Home",
                                                            requestedUser:
                                                                comment
                                                                    .username,
                                                            isCurrentUser:
                                                                false),
                                                  ),
                                                );
                                              }
                                            },
                                        ),
                                        // Display the actual content
                                        TextSpan(
                                          text: " ${comment.content} ${comment.link}",
                                          style: TextStyle(
                                            color: Colors.blue,
                                            fontSize: 12,
                                          ),
                                          recognizer: TapGestureRecognizer()
                                            ..onTap = () {
                                              // Handle link tap here
                                               launchUrlString(comment.link ?? '');
                                            },
                                        ),
                                        // Display the edit button if comment user matches with current user
                                        if (comment.username ==
                                            UserAuth.username)
                                          TextSpan(
                                            text: "      Edit comment",
                                            style: TextStyle(
                                              color: Colors.red,
                                              fontSize:
                                                  12, // Adjust the font size for the content
                                            ),
                                            recognizer: TapGestureRecognizer()
                                              ..onTap = () {
                                                // Call our edit comment pop up
                                                _showEditCommentDialog(
                                                    comment.messageID,
                                                    comment.commentID,
                                                    comment.content);
                                              },
                                          ),
                                      ],
                                    ),
                                  ),
                                ),
                              ),
                          ],
                        );
                      }
                    },
                  ),

                  // Adding a new comment
                  Row(
                   children: [
                    Expanded(
                      child: Column(
                        children: [
                          TextField(
                            onChanged: (value) {
                              setState(() {
                                newComment = value;
                              });
                            },
                            decoration: const InputDecoration(
                              labelText: 'Add a comment...',
                            ),
                          ),
                          TextField(
                            onChanged: (value) {
                              setState(() {
                                newLink = value;
                              });
                            },
                            decoration: const InputDecoration(
                              labelText: 'Add a link...',
                            ),
                          ),
                          ElevatedButton(
                            onPressed: _pickImageBase64,
                            child: const Text('Add Photo'),
                          ),
                                      //  _imageFile == null ? Container() : Image.file(_imageFile!),

                        ],
                      ),
                    ),
                      // Add comment button
                      IconButton(
                        icon: const Icon(Icons.add),
                        onPressed: () async {
                          // Call post comment function
                          await postComment(
                              messageID, UserAuth.username, newComment, newLink, _base64);

                          // Close the dialog after posting a comment
                          Navigator.of(context).pop();
                        },
                      ),
                    ],
                  ),
                ],
              ),
            ),
          ),
          actions: [
            TextButton(
              onPressed: () {
                Navigator.of(context).pop();
              },
              child: const Text('Close'),
            ),
          ],
        );
      },
    );
  }

  // Function to show a dialog for editing comments
  void _showEditCommentDialog(
      int messageID, int commentID, String currentContent) {
    String updatedComment = currentContent;

    showDialog(
      context: context,
      builder: (context) {
        return AlertDialog(
          title: const Text('Edit Comment'),
          content: Column(
            mainAxisSize: MainAxisSize.min,
            children: [
              TextField(
                onChanged: (value) {
                  setState(() {
                    updatedComment = value;
                  });
                },
                decoration: InputDecoration(
                  labelText: 'Edit your comment...',
                  hintText: currentContent,
                ),
              ),
              ElevatedButton(
                onPressed: () async {
                  // Call updateComment function
                  await updateComment(messageID, commentID, updatedComment);

                  // Close the dialog after updating the comment
                  Navigator.of(context).pop();
                },
                child: const Text('Update Comment'),
              ),
              TextButton(
                onPressed: () {
                  // Close the dialog without updating the comment
                  Navigator.of(context).pop();
                },
                child: const Text('Cancel'),
              ),
            ],
          ),
        );
      },
    );
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      // Top Navigation Bar
      appBar: AppBar(
        backgroundColor: Theme.of(context).colorScheme.primary,
        automaticallyImplyLeading:
            false, // Set this to false to hide the back arrow
        leading: Image.asset(
          'lib/assets/images/skytrailLogo.png',
          width: 40, // Adjust the width as needed
          height: 40, // Adjust the height as needed
        ),
        title: Text(
          widget.title,
          style: const TextStyle(
            color: Colors.white, // Set the text color to white
          ),
        ),
        actions: [
          // user profile button - redirects you to user profile route
          IconButton(
            icon: const Icon(Icons.face),
            color: Colors.white,
            onPressed: () {
              Navigator.of(context).pushReplacementNamed('/profile');
            },
          ),
          // logout button - calls logout function from our googleAPI file
          IconButton(
            icon: const Icon(Icons.logout),
            color: Colors.white,
            onPressed: () async {
              showLogoutConfirmationDialog(context);
            },
          ),
        ],
      ),

      // Displays all ideas / messages
      body: RefreshIndicator (
      onRefresh: _refreshData,
      child: FutureBuilder<List<Idea>>(
        future: futureIdeas,
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
            // Display a list of ideas using a ListView
            return ListView.builder(
              itemCount: snapshot.data!.length,
              itemBuilder: (context, index) {
                var idea = snapshot.data![index];
                return Card(
                  child: Column(
                    children: [
                      // Display the subject and idea of the post.
                      ListTile(
                        title: Text('${idea.subject} - ${idea.username}'),
                        // Add username next to title
                        subtitle: Text('${idea.idea}'),
                      ),
                      // Display like count, like/dislike button, comment button
                      ListTile(
                          subtitle: Text('${idea.link}'),
                      ),
                      //Text(_base64 ?? 'No Image Selected'),
                     // _imageFile == null ? Container(): Image.file(_imageFile!),
                      InkWell(
                          onTap: () => launchUrlString(idea.link ?? ''),
                        child: Padding(
                          padding: const EdgeInsets.all(8.0),
                          child: Text(
                            'Click here to open link',
                            style: TextStyle(
                              color: Colors.blue,
                              decoration: TextDecoration.underline,
                            ),
                          ),
                        ),
                      ),
                      //_imageFile == null ? Container(): Image.file(_imageFile!),
                      ListTile(
                        trailing: Row(
                          mainAxisSize: MainAxisSize.min,
                          children: [
                            IconButton(
                              icon: const Icon(Icons.chat_bubble),
                              onPressed: () async {
                                try {
                                  _showCommentDialog(idea.id);
                                } catch (e) {
                                  print(
                                      'Error while clicking on comment icon: $e');
                                }
                              },
                            ),
                            const SizedBox(width: 190),

                            // Like button
                            IconButton(
                              icon: const Icon(Icons.arrow_upward),
                              onPressed: () async {
                                try {
                                  // Call to likeIdeas request
                                  await likeIdeas(idea.id,
                                      idea.likeCount); // Like the idea and update the like count.
                                  setState(() {
                                    idea.likeCount += 1;
                                  });
                                } catch (e) {
                                  print('Error while liking idea: $e');
                                }
                              },
                            ),

                            // Add some spacing between the buttons
                            const SizedBox(width: 10),
                            Text('${idea.likeCount}'),
                            const SizedBox(width: 5),

                            // Dislike button
                            IconButton(
                              icon: const Icon(Icons.arrow_downward),
                              onPressed: () async {
                                try {
                                  // Call to dislikeIdeas request
                                  await dislike(idea.id,
                                      idea.likeCount); // Dislike the idea and update the like count.
                                  setState(() {
                                    idea.likeCount -= 1;
                                  });
                                } catch (e) {
                                  print('Error while disliking idea: $e');
                                }
                              },
                            ),
                          ],
                        ),
                      ),
                    ],
                  ),
                );
              },
            );
          }
        },
      )
      ),
      floatingActionButton: FloatingActionButton(
        onPressed: () {
          _showPostIdeaDialog();
        },
        child: const Icon(Icons.add),
      ),
    );
  }
}
