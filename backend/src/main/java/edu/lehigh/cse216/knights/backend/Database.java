package edu.lehigh.cse216.knights.backend;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.URI;
import java.net.URISyntaxException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.concurrent.TimeoutException;

import com.google.cloud.storage.Blob;
import com.google.cloud.storage.BlobId;
import com.google.cloud.storage.BlobInfo;
import com.google.cloud.storage.Bucket;
import com.google.cloud.storage.Storage;
import com.google.cloud.storage.StorageOptions;
import com.google.gson.Gson;

import edu.lehigh.cse216.knights.backend.Comment.ExtendedComment;
import edu.lehigh.cse216.knights.backend.Idea.ExtendedIdea;
import net.rubyeye.xmemcached.MemcachedClient;
import net.rubyeye.xmemcached.MemcachedClientBuilder;
import net.rubyeye.xmemcached.XMemcachedClientBuilder;
import net.rubyeye.xmemcached.auth.AuthInfo;
import net.rubyeye.xmemcached.command.BinaryCommandFactory;
import net.rubyeye.xmemcached.exception.MemcachedException;
import net.rubyeye.xmemcached.utils.AddrUtil;

/**
 * Database interacts with the ElephantSQL database through a set of
 * preparedStatements
 * and returns the appropriate result to the HTTP server App
 */
public class Database {
    final Gson gson = new Gson();
    /**
     * The connection to the database. When there is no connection, it should
     * be null. Otherwise, there is a valid open connection
     */
    private Connection mConnection;

    // ******************************************************************************

    // The functionality to create and drop tables is the responsibility of the
    // admin,
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
     * But, this functionality is unlikely to be used in practice. Phase 2
     * guidelines do not provide instructions for implementing this feature
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
     * A prepared statement for inserting a new user into the database with default
     * profile
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
     * Based on the user's session key request, limited information can be provided,
     * or all information can be provided.
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
     * A prepared statement for getting the commenter ID of a specific comment
     */
    private PreparedStatement mGetCommenterUserID;

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
            // sej Answer:n PostgreSQL, identifiers (table names, column names, etc.) are
            // case-insensitive by default.
            // If you don't enclose identifiers in double quotes (""), PostgreSQL will
            // convert them to lowercase.
            // We will still use 'ID' for the sake of team convention.

            // ******************************************************************************
            // These four prepared statements are not actually used in the backend.
            // Note: no "IF NOT EXISTS" or "IF EXISTS" checks on table
            // creation/deletion, so multiple executions will cause an exception
            this.mCreateIdeaTable = this.mConnection.prepareStatement(
                    "CREATE TABLE ideas (ID SERIAL PRIMARY KEY, content VARCHAR(2048) NOT NULL, likeCount INT)");

            this.mDropIdeaTable = this.mConnection.prepareStatement("DROP TABLE ideas");

            // Standard CRUD operations
            // tjp: these SQL prepared statement are essential for understanding exactly
            // what the backend is asking the database
            this.mDeleteOneIdea = this.mConnection.prepareStatement("DELETE FROM ideas WHERE ideaid = ?"); // Not
            // implemented in
            // Phase 1?
            this.mInsertOneIdea = this.mConnection
                    .prepareStatement(
                            "INSERT INTO ideas (content, userid, likeCount, valid, fileid ,link) VALUES (?, ?, 0, true, ?, ?)");

            this.mSelectAllIdeas = this.mConnection
                    .prepareStatement("SELECT ideaid, content, likeCount, userid FROM ideas " +
                            "WHERE valid IS TRUE " +
                            "ORDER BY ideaid DESC");

            this.mSelectOneIdea = this.mConnection
                    .prepareStatement("SELECT * from ideas WHERE ideaid=? AND valid IS TRUE");

            this.mUpdateIdeaLikeCount = this.mConnection
                    .prepareStatement("UPDATE ideas SET likeCount = likeCount + ? WHERE ideaid = ?");

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

            this.mDeleteOneIdea = this.mConnection.prepareStatement("DELETE FROM ideas WHERE ideaID = ?"); // Not
            // implemented in
            // Phase 1? - Yes, also in phase 2.
            // ******************************************************************************

            // Standard CRUD operations
            // tjp: these SQL prepared statement are essential for understanding exactly
            // what the backend is asking the database

            this.mInsertNewUser = this.mConnection.prepareStatement(
                    "INSERT INTO users (email, valid, username, GI, SO, note, userID) " +
                            "VALUES ('unknown', true, 'unknown', 'unknown', 'unknown', 'unknown', ?)");
            // Edit user's profile
            this.mUpdateOneUser = this.mConnection.prepareStatement(
                    "UPDATE users SET username = ?, email = ?, GI = ?, SO = ?, note = ? WHERE userID = ?");

