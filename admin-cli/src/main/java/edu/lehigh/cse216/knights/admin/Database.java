package edu.lehigh.cse216.knights.admin;

import java.net.URI;
import java.net.URISyntaxException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import java.util.ArrayList;

public class Database {
    /**
     * The connection to the database.  When there is no connection, it should
     * be null.  Otherwise, there is a valid open connection
     */
    private Connection mConnection;

    /**
     * A prepared statement for getting all data in the database
     */
    private PreparedStatement mSelectAll;

    /**
     * A prepared statement for getting one row from the database
     */
    private PreparedStatement mSelectOne;

    /** A prepared statement for deleting a row from the database */
    private PreparedStatement mDeleteOne;

    /** A prepared statement for inserting into the database  */
    private PreparedStatement mInsertOne;

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

    /** Sets the likeCount of all Ideas to 0. To be called when dropping the likes table. */
    private PreparedStatement mZeroAllLikes;

    private PreparedStatement mSetUservalidation;

    private PreparedStatement mSetIdeaValidation;

    

    /**
     * RowData is like a struct in C: we use it to hold data, and we allow 
     * direct access to its fields.  In the context of this Database, RowData 
     * represents the data we'd see in a row.
     * 
     * We make RowData a static class of Database because we don't really want
     * to encourage users to think of RowData as being anything other than an
     * abstract representation of a row of the database.  RowData and the 
     * Database are tightly coupled: if one changes, the other should too.
     */
    public static class RowData {
        /**
         * The ID of this row of the database
         */
        int mId;
        /**
         * The content stored in this row
         */
        String mContent;
        /**
         * The likeCount stored in this row
         */
        int mLikeCount;

        /**
         * Construct a RowData object by providing values for its fields
         */
        public RowData(int id, String content, int likeCount) {
            mId = id;
            mContent = content;
            mLikeCount = likeCount;
        }
    }

    /**
     * The Database constructor is private: we only create Database objects 
     * through the getDatabase() method.
     */
    private Database() {
    }

    private Database createPreparedStatements() {
        // Add all the prepared statements used to interact with the database
        try {
            // CREATE TABLE statements very importantly must be consistent between admin and backend.
            this.mCreateUsersTable = this.mConnection.prepareStatement(
                "CREATE TABLE users ( userID VARCHAR(256) PRIMARY KEY, username VARCHAR(32) NOT NULL, "+
                "email VARCHAR(64), GI VARCHAR(16), SO VARCHAR(16), note VARCHAR(128), valid BOOLEAN NOT NULL);");
            this.mCreateIdeasTable = this.mConnection.prepareStatement(
                "CREATE TABLE ideas ( ideaID SERIAL PRIMARY KEY, userID VARCHAR(256) REFERENCES users(userID), "+
                "content VARCHAR(2048), likeCount INT, valid BOOLEAN NOT NULL)");
            this.mCreateCommentsTable = this.mConnection.prepareStatement(
                "CREATE TABLE comments (commentID SERIAL PRIMARY KEY, ideaID INT REFERENCES ideas(ideaID), "+
                "userID VARCHAR(256) REFERENCES users(userID), content VARCHAR(2048))");
            this.mCreateLikesTable = this.mConnection.prepareStatement(
                "CREATE TABLE likes (ideaID INT, userID VARCHAR(256), value INT, PRIMARY KEY (ideaID, userID))");
            // DROP TABLE statements for each table. Use carefully- these will permanently delete table data
            this.mDropUsersTable = this.mConnection.prepareStatement("DROP TABLE users");
            this.mDropIdeasTable = this.mConnection.prepareStatement("DROP TABLE ideas");
            this.mDropCommentsTable = this.mConnection.prepareStatement("DROP TABLE comments");
            this.mDropLikesTable = this.mConnection.prepareStatement("DROP TABLE likes");
            this.mZeroAllLikes = this.mConnection.prepareStatement("UPDATE ideas SET likeCount = 0");

            
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
        if( path==null || "".equals(path) ){
            path="/";
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
    * @param db_url The url to the database
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
     *     error occurred during the closing operation.
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
     * Insert a row into the database
     * 
     * @param content The content for this new row
     * @param message The message body for this new row
     * 
     * @return The number of rows that were inserted
     */
    int insertRow(String content) {
        int count = 0;
        try {
            mInsertOne.setString(1, content);
            mInsertOne.setInt(2, 0); // set likeCount to 0 by default
            count += mInsertOne.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return count;
    }

    /**
     * Query the database for a list of all contents and their IDs
     * 
     * @return All rows, as an ArrayList
     */
    ArrayList<RowData> selectAll() {
        ArrayList<RowData> res = new ArrayList<RowData>();
        try {
            ResultSet rs = mSelectAll.executeQuery();
            while (rs.next()) {
                res.add(new RowData(rs.getInt("id"), rs.getString("content"), rs.getInt("likeCount")));
            }
            rs.close();
            return res;
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Get all data for a specific row, by ID
     * 
     * @param id The id of the row being requested
     * 
     * @return The data for the requested row, or null if the ID was invalid
     */
    RowData selectOne(int id) {
        RowData res = null;
        try {
            mSelectOne.setInt(1, id);
            ResultSet rs = mSelectOne.executeQuery();
            if (rs.next()) {
                res = new RowData(rs.getInt("id"), rs.getString("content"), rs.getInt("likeCount"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return res;
    }

    /**
     * Delete a row by ID
     * 
     * @param id The id of the row to delete
     * 
     * @return The number of rows that were deleted.  -1 indicates an error.
     */
    int deleteRow(int id) {
        int res = -1;
        try {
            mDeleteOne.setInt(1, id);
            res = mDeleteOne.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return res;
    }

    /**
     * Update the message for a row in the database
     * 
     * @param id The id of the row to update
     * @param message The new message contents
     * 
     * @return The number of rows that were updated.  -1 indicates an error.
     */
    // int updateOne(int id, String message) {
    //     int res = -1;
    //     try {
    //         mUpdateOne.setString(1, message);
    //         mUpdateOne.setInt(2, id);
    //         res = mUpdateOne.executeUpdate();
    //     } catch (SQLException e) {
    //         e.printStackTrace();
    //     }
    //     return res;
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
            if(tableName.equalsIgnoreCase("users")){
                mCreateUsersTable.execute();
            } else if(tableName.equalsIgnoreCase("ideas")){
                mCreateIdeasTable.execute();
            } else if(tableName.equalsIgnoreCase("comments")){
                mCreateCommentsTable.execute();
            } else if(tableName.equalsIgnoreCase("likes")){
                mCreateLikesTable.execute();
            }
        } catch (SQLException e) {
            // could print stack trace for lots of details, but usually the error is creating an already existing table
            // e.printStackTrace();
            return false;
        }
        return true;
    }

    /**
     * Remove a specified table from the database.  If it does not exist, this will print
     * an error.
     * 
     * @param tableName the name of the table to drop. Case insensitive.
     * 
     * @return true if there was no SQL Error
     */
    boolean dropTable(String tableName) {
        try {
            if(tableName.equalsIgnoreCase("users")){
                mDropUsersTable.execute();
            } else if(tableName.equalsIgnoreCase("ideas")){
                mDropIdeasTable.execute();
            } else if(tableName.equalsIgnoreCase("comments")){
                mDropCommentsTable.execute();
            } else if(tableName.equalsIgnoreCase("likes")){
                mDropLikesTable.execute();
                mZeroAllLikes.execute();
            }
        } catch (SQLException e) {
            // could print stack trace for lots of details, but usually the error is dropped a nonexistent table
            // e.printStackTrace();
         return false;
        }
        return true;
    }
}