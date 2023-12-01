package edu.lehigh.cse216.knights.admin;

import java.net.URI;
import java.net.URISyntaxException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import com.google.gson.*;

/**
 * Object for communication between the PostegreSQL database and the Admin
 * Command Lini Interface.
 */
public class Database {
    /**
     * The connection to the database. When there is no connection, it should
     * be null. Otherwise, there is a valid open connection
     */
    private Connection mConnection;

    /** A prepared statement for getting all ideas in the database */
    private PreparedStatement mSelectAllUsers;

    /** A prepared statement for getting all ideas in the database */
    private PreparedStatement mSelectAllIdeas;

    /** A prepared statement for getting all ideas in the database */
    private PreparedStatement mSelectAllComments;

    /** A prepared statement for getting a likes in the database */
    private PreparedStatement mSelectAllLikes;

    /** A prepared statement for getting all comments on specific post */
    private PreparedStatement mSelectCommentsDetailedPost;

    /**
     * A prepared statement for getting one row from the database
     */
    private PreparedStatement mSelectOne;

    /** A prepared statement for deleting a row from the database */
    private PreparedStatement mDeleteOne;

    /** A prepared statement for inserting into the database */
    private PreparedStatement mInsertOne;

    /** Add a single user to the 'users' table */
    private PreparedStatement mInsertOneUser;

    /** Add a single idea to the 'ideas' table */
    private PreparedStatement mInsertOneIdea;

    /** Add a single comment to the 'comments' table */
    private PreparedStatement mInsertOneComment;

    /** Add a single like to the 'likes' table */
    private PreparedStatement mInsertOneLike;

    /**
     * A prepared statement for updating a single row in the database
     */
    private PreparedStatement mUpdateOne;

    /** A prepared statement for creating the table 'users' in our database */
    private PreparedStatement mCreateUsersTable;

    /** A prepared statement for creating the table 'ideas' in our database */
    private PreparedStatement mCreateIdeasTable;

    /** A prepared statement for creating the table 'comments' in our database */
    private PreparedStatement mCreateCommentsTable;

    /** A prepared statement for creating the table 'likes' in our database */
    private PreparedStatement mCreateLikesTable;

    /** A prepared statement for dropping the table 'users' from our database */
    private PreparedStatement mDropUsersTable;

    /** A prepared statement for dropping the table 'ideas' from our database */
    private PreparedStatement mDropIdeasTable;

    /** A prepared statement for dropping the table 'comments' from our database */
    private PreparedStatement mDropCommentsTable;

    /** A prepared statement for dropping the table 'likes' from our database */
    private PreparedStatement mDropLikesTable;

    /**
     * Sets the likeCount of all Ideas to 0. To be called when dropping the likes
     * table.
     */
    private PreparedStatement mZeroAllLikes;

    /** Set a User's valid field to a specified value */
    private PreparedStatement mSetUserValidation;

    /** Set an Idea's valid field to a specified value */
    private PreparedStatement mSetIdeaValidation;

    /**
     * Incremenet likeCount by some value. Should be called when an Like is
     * added/removed.
     */
    private PreparedStatement mUpdateIdeaLikeCount;

    /** Alter Ideas table with two new fields FiledId and link */
    private PreparedStatement mAlterIdeaTable;

    /** Alter Comments table with two new fields FiledId and link */
    private PreparedStatement mAlterCommentsTable;

    /**
     * The Database constructor is private: we only create Database objects
     * through the getDatabase() method.
     */
    private Database() {
    }

