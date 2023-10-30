package edu.lehigh.cse216.knights.backend;

// import java.util.Date; // should use something like java.sql.Date instead

/**
 * Idea holds a row of information.  A row of information consists of
 * an identifier, a string for "content", and an integer for likeCount.
 * 
 * Because we will ultimately be converting instances of this object into JSON
 * directly, we need to make the fields public.  That being the case, we will
 * not bother with having getters and setters... instead, we will allow code to
 * interact with the fields directly.
 */
public class Idea {
    /**
     * The unique identifier associated with this element.  It's final, because
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
     * The creation date for this idea entry.  Once it is set, it cannot be 
     * changed. Not yet implemented in phase 1
     */
    // public final Date mCreated;

    /**
     * Constructor with the id and content specified.
     * Sets the mLikeCount to 0.
     * 
     * @param id The id to associate with the Idea in this row.  Assumed to be unique 
     *           throughout the whole program.
     * @param content The content string for this Idea
     */
    public Idea(int id, String content) {
        mId = id;
        mContent = content;
        mLikeCount = 0;
    }

    /**
     * Constructor with the id, content, and likeCount specified.
     * Should not be used for creating a new ID
     * 
     * @param id The id to associate with the Idea in this row.  Assumed to be unique 
     *           throughout the whole program.
     * @param content The content string for this Idea
     * @param likeCount The number of likes this Idea has
     */
    public Idea(int id, String content, int likeCount) {
        mId = id;
        mContent = content;
        mLikeCount = likeCount;
    }

    /**
     * Copy constructor to create one Idea from another.
     * Currently not used in production code, just in the MockDatabase.
     */
    Idea(Idea idea) {
        mId = idea.mId;
        // NB: Strings and Dates are immutable, so copy-by-reference is safe
        mContent = idea.mContent;
        mLikeCount = idea.mLikeCount;
    }
}