            // Get all information of specific user
            this.mSelectOneUser = this.mConnection.prepareStatement(
                    "SELECT * from users WHERE userID=?");

            // Post a comment to an specific idea with specific user
            this.mInsertOneComment = this.mConnection.prepareStatement(
                    "INSERT INTO comments (content, userID, ideaID, fileid ,link) VALUES (?, ?, ?,?,?)");

            // Edit a comment to an specific idea with specific user
            this.mUpdateOneComment = this.mConnection.prepareStatement(
                    "UPDATE comments SET content = ? WHERE commentID = ?");

            // Get all comments of specific idea
            this.mSelectAllComments = this.mConnection.prepareStatement(
                    "SELECT * from comments WHERE ideaID=?");

            // Get the commenter username of specific comment
            this.mGetCommenterName = this.mConnection.prepareStatement(
                    "SELECT u.username " +
                            "FROM comments c " +
                            "JOIN users u ON c.userID = u.userID " +
                            "WHERE c.commentID = ?");

            // Get the PosterName
            this.mGetPosterName = this.mConnection.prepareStatement(
                    "SELECT u.username " +
                            "FROM ideas i " +
                            "JOIN users u ON i.userID = u.userID " +
                            "WHERE i.ideaID = ?");

            // Get the commenter userID
            this.mGetCommenterUserID = this.mConnection.prepareStatement(
                    "SELECT userID " +
                            "FROM comments " +
                            "WHERE commentID = ?");

            // Get the value for checking if a like exists
            // in the likes table. The query will return the 'like' value if it exists.
            // If no 'like' is found, the result set will be empty.
            this.mCheckIfLikeExists = this.mConnection.prepareStatement(
                    "SELECT value " +
                            "FROM likes " +
                            "WHERE userID = ? AND ideaID = ?");

            // Insert a new like into the table
            this.mInsertNewLike = this.mConnection.prepareStatement(
                    "INSERT INTO likes (ideaID, userID, value) VALUES (?, ?, ?)");

            // Delete a like from the table
            this.mDeleteOneLike = this.mConnection.prepareStatement(
                    "DELETE FROM likes WHERE ideaID = ? AND userID = ?");

