package edu.lehigh.cse216.knights.admin;


/**
 * Entity in the database. Used to convert sample data from JSON 
 * and to communicate between the Admin App and the PostgreSQL database.
 * ---
 * See backend docs for more information about the entities.
 */
public abstract class Entity {

    /**
     * User to be created from JSON data
     */
    public class User extends Entity {
        
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
    public class Idea extends Entity {

        /** The unique identifier associated with this element.*/
        public int ideaId;

        /** ID of user who posted this idea */
        public String userId;

        /** The content for this idea entry */
        public String content;

        /** The like count for this idea entry */
        public int likeCount;

        /** Validity of the user */
        public boolean valid;

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
    public class Like extends Entity {

        /** The content for this idea entry */
        public String userId;

        /** The content for this idea entry */
        public int ideaId;

        /** The like count for this idea entry */
        public int value;

        public Like(String userId, int ideaId, int value) {
            this.userId = userId;
            this.ideaId = ideaId;
            this.value = value;
        }
    }

    /**
     * Comment to be created from JSON data
     */
    public class Comment extends Entity {

        public int commentId;

        /** The content for this idea entry */
        public String content;

        /** The like count for this idea entry */
        public String userId;

        /** The like count for this idea entry */
        public int ideaId;

        public Comment(int commentId, String content, String userId, int ideaId) {
            this.commentId = commentId;
            this.content = content;
            this.userId = userId;
            this.ideaId = ideaId;
        }
    }

}