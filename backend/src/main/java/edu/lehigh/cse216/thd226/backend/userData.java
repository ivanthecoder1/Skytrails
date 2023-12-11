package edu.lehigh.cse216.thd226.backend;
/**
 * User data class
 */
public class userData {
    /**
     * This user's username
     */
    String uUsername;
    /**
     * The user's first name
     */
    String uFirstname;
    /**
     * The user's last name
     */
    String uLastname;

    String uEmail;
    /**
     * The gender of the user
     */
    String uGender;
    /**
     * The sexual orientation of the user
     */
    String uSexorientation;
    /**
     * A note about the user
     */
    String uNote;
    /**
     * Construct a userData object by providing values for its fields
     * @param username          provided username by user
     * @param firstname         user's first name
     * @param lastname          user's last name
     * @param email             user's email
     * @param gender            user's gender
     * @param sexorientation    user's orientation
     * @param note              a note abt the user
     */
    public userData(String username, String firstname, String lastname, String email, String gender, String sexorientation, String note) {
        uUsername = username;
        uFirstname = firstname;
        uLastname = lastname;
        uEmail = email;
        uGender = gender;
        uSexorientation = sexorientation;
        uNote = note;
    }
}