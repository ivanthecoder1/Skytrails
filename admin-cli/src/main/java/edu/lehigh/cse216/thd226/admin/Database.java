package edu.lehigh.cse216.thd226.admin;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.Statement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

public class Database {
    /**
     * The connection to the database.  When there is no connection, it should
     * be null.  Otherwise, there is a valid open connection
     */
    private Connection mConnection;

    //Message Prepared Statements

    /**
     * A prepared statement for getting all data in the database
     */
    private PreparedStatement mSelectAll;

    /**
     * A prepared statement for getting one row from the database
     */
    private PreparedStatement mSelectOne;

    /**
     * A prepared statement for deleting a row from the database
     */
    private PreparedStatement mDeleteOne;

    /**
     * A prepared statement for inserting into the database
     */
    private PreparedStatement mInsertOne;
    private PreparedStatement mInsertOneIMG;

    /**
     * A prepared statement for updating a single row in the database
     */
    private PreparedStatement mUpdateOne;

    /**
     * A prepared statement for creating the table in our database
     */
    private PreparedStatement mCreateTable;

    /**
     * A prepared statement for dropping the table in our database
     */
    private PreparedStatement mDropTable;


    /**
     * A prepared statements for adding necessary constraints to the message table
     */
    private PreparedStatement mAddConstraint;

    private PreparedStatement mAddColumn;

    //make prepared statement for likes

    private PreparedStatement mEditLike;

    //User prepared statements

    /**
     * A prepared statement for creating the table in our database
     */
    private PreparedStatement uCreateTable;

    /**
     * A prepared statement for banning the user
     */

    private Statement uBanUser;

    /**
     * A prepared statement for returning the list of banned users
     */
    private Statement uGetBannedUsers;

    /**
     * A prepared statement for removing the user
     */
    private PreparedStatement uDeleteOne;

    /**
     * A prepared statement for adding the user
     */
    private PreparedStatement uInsertOne;

    /**
     * A prepared statement for getting all users
     */
    private PreparedStatement uSelectAll;

    /**
     * A prepared statement for getting one user
     */
    private PreparedStatement uSelectOne;

    /**
     * A prepared statement for deleting the table
     */
    private PreparedStatement uDropTable;

    /**
     * A prepared statement for creating comment table
     */
    private PreparedStatement cCreateTable;

    /**
     * A prepared statement for dropping the table
     */
    private PreparedStatement cDropTable;

    /**
     * A prepared statement for adding a column
     */

    private PreparedStatement cAddColumn;

    /**
     * A prepared statement for removing one comment
     */
    private PreparedStatement cDeleteOne;

    /**
     * A prepared statement for adding one comment
     */
    private PreparedStatement cInsertOne;
    private PreparedStatement cInsertOneIMG;

    /**
     * A prepared statement for getting all comments
     */
    private PreparedStatement cSelectAll;


    /**
     * A prepared statement for getting one comment
     */
    private PreparedStatement cSelectOne;

    private PreparedStatement xCreateTable;
    
    private PreparedStatement xDropTable;




    /**
     * messageData
     */
    public static class messageData {
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
        String mUsername;

        /**
         * The file like
         */
        String mFileLink;

        String mGenLink;



        /**
         * 
         * @param id
         * @param subject
         * @param idea
         * @param like_count
         * @param username
         * @param fileLink
         * @param genLink
         
         * Construct a messageData object by providing values for its fields if it has an attached file
         */
        public messageData(int id, String subject, String idea, int like_count, String username, String genLink, String fileLink) {
            mId = id;
            mSubject = subject;
            mIdea = idea;
            mLike_Count = like_count;
            mUsername = username;
            mGenLink = genLink;
            mFileLink = fileLink;
        }
        

        public int getLikes(){
            return mLike_Count;
        }

        public int getID(){
            return mId;
        }
    }

    /**
     * User data class
     */
    public static class userData {
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
        /**
         * The user's email
         */
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
         * Construct a messageData object by providing values for its fields
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

    /**
     * commentData
     */
    public static class commentData {
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
        /* 
         * The link for a file if added
         */
        String cFileLink;
        String cGenLink;

        /**
          * 
          * @param commentID
          * @param content
          * @param username
          * @param messageID
          * @param fileLink
          * @param generalLink
          * Construct a messageData object by providing values for its fields  if it has a file link
          */
        public commentData(int commentID, String content, String username, int messageID,  String genLink, String fileLink) {
            cCommentID = commentID;
            cContent = content;
            cUsername = username;
            cMessageID = messageID;
            cGenLink = genLink;
            cFileLink = fileLink;
            
        }

        public int getID(){
            return cCommentID;
        }
    }

    /**
     * The Database constructor is private: we only create Database objects 
     * through the getDatabase() method.
     */
    private Database() {
    }