            // Update a like in the table
            this.mUpdateOneLike = this.mConnection.prepareStatement(
                    "UPDATE likes SET value = ? WHERE ideaID = ? AND userID = ?");

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
    int insertIdea(String content, String userId, String file, String link) {
        int count = 0;
        try {
            mInsertOneIdea.setString(1, content);
            mInsertOneIdea.setString(2, userId);
            mInsertOneIdea.setString(3, file);
            mInsertOneIdea.setString(4, link);
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

            // Here we will have to retrieve the file ID from the database. Once we have the
            // file, we want to retrieve it from google cloud.
            // Once we have the file we want to encode it into base64. Once in base64 we
            // will be adding it to the extended idea

            // Also must get link from database, and return that as well.

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
                    String link = rsComments.getString("link");
                    String fileId = rs.getString("fileid");
                    FileObject file = null;
                    System.out.println("this is rs" + rs);
                    System.out.println("this is get fileid" + rs.getString("fileid"));

                    if (fileId != null && !fileId.isEmpty()) {
                        file = retrieveFileObject(fileId);
                    }

                    mGetCommenterName.setInt(1, commentId);
                    ResultSet rsCommenter = mGetCommenterName.executeQuery();
                    String commenterUsername = "";
                    if (rsCommenter.next()) {
                        commenterUsername = rsCommenter.getString("username");
                    }
                    rsCommenter.close();

                    comments.add(
                            new ExtendedComment(commentId, userId, ideaId, content, commenterUsername, link, file));
                }
                rsComments.close();

                // resposne from database will have the fileID, not a file object.
                // With this fileID, we will go to the cache or google storage and retrieve the
                // object
                // Once the object is retrieved we decode data and send back proper FileObject
                // ID
                // FileObject file = function(fileId);
                String fileId = rs.getString("fileid");
                FileObject file = null;
                System.out.println("this is rs" + rs);
                System.out.println("this is get fileid" + rs.getString("fileid"));

                if (fileId != null && !fileId.isEmpty()) {
                    file = retrieveFileObject(fileId);
                }

                res = new ExtendedIdea(
                        rs.getInt("ideaid"),
                        rs.getString("content"),
                        rs.getInt("likeCount"),
                        rs.getString("userid"),
                        posterUsername,
                        comments,
                        rs.getString("link"),
                        file);
            }
            rs.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return res;
    }

    /**
     * Gets a single user from the users table. Can specify restriction to prevent
     * sending private
     * info on a GET request on a profile page.
     * 
     * @param userId       userId of the requested user
     * @param restrictInfo set to true is the user accessing data other of a user
     *                     other than themself.
     * @return the User with all non restricted data, or null if no User with the
     *         userId exists
     */
    User selectOneUser(String userId, boolean restrictInfo) {
        User res = null;
        try {
            mSelectOneUser.setString(1, userId);
            ResultSet rs = mSelectOneUser.executeQuery();
            if (rs.next()) {
                if (restrictInfo) {
                    // Do not save the GI or SO if restricted
                    res = new User(
                            rs.getString("userid"),
                            rs.getString("username"),
                            rs.getString("email"),
                            rs.getString("note"),
                            rs.getBoolean("valid"));
                } else {
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
     * @return The previous like value of the user for the idea. 0 indicates that
     *         the user has netural position about this idea
     */
    int previousLikeValue(String userID, int ideaID) {
        try {
            mCheckIfLikeExists.setString(1, userID);
            mCheckIfLikeExists.setInt(2, ideaID);

            // Return true if the user has (dis)liked the post before
            ResultSet res = mCheckIfLikeExists.executeQuery();
            if (res.next()) {
                return res.getInt("value"); // The value will be 1 or -1.
            } else {
                return 0;
            }
        } catch (SQLException e) {
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
                if (previousLikeValue == 0) {
                    mInsertNewLike.setInt(1, ideaId);
                    mInsertNewLike.setString(2, userID);
                    mInsertNewLike.setInt(3, likeValue);
                    res = mInsertNewLike.executeUpdate();
                } else {
                    if (likeValue == previousLikeValue) {
                        // delete (dis)like from table
                        mDeleteOneLike.setInt(1, ideaId);
                        mDeleteOneLike.setString(2, userID);
                        res = mDeleteOneLike.executeUpdate();

                        likeDelta = -1 * likeValue;
                    } else if (likeValue != previousLikeValue) {
                        mUpdateOneLike.setInt(1, likeValue);
                        mUpdateOneLike.setInt(2, ideaId);
                        mUpdateOneLike.setString(3, userID);
                        res = mUpdateOneLike.executeUpdate();

                        likeDelta = 2 * likeValue;
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
     * @param userId  The id of the user
     * @param ideaId  The id of the idea
     * 
     * @return The number of comments that were inserted. 0 indicates an error.
     */
    int insertNewComment(String content, String userId, int ideaId, String file, String link) {
        int count = 0;
        try {
            mInsertOneComment.setString(1, content);
            mInsertOneComment.setString(2, userId);
            mInsertOneComment.setInt(3, ideaId);
            mInsertOneComment.setString(4, file);
            mInsertOneComment.setString(5, link);
            count += mInsertOneComment.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return count;
    }

    /**
     * Update a comment in the database
     * 
     * @param content   The content of the comment
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
                String fileId = rs.getString("fileid");
                FileObject file = null;
                System.out.println("this is rs" + rs);
                System.out.println("this is get fileid" + rs.getString("fileid"));

                if (fileId != null && !fileId.isEmpty()) {
                    file = retrieveFileObject(fileId);
                }

                res.add(new Comment(
                        rs.getInt("id"),
                        rs.getString("userid"),
                        rs.getInt("ideaid"),
                        rs.getString("content"),
                        rs.getString("link"),
                        file));
            }
            rs.close();
            return res;
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Get the commenter username of a specific comment
     * 
     * @param commentID The id of the comment
     * 
     * @return The commenter username. null indicates an error.
     */

    String getCommenterUserID(int commentID) {
        String CommenterUserID = "default";
        try {
            mGetCommenterUserID.setInt(1, commentID);
            ResultSet rs = mGetCommenterUserID.executeQuery();
            if (rs.next()) {
                System.out.println(rs.getString("userID"));
                CommenterUserID = rs.getString("userID");
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
        return CommenterUserID;

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

    String parseFileid(FileObject file) {
        String fileId = "";
        // Get the current timestamp
        LocalDateTime timestamp = LocalDateTime.now();

        // Format the timestamp using a DateTimeFormatter
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd_HH:mm:ss");
        String formattedTimestamp = timestamp.format(formatter);
        try {
            fileId = file.getmFileName();
            fileId = fileId + formattedTimestamp;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return fileId;
    }

    FileObject retrieveFileObject(String fileId) {

        // first we want to check if memecache has the object
        FileObject file = getFileFromCache(fileId);
        if (file != null) {
            System.out.println("\nFile found in cache\n");
            return file;
        } else {
            // if memecache does not have the object then retrieve it from google storage
            // and add it into memecache
            System.out
                    .println("\nFile not found in cache. Retrieved from storage, and put in cache for 1000 seconds\n ");
            return getFileFromStorage(fileId);
        }

    }

    private FileObject getFileFromCache(String fileId) {

        System.out.println("\nTHSI IS FILE ID IN GETFILEFROMCACHE:" + fileId);
        // conncecting to cache
        MemcachedClient mc = null;
        if (fileId == "") {
            System.out.println("not null, 1");
            return null;
        }
        if (fileId == null) {
            System.out.println("not null, 2");
            return null;
        }
        try {
            System.out.println("In try catch ");

            mc = createMemcachedClient();
            String response = mc.get(fileId);
            if (response == null) {
                return null;
            } else {
                System.out.println("\nhere is response from cache:" + response);
                System.out.println(gson.fromJson(response, FileObject.class));
                return gson.fromJson(response, FileObject.class);
            }

        } catch (IOException | TimeoutException | InterruptedException | MemcachedException e) {
            System.err.println("Couldn't create a connection to MemCachier: " +
                    e.getMessage());
        }

        if (mc == null) {
            System.err.println("mc is null. Exiting.");
            System.exit(1);
        }

        return null;
    }

    FileObject getFileFromStorage(String fileId) {
        String fileType = "";
        String base64String = "";
        // Initialize the Google Cloud Storage client
        // Gets information from env var GOOGLE_APPLICATION_CREDENTIALS
        Storage storage = StorageOptions.getDefaultInstance().getService();
        // The name of google cloud storage bucket
        String bucketName = "knights-bucket-2";
        String blobName = fileId;
        BlobId blobId = BlobId.of(bucketName, blobName);
        Blob blob = storage.get(blobId);

        System.out.println("\nhere is blob: " + blobId);

        if (blob != null) {
            // Retrieving data
            fileType = blob.getContentType();
            base64String = Base64.getEncoder().encodeToString(blob.getContent());
            // Output the results
            System.out.println("File Type: " + fileType);
            System.out.println("Base64 Data: " + base64String);
            System.out.println("File Name: " + fileId);
        } else {
            System.out.println("Blob does not exist in cloud storage");
            return null;
        }

        try {
            storage.close();
        } catch (Exception e) {
            System.err.println("Error closing storage in selectOneIdea function");
            e.printStackTrace();
        }

        insertFileToCache(new FileObject(fileId, fileType, base64String));
        return new FileObject(fileId, fileType, base64String);
    }

    private void insertFileToCache(FileObject fileObject) {
        String fileAsString = gson.toJson(fileObject);
        String fileName = fileObject.getmFileName();

        // conncecting to cache
        MemcachedClient mc = null;
        try {
            mc = createMemcachedClient();
        } catch (IOException e) {
            System.err.println("Couldn't create a connection to MemCachier: " +
                    e.getMessage());
        }

        if (mc == null) {
            System.err.println("mc is null. Exiting.");
            System.exit(1);
        }

        try {
            System.out.println("\nfileName (key):" + fileName + " fileAsString: " + fileAsString);
            mc.set(fileName, 1000, fileAsString);
        } catch (TimeoutException | InterruptedException | MemcachedException e) {
            System.err.println("error inserting file to cache:" + e.getMessage());
            e.printStackTrace();
        }

    }

    private static MemcachedClient createMemcachedClient() throws IOException {
        List<InetSocketAddress> servers = AddrUtil.getAddresses(System.getenv("MEMCACHIER_SERVERS").replace(",", " "));
        AuthInfo authInfo = AuthInfo.plain(System.getenv("MEMCACHIER_USERNAME"), System.getenv("MEMCACHIER_PASSWORD"));

        // this builds the memcache client
        MemcachedClientBuilder builder = new XMemcachedClientBuilder(servers);

        // Configure SASL auth for each server
        for (InetSocketAddress server : servers) {
            builder.addAuthInfo(server, authInfo);
        }

        // Use binary protocol
        builder.setCommandFactory(new BinaryCommandFactory());
        // Connection timeout in milliseconds (default: )
        builder.setConnectTimeout(1000);
        // Reconnect to servers (default: true)
        builder.setEnableHealSession(true);
        // Delay until reconnect attempt in milliseconds (default: 2000)
        builder.setHealSessionInterval(2000);
        MemcachedClient mc = builder.build();
        return mc;
    }

}