package edu.lehigh.cse216.knights.backend;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

/**
 * DatabaseTest provides unit tests for the Idea database entry.
 */
public class IdeaTest extends TestCase {
    /**
     * Create the test case
     *
     * @param testName name of the test case
     */
    public IdeaTest(String testName) {
        super(testName);
    }

    /**
     * @return the suite of tests being tested
     */
    public static Test suite() {
        return new TestSuite(IdeaTest.class);
    }

    /**
     * Ensure that the constructor populates every field of the object it
     * creates
     */
    public void testConstructor() {
        int id = 99;
        String content = "Test Content";
        Idea idea = new Idea(id, content);

        assertTrue(idea.mContent.equals(content));
        assertTrue(idea.mId == id);
        assertTrue(idea.mLikeCount == 0);
    }

    /**
     * Ensure that the copy constructor works correctly
     */
    public void testCopyconstructor() {
        String content = "Test Content For Copy";
        int id = 101;
        Idea idea1 = new Idea(id, content);
        idea1.mLikeCount += 10; // add by some amount so it's not the default value
        Idea idea2 = new Idea(idea1);
        assertTrue(idea2.mContent.equals(idea1.mContent));
        assertTrue(idea1.mId == idea2.mId);
        assertTrue(idea1.mLikeCount == idea2.mLikeCount);
    }
}