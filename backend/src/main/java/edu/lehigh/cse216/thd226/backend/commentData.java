package edu.lehigh.cse216.thd226.backend;

/**
 * commentData class, a file used to store the individual components
 * of the given comment class
 */
public class commentData {
    /**
     * The ID of this comment
     */
    int cCommentID;
    /**
     * The content stored in this comment
     */
    String cContent;
    /**
     * The username of the user that posted this comment
     */
    String cUsername;
    /**
     * The ID of the message this comment is attached to
     */
    int cMessageID;

    /**
     * The id of the file attached to a message
     */
    String cLink;

     /**
     * The id of the file attached to a message
     */
    String cFileLink;
  
    /**
     * Constructor for commentData when given the parameters
     * @param commentID the generated id for this comment
     * @param content the user given input
     * @param username the user given username
     * @param messageID the generate id for this message
     * @param link the link attached to comment
     * @param file_link the file attached to comment
     */
    public commentData(int commentID, String content, String username, int messageID, String link, String file_link) {
        cCommentID = commentID;
        cContent = content;
        cUsername = username;
        cMessageID = messageID;
        cLink = link;
        cFileLink = file_link;
    }
}