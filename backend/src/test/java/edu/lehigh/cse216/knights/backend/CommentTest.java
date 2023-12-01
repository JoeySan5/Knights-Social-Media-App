package edu.lehigh.cse216.knights.backend;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

/**
 * Unit test for simple App.
 * This is the test for Comment table.
 * Simply, it tests the constructor.
 */
public class CommentTest extends TestCase {
    /**
     * Create the test case
     * 
     * @param testName name of the test case
     */
    public CommentTest(String testName) {
        super(testName);
    }

    /**
     * @return the suite of tests being tested
     */
    public static Test suite() {
        return new TestSuite(CommentTest.class);
    }

    /**
     * Ensure that the constructor populates every field of the object it
     * creates for Comment table.
     */
    public void testConstructor() {
        int id = 1;
        String userID = "testUser";
        int ideaID = 2;
        String content = "Test Comment";
        String link = null;
        FileObject file = null;

        Comment comment = new Comment(id, userID, ideaID, content, link, file);

        assertEquals(id, comment.mId);
        assertEquals(userID, comment.mUserId);
        assertEquals(ideaID, comment.mIdeaId);
        assertEquals(content, comment.mContent);
    }

}