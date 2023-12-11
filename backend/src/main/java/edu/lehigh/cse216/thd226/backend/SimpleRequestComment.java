package edu.lehigh.cse216.thd226.backend;

/**
 * SimpleRequestComment provides a format for clients to present username and message 
 * strings to the server.
 * 
 * NB: since this will be created from JSON, all fields must be public, and we
 *     do not need a constructor.
 */
public class SimpleRequestComment{
    /**
     * The message being provided by the client.
     */
    public String cContent;
    
    /**
     * The username being provided by the client.
     */
    public String cUsername;


    /**
     * The id the message has
     */
    public int cMessageID;


    /**
     * The link attached to a comment
     */
    public String cLink;

    /**
     * The base64 string of a file attached to a comment
     */
    public String cBase64String;
    

}