 package edu.lehigh.cse216.thd226.admin;

import java.util.Map;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

/**
 * Unit test for simple App.
*/
public class AppTest 
    extends TestCase
{
    /**
     * Create the test case
     *
     * @param testName name of the test case
    
    public AppTest( String testName )
    {
        super( testName );

    }

    /**
     * @return the suite of tests being tested
    
    public static Test suite()
    {
        return new TestSuite( AppTest.class );
    }

    public void test(){
        assertTrue(5 == 5);
    }

    /**
     * Testing insert row by adding TEST
    
   public void testInsertRow(){
        // get the Postgres configuration from the environment
        Map<String, String> env = System.getenv();
        String ip = env.get("POSTGRES_IP");
        String port = env.get("POSTGRES_PORT");
        String user = env.get("POSTGRES_USER");
        String pass = env.get("POSTGRES_PASS");

        // Get a fully-configured connection to the database, or exit 
        // immediately
        Database db = Database.getDatabase(ip, port, user, pass);
       String subject = "TEST";
       String idea = "TEST IDEA: YOU CAN DELETE";
       int like_count = 20;
       assertTrue(db.insertRow(subject, idea, like_count) != 0); 
       //db.deleteRow(45);
       
    
    }
    */

    /**
    * Testing if edit like works by editing the like count of first test
    */
    public void testEditLike(){
    // get the Postgres configuration from the environment
        Map<String, String> env = System.getenv();
        String ip = env.get("POSTGRES_IP");
        String port = env.get("POSTGRES_PORT");
        String user = env.get("POSTGRES_USER");
        String pass = env.get("POSTGRES_PASS");

        // Get a fully-configured connection to the database, or exit 
        // immediately
        Database db = Database.getDatabase(ip, port, user, pass);       
        int id = 26;
        int like_count = 22;
        assertTrue(db.editLike(id, like_count) != 0); 
        //delete it after it's run
        
    } 
     
}