    /**
     * Get a fully-configured connection to the database
     * 
     * @param ip   The IP address of the database server
     * @param port The port on the database server to which connection requests
     *             should be sent
     * @param user The user ID to use when connecting
     * @param pass The password to use when connecting
     * 
     * @return A Database object, or null if we cannot connect properly
     */

    static Database getDatabase(String ip, String port, String user, String pass) {
        // Create an un-configured Database object
        Database db = new Database();

        // Give the Database object a connection, fail if we cannot get one
        try {
            Connection conn = DriverManager.getConnection("jdbc:postgresql://" + ip + ":" + port + "/", user, pass);
            if (conn == null) {
                System.err.println("Error: DriverManager.getConnection() returned a null object");
                return null;
            }
            db.mConnection = conn;
        } catch (SQLException e) {
            System.err.println("Error: DriverManager.getConnection() threw a SQLException");
            e.printStackTrace();
            return null;
        }

        // Attempt to create all of our prepared statements.  If any of these 
        // fail, the whole getDatabase() call should fail
        try {
            // NB: we can easily get ourselves in trouble here by typing the
            //     SQL incorrectly.  We really should have things like "tblData"
            //     as constants, and then build the strings for the statements
            //     from those constants.

            // Note: no "IF NOT EXISTS" or "IF EXISTS" checks on table 
            // creation/deletion, so multiple executions will cause an exception
            db.mCreateTable = db.mConnection.prepareStatement("CREATE TABLE message (id SERIAL PRIMARY KEY, subject VARCHAR(50) NOT NULL, idea VARCHAR(1024) NOT NULL, like_count INTEGER, username VARCHAR(255) NOT NULL, genLink VARCHAR(255), fileLink VARCHAR(255) )");
            db.mAddConstraint = db.mConnection.prepareStatement("ALTER TABLE message ADD CONSTRAINT message_user FOREIGN KEY (username) REFERENCES users (username)");
            db.mAddColumn = db.mConnection.prepareStatement("ALTER TABLE message ADD file_link VARCHAR(255)");
            db.mDropTable = db.mConnection.prepareStatement("DROP TABLE message");
            db.mDeleteOne = db.mConnection.prepareStatement("DELETE FROM message WHERE id = ?");
            db.mInsertOne = db.mConnection.prepareStatement("INSERT INTO message VALUES (default, ?, ?, ?, ?, ?)");
            db.mInsertOneIMG = db.mConnection.prepareStatement("INSERT INTO message VALUES (default, ?, ?, ?, ?, ?)");
            db.mSelectAll = db.mConnection.prepareStatement("SELECT * FROM message");
            db.mSelectOne = db.mConnection.prepareStatement("SELECT * from message WHERE id=?");
            db.mUpdateOne = db.mConnection.prepareStatement("UPDATE message SET idea = ? WHERE id = ?");
            db.mEditLike = db.mConnection.prepareStatement("UPDATE message SET like_count = ? WHERE id = ?");

            db.uCreateTable = db.mConnection.prepareStatement("CREATE TABLE users (username VARCHAR(255) PRIMARY KEY, firstname VARCHAR(50), lastname VARCHAR(50), email VARCHAR(50), gender VARCHAR(50), sexorientation VARCHAR(50), note VARCHAR(255) )");
            db.uDropTable = db.mConnection.prepareStatement("DROP TABLE users");
            db.uDeleteOne = db.mConnection.prepareStatement("DELETE FROM users WHERE username = ?");
            db.uInsertOne = db.mConnection.prepareStatement("INSERT INTO users VALUES (?, ?, ?, ?, ?, ?, ?)");
            db.uSelectAll = db.mConnection.prepareStatement("SELECT * FROM users");
            db.uSelectOne = db.mConnection.prepareStatement("SELECT * from users WHERE username = ?");
            db.uBanUser = db.mConnection.createStatement();
            db.uGetBannedUsers = db.mConnection.createStatement();

            db.cCreateTable = db.mConnection.prepareStatement("CREATE TABLE comment(comment_id SERIAL PRIMARY KEY, content VARCHAR(1024), username VARCHAR(255), message_id INTEGER,genLink VARCHAR(255), fileLink VARCHAR(255), CONSTRAINT comment_user FOREIGN KEY(username) REFERENCES users(username), CONSTRAINT comment_message FOREIGN KEY(message_id) REFERENCES message(id) )");
            db.cDropTable = db.mConnection.prepareStatement("DROP TABLE comment");
            db.cAddColumn = db.mConnection.prepareStatement("ALTER TABLE comment ADD file_link VARCHAR(255)");
            db.cDeleteOne = db.mConnection.prepareStatement("DELETE FROM comment WHERE comment_id = ?");
            db.cInsertOne = db.mConnection.prepareStatement("INSERT INTO comment VALUES (default, ?, ?, ?)");
            db.cInsertOneIMG = db.mConnection.prepareStatement("INSERT INTO comment VALUES (default, ?, ?, ?, ?)");
            db.cSelectAll = db.mConnection.prepareStatement("SELECT * FROM comment");
            db.cSelectOne = db.mConnection.prepareStatement("SELECT * from comment WHERE comment_id = ?");            
        
            db.xCreateTable = db.mConnection.prepareStatement("CREATE TABLE user_likes (username VARCHAR(255), message_id INT, likes INT, wascalled INT)");
            db.xDropTable = db.mConnection.prepareStatement("DROP TABLE user_likes");
        
        } catch (SQLException e) {
            System.err.println("Error creating prepared statement");
            e.printStackTrace();
            db.disconnect();
            return null;
        }
        return db;
    }

