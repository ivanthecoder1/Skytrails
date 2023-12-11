/*package edu.lehigh.cse216.thd226.backend;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;
import java.util.Map;

public class DatabaseTest extends TestCase {
    private static Database db;
    private static boolean setUpDone = false;

    private static final String DEFAULT_PORT_DB = "5432";
    private static final int DEFAULT_PORT_SPARK = 8000;

    public void setUp() {
        if (!setUpDone) {
            db = getDatabaseConnection();
            setUpDone = true;
        }
    }

    private static Database getDatabaseConnection() {
        if (System.getenv("DATABASE_URL") != null) {
            return Database.getDatabase(System.getenv("DATABASE_URL"), DEFAULT_PORT_DB, System.getenv("POSTGRES_USER"), System.getenv("POSTGRES_PASS"));
        }

        Map<String, String> env = System.getenv();
        String ip = env.get("POSTGRES_IP");
        String port = env.get("POSTGRES_PORT");
        String user = env.get("POSTGRES_USER");
        String pass = env.get("POSTGRES_PASS");
        return Database.getDatabase(ip, port, user, pass);
    }

    public static Test suite() {
        return new TestSuite(DatabaseTest.class);
    }

    /*public void testInsertMessage() {
        db.insertMessage("Test Subject", "Test Idea", 0, "testUser");
    }
}
*/