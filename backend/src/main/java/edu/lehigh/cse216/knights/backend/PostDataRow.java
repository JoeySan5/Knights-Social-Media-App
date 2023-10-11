package edu.lehigh.cse216.tjp225.backend;

import java.util.Date;

/**
 * DataRow holds a row of information.  A row of information consists of
 * an identifier, strings for a "title" and "content", and a creation date.
 * 
 * Because we will ultimately be converting instances of this object into JSON
 * directly, we need to make the fields public.  That being the case, we will
 * not bother with having getters and setters... instead, we will allow code to
 * interact with the fields directly.
 */
public class PostDataRow {
    /**
     * The unique identifier associated with this element.  It's final, because
     * we never want to change it.
     */
    public final int mId;

    /**
     * The title for this row of data
     */
    public String mTitle;

    /**
     * The content for this row of data
     */
    public String mContent;

    /**
     * The like count for this row of data
     */
    public int mLikeCount;

    /**
     * The creation date for this row of data.  Once it is set, it cannot be 
     * changed. Not yet implemented in phase 1
     */
    // public final Date mCreated;

    /**
     * Create a new DataRow with the provided id and title/content, and a 
     * creation date based on the system clock at the time the constructor was
     * called
     * 
     * @param id The id to associate with this row.  Assumed to be unique 
     *           throughout the whole program.
     * 
     * @param title The title string for this row of data
     * 
     * @param content The content string for this row of data
     */
    public PostDataRow(int id, String title, String content, int likeCount) {
        mId = id;
        mTitle = title;
        mContent = content;
        mLikeCount = likeCount;
        // mCreated = new Date();
    }

    /**
     * Copy constructor to create one datarow from another
     */
    PostDataRow(PostDataRow data) {
        mId = data.mId;
        // NB: Strings and Dates are immutable, so copy-by-reference is safe
        mTitle = data.mTitle;
        mContent = data.mContent;
        mLikeCount = data.mLikeCount;
        // mCreated = data.mCreated;
    }
}