package edu.lehigh.cse216.knights.backend;

/**
 * IdeaRequest provides a format for clients to present a content for POST-ing ideas, 
 * or a likeIncrement value for PUT-ing ideas through likes and dislikes
 * 
 * NB: since this will be created from JSON, all fields must be public, and we
 *     do not need a constructor.
 */
public class IdeaRequest {    

    
    /**
     * The unique identifier for the idea. This is generally provided by the database and is unique
     * thoruhg the whole program.
     * 
     * This field can be used for display the comments on the idea.
     */
    public int mId;


    /**
     * The content being provided by the client.
     * 
     * In the case of POST, mContent contains the idea's content, so the json should
     * look like "{'mContent':'an example idea'}".
     */
    public String mContent;

    /**
     * In the case of like/dislike requests through PUT, mLikeIncrement is the amount 
     * by which the like total should be changed (+1 or -1). The json should
     * look like "{'mLikeIncrement':'1'}" or "{'mLikeIncrement':'-1'}".
     */
    public int mLikeIncrement;

    /**
     * The userId associated with the idea.
     */
    public String mUserId;

}
