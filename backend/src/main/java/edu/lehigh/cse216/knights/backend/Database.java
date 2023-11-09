package edu.lehigh.cse216.knights.backend;

import java.net.URI;
import java.net.URISyntaxException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import java.util.ArrayList;

import edu.lehigh.cse216.knights.backend.Comment.ExtendedComment;
import edu.lehigh.cse216.knights.backend.Idea.ExtendedIdea;

/**
 * Database interacts with the ElephantSQL database through a set of
 * preparedStatements
 * and returns the appropriate result to the HTTP server App
 */
public class Database {
    /**
     * The connection to the database. When there is no connection, it should
     * be null. Otherwise, there is a valid open connection
     */
    private Connection mConnection;

    // ******************************************************************************

    // The functionality to create and drop tables is the responsibility of the admin,
    // and these prepared statements are not typically used in ordinary cases
    /**
     * A prepared statement for creating the table in our database
     */
    private PreparedStatement mCreateIdeaTable;

    /**
     * A prepared statement for dropping the table in our database
     */
    private PreparedStatement mDropIdeaTable;

    /**
     * A prepared statement for creating the user table in our database
     */
    private PreparedStatement mCreateUserTable;

    /**
     * A prepared statement for dropping the user table in our database
     */
    private PreparedStatement mDropUserTable;

    // ******************************************************************************

    /**
     * A prepared statement for getting all ideas in the database
     */
    private PreparedStatement mSelectAllIdeas;

    /**
     * A prepared statement for getting one idea from the database
     */
    private PreparedStatement mSelectOneIdea;

    /**
     * A prepared statement for deleting an idea from the database
     * But, this functionality is unlikely to be used in practice. Phase 2 guidelines do not provide instructions for implementing this feature
     */
    private PreparedStatement mDeleteOneIdea;

    /**
     * A prepared statement for inserting an idea into the database
     */
    private PreparedStatement mInsertOneIdea;

    /**
     * A prepared statement for updating the likeCount of a single idea in the
     * database
     */
    private PreparedStatement mUpdateIdeaLikeCount;

    /**
     * A prepared statement for checking if a like exists
     * This statement is related to the like functionality
     */
    private PreparedStatement mCheckIfLikeExists;

    /**
     * A prepared statement for inserting a new like into the database
     * This statement is related to the like functionality
     */
    private PreparedStatement mInsertNewLike;

    /**
     * A prepared statement for deleting a like from the database
     * This statement is related to the like functionality
     */
    private PreparedStatement mDeleteOneLike;

    /**
     * A prepared statement for updating a like in the database
     * This statement is related to the like functionality
     */
    private PreparedStatement mUpdateOneLike;

    /**
     * A prepared statement for inserting a new user into the database with default profile
     */
    private PreparedStatement mInsertNewUser;

    /**
     * A prepared statement for updating a user's profile in the database
     */
    private PreparedStatement mUpdateOneUser;

    /**
     * A prepared statement for getting the poster name of an idea
     */
    private PreparedStatement mGetPosterName;

    /**
     * A prepared statement for getting information of a specific user
     * Based on the user's session key request, limited information can be provided, or all information can be provided.
     */
    private PreparedStatement mSelectOneUser;

    /**
     * A prepared statement for inserting a new comment into the database
     */
    private PreparedStatement mInsertOneComment;

    /**
     * A prepared statement for updating a comment in the database
     */
    private PreparedStatement mUpdateOneComment;

    /**
     * A prepared statement for getting all comments of a specific idea
     */
    private PreparedStatement mSelectAllComments;

    /**
     * A prepared statement for getting the commenter username of a specific comment
     */
    private PreparedStatement mGetCommenterName;

    /**
     * The Database constructor is private: we only create Database objects
     * through the getDatabase() method.
     */
    private Database() {
    }

