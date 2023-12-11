// Define a class user so we can extract these fields
class User {
  final String username;
  final String firstName;
  final String lastName;
  final String email;
  final String gender;
  final String sexOrientation;
  final String note;

  User ({
    required this.username,
    required this.firstName,
    required this.lastName,
    required this.email,
    required this.gender,
    required this.sexOrientation,
    required this.note,
  });

  // Factory method to create an 'Idea' object from a JSON map.
  factory User.fromJson(Map<String, dynamic> json) {
    return User(
      username: json['uUsername'] as String,
      firstName: json['uFirstname'] as String,
      lastName: json['uLastname'] as String,
      email: json['uEmail'] as String,
      gender: json['uGender'] as String,
      sexOrientation: json['uSexorientation'] as String,
      note: json['uNote'] as String
    ); //The fallbacks were added as an attempt to remove null-pointers
  }

  // Method to convert an 'User' object to a JSON map.
  Map<String, dynamic> toJson() => {
        'uUsername': username, 
        'uFirstname': firstName, 
        'uLastname': lastName, 
        'uEmail': email,
        'uGender': gender,
        'uSexorientation': sexOrientation,
        'uNote': note, 
      };
}
