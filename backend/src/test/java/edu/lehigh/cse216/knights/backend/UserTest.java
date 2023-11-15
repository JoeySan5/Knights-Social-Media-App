package edu.lehigh.cse216.knights.backend;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

/**
 * Unit test for simple App.
 * This is the test for User table.
 * Simply, it tests the constructor.
 */
public class UserTest extends TestCase {
    /**
     * Create the test case
     * @param testName name of the test case
     */
    public UserTest(String testName) {
        super(testName);
    }

    /**
     * @return the suite of tests being tested
     */
    public static Test suite() {
        return new TestSuite(UserTest.class);
    }

    /**
     * Ensure that the constructor populates every field of the object it
     * creates for User table.
     */
    public void testConstructor() {
        String userID = "testUser";
        String username = "TestUser123";
        String email = "test@example.com";
        String GI = "github_username";
        String SO = "stackoverflow_username";
        String note = "Test user note";
        boolean valid = true;
        User user = new User(userID, username, email, GI, SO, note, valid);

        assertEquals(userID, user.mId); 
        assertEquals(username, user.mUsername);
        assertEquals(email, user.mEmail);
        assertEquals(GI, user.mGI);
        assertEquals(SO, user.mSO);
        assertEquals(note, user.mNote);
        assertEquals(valid, user.mValid);
    }

}