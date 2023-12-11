package edu.lehigh.cse216.thd226.backend;

/**
 * messageData object
 */
public class messageData {
    /**
     * The ID of this row of the database
     */
    int mId;
    /**
     * The subject stored in this row
     */
    String mSubject;
    /**
     * The message stored in this row
     */
    String mIdea;
     /**
     * The Like_count stored in this row
     */
    int mLike_Count;
    /**
     * The user that made this row
     */
    String mUserName;


    /**
     * The link attached to a message
     */
    String mLink;
    
     /**
     * The id of the file attached to a message
     */
    String mFileLink;
    /**
     * Construct a messageData object by providing values for its fields
     * @param id            id of the message
     * @param subject       title of the message
     * @param idea          content of the message
     * @param like_count    total number of likes of the message
     * @param username      username of the poster
     * @param link          link attached to message
     * @param file_link        link of the file on google drive
     */
    public messageData(int id, String subject, String idea, int like_count, String username, String link, String file_link) {
        mId = id;
        mSubject = subject;
        mIdea = idea;
        mLike_Count = like_count;
        mUserName = username;
        mLink = link;
        mFileLink = file_link;
    }
}
