package edu.lehigh.cse216.knights.backend;

public class Comment {
    public final int mId;

    public String mUserId;

    public int mIdeaId;

    public String mContent;

    public Comment(int id, String userid, int ideaid, String content) {
        mId = id;
        mUserId = userid;
        mIdeaId = ideaid;
        mContent = content;
    }

    public Comment(Comment comment) {
        mId = comment.mId;
        mUserId = comment.mUserId;
        mIdeaId = comment.mIdeaId;
        mContent = comment.mContent;
    }
 
    public static class ExtendedComment extends Comment {
        public String mCommenterUsername;
    
        public ExtendedComment(int id, String userid, int ideaid, String content, String commenterUsername) {
            super(id, userid, ideaid, content);
            this.mCommenterUsername = commenterUsername;
        }
    }
    
}