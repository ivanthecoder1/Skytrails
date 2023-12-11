package edu.lehigh.cse216.thd226.backend;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;


/**
 * Database object
 */
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
    private PreparedStatement mSelectAllMessages;

    /**
     * A prepared statement for getting one row from the database
     */
    private PreparedStatement mSelectOne;

    /**
     * A prepared statement for deleting a row from the database
     */
    private PreparedStatement mDeleteOne;

    /**
     * A prepared statement for deleting a file link from a row from the message database
     * by setting the file_link column to null for that row
     */
    private PreparedStatement mDeleteOneWithFile;

    /**
     * A prepared statement for deleting a link from a row from the message database
     * by setting the file_link column to null for that row
     */
    private PreparedStatement mDeleteOneWithLink;

    /**
     * A prepared statement for inserting into the database
     */
    private PreparedStatement mInsertOne;

    /**
     * A prepared statement for inserting into the database with file
     */
    private PreparedStatement mInsertOneWithFile;

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

    
    //Prepared Stataments for Likes
    /**
     * A prepared statement for adding likes in our database
     */
    private PreparedStatement mInsertLike;

       /**
     * A prepared statement for adding dislikes in our database
     */
    private PreparedStatement mInsertDislike;

    private PreparedStatement mGetLikeCount;
    
    /**
     * A prepared statement for updating likes in our database
     */

    /**
     * Prepared statement for retrieving a list of message IDs that a user has liked.
     */
    private PreparedStatement mGetUserLikes;

    /**
     * Prepared statement for retrieving the like status for a specific message and user.
     */

    private PreparedStatement mGetUserPosts;

   
    //User Prepared Statements
    private PreparedStatement uCreateTable;

    private PreparedStatement uDropTable;

    /**
     * Prepared statement for deleting a single item from a user-related table.
     */
    private PreparedStatement uDeleteOne;

    /**
     * Prepared statement for inserting a new item into a user-related table.
     */

    private PreparedStatement uInsertOne;

    /**
     * Prepared statement for selecting all items from a user-related table.
     */
    private PreparedStatement uSelectAllUsers;

    /**
     * Prepared statement for selecting a single item from a user-related table based on an identifier.
     */
    private PreparedStatement uSelectOne;

    private PreparedStatement uUpdateOne;


    //User prepared Statements


    private PreparedStatement cCreateTable;

    private PreparedStatement cDropTable;

   

    /**
     * Prepared statement for inserting a new item into a category-related table.
     */
    private PreparedStatement cInsertOne;

     /**
     * Prepared statement for inserting a new item into a comment containing a file link
     */
    private PreparedStatement cInsertOneWithFile;

    /**
     * Prepared statement for selecting all comments from the messages.
     */
    private PreparedStatement cSelectAllComments;


    private PreparedStatement cSelectOne;

     /**
     * Prepared statement for deleting a single item from a category-related table.
     */
    private PreparedStatement cDeleteOne;

    /**
     * Prepared statement for deleting a file link attached to a comment
     */
    private PreparedStatement cDeleteOneWithFile;

    /**
     * Prepared statement for deleting a link attached to a comment
     */
    private PreparedStatement cDeleteOneWithLink;

    private PreparedStatement cGetUserComments;

    private PreparedStatement cUpdateComment;


    private PreparedStatement xGetLikeStatus;
    private PreparedStatement xSetLikeStatus;
    private PreparedStatement xGetCalls;

    



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
            db.mCreateTable = db.mConnection.prepareStatement("CREATE TABLE message (id SERIAL PRIMARY KEY, subject VARCHAR(50) NOT NULL, idea VARCHAR(1024) NOT NULL, like_count INTEGER, username VARCHAR(50) NOT NULL)");
            db.mDropTable = db.mConnection.prepareStatement("DROP TABLE message");
            db.mDeleteOne = db.mConnection.prepareStatement("DELETE FROM message WHERE id = ?");
            db.mDeleteOneWithFile = db.mConnection.prepareStatement("UPDATE message SET file_link = NULL WHERE id = ?");
            db.mDeleteOneWithLink = db.mConnection.prepareStatement("UPDATE message SET gen_link = NULL WHERE id = ?");
            db.mInsertOne = db.mConnection.prepareStatement("INSERT INTO message VALUES (default, ?, ?, ?, ?, ?, ?)");
            db.mInsertOneWithFile = db.mConnection.prepareStatement("INSERT INTO message VALUES (default, ?, ?, ?, ?, ?)");
            db.mSelectAllMessages = db.mConnection.prepareStatement("SELECT * FROM message");
            db.mGetLikeCount = db.mConnection.prepareStatement("SELECT like_count FROM message WHERE id=?");
            db.mSelectOne = db.mConnection.prepareStatement("SELECT * FROM message WHERE id=?");
            db.mGetUserPosts = db.mConnection.prepareStatement("SELECT * FROM message WHERE username=?");
            db.mUpdateOne = db.mConnection.prepareStatement("UPDATE message SET idea = ? WHERE id = ?");
            db.mInsertLike = db.mConnection.prepareStatement("UPDATE message SET like_count = ? WHERE id=?");
            db.mInsertDislike = db.mConnection.prepareStatement("UPDATE message SET like_count = ? WHERE id=?");
            db.mGetUserLikes = db.mConnection.prepareStatement("SELECT id FROM message WHERE userna = ? AND like_value = 1");
            
            db.xGetLikeStatus = db.mConnection.prepareStatement("SELECT like_value FROM user_likes WHERE message_id = ? AND username = ?");
            db.xSetLikeStatus = db.mConnection.prepareStatement("INSERT INTO user_likes (username, message_id, like_value, wasCalled) VALUES (?, ?, ?, ?)");
            db.xGetCalls = db.mConnection.prepareStatement("SELECT wasCalled FROM message WHERE message_id=? AND username=?");

            db.uCreateTable = db.mConnection.prepareStatement("CREATE TABLE users (username VARCHAR(255) PRIMARY KEY, firstname VARCHAR(50), lastname VARCHAR(50), gender VARCHAR(50), sexorientation VARCHAR(50), note VARCHAR(255) );");
            db.uDropTable = db.mConnection.prepareStatement("DROP TABLE users");
            db.uDeleteOne = db.mConnection.prepareStatement("DELETE FROM users WHERE username = ?");
            db.uInsertOne = db.mConnection.prepareStatement("INSERT INTO users VALUES (?, ?, ?, ?, ?, ?, ?)");
            db.uSelectAllUsers = db.mConnection.prepareStatement("SELECT * FROM users");
            db.uSelectOne = db.mConnection.prepareStatement("SELECT * from users WHERE username = ?");
            db.uUpdateOne = db.mConnection.prepareStatement("UPDATE users "+"SET firstname = ?, lastname = ?, email = ?, gender = ?, sexorientation = ?, note = ? "+"WHERE username = ?");
            
            db.cCreateTable = db.mConnection.prepareStatement("CREATE TABLE comment(comment_id SERIAL PRIMARY KEY, content VARCHAR(1024), username VARCHAR(255), message_id INTEGER, CONSTRAINT comment_user FOREIGN KEY(username) REFERENCES users(username), CONSTRAINT comment_message FOREIGN KEY(message_id) REFERENCES message(id));");
            db.cDropTable = db.mConnection.prepareStatement("DROP TABLE comment");
            db.cDeleteOne = db.mConnection.prepareStatement("DELETE FROM comment WHERE comment_id = ?");
            db.cDeleteOneWithFile = db.mConnection.prepareStatement("UPDATE comment SET file_link = NULL WHERE message_id = ? and comment_id = ?");
            db.cDeleteOneWithLink = db.mConnection.prepareStatement("UPDATE comment SET gen_link = NULL WHERE message_id = ? and comment_id = ?");
            db.cInsertOne = db.mConnection.prepareStatement("INSERT INTO comment (content, username, message_id, gen_link, file_link) VALUES (?, ?, ?, ?, ?)");
            db.cInsertOneWithFile = db.mConnection.prepareStatement("INSERT INTO comment (content, username, message_id, filelink) VALUES (?, ?, ?, ?)");
            db.cSelectAllComments = db.mConnection.prepareStatement("SELECT * FROM comment WHERE message_id = ?");
            db.cSelectOne = db.mConnection.prepareStatement("SELECT * from comment WHERE comment_id = ?");
            db.cGetUserComments = db.mConnection.prepareStatement("SELECT comment_Id FROM message WHERE username=?");
            db.cUpdateComment = db.mConnection.prepareStatement("UPDATE comment SET content = ? WHERE comment_id = ? AND message_id = ?");



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
     *  @param like_count The like_count for this new row
     * @param username The identity of the user who posted the message
     * @param link The link sent with message if any
     * @param fileID The id of the file attached to the message
     * @return The number of rows that were inserted
     */
    int insertMessage(String subject, String idea, int like_count, String username, String link, String fileID) {
        int count = 0;
        try {
            mInsertOne.setString(1, subject);
            mInsertOne.setString(2, idea);
            mInsertOne.setInt(3, like_count);
            mInsertOne.setString(4, username);
            mInsertOne.setString(5, link);
            mInsertOne.setString(6, fileID);
            count += mInsertOne.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return count;
    }

    /**
     * Insert a message into the database that has a folder
     * 
     * @param subject The subject for this new row
     * @param idea The message body for this new row
     * @param like_count The like_count for this new row
     * @param username The identity of the user who posted the message
     * @param fileID The id of the file attached to the message
     * 
     * @return The number of rows that were inserted
     */
    int insertMessageWithFile(String subject, String idea, int like_count, String username, String fileID) {
        int count = 0;
        try {
            mInsertOneWithFile.setString(1, subject);
            mInsertOneWithFile.setString(2, idea);
            mInsertOneWithFile.setInt(3, like_count);
            mInsertOneWithFile.setString(4, username);
            mInsertOneWithFile.setString(5, fileID);
            count += mInsertOneWithFile.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
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
            ResultSet rs = mSelectAllMessages.executeQuery();
            while (rs.next()) {
                res.add(new messageData(rs.getInt("id"), rs.getString("subject"), rs.getString("idea"), rs.getInt("like_count"), rs.getString("username"), rs.getString("gen_link"), rs.getString("file_link")));
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
                res = new messageData(rs.getInt("id"), rs.getString("subject"), rs.getString("idea"), rs.getInt("like_count"), rs.getString("username"), rs.getString("gen_link"), rs.getString("file_link"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
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
            e.printStackTrace();
        }
        return res;
    }

    /**
     * Delete a file in a row by ID
     * 
     * @param id The id of the message row to delete the file link from
     * 
     * @return The number of rows that were deleted.  -1 indicates an error.
     */
    int deleteFileMessage(int id) {
        int res = -1;
        try {
            mDeleteOneWithFile.setInt(1, id);
            res = mDeleteOneWithFile.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return res;
    }

    /**
     * Delete a link in a row by ID
     * 
     * @param id The id of the message row to delete the link from
     * 
     * @return The number of rows that were deleted.  -1 indicates an error.
     */
    int deleteLinkMessage(int id) {
        int res = -1;
        try {
            mDeleteOneWithLink.setInt(1, id);
            res = mDeleteOneWithLink.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return res;
    }


    /**
     * Updates the idea of a message with the specific ID in the database.
     *
     * @param id   The ID of the message to update.
     * @param idea The new content for the message.
     * @return The number of rows affected. Returns -1 if an error occurs.
     */
    int updateMessage(int id, String idea){
        int res = -1;
        try{
            mUpdateOne.setInt(2, id);
            mUpdateOne.setString(1, idea);
            res = mUpdateOne.executeUpdate();
        }catch(SQLException e){
            e.printStackTrace();
        }
        return res;
    }



    /**
     * Inserts a like for a message with the specific ID and updates the like count in the database(hopefully)
     *
     * @param id The ID of the message .
     * @param username The username of the user.
     * @return The number of rows affected. Returns -1 if an error occurs.
     */
    int insertLike(int id, String username) {
        int wasCalled = getCalls(id, username);
        int likeval = getLikeStatus(id, username);
        int res = -1;
        
        try {
            mGetLikeCount.setInt(1, id);
            ResultSet rs = mGetLikeCount.executeQuery();
            int currLikes = 0;
            if (rs.next()){
                currLikes = rs.getInt("like_count");
            }

            if (wasCalled != 1){
                setLikeStatus(username,id, 1, 1);
                mInsertLike.setInt(2, id);
                mInsertLike.setInt(1, currLikes+1);
                res = mInsertLike.executeUpdate();
            }else{
                if(likeval == 0){
                    mInsertLike.setInt(2, id);
                    mInsertLike.setInt(1, currLikes+1);
                    res = mInsertLike.executeUpdate();
                    setLikeStatus(username,id, 1, 1);
                }
                else if(likeval == -1){
                    mInsertLike.setInt(2, id);
                    mInsertLike.setInt(1, currLikes+2);
                    res = mInsertLike.executeUpdate();
                    setLikeStatus(username,id, 1, 1);
                }
                else{
                    mInsertLike.setInt(2, id);
                    mInsertLike.setInt(1, currLikes-1);
                    res = mInsertLike.executeUpdate();
                    setLikeStatus(username,id, 0, 1);
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return res;
    }

    /**
     * Inserts a dislike for a message with the specific  ID and updates the like count in the database.
     *
     * @param id The ID of the message.
     * @param username The username.
     * @return The number of rows affected. Returns -1 if an error occurs.
     */
    int insertDisike(int id, String username) {
        int likeval = getLikeStatus(id, username);
        int res = -1;
        int wasCalled = getCalls(id, username);        
        try {
            mGetLikeCount.setInt(1, id);
            ResultSet rs = mGetLikeCount.executeQuery();
            int currLikes = 0;
            if (rs.next()){
                currLikes = rs.getInt("like_count");
            }
            if (wasCalled != 1){
                setLikeStatus(username,id, 1, 1);
                mInsertLike.setInt(2, id);
                mInsertLike.setInt(1, currLikes-1);
                res = mInsertLike.executeUpdate();
            }else{
                if(likeval == 0){
                    mInsertDislike.setInt(2, id);
                    mInsertDislike.setInt(1, currLikes-1);
                    res = mInsertDislike.executeUpdate();
                    setLikeStatus(username, id, -1, 1);
                }
                else if(likeval == 1){
                    mInsertLike.setInt(2, id);
                    mInsertLike.setInt(1, currLikes-2);
                    res = mInsertLike.executeUpdate();
                    setLikeStatus(username,id, -1, 1);
                }
                else{
                    mInsertLike.setInt(2, id);
                    mInsertLike.setInt(1, currLikes+1);
                    res = mInsertLike.executeUpdate();
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return res;
    }


    int getCalls(int message_id, String username){
        int res = -1;
        try {
            xGetCalls.setString(2, username);
            xGetCalls.setInt(1, message_id);
            res = xGetCalls.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return res;
    }

    void setLikeStatus(String username, int message_id, int like_value, int wasCalled){
        try {
            xSetLikeStatus.setString(1, username);
            xSetLikeStatus.setInt(2, message_id);
            xSetLikeStatus.setInt(3, like_value);
            xSetLikeStatus.setInt(4, wasCalled);
            xSetLikeStatus.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    int getLikeStatus(int message_id, String username){
        int res = 0;
        try{
            xGetLikeStatus.setInt(1, message_id);
            xGetLikeStatus.setString(2, username);
            res = xGetLikeStatus.executeUpdate();
        }catch (SQLException e) {
            e.printStackTrace();
        }
        return res;
    }



    List<Integer> getUserPosts(String username){
        List<Integer> userPosts = new ArrayList<>();
        try{
            mGetUserPosts.setString(1, username);
            try (ResultSet resultSet = mGetUserPosts.executeQuery()) {
                while (resultSet.next()) {
                    userPosts.add(resultSet.getInt("message_id"));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return userPosts;
    }


    List<Integer> getUserLikes(String username) {
        List<Integer> likedMessages = new ArrayList<>();
        try {
            mGetUserLikes.setString(1, username);
            try (ResultSet resultSet = mGetUserLikes.executeQuery()) {
                while (resultSet.next()) {
                    likedMessages.add(resultSet.getInt("message_id"));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return likedMessages;
    }

    int getLikeStatus(int id) {
        return -1;
    }



//User Functions

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
            e.printStackTrace();
        }
        return count;
    }

    /**
     * Query the database for a list of all subjects and their IDs
     * 
     * @return All rows, as an ArrayList
     */
    ArrayList<userData> selectAllUsers() {
        ArrayList<userData> users = new ArrayList<>();

        try {
            ResultSet resultSet = uSelectAllUsers.executeQuery();
            while (resultSet.next()) {
                String username = resultSet.getString("username");
                String firstname = resultSet.getString("firstname");
                String lastname = resultSet.getString("lastname");
                String email = resultSet.getString("email");
                String gender = resultSet.getString("gender");
                String sexorientation = resultSet.getString("sexorientation");
                String note = resultSet.getString("note");

                userData user = new userData(username, firstname, lastname, email, gender, sexorientation, note);
                users.add(user);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return users;
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
            }
        } catch (SQLException e) {
            e.printStackTrace();
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
            e.printStackTrace();
        }
        return res;
    }

    /**
     * Update user data by username
     *
     * @param username        The username of the user to update
     * @param firstname       The updated first name
     * @param lastname        The updated last name
     * @param email            The updated email
     * @param gender          The updated gender
     * @param sexorientation  The updated sexual orientation
     * @param note            The updated note about the user
     *
     * @return The number of rows that were updated. -1 indicates an error.
     */
    int updateUser(String username, String firstname, String lastname, String email, String gender, String sexorientation, String note) {
        int res = -1;
        try {
            uUpdateOne.setString(1, firstname);
            uUpdateOne.setString(2, lastname);
            uUpdateOne.setString(3, email);
            uUpdateOne.setString(4, gender);
            uUpdateOne.setString(5, sexorientation);
            uUpdateOne.setString(6, note);
            uUpdateOne.setString(7, username);
            res = uUpdateOne.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return res;
    }



    //Comment Functions

    /**
     * Insert a comment into the database
     * 
     * @param content The text of the comment
     * @param username The user that posted this comment
     * @param message_id The message this comment is attached to
     * @param link The link attached to the comment
     * @param file_link The file link attached to the comment
     * 
     * @return The number of rows that were inserted
     */
    int insertComment(String content, String username, int messageId, String link, String file_link) {
        int count = 0;
        try {
            cInsertOne.setString(1, content);
            cInsertOne.setString(2, username);
            cInsertOne.setInt(3, messageId);
            cInsertOne.setString(4, link);
            cInsertOne.setString(5, file_link);
            count = cInsertOne.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace(); 
        }
        return count;
    }

    /**
     * Insert a comment into the database that has a file link attached
     * 
     * @param content The text of the comment
     * @param username The user that posted this comment
     * @param message_id The message this comment is attached to
     * @param file_link The file link attached to the comment
     * 
     * @return The number of rows that were inserted
     */
    int insertCommentWithFile(String content, String username, int messageId, String file_link) {
        int count = 0;
        try {
            cInsertOneWithFile.setString(1, content);
            cInsertOneWithFile.setString(2, username);
            cInsertOneWithFile.setInt(3, messageId);
            cInsertOneWithFile.setString(4, file_link);
            count = cInsertOneWithFile.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace(); 
        }
        return count;
    }
    
    int updateComment(int commentId, String content, int messageId) {
        int count = 0;
        try {
            cUpdateComment.setString(1, content);
            cUpdateComment.setInt(2, commentId);
            cUpdateComment.setInt(3, messageId);
    
            count += cUpdateComment.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return count;
    }
    

    List<Integer> getUserComments(String username){
        List<Integer> userComments = new ArrayList<>();
        try{
            cGetUserComments.setString(1, username);
            try (ResultSet resultSet = cGetUserComments.executeQuery()) {
                while (resultSet.next()) {
                    userComments.add(resultSet.getInt("message_id"));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return userComments;
    }

    /**
     * Query the database for a list of all comments and their IDs
     * 
     * @return All rows, as an ArrayList
     */
    ArrayList<commentData> selectAllComments(int ideaId) {
        ArrayList<commentData> comments = new ArrayList<commentData>();
        try {
            cSelectAllComments.setInt(1, ideaId);
            ResultSet rs = cSelectAllComments.executeQuery();
            while (rs.next()) {
                int commentID = rs.getInt("comment_id");
                String content = rs.getString("content");
                String username = rs.getString("username");
                int messageID = rs.getInt("message_id");
                String link = rs.getString("gen_link");
                String file_link = rs.getString("file_link");
    
                commentData comment = new commentData(commentID, content, username, messageID, link, file_link);
                comments.add(comment);
            }
            rs.close();
            return comments;
        } catch (SQLException e) {
            e.printStackTrace();
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
    commentData getComment(int comment_id) {
        commentData res = null;
        try {
            cSelectOne.setInt(1, comment_id);
            ResultSet rs = cSelectOne.executeQuery();
            if (rs.next()) {
                res = new commentData(rs.getInt("comment_id"), rs.getString("content"), rs.getString("username"), rs.getInt("message_id"), rs.getString("gen_link"), rs.getString("file_link"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return res;
    }
    
    /**
     * Delete a comment
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
            e.printStackTrace();
        }
        return res;
    }

    /**
     * Delete a file link attached to a comment
     * @param message_id The id of the message 
     * @param comment_id The id of the comment within message
     * 
     * @return The number of rows that were deleted.  -1 indicates an error.
     */
    int deleteFileComment(int message_id, int comment_id) {
        int res = -1;
        try {
            cDeleteOneWithFile.setInt(1, message_id);
            cDeleteOneWithFile.setInt(2, comment_id);
            res = cDeleteOneWithFile.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return res;
    }

    /**
     * Delete a link attached to a comment
     * @param message_id The id of the message 
     * @param comment_id The id of the comment within message
     * 
     * @return The number of rows that were deleted.  -1 indicates an error.
     */
    int deleteLinkComment(int message_id, int comment_id) {
        int res = -1;
        try {
            cDeleteOneWithLink.setInt(1, message_id);
            cDeleteOneWithLink.setInt(2, comment_id);
            res = cDeleteOneWithLink.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return res;
    }

    /**
     * Create tblData.  If it already exists, this will print an error
     */
    void createTable() {
        try {
            mCreateTable.execute();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * Remove tblData from the database.  If it does not exist, this will print
     * an error.
     */
    void dropTable() {
        try {
            mDropTable.execute();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}