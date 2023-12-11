class Comment {
  final int commentID; // The comment identifier
  final String content; // Content of the comment
  final String username; // The username attached to comment
  final int messageID; // MessageID that comment is posted to
  final String? link;
  final String? base64String;


  Comment({
    required this.commentID,
    required this.content,
    required this.username,
    required this.messageID,
    this.link,
     this.base64String

  });

  // Factory method to create an 'Idea' object from a JSON map.
  factory Comment.fromJson(Map<String, dynamic> json) {
    return Comment(
      commentID: json['cCommentID'] as int, 
      content: json['cContent'] as String? ??
          '', 
      username: json['cUsername'] as String? ??
          '', 
      messageID: json['cMessageID'] as int,

      link: json['cLink'] as String?,

      base64String: json['cBase64String'] as String? 

    ); 
  }

  // Method to convert an 'Comment' object to a JSON map.
  Map<String, dynamic> toJson() => {
        'cCommentID': commentID, 
        'cContent': content, 
        'cUsername': username, 
        'cMessageID': messageID, 
        'cLink': link,
        'cBase64String': base64String

      };
}
