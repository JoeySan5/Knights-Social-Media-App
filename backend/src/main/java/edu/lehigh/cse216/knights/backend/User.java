package edu.lehigh.cse216.knights.backend;

/**
 * User holds a row of information for a user. A row of information consists of
 * various fields like userID, username, email, etc.
 * 
 * Because we will ultimately be converting instances of this object into JSON
 * directly, we need to make the fields public. That being the case, we will
 * not bother with having getters and setters... instead, we will allow code to
 * interact with the fields directly.
 */
public class User {
    /**
     * The unique identifier associated with this user. It's final, because
     * we never want to change it.
     */
    public final String mId;

    /**
     * The username for this user entry
     */
    public String mUsername;

    /**
     * The email for this user entry
     */
    public String mEmail;

    /**
     * The GI for this user entry
     */
    public String mGI;

    /**
     * The SO for this user entry
     */
    public String mSO;

    /**
     * A note associated with this user
     */
    public String mNote;

    /**
     * Validity of the user
     */
    public boolean mValid;

    /**
     * Constructor for creating a new User instance when they first register.
     * This constructor only sets the user's ID and email, initializing other fields 
     * with default values. It's assumed that the user will update these fields later.
     * 
     * @param id The id to associate with the User in this row. Assumed to be unique 
     *               throughout the whole program.
     * @param email The email for this User
     */

    public User(String id, String email) {
        mId = id;
        mEmail = email;

        mUsername = "";
        mGI = "";
        mSO = "";
        mNote = "";
        mValid = true;
    }

    /**
     * Constructor for creating a User instance with full profile details.
     * This constructor is intended to be used when a user updates their profile
     * or when loading a user's full profile from a data source.
     * 
     * @param id The id to associate with the User in this row. Assumed to be unique
     *          throughout the whole program.
     * @param username The username for this User
     * @param email The email for this User
     * @param gi The GI for this User
     * @param so The SO for this User
     * @param note The note for this User
     */

    public User(String id, String username, String email, String gi, String so, String note, boolean valid) {
        mId = id;
        mUsername = username;
        mEmail = email;
        mGI = gi;
        mSO = so;
        mNote = note;
        mValid = valid;
    }

    /**
     * Constructor for creating a User instance when request the profile page of another user.
     * This constructor sets GI and SO to null, but initializes other fields 
     * with given values.
     * 
     * @param id The id to associate with the User in this row. Assumed to be unique 
     *               throughout the whole program.
     * @param email The email for this User
     */

    public User(String id, String username, String email, String note, boolean valid) {
        mId = id;
        mUsername = username;
        mEmail = email;
        mGI = null;
        mSO = null;
        mNote = note;
        mValid = valid;
    }

}