    /**
     * Close the current connection to the database, if one exists.
     * 
     * NB: The connection will always be null after this call, even if an 
     *     error occurred during the closing operation.
     * 
     * @return True if the connection was cleanly closed, false otherwise
     */
    boolean disconnect() {
        if (mConnection == null) {
            System.err.println("Unable to close connection: Connection was null");
            return false;
        }
        try {
            mConnection.close();
        } catch (SQLException e) {
            System.err.println("Error: Connection.close() threw a SQLException");
            e.printStackTrace();
            mConnection = null;
            return false;
        }
        mConnection = null;
        return true;
    }

    /**
     * Insert a message into the database
     * 
     * @param subject The subject for this new row
     * @param idea The message body for this new row
     * @param like_count The like_count for this new row
     * 
     * @return The number of rows that were inserted
     */
    int insertMessage(String subject, String idea, int like_count, String username) {
        int count = 0;
        try {
            mInsertOne.setString(1, subject);
            mInsertOne.setString(2, idea);
            mInsertOne.setInt(3, like_count);
            mInsertOne.setString(4, username);
            mInsertOne.setString(5, null);
            count += mInsertOne.executeUpdate();
        } catch (SQLException e) {
            count = -1;
        }
        return count;
    }

    int insertMessage(String subject, String idea, int like_count, String username, String genLink){
        int count = 0;
        try {
            mInsertOne.setString(1, subject);
            mInsertOne.setString(2, idea);
            mInsertOne.setInt(3, like_count);
            mInsertOne.setString(4, username);
            mInsertOne.setString(5, genLink);
            count += mInsertOne.executeUpdate();
        } catch (SQLException e) {
            count = -1;
        }
        return count;
    }

    /**
     * Insert a message into the database
     * 
     * @param subject The subject for this new row
     * @param idea The message body for this new row
     * @param like_count The like_count for this new row
     * @param link The link for the image being shared
     * 
     * @return The number of rows that were inserted
     */
    int insertImageMessage(String subject, String idea, int like_count, String username, String fileLink){
        int count = 0;
        try {
            mInsertOneIMG.setString(1, subject);
            mInsertOneIMG.setString(2, idea);
            mInsertOneIMG.setInt(3, like_count);
            mInsertOneIMG.setString(4, username);
            mInsertOneIMG.setString(5, null);
            mInsertOneIMG.setString(6, fileLink);
            count += mInsertOne.executeUpdate();
        } catch (SQLException e) {
            count = -1;
        }
        return count;
    }

    /**
     * Insert a message into the database
     * 
     * @param subject The subject for this new row
     * @param idea The message body for this new row
     * @param like_count The like_count for this new row
     * @param link The link for the image being shared
     * 
     * @return The number of rows that were inserted
     */
    int insertImageMessage(String subject, String idea, int like_count, String username, String genLink, String fileLink){
        int count = 0;
        try {
            mInsertOneIMG.setString(1, subject);
            mInsertOneIMG.setString(2, idea);
            mInsertOneIMG.setInt(3, like_count);
            mInsertOneIMG.setString(4, username);
            mInsertOneIMG.setString(5, genLink);
            mInsertOneIMG.setString(6, fileLink);
            count += mInsertOne.executeUpdate();
        } catch (SQLException e) {
            count = -1;
        }
        return count;
    }

    /**
     * Query the database for a list of all subjects and their IDs
     * 
     * @return All rows, as an ArrayList
     */
    
