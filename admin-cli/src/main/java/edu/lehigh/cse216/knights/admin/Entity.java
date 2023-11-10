package edu.lehigh.cse216.knights.admin;


/**
 * Entity in the database. Used to convert sample data from JSON 
 * and to communicate between the Admin App and the PostgreSQL database.
 * ---
 * See backend docs for more information about the entities.
 */
public class Entity {

    /**
     * User to be created from JSON data
     */
    public static class User {
        
        /** Unique 32-character string representing the user */
        public String userId;
    
        /** The username for this user entry */
        public String username;
    
        /** The email for this user entry */
        public String email;
    
        /** The GI for this user entry */
        public String GI;
    
        /** The SO for this user entry */
        public String SO;
    
        /** A note associated with this user */
        public String note;
    
        /** Validity of the user */
        public boolean valid;

        /**
         * User constructor with all parameters specified
         * @param userId id
         * @param username username
         * @param email email
         * @param GI GI
         * @param SO SO
         * @param note note
         * @param valid validity
         */
        public User(String userId, String username, String email, String GI, String SO, String note, boolean valid) {
            this.userId = userId;
            this.username = username;
            this.email = email;
            this.GI = GI;
            this.SO = SO;
            this.note = note;
            this.valid = valid;
        }        
    }

    /**
     * Idea to be created from JSON data
     */
    public static class Idea {

        /** The unique identifier associated with this element.*/
        public int ideaId;

        /** ID of user who posted this idea */
        public String userId;

        /** The content for this idea entry */
        public String content;

        /** The like count for this idea entry. 
         * Integer class type to allow for null values from sample data */
        public Integer likeCount;

        /** Validity of the user */
        public boolean valid;

        /**
         * Idea constructor with all parameters specified
         * @param ideaId idea id
         * @param userId user id
         * @param content content of idea
         * @param likeCount number of likes
         * @param valid validity
         */
        public Idea(int ideaId, String userId, String content, int likeCount, boolean valid) {
            this.ideaId = ideaId;
            this.userId = userId;
            this.content = content;
            this.likeCount = likeCount;
            this.valid = valid;
        }
    }

    /**
     * Like to be created from JSON data
     */
    public static class Like {

        /** The content for this idea entry */
        public String userId;

        /** The content for this idea entry */
        public int ideaId;

        /** The like count for this idea entry */
        public int value;

        /**
         * Like constructor with all parameters specified
         * @param userId user ID
         * @param ideaId idea ID
         * @param value 1 for upvote or -1 for downvote
         */
        public Like(String userId, int ideaId, int value) {
            this.userId = userId;
            this.ideaId = ideaId;
            this.value = value;
        }
    }

    /**
     * Comment to be created from JSON data
     */
    public static class Comment {

        /** CommentID for the comment.
         * Not read from JSON, automatically tracked as SERIAL PRIMARY */
        public int commentId;

        /** The content for this idea entry */
        public String content;

        /** The like count for this idea entry */
        public String userId;

        /** The like count for this idea entry */
        public int ideaId;

        /**
         * Comment constructor with all parameters specified 
         * @param commentId comment ID
         * @param content content of comment
         * @param userId user ID
         * @param ideaId idea ID
         */
        public Comment(int commentId, String content, String userId, int ideaId) {
            this.commentId = commentId;
            this.content = content;
            this.userId = userId;
            this.ideaId = ideaId;
        }
    }

}