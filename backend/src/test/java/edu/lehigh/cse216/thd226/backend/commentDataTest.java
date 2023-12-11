package edu.lehigh.cse216.thd226.backend;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

/**
 * Unit test for commentData class.
 */
public class commentDataTest extends TestCase {
    /**
     * Create the test case
     *
     * @param testName name of the test case
     */
    public commentDataTest(String testName) {
        super(testName);
    }

    /**
     * @return the suite of tests being tested
     */
    public static Test suite() {
        return new TestSuite(commentDataTest.class);
    }

    /**
     * Ensure that the constructor populates every field of the object it creates
     */
    public void testConstructor() {
        int commentID = 1;
        String content = "Test Content";
        String username = "TestUser";
        int messageID = 42;
        String link = "https://www.youtube.com/";
        String file_link = "https://drive.google.com/file/d/1JaLXdwaK-NNMhDr-Hnxa_s9FnIvgR7FD/view";

        commentData comment = new commentData(commentID, content, username, messageID, link, file_link);

        assertTrue(comment.cCommentID == commentID);
        assertTrue(comment.cContent.equals(content));
        assertTrue(comment.cUsername.equals(username));
        assertTrue(comment.cMessageID == messageID);
        assertTrue(comment.cLink.equals(link));
        assertTrue(comment.cFileLink.equals(file_link));
    }
}
