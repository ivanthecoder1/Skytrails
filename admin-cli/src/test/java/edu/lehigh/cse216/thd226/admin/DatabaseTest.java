package edu.lehigh.cse216.thd226.admin;

import java.util.ArrayList;
import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;
import java.util.Map;

public class DatabaseTest extends TestCase
    {
    private static int message_id = 0;
    private static int comment_id = 0;
    /**
    * Create the test case
    *
    * @param testName name of the test case
    */
    public DatabaseTest( String testName )
        {
        super( testName );
    }

    /**
     * @return the suite of tests being tested
     */ 
    public static Test suite()
    {
        return new TestSuite( DatabaseTest.class );
    }

    private static Database db;
    private static boolean setUpDone = false;

    private static final String DEFAULT_PORT_DB = "8000";
    private static final int DEFAULT_PORT_SPARK = 4567;

    public void setUp() {
        if (!setUpDone){
            db = getDatabaseConnection();
            setUpDone = true;
        }
    }

    private static Database getDatabaseConnection(){
        Map<String, String> env = System.getenv();
        String ip = env.get("POSTGRES_IP");
        String port = env.get("POSTGRES_PORT");
        String user = env.get("POSTGRES_USER");
        String pass = env.get("POSTGRES_PASS");
        return Database.getDatabase(ip, port, user, pass);
    }

    public void testUserInsert() {
        int res = db.insertUser("UserTest", "test", "user", "testuser@gmail.com", "test", "test", "unit test user insert");
        assertTrue(res > 0);
    }

    public void testMessageInsert() {
        int res = db.insertMessage("test","Test", 0, "UserTest");
        assertTrue(res > 0);
    }

    public void testSelectAllMessages(){
        ArrayList<Database.messageData> res = db.selectAllMessages();
        message_id = res.get(res.size() - 1).getID();
        assertTrue(res.size() > 0);
    }

    public void testCommentInsert(){
        int res = db.insertComment("Test", "UserTest", message_id);
        assertTrue(res > 0);        
    }

    public void testUserSelect() {
        Database.userData res = db.selectUser("UserTest");
        assertTrue(res != null);
    }

    public void testMessageSelect() {
        Database.messageData res = db.selectMessage(message_id);
        assertTrue(res != null);
    }

    public void testSelectAllComments(){
        ArrayList<Database.commentData> res = db.selectAllComments();
        comment_id = res.get(res.size() - 1).getID();
        assertTrue(res.size() > 0);
    }

    public void testDeleteComment(){
        int res = db.deleteComment(comment_id);
        assertTrue(res > 0);
    }

    public void testDeleteMessage(){
        int res = db.deleteMessage(message_id);
        assertTrue(res > 0);
    }

    public void testDeleteUser(){
        int res = db.deleteUser("UserTest");
        assertTrue(res > 0);
    }

    public void testInsertMessageWithFakeUser(){
        int res = db.insertMessage("test","Test", 0, "FakeUser");
        assertTrue (res == -1);
    }

    public void testInsertCommentWithFakeMessage(){
        int res = db.insertComment("test","commentUser",1000);
        assertTrue (res == -1);
    }
    
    public void testInsertWithFakeUser(){
        int res = db.insertComment("test","FakeUser",10);
        assertTrue (res == -1);
    }
}