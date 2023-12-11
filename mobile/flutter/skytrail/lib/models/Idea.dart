class Idea {
  final int id; // Unique identifier for the idea.
  final String subject; // Title or subject of the idea.
  final String idea; // The content or description of the idea.
  int likeCount; // The number of likes the idea has received.
  final String username; // The username attached to idea
  final String? link;
  final String? base64String;

  Idea({
    required this.id,
    required this.subject,
    required this.idea,
    required this.likeCount,
    required this.username,
     this.link,
     this.base64String
  });

  // Factory method to create an 'Idea' object from a JSON map.
  factory Idea.fromJson(Map<String, dynamic> json) {
    return Idea(
      id: json['mId'] as int, // Extract the 'id' as an integer.
      subject: json['mSubject'] as String? ??
          '', // Extract 'subject' with a fallback to an empty string.
      idea: json['mIdea'] as String? ??
          '', // Extract 'idea' with a fallback to an empty string.
      likeCount: json['mLike_Count'] as int? ??
          0, // Extract 'likeCount' with a fallback to 0.
      username: json['mUserName'] as String? ??
          '', 
      link: json['mLink'] as String?,
      base64String: json['mBase64String'] as String? 
    ); //The fallbacks were added as an attempt to remove null-pointers
  }

  // Method to convert an 'Idea' object to a JSON map.
  Map<String, dynamic> toJson() => {
        'mId': id, // Map 'id' to 'mID'.
        'mSubject': subject, // Map 'subject' to 'mTitle'.
        'mIdea': idea, // Map 'idea' to 'mContent'.
        'mLike_Count': likeCount, // Map 'likeCount' to 'mLikes'.
        'mUserName': username, // Map 'username' to 'mUserName'.
        'mLink': link,
        'mBase64String': base64String
      };
}
