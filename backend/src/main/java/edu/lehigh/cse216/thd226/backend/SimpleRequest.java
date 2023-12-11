package edu.lehigh.cse216.thd226.backend;

/**
 * SimpleRequest provides a format for clients to present title and message 
 * strings to the server.
 * 
 * NB: since this will be created from JSON, all fields must be public, and we
 *     do not need a constructor.
 */
public class SimpleRequest {
    /**
     * The title being provided by the client.
     */
    public String mTitle;

    /**
     * The message being provided by the client.
     */
    public String mMessage;

    /**
     * The number of likes the message has
     */
    public int mLikes;

    /**
     * The username of the poster
     */
    public String mUserName;


    /**
     * The link url sent from client
     */
    public String mLink;

    /**
     * The base64 string sent from client
     */
    public String mBase64String;


}