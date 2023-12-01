package edu.lehigh.cse216.knights.backend;

/**
 * Comment is a class that represents a single comment on a single idea. It
 * includes the comment's unique id, the id of the idea it is commenting on,
 * the id of the user who wrote the comment, and the comment's content.
 * Also, ExtendedComment is a subclass of Comment that includes the username
 */
public class Comment {

    /**
     * The unique identifier associated with this element. It's final, because
     * we never want to change it.
     */
    public final int mId;

    /**
     * The user id associated with this comment
     */
    public String mUserId;

    /**
     * The idea id associated with this comment
     */
    public int mIdeaId;

    /**
     * The content for this comment entry
     */
    public String mContent;

    // This is the link string
    public String mLink;

    // File object
    public FileObject mFile;

    /**
     * Constructor with the id, userid, ideaid, and content specified.
     * 
     * @param id      The id to associate with the Comment in this row. Assumed to
     *                be unique
     * @param userid  The userid associated with this Comment
     * @param ideaid  The ideaid associated with this Comment
     * @param content The content string for this Comment
     */
    public Comment(int id, String userid, int ideaid, String content, String mLink, FileObject mFile) {
        this.mId = id;
        this.mUserId = userid;
        this.mIdeaId = ideaid;
        this.mContent = content;
        this.mLink = mLink;
        this.mFile = mFile;
    }

    /**
     * Copy constructor
     * 
     * @param comment The comment to copy
     */
    public Comment(Comment comment) {
        mId = comment.mId;
        mUserId = comment.mUserId;
        mIdeaId = comment.mIdeaId;
        mContent = comment.mContent;
    }

    /**
     * ExtendedComment is a subclass of Comment that includes the username
     * of the commenter
     */
    public static class ExtendedComment extends Comment {
        /**
         * The username of the commenter
         */
        public String mCommenterUsername;

        /**
         * Constructor with the id, userid, ideaid, content, and commenterUsername
         * specified.
         * 
         * @param id                The id to associate with the Comment in this row.
         *                          Assumed to be unique
         * @param userid            The userid associated with this Comment
         * @param ideaid            The ideaid associated with this Comment
         * @param content           The content string for this Comment
         * @param commenterUsername The username of the commenter
         */
        public ExtendedComment(int id, String userid, int ideaid, String content, String commenterUsername,
                String mLink, FileObject mFile) {
            super(id, userid, ideaid, content, mLink, mFile);
            this.mCommenterUsername = commenterUsername;
        }
    }

}