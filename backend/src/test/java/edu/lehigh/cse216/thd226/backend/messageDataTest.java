package edu.lehigh.cse216.thd226.backend;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

/**
 * Unit test for messageData class.
 */
public class messageDataTest extends TestCase {
    /**
     * Create the test case
     *
     * @param testName name of the test case
     */
    public messageDataTest(String testName) {
        super(testName);
    }

    /**
     * @return the suite of tests being tested
     */
    public static Test suite() {
        return new TestSuite(messageDataTest.class);
    }

    /**
     * Ensure that the constructor populates every field of the object it creates
     */
    public void testConstructor() {
        int id = 1;
        String subject = "Test Subject";
        String idea = "Test Idea";
        int likeCount = 5;
        String username = "TestUser";
        String link = "https://www.youtube.com/";
        String file_link = "https://drive.google.com/file/d/1JaLXdwaK-NNMhDr-Hnxa_s9FnIvgR7FD/view";

        messageData message = new messageData(id, subject, idea, likeCount, username, link, file_link);

        assertTrue(message.mId == id);
        assertTrue(message.mSubject.equals(subject));
        assertTrue(message.mIdea.equals(idea));
        assertTrue(message.mLike_Count == likeCount);
        assertTrue(message.mUserName.equals(username));
        assertTrue(message.mLink.equals(link));
        assertTrue(message.mFileLink.equals(file_link));
    }
}