    /**
     * Helper for getDatabase(). Attempts to create all prepared statements.
     * Disconnects from the server on any failure.
     * 
     * @return A version of the Database caller with prepared statements, or null on
     *         failure.
     */
    private Database createPreparedStatements() {
        // Attempt to create all of our prepared statements. If any of these
        // fail, the whole getDatabase() call should fail
        try {
            // NB: the SQL fields must exactly match with the SQL specified by the Admin
            // CLI.
            // We have "ideas" as the table name for example - this must be consistent
            // across the Admin and Backend components

            // ******************************************************************************

            // tjp Question: should we use 'id' or 'ID'? Really a choice for admin to make
            // sej Answer:n PostgreSQL, identifiers (table names, column names, etc.) are case-insensitive by default. 
            // If you don't enclose identifiers in double quotes (""), PostgreSQL will convert them to lowercase.
            // We will still use 'ID' for the sake of team convention.

            // ******************************************************************************
            // These four prepared statements are not actually used in the backend.
            // Note: no "IF NOT EXISTS" or "IF EXISTS" checks on table
            // creation/deletion, so multiple executions will cause an exception
            this.mCreateIdeaTable = this.mConnection.prepareStatement(
                    "CREATE TABLE ideas (ID SERIAL PRIMARY KEY, content VARCHAR(2048) NOT NULL, likeCount INT)");

            this.mDropIdeaTable = this.mConnection.prepareStatement("DROP TABLE ideas");

            this.mCreateUserTable = this.mConnection.prepareStatement(
                    "CREATE TABLE users (" +
                            "userID VARCHAR(256) PRIMARY KEY, " +
                            "username VARCHAR(30) NOT NULL, " +
                            "email VARCHAR(50), " +
                            "GI VARCHAR(10), " +
                            "SO VARCHAR(15), " +
                            "note VARCHAR(100), " +
                            "valid BOOLEAN NOT NULL" +
                            ")");

            this.mDropUserTable = this.mConnection.prepareStatement("DROP TABLE users");

            this.mDeleteOneIdea = this.mConnection.prepareStatement("DELETE FROM ideas WHERE ideaid = ?"); // Not
                                                                                                       // implemented in
                                                                                                       // Phase 1? - Yes, also in phase 2. 
            // ******************************************************************************

            // Standard CRUD operations
            // tjp: these SQL prepared statement are essential for understanding exactly
            // what the backend is asking the database
            this.mInsertOneIdea = this.mConnection
                    .prepareStatement("INSERT INTO ideas (content, userid, likeCount) VALUES (?, ?, 0)");

            // Get all ideas in the database
            this.mSelectAllIdeas = this.mConnection
                    .prepareStatement("SELECT ideaid, content, likeCount, userid FROM ideas " +
                                        "WHERE valid IS TRUE " +
                                        "ORDER BY ideaid DESC");

            // Get one idea information from the database
            this.mSelectOneIdea = this.mConnection.prepareStatement("SELECT * from ideas WHERE ideaid=? AND valid IS TRUE");

            // Update the likeCount of a single idea in the database
            this.mUpdateIdeaLikeCount = this.mConnection
                    .prepareStatement("UPDATE ideas SET likeCount = likeCount + ? WHERE ideaid = ?");

            // Register user's default profile
            this.mInsertNewUser = this.mConnection.prepareStatement(
                    "INSERT INTO users (email, valid, username, GI, SO, note, userid) " +
                            "VALUES ('unknown', true, 'unknown', 'unknown', 'unknown', 'unknown', ?)");
            // Edit user's profile
            this.mUpdateOneUser = this.mConnection.prepareStatement(
                    "UPDATE users SET username = ?, email = ?, GI = ?, SO = ?, note = ? WHERE userId = ?");

            // Get all information of specific user
            this.mSelectOneUser = this.mConnection.prepareStatement(
                    "SELECT * from users WHERE userid=?");

            // Post a comment to an specific idea with specific user
            this.mInsertOneComment = this.mConnection.prepareStatement(
                    "INSERT INTO comments (content, userid, ideaid) VALUES (?, ?, ?)");

            // Edit a comment to an specific idea with specific user
            this.mUpdateOneComment = this.mConnection.prepareStatement(
                    "UPDATE comments SET content = ? WHERE commentid = ?");

            // Get all comments of specific idea
            this.mSelectAllComments = this.mConnection.prepareStatement(
                    "SELECT * from comments WHERE ideaid=?");

            // Get the commenter username of specific comment
            this.mGetCommenterName = this.mConnection.prepareStatement(
                    "SELECT u.username " +
                            "FROM comments c " +
                            "JOIN users u ON c.userid = u.userid " +
                            "WHERE c.commentid = ?");

            // Get the PosterName
            this.mGetPosterName = this.mConnection.prepareStatement(
                    "SELECT u.username " +
                            "FROM ideas i " +
                            "JOIN users u ON i.userid = u.userid " +
                            "WHERE i.ideaid = ?");

            // Get the value for checking if a like exists
            // in the likes table. The query will return the 'like' value if it exists.
            // If no 'like' is found, the result set will be empty.
            this.mCheckIfLikeExists = this.mConnection.prepareStatement(
                    "SELECT value " +
                            "FROM likes " +
                            "WHERE userid = ? AND ideaid = ?");

            // Insert a new like into the table
            this.mInsertNewLike = this.mConnection.prepareStatement(
                    "INSERT INTO likes (ideaid, userid, value) VALUES (?, ?, ?)");
            
            // Delete a like from the table
            this.mDeleteOneLike = this.mConnection.prepareStatement(
                    "DELETE FROM likes WHERE ideaid = ? AND userid = ?");

            // Update a like in the table
            this.mUpdateOneLike = this.mConnection.prepareStatement(
                    "UPDATE likes SET value = ? WHERE ideaid = ? AND userid = ?");

        } catch (SQLException e) {
            System.err.println("Error creating prepared statement");
            e.printStackTrace();
            this.disconnect();
            return null;
        }
        return this;
    }

