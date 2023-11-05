package edu.lehigh.cse216.knights.admin;


/**
 * Entity in the database. Used to convert sample JSON data to actual entries in the PostgreSQL database.
 * See backend docs for more information about the entities.
 * 
 * NB: since this will be created from JSON, all fields must be public, and we do not need a constructor.
 */
public abstract class Entity {

    /**
     * User to be created from JSON data
     */
    public class User extends Entity {
        /**
         * The unique identifier associated with this user. It's final, because
         * we never want to change it.
         */
        public String userId;
    
        /**
         * The username for this user entry
         */
        public String username;
    
        /**
         * The email for this user entry
         */
        public String email;
    
        /**
         * The GI for this user entry
         */
        public String GI;
    
        /**
         * The SO for this user entry
         */
        public String SO;
    
        /**
         * A note associated with this user
         */
        public String note;
    
        /**
         * Validity of the user
         */
        public boolean valid;
    }

    /**
     * Idea to be created from JSON data
     */
    public class Idea extends Entity {
        /**
         * The unique identifier associated with this element.  It's final, because
         * we never want to change it.
         */
        public int ideaId;

        /** ID of user who posted this idea */
        public String userId;

        /**
         * The content for this idea entry
         */
        public String content;

        /**
         * The like count for this idea entry
         */
        public int likeCount;

        /**
         * Validity of the user
         */
        public boolean valid;
    }

    /**
     * Like to be created from JSON data
     */
    public class Like extends Entity {
        /**
         * The unique identifier associated with this element.  It's final, because
         * we never want to change it.
         */

        /**
         * The content for this idea entry
         */
        public String userId;

        /**
         * The content for this idea entry
         */
        public int ideaId;

        /**
         * The like count for this idea entry
         */
        public int value;
    }

    /**
     * Comment to be created from JSON data
     */
    public class Comment extends Entity {
        /**
         * The unique identifier associated with this element.  It's final, because
         * we never want to change it.
         */
        public int commentId;

        /**
         * The content for this idea entry
         */
        public String content;

        /**
         * The like count for this idea entry
         */
        public String userId;

        /**
         * The like count for this idea entry
         */
        public int ideaId;
    }

}