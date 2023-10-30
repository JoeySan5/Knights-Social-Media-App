package edu.lehigh.cse216.knights.backend;

public class UserRequest {    

    /**
     * The unique identifier for the user. This is generally provided by the database and is unique 
     * throughout the whole program.
     * This field can be used for check the user's edit and delete permission.
     */
    public int mId;
    
    /**
     * The username of the user.
     * Display the username of the user who posted the comment and idea.
     */
    public String mUsername;

    /**
     * The email of the user.
     * By using the email, we can check that user is exist or not.
     */
    public String mEmail;

    /**
     * The GI (General Information) associated with the user. This could be additional information 
     * or metadata about the user.
     */
    public String mGI;

    /**
     * The SO (Specific Information) associated with the user. This could represent more specific 
     * details about the user.
     */
    public String mSO;

    /**
     * A note associated with the user. This could be any additional remarks or comments about the user.
     */
    public String mNote;

    /**
     * Validity of the user. This field indicates if the user is active and valid in the system.
     */
    public boolean mValid;
}