    /**
     * Get a fully-configured connection to the database
     * 
     * @param host The IP address or hostname of the database server
     * @param port The port on the database server to which connection requests
     *             should be sent
     * @param path The path to use, can be null
     * @param user The user ID to use when connecting
     * @param pass The password to use when connecting
     * 
     * @return A Database object, or null if we cannot connect properly
     */
    static Database getDatabase(String host, String port, String path, String user, String pass) {
        if (path == null || "".equals(path)) {
            path = "/";
        }

        // Create an un-configured Database object
        Database db = new Database();

        // Give the Database object a connection, fail if we cannot get one
        try {
            String dbUrl = "jdbc:postgresql://" + host + ':' + port + path;
            Connection conn = DriverManager.getConnection(dbUrl, user, pass);
            if (conn == null) {
                System.err.println("Error: DriverManager.getConnection() returned a null object");
                return null;
            }
            db.mConnection = conn;
        } catch (SQLException e) {
            System.err.println("Error: DriverManager.getConnection() threw a SQLException");
            e.printStackTrace();
            return null;
        }

        db = db.createPreparedStatements();
        return db;
    }

    /**
     * Get a fully-configured connection to the database
     * 
     * @param db_url       The url to the database
     * @param port_default port to use if absent in db_url
     * 
     * @return A Database object, or null if we cannot connect properly
     */
    static Database getDatabase(String db_url, String port_default) {
        try {
            URI dbUri = new URI(db_url);
            String username = dbUri.getUserInfo().split(":")[0];
            String password = dbUri.getUserInfo().split(":")[1];
            String host = dbUri.getHost();
            String path = dbUri.getPath();
            String port = dbUri.getPort() == -1 ? port_default : Integer.toString(dbUri.getPort());

            return getDatabase(host, port, path, username, password);
        } catch (URISyntaxException s) {
            System.out.println("URI Syntax Error");
            return null;
        }
    }

