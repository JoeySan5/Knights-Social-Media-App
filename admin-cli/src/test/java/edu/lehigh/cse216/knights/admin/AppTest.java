package edu.lehigh.cse216.knights.admin;

import edu.lehigh.cse216.knights.admin.Entity.Comment;
import edu.lehigh.cse216.knights.admin.Entity.Idea;
import edu.lehigh.cse216.knights.admin.Entity.Like;
import edu.lehigh.cse216.knights.admin.Entity.User;
import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

/**
 * Unit test for simple App.
 */
public class AppTest
        extends TestCase {
    // creating new mock database object to test insertion functionality
    MockDatabase md = new MockDatabase();

    /**
     * Create the test case
     *
     * @param testName name of the test case
     */
    public AppTest(String testName) {
        super(testName);
    }

    /**
     * @return the suite of tests being tested
     */
    public static Test suite() {
        return new TestSuite(AppTest.class);
    }

    /**
     * Testing the functionality of inserting a user
     */
    public void testUserInsert() {
        // test data for users
        String userId = "ericTest";
        String username = "ericUsernameTest";
        String email = "eric@test.com";
        String GI = "GI";
        String SO = "SO";
        String note = "note";
        boolean valid = true;
        // creating test user
        User user = new User(userId, username, email, GI, SO, note, valid);
        md.insertUser(user);
        // assert true statements that user info was properly added
        assertTrue(user.userId.equals(userId));
        assertTrue(user.username.equals(username));
        assertTrue(user.email.equals(email));
        assertTrue(user.GI.equals(GI));
        assertTrue(user.SO.equals(SO));
        assertTrue(user.note.equals(note));
        // get error if use .equals on boolean
        assertTrue(user.valid == valid);
    }

    /**
     * Testing the functionality of making sure user can be added and retreived
     * indicating they have been added properly
     */
    public void testGetUser() {
        // test data for users
        String userId = "ericTestGet";
        String username = "ericUsernameTestGet";
        String email = "ericGet@test.com";
        String GI = "GI";
        String SO = "SO";
        String note = "note";
        boolean valid = true;
        // creating test user
        User user = new User(userId, username, email, GI, SO, note, valid);
        md.insertUser(user);
        User getUser = md.getUser(0);
        assertTrue(getUser.equals(user));
    }

    /**
     * Testing the functionality of inserting an idea
     */
    public void testIdeaInsert() {
        int ideaId = 105;
        String userId = "ericTest123";
        String content = "test";
        Integer likeCount = -1;
        boolean valid = true;
        String fileId = "Knights.jpg";
        // might have messed up the url lol
        String link = "www.dokku.knights.edu";
        Idea idea = new Idea(ideaId, userId, content, likeCount, valid, fileId, link);
        md.insertIdea(idea);
        assertTrue(idea.ideaId == ideaId);
        assertTrue(idea.userId.equals(userId));
        assertTrue(idea.content.equals(content));
        assertTrue(idea.likeCount.equals(likeCount));
        assertTrue(idea.valid == valid);
        assertTrue(idea.fileId.equals(fileId));
        assertTrue(idea.link.equals(link));
    }

    /**
     * Testing the functionality of making sure idea can be added and retreived
     * indicating they have been added properly
     */
    public void testGetIdea() {
        int ideaId = 105;
        String userId = "ericTest123";
        String content = "test";
        Integer likeCount = -1;
        boolean valid = true;
        String fileId = "Knights.jpg";
        // might have messed up the url lol
        String link = "www.dokku.knights.edu";
        Idea idea = new Idea(ideaId, userId, content, likeCount, valid, fileId, link);
        md.insertIdea(idea);

        int ideaId1 = 106;
        String userId1 = "ericTest123";
        String content1 = "test";
        Integer likeCount1 = 1;
        boolean valid1 = true;
        String fileId1 = "Knights.jpg";
        // might have messed up the url lol
        String link1 = "www.dokku.knights.edu";
        Idea idea1 = new Idea(ideaId1, userId1, content1, likeCount1, valid1, fileId1, link1);
        md.insertIdea(idea1);
        Idea ideaGet1 = md.getIdea(1);
        assertTrue(ideaGet1.equals(idea1));
    }

    public void testInsertComment() {
        // first create mock idea
        int ideaId = 105;
        String userId = "ericTest123";
        String content = "test";
        Integer likeCount = -1;
        boolean valid = true;
        String fileId = "Knights.jpg";
        // might have messed up the url lol
        String link = "www.dokku.knights.edu";
        Idea idea = new Idea(ideaId, userId, content, likeCount, valid, fileId, link);
        md.insertIdea(idea);
        // now create comment
        int commentId = 1;
        String comContent = "test comment";
        String comUserId = "testUser321";
        // use idea id from above
        String comFileId = "";
        String comLink = "www.lehigh.edu.com";

        Comment comment = new Comment(commentId, comContent, comUserId, ideaId, comFileId, comLink);
        md.insertComment(comment);

        assertTrue(comment.commentId == commentId);
        assertTrue(comment.content.equals(comContent));
        assertTrue(comment.userId.equals(comUserId));
        assertTrue(comment.ideaId == ideaId);
        assertTrue(comment.fileId.equals(comFileId));
        assertTrue(comment.link.equals(comLink));
    }

    public void testGetComment() {
        // first create mock idea
        int ideaId = 105;
        String userId = "ericTest123";
        String content = "test";
        Integer likeCount = -1;
        boolean valid = true;
        String fileId = "Knights.jpg";
        // might have messed up the url lol
        String link = "www.dokku.knights.edu";
        Idea idea = new Idea(ideaId, userId, content, likeCount, valid, fileId, link);
        md.insertIdea(idea);

        // now create comment
        int commentId = 1;
        String comContent = "test comment";
        String comUserId = "testUser321";
        // use idea id from above
        String comFileId = "";
        String comLink = "www.lehigh.edu.com";

        Comment comment = new Comment(commentId, comContent, comUserId, ideaId, comFileId, comLink);
        md.insertComment(comment);
        // get comment and test that it is equal to one created
        Comment getComment = md.getComment(0);
        assertTrue(getComment.equals(comment));
    }

    public void testInsertLikes(){
        // first create mock idea
        int ideaId = 105;
        String userId = "ericTest123";
        String content = "test";
        Integer likeCount = -1;
        boolean valid = true;
        String fileId = "Knights.jpg";
        // might have messed up the url lol
        String link = "www.dokku.knights.edu";
        Idea idea = new Idea(ideaId, userId, content, likeCount, valid, fileId, link);
        md.insertIdea(idea);

        String likeUserId = "testLikeUserId";
        int value = 1;
        Like like = new Like(likeUserId, ideaId, value);
        md.insertLike(like);
        assertTrue(like.userId.equals(likeUserId));
        assertTrue(like.ideaId == ideaId);
        assertTrue(like.value == value);
    }

    public void testGetLikes(){
        // first create mock idea
        int ideaId = 105;
        String userId = "ericTest123";
        String content = "test";
        Integer likeCount = -1;
        boolean valid = true;
        String fileId = "Knights.jpg";
        // might have messed up the url lol
        String link = "www.dokku.knights.edu";
        Idea idea = new Idea(ideaId, userId, content, likeCount, valid, fileId, link);
        md.insertIdea(idea);

        String likeUserId = "testLikeUserId";
        int value = 1;
        Like like = new Like(likeUserId, ideaId, value);
        md.insertLike(like);

        // now get likes
        Like getLike = md.getLike(0);
        assertTrue(getLike.equals(getLike));
    }







}
