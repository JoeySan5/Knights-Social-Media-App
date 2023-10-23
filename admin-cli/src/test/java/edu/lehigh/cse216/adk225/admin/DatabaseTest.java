package edu.lehigh.cse216.knights.admin;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;
import java.util.ArrayList;

/**
 * DatabaseTest provides unit tests for the database of Ideas, 
 * using a MockDatabase.
 */
public class DatabaseTest extends TestCase {
    
    /**
     * Create the test case
     *
     * @param testName name of the test case
     */
    public DatabaseTest(String testName) {
        super(testName);
    }

    /**
     * @return the suite of tests being tested
     */
    public static Test suite() {
        return new TestSuite(DatabaseTest.class);
    }

    /**
     * Ensures the MockDatabase can add Ideas to the database. 
     * The real database would follow similar instructions after
     * the server recieves a POST request
     */
    public void testPOST() {
        MockDatabase mockDB = new MockDatabase();
        // Try to add the first Idea
        String content = "sample content";
        assertTrue(mockDB.createEntry(content) == 1);
        // Try to add the second Idea
        assertTrue(mockDB.createEntry(content) == 2);
    }

    /**
     * Ensures that the MockDatabase correctly returns its 
     * current entries, and that it stores its entries correctly.
     * Tests for getting all posts from 0, 1, and 2 (>1) entries.
     */
    public void testGETAll(){
        MockDatabase mockDB = new MockDatabase();
        // GET with no posts
        ArrayList<Idea> ideas = mockDB.readAll();
        assertTrue(ideas.size() == 0);
        // GET with exactly 1 post
        String content1 = "first content";
        mockDB.createEntry(content1);
        ideas = mockDB.readAll();
        assertTrue(ideas.get(0).mId == 1);
        assertTrue(ideas.get(0).mContent.equals(content1));
        assertTrue(ideas.get(0).mLikeCount == 0);
        // GET with more than 1 post
        String content2 = "second content";
        mockDB.createEntry(content2);
        ideas = mockDB.readAll();
        assertTrue(ideas.get(0).mId == 1);
        assertTrue(ideas.get(0).mContent.equals(content1));
        assertTrue(ideas.get(0).mLikeCount == 0);
        assertTrue(ideas.get(1).mId == 2);
        assertTrue(ideas.get(1).mContent.equals(content2));
        assertTrue(ideas.get(1).mLikeCount == 0);
    }

    /**
     * Ensures that the MockDatabase correctly returns the  
     * queried entry. Tests for valid and invalid IDs.
     */
    public void testGETOne(){
        MockDatabase mockDB = new MockDatabase();
        // GET before any entries exits
        Idea idea = mockDB.readOne(1);
        assertTrue(idea == null);
        // GET an entry after it's added
        String content = "example content";
        mockDB.createEntry(content);
        idea = mockDB.readOne(1);
        assertTrue(idea.mId == 1);
        assertTrue(idea.mContent.equals(content));
        assertTrue(idea.mLikeCount == 0);
        // GET an entry that doesn't yet exist
        idea = mockDB.readOne(3);
        assertTrue(idea == null);
        // GET arbitrary entry out of many entries
        for(int i=2; i<content.length(); i++){
            mockDB.createEntry(content.substring(0, i));
        }
        int arbitraryID = 5;
        idea = mockDB.readOne(arbitraryID);
        assertTrue(idea.mId == arbitraryID);
        assertTrue(idea.mContent.equals(content.substring(0, arbitraryID)));
        assertTrue(idea.mLikeCount == 0);
        // GET an ID that cannot exist
        idea = mockDB.readOne(-9);
        assertTrue(idea == null);
    }

    /**
     * Ensures that liking is handled appropriately, by either incrementing or  
     * decrementing the likeCount, or handling an invald input.
     */
    public void testPUTLikes(){
        MockDatabase mockDB = new MockDatabase();
        mockDB.createEntry("example content");
        // PUT with invalid increment values (0, < -1, > 1)
        assert(mockDB.updateOneLikeCount(1, 0) == -1);
        assert(mockDB.updateOneLikeCount(1, -11) == -1);
        assert(mockDB.updateOneLikeCount(1, 22) == -1);
        // PUT with invalid IDs
        assert(mockDB.updateOneLikeCount(2, 1) == 0);
        assert(mockDB.updateOneLikeCount(-1, 1) == 0);
        // PUT for a successful like, multiple times
        assert(mockDB.readOne(1).mLikeCount == 0);
        mockDB.updateOneLikeCount(1, 1);
        assert(mockDB.readOne(1).mLikeCount == 1);
        mockDB.updateOneLikeCount(1, 1);
        assert(mockDB.readOne(1).mLikeCount == 2);
        // PUT for a successful dislike
        mockDB.updateOneLikeCount(1, -1);
        assert(mockDB.readOne(1).mLikeCount == 1);
        // PUT on a deleted row
        mockDB.deleteOne(1);
        assert(mockDB.updateOneLikeCount(1, 1) == 0);        
    }

    /**
     * Ensures that deleting an entry from the database
     * work correctly
     */
    public void testDELETE(){
        MockDatabase mockDB = new MockDatabase();
        // DELETE a nonexistent entry
        assertFalse(mockDB.deleteOne(1));
        mockDB.createEntry("example content");
        assertTrue(mockDB.readOne(1) != null);
        // DELETE an entry, then try again
        assertTrue(mockDB.deleteOne(1));
        assertTrue(mockDB.readOne(1) == null);
        // try DELETE-ing the same entry
        assertFalse(mockDB.deleteOne(1));
        // DELETE extreme invalid IDs
        assertFalse(mockDB.deleteOne(-100));
        assertFalse(mockDB.deleteOne(100));
    }

}