    /**
     * Close the current connection to the database, if one exists.
     * 
     * NB: The connection will always be null after this call, even if an
     * error occurred during the closing operation.
     * 
     * @return True if the connection was cleanly closed, false otherwise
     */
    boolean disconnect() {
        if (mConnection == null) {
            System.err.println("Unable to close connection: Connection was null");
            return false;
        }
        try {
            mConnection.close();
        } catch (SQLException e) {
            System.err.println("Error: Connection.close() threw a SQLException");
            e.printStackTrace();
            mConnection = null;
            return false;
        }
        mConnection = null;
        return true;
    }

    /**
     * Insert an idea into the database. Content is given in the request,
     * and likeCount is set to 0
     * 
     * @param content The content body for this new idea
     * 
     * @return The number of ideas that were inserted
     */
    int insertIdea(String content, String userId) {
        int count = 0;
        try {
            mInsertOneIdea.setString(1, content);
            mInsertOneIdea.setString(2, userId);
            // likeCount will automatically be set to 0; it is written into the
            // preparedStatement
            count += mInsertOneIdea.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return count;
    }

    /**
     * Query the database for a list of ideas
     * with their IDs, content, and likeCounts
     * 
     * @return All Ideas, as an ArrayList
     */
    ArrayList<ExtendedIdea> selectAllIdeas() {
        ArrayList<ExtendedIdea> res = new ArrayList<ExtendedIdea>();
        try {
            ResultSet rs = mSelectAllIdeas.executeQuery();
            
            while (rs.next()) {
                int ideaId = rs.getInt("ideaid");
                mGetPosterName.setInt(1, ideaId);
                ResultSet rsPoster = mGetPosterName.executeQuery();
                String posterUsername = "";
                if (rsPoster.next()) {
                    posterUsername = rsPoster.getString("username");
                }
            rsPoster.close();
                res.add(new ExtendedIdea(
                        rs.getInt("ideaid"),
                        rs.getString("content"),
                        rs.getInt("likeCount"),
                        rs.getString("userid"),
                        posterUsername));
            }
            rs.close();
            return res;
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Get all data for a specific idea, by ID
     * 
     * @param id The ID of the idea being requested
     * 
     * @return The data for the requested idea, or null if the ID was invalid
     */
    ExtendedIdea selectOneIdea(int ideaId) {
        ExtendedIdea res = null;
        try {
            mSelectOneIdea.setInt(1, ideaId);
            ResultSet rs = mSelectOneIdea.executeQuery();
            if (rs.next()) {
                mGetPosterName.setInt(1, ideaId);
                ResultSet rsPoster = mGetPosterName.executeQuery();
                String posterUsername = "";
                if (rsPoster.next()) {
                    posterUsername = rsPoster.getString("username");
                }
                rsPoster.close();

                ArrayList<ExtendedComment> comments = new ArrayList<>();
                mSelectAllComments.setInt(1, ideaId);
                ResultSet rsComments = mSelectAllComments.executeQuery();
                while (rsComments.next()) {
                    int commentId = rsComments.getInt("commentid");
                    String content = rsComments.getString("content");
                    String userId = rsComments.getString("userid");

                    mGetCommenterName.setInt(1, commentId);
                    ResultSet rsCommenter = mGetCommenterName.executeQuery();
                    String commenterUsername = "";
                    if (rsCommenter.next()) {
                        commenterUsername = rsCommenter.getString("username");
                    }
                    rsCommenter.close();

                    comments.add(new ExtendedComment(commentId, userId, ideaId, content, commenterUsername));
                }
                rsComments.close();

                res = new ExtendedIdea(
                        rs.getInt("ideaid"),
                        rs.getString("content"),
                        rs.getInt("likeCount"),
                        rs.getString("userid"),
                        posterUsername,
                        comments);
            }
            rs.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return res;
    }

    /**
     * Gets a single user from the users table. Can specify restriction to prevent sending private
     * info on a GET request on a profile page.
     * @param userId userId of the requested user
     * @param restrictInfo set to true is the user accessing data other of a user other than themself.
     * @return the User with all non restricted data, or null if no User with the userId exists
     */
    User selectOneUser(String userId, boolean restrictInfo) {
        User res = null;
        try {
            mSelectOneUser.setString(1, userId);
            ResultSet rs = mSelectOneUser.executeQuery();
            if (rs.next()) {
                if(restrictInfo){
                    // Do not save the GI or SO if restricted
                    res = new User(
                        rs.getString("userid"),
                        rs.getString("username"),
                        rs.getString("email"),
                        rs.getString("note"),
                        rs.getBoolean("valid"));
                }
                else{
                    res = new User(
                        rs.getString("userid"),
                        rs.getString("username"),
                        rs.getString("email"),
                        rs.getString("GI"),
                        rs.getString("SO"),
                        rs.getString("note"),
                        rs.getBoolean("valid"));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return res;
    }


    // ******************************************************************************
    // unused in phase 1, 2
    /**
     * Delete an idea by ID
     * 
     * @param id The id of the row to delete
     * 
     * @return The number of rows that were deleted. -1 indicates an error.
     */
    int deleteIdea(int ideaId) {
        int res = -1;
        try {
            mDeleteOneIdea.setInt(1, ideaId);
            res = mDeleteOneIdea.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return res;
    }
    // ******************************************************************************


    /**
     * Get the previous like value of a user for an idea
     * 
     * @param userID The id of the user
     * @param ideaID The id of the idea
     * 
     * @return The previous like value of the user for the idea. 0 indicates that the user has netural position about this idea
     */
    int previousLikeValue(String userID, int ideaID){
        try{
        mCheckIfLikeExists.setString(1, userID);
        mCheckIfLikeExists.setInt(2, ideaID);
        
        // Return true if the user has (dis)liked the post before
        ResultSet res = mCheckIfLikeExists.executeQuery();
        if(res.next()){
            return res.getInt("value"); // The value will be 1 or -1.
        } else{
            return 0;
        }
        }
        catch(SQLException e){
            e.printStackTrace();
        }
        return -1;
    }


    /**
     * Update the likeCount for an idea in the database
     * 
     * @param id        The id of the row to update
     * @param likeDelta the requested amount to change likes by; must be 1 or -1 to
     *                  be successful
     * 
     * @return The amount of posts affected by the given likeCountged. 0 indicates
     *         an error.
     */
    int updateIdeaLikeCount(String userID, int ideaId, int likeValue) {
        // tjp: I'm open to changing what the return value should be, e.g.
        // differentiating between a dislike and like
        int res = -1;
        try {
            if (likeValue == 1 || likeValue == -1) {
                int likeDelta = likeValue;
                int previousLikeValue = previousLikeValue(userID, ideaId);
                // Case 1: No like currently exists
                if(previousLikeValue == 0){
                    mInsertNewLike.setInt(1, ideaId);
                    mInsertNewLike.setString(2, userID);
                    mInsertNewLike.setInt(3, likeValue);
                    res = mInsertNewLike.executeUpdate();
                }
                else {
                    if(likeValue == previousLikeValue){
                        // delete (dis)like from table
                        mDeleteOneLike.setInt(1, ideaId);
                        mDeleteOneLike.setString(2, userID);
                        res = mDeleteOneLike.executeUpdate();

                        likeDelta = -1*likeValue;
                    }
                    else if(likeValue != previousLikeValue){
                        mUpdateOneLike.setInt(1, likeValue);
                        mUpdateOneLike.setInt(2, ideaId);
                        mUpdateOneLike.setString(3, userID);
                        res = mUpdateOneLike.executeUpdate();

                        likeDelta = 2*likeValue;
                    }
                }
                mUpdateIdeaLikeCount.setInt(1, likeDelta);
                mUpdateIdeaLikeCount.setInt(2, ideaId);
                res = mUpdateIdeaLikeCount.executeUpdate();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return res;
    }

    /**
     * Insert a new user into the database
     * 
     * @param userId The id of the user
     * 
     * @return The number of users that were inserted. 0 indicates an error.
     */
    int insertNewUser(String userId) {
        int count = 0;
        try {
            mInsertNewUser.setString(1, userId);
            count += mInsertNewUser.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return count;
    }

    /**
     * Update a user's profile in the database
     * 
     * @param req The request object containing the user's information
     * 
     * @return The number of users that were updated = 1. 0 indicates an error.
     */
    int updateOneUser(Request.UserRequest req) {
        try {
            mUpdateOneUser.setString(1, req.mUsername);
            mUpdateOneUser.setString(2, req.mEmail);
            mUpdateOneUser.setString(3, req.mGI);
            mUpdateOneUser.setString(4, req.mSO);
            mUpdateOneUser.setString(5, req.mNote);
            mUpdateOneUser.setString(6, req.mId);
            return mUpdateOneUser.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
            return 0;
        }
    }
    
    /**
     * Get the poster name of an idea
     * 
     * @param ideaid The id of the idea
     * 
     * @return The poster name of the idea. null indicates an error.
     */
    String getPosterName(int ideaid) {
        String posterName = null;
        try {
            mGetPosterName.setInt(1, ideaid);
            ResultSet rs = mGetPosterName.executeQuery();
            if (rs.next()) {
                posterName = rs.getString("username");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return posterName;
    }

    /**
     * Insert a new comment into the database
     * 
     * @param content The content of the comment
     * @param userId The id of the user
     * @param ideaId The id of the idea
     * 
     * @return The number of comments that were inserted. 0 indicates an error.
     */
    int insertNewComment(String content, String userId, int ideaId) {
        int count = 0;
        try {
            mInsertOneComment.setString(1, content);
            mInsertOneComment.setString(2, userId);
            mInsertOneComment.setInt(3, ideaId);
            count += mInsertOneComment.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return count;
    }

    /**
     * Update a comment in the database
     * 
     * @param content The content of the comment
     * @param commentId The id of the comment
     * 
     * @return The number of comments that were updated. 0 indicates an error.
     */
    int updateOneComment(String content, int commentId) {
        try {
            mUpdateOneComment.setString(1, content);
            mUpdateOneComment.setInt(2, commentId);
            return mUpdateOneComment.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
            return 0;
        }
    }

    /**
     * Get the Comment information of a specific comment
     * 
     * @param commentId The id of the comment
     * 
     * @return The comment information. null indicates an error.
     */
    ArrayList<Comment> selectAllComments(int ideaId) {
        ArrayList<Comment> res = new ArrayList<Comment>();
        try {
            mSelectAllComments.setInt(1, ideaId);
            ResultSet rs = mSelectAllComments.executeQuery();
            while (rs.next()) {
                res.add(new Comment(
                        rs.getInt("id"),
                        rs.getString("userid"),
                        rs.getInt("ideaid"),
                        rs.getString("content")));
            }
            rs.close();
            return res;
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }


    // ******************************************************************************
    // unused functions in Backend. in phase 1, 2
    /**
     * Create idea tblData. If it already exists, this will print an error
     */
    void createIdeaTable() {
        try {
            mCreateIdeaTable.execute();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * Remove idea tblData from the database. If it does not exist, this will print
     * an error.
     */
    void dropIdeaTable() {
        try {
            mDropIdeaTable.execute();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * Create user tblData. If it already exists, this will print an error
     */

    void CreateUserTable() {
        try {
            mCreateUserTable.execute();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * Remove user tblData from the database. If it does not exist, this will print
     * an error.
     */
    void DropUserTable() {
        try {
            mDropUserTable.execute();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    // ******************************************************************************

}