    private Database createPreparedStatements() {
        // Add all the prepared statements used to interact with the database
        try {
            // CREATE TABLE statements very importantly must be consistent between admin and
            // backend.
            this.mCreateUsersTable = this.mConnection.prepareStatement(
                    "CREATE TABLE users (userID VARCHAR(256) PRIMARY KEY, username VARCHAR(32) NOT NULL, " +
                            "email VARCHAR(64), GI VARCHAR(16), SO VARCHAR(16), note VARCHAR(128), valid BOOLEAN NOT NULL);");
            this.mCreateIdeasTable = this.mConnection.prepareStatement(
                    "CREATE TABLE ideas (ideaID SERIAL PRIMARY KEY, userID VARCHAR(256) REFERENCES users(userID), " +
                            "content VARCHAR(2048), likeCount INT, fileId VARCHAR(256), link VARCHAR(256), valid BOOLEAN NOT NULL)");
            this.mCreateCommentsTable = this.mConnection.prepareStatement(
                    "CREATE TABLE comments (commentID SERIAL PRIMARY KEY, ideaID INT REFERENCES ideas(ideaID), " +
                            "userID VARCHAR(256) REFERENCES users(userID), content VARCHAR(2048), fileId VARCHAR(256), link VARCHAR(256))");
            this.mCreateLikesTable = this.mConnection.prepareStatement(
                    "CREATE TABLE likes (ideaID INT REFERENCES ideas(ideaID), userID VARCHAR(256) REFERENCES users(userID), "
                            +
                            "value INT, PRIMARY KEY(ideaID, userID))");
            // DROP TABLE statements for each table. Use carefully- these will permanently
            // delete table data
            this.mDropUsersTable = this.mConnection.prepareStatement("DROP TABLE users");
            this.mDropIdeasTable = this.mConnection.prepareStatement("DROP TABLE ideas");
            this.mDropCommentsTable = this.mConnection.prepareStatement("DROP TABLE comments");
            this.mDropLikesTable = this.mConnection.prepareStatement("DROP TABLE likes");
            this.mZeroAllLikes = this.mConnection.prepareStatement("UPDATE ideas SET likeCount = 0");

            // Statements for adding new data
            this.mInsertOneLike = this.mConnection.prepareStatement(
                    "INSERT INTO likes (ideaId, userId, value) VALUES (?, ?, ?)");
            this.mUpdateIdeaLikeCount = this.mConnection
                    .prepareStatement("UPDATE ideas SET likeCount = likeCount + ? WHERE ideaID = ?");
            this.mInsertOneComment = this.mConnection.prepareStatement(
                    "INSERT INTO comments (ideaId, userId, content, fileId, link) VALUES (?, ?, ?, ? ,?)");
            this.mInsertOneUser = this.mConnection.prepareStatement(
                    "INSERT INTO users (userId, username, email, GI, SO, note, valid) VALUES (?, ?, ?, ?, ?, ?, ?)");
            this.mInsertOneIdea = this.mConnection.prepareStatement(
                    "INSERT INTO ideas (userId, content, likeCount, fileId, link, valid) VALUES (?, ?, ?, ?, ?, ?)");

            // Statements for getting values from tables
            this.mSelectAllUsers = this.mConnection.prepareStatement(
                    "SELECT * FROM users ORDER BY userID DESC");
            this.mSelectAllIdeas = this.mConnection.prepareStatement(
                    "SELECT * FROM ideas ORDER BY ideaID DESC");
            this.mSelectAllComments = this.mConnection.prepareStatement(
                    "SELECT * FROM comments ORDER BY commentid DESC");
            this.mSelectAllLikes = this.mConnection.prepareStatement(
                    "SELECT * FROM likes ORDER BY ideaId DESC");
            this.mSelectCommentsDetailedPost = this.mConnection.prepareStatement(
                "SELECT * FROM comments where ideaId = ?");

            // Statements for modifying a single entry in a table
            this.mSetUserValidation = this.mConnection.prepareStatement(
                    "UPDATE users SET valid = ? WHERE userID = ?");
            this.mSetIdeaValidation = this.mConnection.prepareStatement(
                    "UPDATE ideas SET valid = ? WHERE ideaID = ?");

            this.mAlterIdeaTable = this.mConnection.prepareStatement(
                    "SELECT COUNT(*) " +
                            "FROM information_schema.columns " +
                            "WHERE table_name = 'ideas' AND column_name IN ('FileId', 'link')");

            // checks if column exists
            this.mAlterCommentsTable = this.mConnection.prepareStatement(
                    "SELECT COUNT(*) " +
                            "FROM information_schema.columns " +
                            "WHERE table_name = 'comments' AND column_name IN ('FileId', 'link')");

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
     * Add a user to the users table
     * 
     * @param user the user to add, with all desired fields initialized
     * @return the number of rows added. Will be 0 if a SQLException occurs.
     */
    int insertUser(Entity.User user) {
        int count = 0;
        try {
            mInsertOneUser.setString(1, user.userId);
            mInsertOneUser.setString(2, user.username);
            mInsertOneUser.setString(3, user.email);
            mInsertOneUser.setString(4, user.GI);
            mInsertOneUser.setString(5, user.SO);
            mInsertOneUser.setString(6, user.note);
            mInsertOneUser.setBoolean(7, user.valid);
            count += mInsertOneUser.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return count;
    }

    /**
     * Add an idea to the ideas table
     * 
     * @param idea the idea to add, with all desired fields initialized.
     *             likeCount should be set to 0 for normal use.
     * @return the number of rows added. Will be 0 if a SQLException occurs.
     */
    int insertIdea(Entity.Idea idea) {
        int count = 0;
        try {
            // ideaID is type SERIAL, will increment automatically
            mInsertOneIdea.setString(1, idea.userId);
            mInsertOneIdea.setString(2, idea.content);
            mInsertOneIdea.setString(4, idea.fileId);
            mInsertOneIdea.setString(5, idea.link);
            // Could hard-code likeCount to be 0, but instead this control lets an admin
            // test edge cases
            if (idea.likeCount == null) {
                // Allow it to be not specified in sample data too
                mInsertOneIdea.setInt(3, 0);
            } else {
                // Backwards compatible with old sample data formatting with likeConut specified
                mInsertOneIdea.setInt(3, idea.likeCount);
            }
            mInsertOneIdea.setBoolean(6, idea.valid);
            count += mInsertOneIdea.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return count;
    }

    /**
     * Add a comment to the comments table
     * 
     * @param comment the comment to add, with all desired fields initialized
     * @return the number of comments added. Will be 0 if a SQLException occurs.
     */
    int insertComment(Entity.Comment comment) {
        int count = 0;
        try {
            // commentID is type SERIAL, will increment automatically
            mInsertOneComment.setInt(1, comment.ideaId);
            mInsertOneComment.setString(2, comment.userId);
            mInsertOneComment.setString(3, comment.content);
            mInsertOneComment.setString(4, comment.fileId);
            mInsertOneComment.setString(5, comment.link);
            count += mInsertOneComment.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return count;
    }

    /**
     * Add a like to the likes table, and increments the related idea appropriately.
     * 
     * @param like the like to add, with all desired fields initialized
     * @return the number of likes added. Will be 0 if a SQLException occurs.
     */
    int insertLike(Entity.Like like) {
        int count = 0;
        try {
            mInsertOneLike.setInt(1, like.ideaId);
            mInsertOneLike.setString(2, like.userId);
            mInsertOneLike.setInt(3, like.value);
            count += mInsertOneLike.executeUpdate();
            mUpdateIdeaLikeCount.setInt(1, like.value);
            mUpdateIdeaLikeCount.setInt(2, like.ideaId);
            mUpdateIdeaLikeCount.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return count;
    }

    /**
     * Query the database for a list of users
     * 
     * @return All users, as an ArrayList
     */
    ArrayList<Entity.User> selectAllUsers() {
        ArrayList<Entity.User> res = new ArrayList<Entity.User>();
        try {
            ResultSet rs = mSelectAllUsers.executeQuery();
            while (rs.next()) {
                res.add(new Entity.User(
                        rs.getString("userID"), rs.getString("username"),
                        rs.getString("email"), rs.getString("GI"),
                        rs.getString("SO"), rs.getString("note"),
                        rs.getBoolean("valid")));
            }
            rs.close();
            return res;
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Query the database for a list of all ideas
     * 
     * @return All Ideas, as an ArrayList
     */
    ArrayList<Entity.Idea> selectAllIdeas() {
        ArrayList<Entity.Idea> res = new ArrayList<Entity.Idea>();
        try {
            ResultSet rs = mSelectAllIdeas.executeQuery();
            while (rs.next()) {
                res.add(new Entity.Idea(
                        rs.getInt("ideaID"), rs.getString("userID"),
                        rs.getString("content"), rs.getInt("likeCount"),
                        rs.getBoolean("valid"), rs.getString("fileId"), rs.getString("link")));
            }
            rs.close();
            return res;
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Query the database for a list of all comments
     * 
     * @return
     */
    ArrayList<Entity.Comment> selectAllComments() {
        ArrayList<Entity.Comment> res = new ArrayList<Entity.Comment>();
        try {
            ResultSet rs = mSelectAllComments.executeQuery();
            while (rs.next()) {
                res.add(new Entity.Comment(
                        rs.getInt("commentId"), rs.getString("content"), rs.getString("userId"), rs.getInt("ideaid"),
                        rs.getString("fileId"), rs.getString("link")));
            }
            rs.close();
            return res;
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Query the database for a list of all likes
     * 
     * @return
     */
    ArrayList<Entity.Like> selectAllLikes() {
        ArrayList<Entity.Like> res = new ArrayList<Entity.Like>();
        try {
            ResultSet rs = mSelectAllLikes.executeQuery();
            while (rs.next()) {
                res.add(new Entity.Like(
                        rs.getString("userId"), rs.getInt("ideaId"), rs.getInt("value")));
            }
            rs.close();
            return res;
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    // select comments on a specific post
    ArrayList<Entity.Comment> selectCommentsDetailedPost(int ideaId){
        ArrayList<Entity.Comment> res = new ArrayList<Entity.Comment>();
        try{
            mSelectCommentsDetailedPost.setInt(1, ideaId);
            ResultSet rs = mSelectCommentsDetailedPost.executeQuery();
            if(rs.next() && rs.getInt(1) != 0){
                while(rs.next()){
                    res.add(new Entity.Comment(
                        rs.getInt("commentId"), rs.getString("content"), rs.getString("userId"), rs.getInt("ideaid"),
                        rs.getString("fileId"), rs.getString("link")));
                }
                rs.close();
                return res;
            } else {
                System.out.println("No comments under idea. Make sure the ideaId exists and is valid.");
                return null;
            }
        } catch (SQLException e){
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Sets a User to valid or invalid
     * 
     * @param userId   the ID of the user to modify
     * @param validity the new status for this user
     * @return the number of users affected. Will be 0 if a SQLException occurs.
     */
    int setUserValidity(String userId, boolean validity) {
        int res = 0;
        try {
            mSetUserValidation.setBoolean(1, validity);
            mSetUserValidation.setString(2, userId);
            res = mSetUserValidation.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return res;
    }

    /**
     * Sets an Idea to valid or invalid
     * 
     * @param ideaId   the ID of the idea to modify
     * @param validity the new status for this idea
     * @return the number of ideas affected. Will be 0 if a SQLException occurs.
     */
    int setIdeaValidity(int ideaId, boolean validity) {
        int res = 0;
        try {
            mSetIdeaValidation.setBoolean(1, validity);
            mSetIdeaValidation.setInt(2, ideaId);
            res = mSetIdeaValidation.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return res;
    }

    // /**
    // * Get all data for a specific row, by ID
    // *
    // * @param id The id of the row being requested
    // *
    // * @return The data for the requested row, or null if the ID was invalid
    // */
    // RowData selectOne(int id) {
    // RowData res = null;
    // try {
    // mSelectOne.setInt(1, id);
    // ResultSet rs = mSelectOne.executeQuery();
    // if (rs.next()) {
    // res = new RowData(rs.getInt("id"), rs.getString("content"),
    // rs.getInt("likeCount"));
    // }
    // } catch (SQLException e) {
    // e.printStackTrace();
    // }
    // return res;
    // }

    // /**
    // * Delete a row by ID
    // *
    // * @param id The id of the row to delete
    // *
    // * @return The number of rows that were deleted. -1 indicates an error.
    // */
    // int deleteRow(int id) {
    // int res = -1;
    // try {
    // mDeleteOne.setInt(1, id);
    // res = mDeleteOne.executeUpdate();
    // } catch (SQLException e) {
    // e.printStackTrace();
    // }
    // return res;
    // }

    /**
     * Creates one or all tables in the database. If the table(s) already exists,
     * this will print an error.
     * 
     * @param tableName the name of the table to be created. Case insensitive.
     * 
     * @return true if there was no SQL Error
     */
    boolean createTable(String tableName) {
        try {
            if (tableName.equalsIgnoreCase("users")) {
                mCreateUsersTable.execute();
            } else if (tableName.equalsIgnoreCase("ideas")) {
                mCreateIdeasTable.execute();
            } else if (tableName.equalsIgnoreCase("comments")) {
                mCreateCommentsTable.execute();
            } else if (tableName.equalsIgnoreCase("likes")) {
                mCreateLikesTable.execute();
            }
        } catch (SQLException e) {
            // could print stack trace for lots of details, but usually the error is
            // creating an already existing table
            // e.printStackTrace();
            return false;
        }
        return true;
    }

    /**
     * Remove a specified table from the database. If it does not exist, this will
     * print
     * an error.
     * 
     * @param tableName the name of the table to drop. Case insensitive.
     * 
     * @return true if there was no SQL Error
     */
    boolean dropTable(String tableName) {
        try {
            if (tableName.equalsIgnoreCase("users")) {
                mDropUsersTable.execute();
            } else if (tableName.equalsIgnoreCase("ideas")) {
                mDropIdeasTable.execute();
            } else if (tableName.equalsIgnoreCase("comments")) {
                mDropCommentsTable.execute();
            } else if (tableName.equalsIgnoreCase("likes")) {
                mDropLikesTable.execute();
                mZeroAllLikes.execute();
            }
        } catch (SQLException e) {
            // could print stack trace for lots of details, but usually the error is dropped
            // a nonexistent table
            // e.printStackTrace();
            return false;
        }
        return true;
    }

    /**
     * command to alter ideas table and add two new fields of FileId, link
     * checks if the column exists, if not then it does not create the columns
     * if columns do not exist, creates them
     * 
     * @return true if sucessfully executed, false if not
     */
    boolean alterIdeaTable() {
        try {
            // mAlterIdeaTable.execute();
            ResultSet ideasAltCheck = mAlterIdeaTable.executeQuery();
            // if column does not exists, alter table and add it
            if (ideasAltCheck.next() && ideasAltCheck.getInt(1) == 0) {
                this.mAlterCommentsTable = this.mConnection.prepareStatement(
                        "ALTER TABLE ideas " +
                                "ADD FileId VARCHAR(256), " +
                                "ADD link VARCHAR(256)");

                this.mAlterCommentsTable.executeQuery();
            }
        } catch (SQLException e) {
            System.out.println("Error updating Ideas table using Alt command.");
            e.printStackTrace();
            return false;
        }
        return true;
    }

    /**
     * command to alter comments table and add two new fields of FileId and link
     * checks if the column exists, if not then it does not create the columns
     * if columns do not exist, creates them
     * 
     * @return true if sucessful, false if not
     */
    boolean alterCommentsTable() {
        try {
            // mAlterCommentsTable.execute();
            ResultSet commentsAltCheck = mAlterCommentsTable.executeQuery();
            // if column does not exists, alter table and add it
            if (commentsAltCheck.next() && commentsAltCheck.getInt(1) == 0) {
                this.mAlterCommentsTable = this.mConnection.prepareStatement(
                        "ALTER TABLE comments " +
                                "ADD FileId VARCHAR(256), " +
                                "ADD link VARCHAR(256)");

                this.mAlterCommentsTable.executeQuery();
            }
        } catch (SQLException e) {
            System.out.println("Error updating Comments table using Alt command.");
            e.printStackTrace();
            return false;
        }
        return true;
    }
}