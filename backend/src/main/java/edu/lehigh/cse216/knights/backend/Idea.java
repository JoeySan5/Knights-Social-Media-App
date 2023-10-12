package edu.lehigh.cse216.knights.backend;

// import java.util.Date;

/**
 * Idea holds a row of information.  A row of information consists of
 * an identifier, strings for a "title" and "content", and a creation date.
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
     * Create a new Idea with the provided id and title/content, and a 
     * creation date based on the system clock at the time the constructor was
     * called. Like count always starts at 0.
     * 
     * @param id The id to associate with this row.  Assumed to be unique 
     *           throughout the whole program.
     * 
     * @param title The title string for this row of idea
     * 
     * @param content The content string for this row of idea
     */
    public Idea(int id, String content) {
        mId = id;
        mContent = content;
        mLikeCount = 0;
        // mCreated = new Date();
    }

    /**
     * Copy constructor to create one Idea from another.
     * Not currently used for Phase 1 implementation
     */
    Idea(Idea idea) {
        mId = idea.mId;
        // NB: Strings and Dates are immutable, so copy-by-reference is safe
        mContent = idea.mContent;
        mLikeCount = idea.mLikeCount;
        // mCreated = idea.mCreated;
    }
}