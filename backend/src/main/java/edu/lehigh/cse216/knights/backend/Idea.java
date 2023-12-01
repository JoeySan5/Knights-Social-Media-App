package edu.lehigh.cse216.knights.backend;

import java.util.ArrayList;

import edu.lehigh.cse216.knights.backend.Comment.ExtendedComment;

// import java.util.Date; // should use something like java.sql.Date instead

/**
 * Idea holds a row of information. A row of information consists of
 * an identifier, a string for "content", and an integer for likeCount.
 * 
 * Because we will ultimately be converting instances of this object into JSON
 * directly, we need to make the fields public. That being the case, we will
 * not bother with having getters and setters... instead, we will allow code to
 * interact with the fields directly.
 */
public class Idea {
    /**
     * The unique identifier associated with this element. It's final, because
     * we never want to change it.
     */
    public final int mId;

    /**
     * The content for this idea entry
     */
    public String mContent;

    /**
     * The like count for this idea entry
     */
    public int mLikeCount;

    /**
     * The userid associated with this idea entry
     */
    public String mUserId;

    /**
     * The creation date for this idea entry. Once it is set, it cannot be
     * changed. Not yet implemented in phase 1
     */
    // public final Date mCreated;

    /**
     * Constructor with the id and content specified.
     * Sets the mLikeCount to 0.
     * 
     * @param id      The id to associate with the Idea in this row. Assumed to be
     *                unique
     *                throughout the whole program.
     * @param content The content string for this Idea
     * @param userid  The userid associated with this Idea
     * 
     */
    public Idea(int id, String content, String userid) {
        mId = id;
        mContent = content;
        mUserId = userid;
        mLikeCount = 0;
    }

    /**
     * Constructor with the id, content, and likeCount specified.
     * Should not be used for creating a new ID
     * 
     * @param id        The id to associate with the Idea in this row. Assumed to be
     *                  unique
     *                  throughout the whole program.
     * @param content   The content string for this Idea
     * @param likeCount The number of likes this Idea has
     * @param userid    The userid associated with this Idea
     */
    public Idea(int id, String content, int likeCount, String userid) {
        mId = id;
        mContent = content;
        mLikeCount = likeCount;
        mUserId = userid;
    }

    /**
     * Copy constructor to create one Idea from another.
     * Currently not used in production code, just in the MockDatabase.
     * 
     * @param idea The idea to copy
     */
    Idea(Idea idea) {
        mId = idea.mId;
        // NB: Strings and Dates are immutable, so copy-by-reference is safe
        mContent = idea.mContent;
        mLikeCount = idea.mLikeCount;
        mUserId = idea.mUserId;
    }

    /**
     * ExtendedIdea is a subclass of Idea that includes the username
     * of the poster and the comments associated with the idea
     */
    public static class ExtendedIdea extends Idea {
        /**
         * The username of the poster
         */
        public String mPosterUsername;
        /**
         * The comments associated with this idea
         */
        public ArrayList<ExtendedComment> mComments;

        // This is the link stri
        public String mLink;

        // TODO: String format should be fileid
        public FileObject mFile;

        /**
         * Constructor with the id, content, likeCount, userid, posterUsername, and
         * comments specified.
         * 
         * @param id             The id to associate with the Idea in this row. Assumed
         *                       to be unique
         * @param content        The content string for this Idea
         * @param likeCount      The number of likes this Idea has
         * @param userid         The userid associated with this Idea
         * @param posterUsername The username of the poster
         * @param comments       The comments associated with this idea
         */
        public ExtendedIdea(int id, String content, int likeCount, String userid,
                String posterUsername, ArrayList<ExtendedComment> comments, FileObject file, String link) {
            super(id, content, likeCount, userid);
            this.mPosterUsername = posterUsername;
            this.mComments = comments;
            this.mFile = file;
            this.mLink = link;
        }

        /**
         * Constructor with the id, content, likeCount, userid, and posterUsername
         * specified.
         * 
         * @param id             The id to associate with the Idea in this row. Assumed
         *                       to be unique
         * @param content        The content string for this Idea
         * @param likeCount      The number of likes this Idea has
         * @param userid         The userid associated with this Idea
         * @param posterUsername The username of the poster
         */
        public ExtendedIdea(int id, String content, int likeCount, String userid,
                String posterUsername) {
            super(id, content, likeCount, userid);
            this.mPosterUsername = posterUsername;
            this.mComments = new ArrayList<ExtendedComment>();
        }
    }
}