    ArrayList<messageData> selectAllMessages() {
        ArrayList<messageData> res = new ArrayList<messageData>();
        try {
            ResultSet rs = mSelectAll.executeQuery();
            while (rs.next()) {
                res.add(new messageData(
                        rs.getInt("id"),
                        rs.getString("subject"),
                        rs.getString("idea"),
                        rs.getInt("like_count"),
                        rs.getString("username"),
                        rs.getString("genLink"),
                        rs.getString("fileLink")
                ));
            }
            rs.close();
            return res;
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }
    

    /**
     * Get all data for a specific row, by ID
     * 
     * @param id The id of the row being requested
     * 
     * @return The data for the requested row, or null if the ID was invalid
     */
    messageData selectMessage(int id) {
        messageData res = null;
        try {
            mSelectOne.setInt(1, id);
            ResultSet rs = mSelectOne.executeQuery();
            if (rs.next()) {
                res = new messageData(rs.getInt("id"), rs.getString("subject"), rs.getString("idea"), rs.getInt("like_count"), rs.getString("username"), rs.getString("genLink"), rs.getString("fileLink"));
            }else{
                return null;
            }
        } catch (SQLException e) {
            return null;
        }
        return res;
    }

    /**
     * Delete a row by ID
     * 
     * @param id The id of the row to delete
     * 
     * @return The number of rows that were deleted.  -1 indicates an error.
     */
    int deleteMessage(int id) {
        int res = -1;
        try {
            mDeleteOne.setInt(1, id);
            res = mDeleteOne.executeUpdate();
        } catch (SQLException e) {
            return -1;
        }
        return res;
    }

    /**
     * Update idea for a specific row, by ID
     * 
     * @param id The id of the row being requested
     * @param idea The new idea
     * 
     * @return The data for the requested row, or null if the ID was invalid
     */
    int updateMessage(int id, String idea){
        int res = -1;
        try{
            mUpdateOne.setInt(2, id);
            mUpdateOne.setString(1, idea);
            res = mUpdateOne.executeUpdate();
        }catch(SQLException e){
            return -1;
        }
        return res;
    }

    /**
     * Update the like count for a row in the database
     * 
     * @param id The id of the row to update
     * @param like_count new like count
     * 
     * @return The number of rows that were updated.  -1 indicates an error.
     */
    int editLike(int id, int like_count){
        int res = -1;
        try {
            mEditLike.setInt(2, id);
            mEditLike.setInt(1, like_count);
            res = mEditLike.executeUpdate();
        } catch (SQLException e) {
            return -1;
        } 
        return res;
    }

    /**
     * Insert a message into the database
     * 
     * @param username The username for this new row
     * @param firstname The user's first name
     * @param lastname The user's last name
     * @param gender The user's gender
     * @param sexorientation The user's sexual orientation
     * 
     * @return The number of rows that were inserted
     */
    int insertUser(String username, String firstname, String lastname, String email, String gender, String sexorientation, String note) {
        int count = 0;
        try {
            uInsertOne.setString(1, username);
            uInsertOne.setString(2, firstname);
            uInsertOne.setString(3, lastname);
            uInsertOne.setString(4, email);
            uInsertOne.setString(5, gender);
            uInsertOne.setString(6, sexorientation);
            uInsertOne.setString(7, note);
            count += uInsertOne.executeUpdate();
        } catch (SQLException e) {
            return -1;
        }
        return count;
    }

    /**
     * Query the database for a list of all subjects and their IDs
     * 
     * @return All rows, as an ArrayList
     */
    ArrayList<userData> selectAllUsers() {
        ArrayList<userData> res = new ArrayList<userData>();
        try {
            ResultSet rs = uSelectAll.executeQuery();
            while (rs.next()) {
                res.add(new userData(rs.getString("username"), rs.getString("firstname"), rs.getString("lastname"), rs.getString("email"), rs.getString("gender"), rs.getString("sexorientation"), rs.getString("note")));
            }
            rs.close();
            return res;
        } catch (SQLException e) {
            return null;
        }
    }

    /**
     * Get all data for a specific user, by ID
     * 
     * @param id The username of the row being requested
     * 
     * @return The data for the requested row, or null if the ID was invalid
     */
    userData selectUser(String username) {
        userData res = null;
        try {
            uSelectOne.setString(1, username);
            ResultSet rs = uSelectOne.executeQuery();
            if (rs.next()) {
                res = new userData(rs.getString("username"), rs.getString("firstname"), rs.getString("lastname"), rs.getString("email"), rs.getString("gender"), rs.getString("sexorientation"), rs.getString("note"));
            }else{
                return null;
            }
        } catch (SQLException e) {
            return null;
        }
        return res;
    }
    
    /**
     * Delete a user by username
     * 
     * @param id The id of the row to delete
     * 
     * @return The number of rows that were deleted.  -1 indicates an error.
     */
    int deleteUser(String username) {
        int res = -1;
        try {
            uDeleteOne.setString(1, username);
            res = uDeleteOne.executeUpdate();
        } catch (SQLException e) {
            return -1;
        }
        return res;
    }

    /**
     * Ban a user by username
     * 
     * @param username The username of the user to ban
     * 
     * @return The number of rows that were deleted.  -1 indicates an error.
     */
    boolean banUser(String username) {
        String constraint = "disallow" + username;
        try {
            uBanUser.execute("ALTER TABLE users ADD CONSTRAINT " + constraint + " CHECK (username <> '" + username + "' )");
        } catch (SQLException e) {
            return false;
        }
        return true;
    }

    /**
     * Ban a user by username
     * 
     * @param username The username of the user to ban
     * 
     * @return The number of rows that were deleted.  -1 indicates an error.
     */
    ArrayList<String> getBannedUsers(){
        ArrayList<String> res = new ArrayList<String>();
        try {
            ResultSet rs = uGetBannedUsers.executeQuery("SELECT constraint_name FROM information_schema.table_constraints WHERE table_name='users' AND constraint_type='CHECK';");
            while (rs.next()) {
                res.add(rs.getString("constraint_name"));
            }
        } catch (SQLException e) {
            return null;
        }
        res.remove(res.size() - 1);
        return res;
    }

    /**
     * Insert a comment into the database
     * 
     * @param content The text of the comment
     * @param username The user that posted this comment
     * @param message_id The message this comment is attached to
     * 
     * @return The number of rows that were inserted
     */
    int insertComment(String content, String username, int message_id) {
        int count = 0;
        try {
            cInsertOne.setString(1, content);
            cInsertOne.setString(2, username);
            cInsertOne.setInt(3, message_id);
            count += cInsertOne.executeUpdate();
        } catch (SQLException e) {
            return -1;
        }
        return count;
    }

    /**
     * Insert a comment into the database
     * 
     * @param content The text of the comment
     * @param username The user that posted this comment
     * @param message_id The message this comment is attached to
     * @param genLink The link added to the comment
     * 
     * @return The number of rows that were inserted
     */
    int insertCommentLink(String content, String username, int message_id) {
        int count = 0;
        try {
            cInsertOne.setString(1, content);
            cInsertOne.setString(2, username);
            cInsertOne.setInt(3, message_id);
            count += cInsertOne.executeUpdate();
        } catch (SQLException e) {
            return -1;
        }
        return count;
    }

    /**
     * Insert a comment into the database
     * 
     * @param content The text of the comment
     * @param username The user that posted this comment
     * @param message_id The message this comment is attached to
     * @param link The image of the image being shared
     * 
     * @return The number of rows that were inserted
     */
    int insertCommentIMG(String content, String username, int message_id, String link) {
        int count = 0;
        try {
            cInsertOneIMG.setString(1, content);
            cInsertOneIMG.setString(2, username);
            cInsertOneIMG.setInt(3, message_id);
            cInsertOneIMG.setString(4, link);
            count += cInsertOne.executeUpdate();
        } catch (SQLException e) {
            return -1;
        }
        return count;
    }

    /**
     * Query the database for a list of all comments and their IDs
     * 
     * @return All rows, as an ArrayList
     */
    ArrayList<commentData> selectAllComments() {
        ArrayList<commentData> res = new ArrayList<commentData>();
        try {
            ResultSet rs = cSelectAll.executeQuery();
            while (rs.next()) {
                res.add(new commentData(rs.getInt("comment_id"), rs.getString("content"), rs.getString("username"), rs.getInt("message_id"), rs.getString("gen_link"), rs.getString("file_link")));
            }
            rs.close();
            return res;
        } catch (SQLException e) {
            return null;
        }
    }

    /**
     * Get all data for a specific user, by ID
     * 
     * @param comment_id The id of the comment being requested
     * 
     * @return The data for the requested row, or null if the ID was invalid
     */
    commentData selectComment(int comment_id) {
        commentData res = null;
        try {
            cSelectOne.setInt(1, comment_id);
            ResultSet rs = cSelectOne.executeQuery();
            if (rs.next()) {
                res = new commentData(rs.getInt("comment_id"), rs.getString("content"), rs.getString("username"), rs.getInt("message_id"), rs.getString("gen_link"), rs.getString("file_link"));
            }
        } catch (SQLException e) {
            return null;
        }
        return res;
    }
    
    /**
     * Delete a user by username
     * 
     * @param comment_id The id of the row to delete
     * 
     * @return The number of rows that were deleted.  -1 indicates an error.
     */
    int deleteComment(int comment_id) {
        int res = -1;
        try {
            cDeleteOne.setInt(1, comment_id);
            res = cDeleteOne.executeUpdate();
        } catch (SQLException e) {
            return -1;
        }
        return res;
    }






    /**
     * Create tblData.  If it already exists, this will print an error
     */
    void createAllTables() {
        try{
            uCreateTable.execute();
            mCreateTable.execute();
            mAddConstraint.execute();
            cCreateTable.execute();
            xCreateTable.execute();
        }catch(SQLException e){
            e.printStackTrace();
        }
    }

    /**
     * Remove tblData from the database.  If it does not exist, this will print
     * an error.
     */
    void dropAllTables() {
        try {
            xDropTable.execute();
            cDropTable.execute();
            mDropTable.execute();
            uDropTable.execute();
            
            
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * Adds the file link column to the tables. If it already exists then it will print an error.
     */

    void addColumns() {
        try{
            mAddColumn.execute();
            cAddColumn.execute();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


    /**
     * Generates filler data for the user table
     */

    void populateUsers(){
        try{
            uInsertOne.setString(1, "username");
            uInsertOne.setString(2, "first_name");
            uInsertOne.setString(3, "last_name");
            uInsertOne.setString(4, "email");
            uInsertOne.setString(5, "gender");
            uInsertOne.setString(6, "sexualorientation");
            uInsertOne.setString(7, "note");
            uInsertOne.executeUpdate();

            uInsertOne.setString(1, "ecunnington0");
            uInsertOne.setString(2, "Eberhard");
            uInsertOne.setString(3, "Cunnington");
            uInsertOne.setString(4, "ecunnington0@aol.com");
            uInsertOne.setString(5, "Male");
            uInsertOne.setString(6, "Browsetype");
            uInsertOne.setString(7, "Expanded homogeneous archive");
            uInsertOne.executeUpdate();

            uInsertOne.setString(1, "aarsey1");
            uInsertOne.setString(2, "Annnora");
            uInsertOne.setString(3, "Arsey");
            uInsertOne.setString(4, "aarsey1@weather.com");
            uInsertOne.setString(5, "Female");
            uInsertOne.setString(6, "Skiba");
            uInsertOne.setString(7, "Reduced modular budgetary management");
            uInsertOne.executeUpdate();

            uInsertOne.setString(1, "celson2");
            uInsertOne.setString(2, "Carmine");
            uInsertOne.setString(3, "Elson");
            uInsertOne.setString(4, "celson2@answers.com");
            uInsertOne.setString(5, "Male");
            uInsertOne.setString(6, "Twiyo");
            uInsertOne.setString(7, "Upgradable non-volatile internet solution");
            uInsertOne.executeUpdate();

            uInsertOne.setString(1, "fmacallaster3");
            uInsertOne.setString(2, "Frasco");
            uInsertOne.setString(3, "MacAllaster");
            uInsertOne.setString(4, "fmacallaster3@odnoklassniki.ru");
            uInsertOne.setString(5, "Male");
            uInsertOne.setString(6, "Eire");
            uInsertOne.setString(7, "Decentralized dynamic model");
            uInsertOne.executeUpdate();

            uInsertOne.setString(1, "mdugald4");
            uInsertOne.setString(2, "Murielle");
            uInsertOne.setString(3, "Dugald");
            uInsertOne.setString(4, "mdugald4@si.edu");
            uInsertOne.setString(5, "Female");
            uInsertOne.setString(6, "Avamba");
            uInsertOne.setString(7, "User-centric full-range knowledge base");
            uInsertOne.executeUpdate();

            uInsertOne.setString(1, "ldominec5");
            uInsertOne.setString(2, "Lisha");
            uInsertOne.setString(3, "Dominec");
            uInsertOne.setString(4, "ldominec5@mediafire.com");
            uInsertOne.setString(5, "Female");
            uInsertOne.setString(6, "Tambee");
            uInsertOne.setString(7, "Front-line leading edge solution");
            uInsertOne.executeUpdate();

            uInsertOne.setString(1, "aterlinden6");
            uInsertOne.setString(2, "Armstrong");
            uInsertOne.setString(3, "Terlinden");
            uInsertOne.setString(4, "aterlinden6@google.pl");
            uInsertOne.setString(5, "Male");
            uInsertOne.setString(6, "Mycat");
            uInsertOne.setString(7, "Business-focused bifurcated matrices");
            uInsertOne.executeUpdate();

            uInsertOne.setString(1, "claister7");
            uInsertOne.setString(2, "Cherin");
            uInsertOne.setString(3, "Laister");
            uInsertOne.setString(4, "claister7@cafepress.com");
            uInsertOne.setString(5, "Female");
            uInsertOne.setString(6, "Twitterbridge");
            uInsertOne.setString(7, "Polarised dedicated workforce");
            uInsertOne.executeUpdate();

            uInsertOne.setString(1, "gmcgow8");
            uInsertOne.setString(2, "Guido");
            uInsertOne.setString(3, "McGow");
            uInsertOne.setString(4, "gmcgow8@yale.edu");
            uInsertOne.setString(5, "Male");
            uInsertOne.setString(6, "Vidoo");
            uInsertOne.setString(7, "Digitized executive capacity");
            uInsertOne.executeUpdate();

            uInsertOne.setString(1, "ahitchens9");
            uInsertOne.setString(2, "Alexei");
            uInsertOne.setString(3, "Hitchens");
            uInsertOne.setString(4, "ahitchens9@newyorker.com");
            uInsertOne.setString(5, "Male");
            uInsertOne.setString(6, "Edgepulse");
            uInsertOne.setString(7, "Visionary incremental workforce");
            uInsertOne.executeUpdate();
        }catch(SQLException e){
            e.printStackTrace();
        }
    }

    /**
     * Generates filler data for the message table
     */
    void populateMessages(){
        try{
            uInsertOne.setString(1, "messageUsername");
            uInsertOne.setString(2, "Alexei");
            uInsertOne.setString(3, "Hitchens");
            uInsertOne.setString(4, "ahitchens9@newyorker.com");
            uInsertOne.setString(5, "Male");
            uInsertOne.setString(6, "Edgepulse");
            uInsertOne.setString(7, "Visionary incremental workforce");
            uInsertOne.executeUpdate();

            mInsertOne.setString(1, "Dodge");
            mInsertOne.setString(2, "Querétaro Intercontinental Airport");
            mInsertOne.setInt(3, 66);
            mInsertOne.setString(4, "messageUsername");
            mInsertOne.setString(5, null);
            mInsertOne.executeUpdate();

            mInsertOne.setString(1, "Honda");
            mInsertOne.setString(2, "Makokou Airport");
            mInsertOne.setInt(3, 17);
            mInsertOne.setString(4, "messageUsername");
            mInsertOne.setString(5, null);
            mInsertOne.executeUpdate();

            mInsertOne.setString(1, "Ford");
            mInsertOne.setString(2, "Toksook Bay Airport");
            mInsertOne.setInt(3, 15);
            mInsertOne.setString(4, "messageUsername");
            mInsertOne.setString(5, "https://www.google.com/imghp?hl=en&authuser=0&ogbl");
            mInsertOne.executeUpdate();

            mInsertOne.setString(1, "Toyota");
            mInsertOne.setString(2, "Wave Hill Airport");
            mInsertOne.setInt(3, 26);
            mInsertOne.setString(4, "messageUsername");
            mInsertOne.setString(5, "https://www.google.com/");
            mInsertOne.executeUpdate();

            mInsertOne.setString(1, "Mitsubishi");
            mInsertOne.setString(2, "Pirapora Airport");
            mInsertOne.setInt(3, 42);
            mInsertOne.setString(4, "messageUsername");
            mInsertOne.setString(5, "https://www.wikipedia.org/");
            mInsertOne.executeUpdate();

            //Image messages
            mInsertOneIMG.setString(1, "My pup");
            mInsertOneIMG.setString(2, "My dog is the cutest");
            mInsertOneIMG.setInt(3, 45);
            mInsertOneIMG.setString(4, "messageUsername");
            mInsertOneIMG.setString(5, null);
            mInsertOneIMG.setString(6, "https://drive.google.com/file/d/1CGlCWO76Q5oLmmdf8LwRNtcEuP-e59aQ/view?usp=drive_link");
            mInsertOneIMG.executeUpdate();

            mInsertOneIMG.setString(1, "My kitty");
            mInsertOneIMG.setString(2, "My cat is the cutest");
            mInsertOneIMG.setInt(3, 36);
            mInsertOneIMG.setString(4, "messageUsername");
            mInsertOneIMG.setString(5, null);
            mInsertOneIMG.setString(6, "https://drive.google.com/file/d/1vvLPJvCeHetn1sA5NVwR5BnsHAcPK20Q/view?usp=drive_link");
            mInsertOneIMG.executeUpdate();

            mInsertOneIMG.setString(1, "Sleeping kitty");
            mInsertOneIMG.setString(2, "My cat took a nap");
            mInsertOneIMG.setInt(3, 64);
            mInsertOneIMG.setString(4, "messageUsername");
            mInsertOneIMG.setString(5, "https://www.wikipedia.org/");
            mInsertOneIMG.setString(6, "https://drive.google.com/file/d/1gz9V_ouEd0iBvIbyvHcrNgnuOMPGaz62/view?usp=drive_link");
            mInsertOneIMG.executeUpdate();
        }catch(SQLException e){
            e.printStackTrace();
        }
    }

    /**
     * Generates filler data for the comments table
     */

    void populateComments(){
        try{
            uInsertOne.setString(1, "commentUser");
            uInsertOne.setString(2, "Alexei");
            uInsertOne.setString(3, "Hitchens");
            uInsertOne.setString(4, "ahitchens9@newyorker.com");
            uInsertOne.setString(5, "Male");
            uInsertOne.setString(6, "Edgepulse");
            uInsertOne.setString(7, "Visionary incremental workforce");
            uInsertOne.executeUpdate();

            int messageID = 0;

            ResultSet rs = mSelectAll.executeQuery();
            if (rs.next()) {
                messageID = rs.getInt("id");
            }
            rs.close();

            cInsertOne.setString(1, "task-force");
            cInsertOne.setString(2, "commentUser");
            cInsertOne.setInt(3, messageID);
            cInsertOne.setString(4, null);
            cInsertOne.executeUpdate();

            cInsertOne.setString(1, "content-based");
            cInsertOne.setString(2, "commentUser");
            cInsertOne.setInt(3, messageID);
            cInsertOne.setString(4, null);
            cInsertOne.executeUpdate();

            cInsertOne.setString(1, "Secured");
            cInsertOne.setString(2, "commentUser");
            cInsertOne.setInt(3, messageID);
            cInsertOne.setString(4, null);
            cInsertOne.executeUpdate();

            cInsertOne.setString(1, "user-facing");
            cInsertOne.setString(2, "commentUser");
            cInsertOne.setInt(3, messageID);
            cInsertOne.setString(4, null);
            cInsertOne.executeUpdate();

            cInsertOne.setString(1, "heuristic");
            cInsertOne.setString(2, "commentUser");
            cInsertOne.setInt(3, messageID);
            cInsertOne.setString(4, null);
            cInsertOne.executeUpdate();

            cInsertOne.setString(1, "Organized");
            cInsertOne.setString(2, "commentUser");
            cInsertOne.setInt(3, messageID);
            cInsertOne.setString(4, null);
            cInsertOne.executeUpdate();

            cInsertOne.setString(1, "zero defect");
            cInsertOne.setString(2, "commentUser");
            cInsertOne.setInt(3, messageID);
            cInsertOne.setString(4, null);
            cInsertOne.executeUpdate();

            cInsertOne.setString(1, "definition");
            cInsertOne.setString(2, "commentUser");
            cInsertOne.setInt(3, messageID);
            cInsertOne.setString(4, null);
            cInsertOne.executeUpdate();

            cInsertOne.setString(1, "matrices");
            cInsertOne.setString(2, "commentUser");
            cInsertOne.setInt(3, messageID);
            cInsertOne.setString(4, null);
            cInsertOne.executeUpdate();

            cInsertOne.setString(1, "installation");
            cInsertOne.setString(2, "commentUser");
            cInsertOne.setInt(3, messageID);
            cInsertOne.setString(4, null);
            cInsertOne.executeUpdate();

            cInsertOne.setString(1, "Grass-roots");
            cInsertOne.setString(2, "commentUser");
            cInsertOne.setInt(3, messageID);
            cInsertOne.setString(4, null);
            cInsertOne.executeUpdate();

            cInsertOne.setString(1, "array");
            cInsertOne.setString(2, "commentUser");
            cInsertOne.setInt(3, messageID);
            cInsertOne.setString(4, null);
            cInsertOne.executeUpdate();

            cInsertOne.setString(1, "Multi-lateral");
            cInsertOne.setString(2, "commentUser");
            cInsertOne.setInt(3, messageID);
            cInsertOne.setString(4, "https://www.wikipedia.org/");
            cInsertOne.executeUpdate();

            cInsertOne.setString(1, "Configurable");
            cInsertOne.setString(2, "commentUser");
            cInsertOne.setInt(3, messageID);
            cInsertOne.setString(4, "https://www.wikipedia.org/");
            cInsertOne.executeUpdate();

            cInsertOne.setString(1, "3rd generation");
            cInsertOne.setString(2, "commentUser");
            cInsertOne.setInt(3, messageID);
            cInsertOne.setString(4, "https://www.wikipedia.org/");
            cInsertOne.executeUpdate();

            cInsertOne.setString(1, "fault-tolerant");
            cInsertOne.setString(2, "commentUser");
            cInsertOne.setInt(3, messageID);
            cInsertOne.setString(4, "Coursesite.lehigh.edu");
            cInsertOne.executeUpdate();

            cInsertOneIMG.setString(1, "I stole your cat");
            cInsertOneIMG.setString(2, "commentUser");
            cInsertOneIMG.setInt(3, messageID);
            cInsertOneIMG.setString(4, "https://www.google.com/imghp?hl=en&authuser=0&ogbl");
            cInsertOneIMG.setString(5, "https://drive.google.com/file/d/1gz9V_ouEd0iBvIbyvHcrNgnuOMPGaz62/view?usp=drive_link");
            cInsertOneIMG.executeUpdate();

            cInsertOneIMG.setString(1, "I stole YOUR cat too!!");
            cInsertOneIMG.setString(2, "commentUser");
            cInsertOneIMG.setInt(3, messageID); 
            cInsertOneIMG.setString(4, null);
            cInsertOneIMG.setString(5, "https://drive.google.com/file/d/1exZaO4Xs-KA_OGKcb6SK8DOdbjKjqz7b/view?usp=drive_link");
            cInsertOneIMG.executeUpdate();

            cInsertOneIMG.setString(1, "Your Puppy is mine now");
            cInsertOneIMG.setString(2, "commentUser");
            cInsertOneIMG.setInt(3, messageID);
            cInsertOneIMG.setString(4, null);
            cInsertOneIMG.setString(5, "https://drive.google.com/file/d/1CGlCWO76Q5oLmmdf8LwRNtcEuP-e59aQ/view?usp=drive_link");
            cInsertOneIMG.executeUpdate();
        }catch(SQLException e){
            e.printStackTrace();
        }
    }

}