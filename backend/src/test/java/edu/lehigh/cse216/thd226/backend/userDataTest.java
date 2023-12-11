package edu.lehigh.cse216.thd226.backend;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

public class userDataTest extends TestCase {
 /**
     * Create the test case
     *
     * @param testName name of the test case
     */
    public userDataTest(String testName) {
        super(testName);
    }

    /**
     * @return the suite of tests being tested
     */
    public static Test suite() {
        return new TestSuite(userDataTest.class);
    }

    /**
     * Ensure that the constructor populates every field of the object it creates
     */
    public void testConstructor() {
        String username = "TestUser";
        String firstname = "John";
        String lastname = "Doe";
        String email = "lji225@lehigh.edu";
        String gender = "Male";
        String sexOrientation = "Bisexual";
        String note = "A test user";

        userData user = new userData(username, firstname, lastname, email, gender, sexOrientation, note);

        assertTrue(user.uUsername.equals(username));
        assertTrue(user.uFirstname.equals(firstname));
        assertTrue(user.uLastname.equals(lastname));
        assertTrue(user.uEmail.equals(email));
        assertTrue(user.uGender.equals(gender));
        assertTrue(user.uSexorientation.equals(sexOrientation));
        assertTrue(user.uNote.equals(note));
    }    
}
