package edu.lehigh.cse216.knights.backend;

import java.net.URI;
import java.net.URISyntaxException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import java.util.ArrayList;

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
     * A prepared statement for creating the table in our database
     */
    private PreparedStatement mCreateIdeaTable;

    /**
     * A prepared statement for dropping the table in our database
     */
    private PreparedStatement mDropIdeaTable;

    private PreparedStatement mCreateUserTable;

    private PreparedStatement mDropUserTable;

    private PreparedStatement mInsertNewUser;

    private PreparedStatement mUpdateOneUser;

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

            // Note: no "IF NOT EXISTS" or "IF EXISTS" checks on table
            // creation/deletion, so multiple executions will cause an exception
            this.mCreateIdeaTable = this.mConnection.prepareStatement(
                    "CREATE TABLE ideas (id SERIAL PRIMARY KEY, content VARCHAR(2048) NOT NULL, likeCount INT)");
            // tjp Question: should we use 'id' or 'ID'? Really a choice for admin to make
            this.mDropIdeaTable = this.mConnection.prepareStatement("DROP TABLE ideas");

            // Standard CRUD operations
            // tjp: these SQL prepared statement are essential for understanding exactly
            // what the backend is asking the database
            this.mDeleteOneIdea = this.mConnection.prepareStatement("DELETE FROM ideas WHERE id = ?"); // Not
                                                                                                       // implemented in
                                                                                                       // Phase 1?
            this.mInsertOneIdea = this.mConnection.prepareStatement("INSERT INTO ideas VALUES (default, ?, 0)");
            this.mSelectAllIdeas = this.mConnection
                    .prepareStatement("SELECT id, content, likeCount FROM ideas ORDER BY id DESC");
            this.mSelectOneIdea = this.mConnection.prepareStatement("SELECT * from ideas WHERE id=?");
            this.mUpdateIdeaLikeCount = this.mConnection
                    .prepareStatement("UPDATE ideas SET likeCount = likeCount + ? WHERE id = ?");

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

            this.mInsertNewUser = this.mConnection.prepareStatement(
                    "INSERT INTO users (email, valid, username, GI, SO, note) " +
                            "VALUES (?, true, 'unknown', 'unknown', 'unknown', 'unknown')");

            this.mUpdateOneUser = this.mConnection.prepareStatement(
                    "UPDATE users SET username = ?, GI = ?, SO = ?, note = ? WHERE userID = ?");

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
    int insertIdea(String content) {
        int count = 0;
        try {
            mInsertOneIdea.setString(1, content);
            // likeCount will automatically be set to 0; it is written into the
            // preparedStatement
            count += mInsertOneIdea.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return count;
    }

    int insertNewUser(String email) {
        int count = 0;
        try {
            mInsertNewUser.setString(1, email);
            count += mInsertNewUser.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return count;
    }

    int updateOneUser(UserRequest req) {
        try {
            mUpdateOneUser.setString(1, req.mUsername);
            mUpdateOneUser.setString(2, req.mGI);
            mUpdateOneUser.setString(3, req.mSO);
            mUpdateOneUser.setString(4, req.mNote);
            mUpdateOneUser.setInt(5, req.mId);
            return mUpdateOneUser.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
            return 0;
        }
    }

    /**
     * Query the database for a list of ideas
     * with their IDs, content, and likeCounts
     * 
     * @return All Ideas, as an ArrayList
     */
    ArrayList<Idea> selectAllIdeas() {
        ArrayList<Idea> res = new ArrayList<Idea>();
        try {
            ResultSet rs = mSelectAllIdeas.executeQuery();
            while (rs.next()) {
                res.add(new Idea(rs.getInt("id"), rs.getString("content"), rs.getInt("likeCount")));
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
    Idea selectOneIdea(int id) {
        Idea res = null;
        try {
            mSelectOneIdea.setInt(1, id);
            ResultSet rs = mSelectOneIdea.executeQuery();
            if (rs.next()) {
                res = new Idea(rs.getInt("id"), rs.getString("content"), rs.getInt("likeCount"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return res;
    }

    /**
     * Delete an idea by ID
     * 
     * @param id The id of the row to delete
     * 
     * @return The number of rows that were deleted. -1 indicates an error.
     */
    int deleteIdea(int id) {
        int res = -1;
        try {
            mDeleteOneIdea.setInt(1, id);
            res = mDeleteOneIdea.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return res;
    }

    /**
     * Update the likeCount for an idea in the database
     * 
     * @param id        The id of the row to update
     * @param likeDelta the requested amount to change likes by; must be 1 or -1 to
     *                  be successful
     * 
     * @return The amount of posts affected by the given likeCountged. -1 indicates
     *         an error.
     */
    int updateIdeaLikeCount(int id, int likeDelta) {
        // tjp: I'm open to changing what the return value should be, e.g.
        // differentiating between a dislike and like
        int res = -1;
        try {
            if (likeDelta == 1 || likeDelta == -1) {
                mUpdateIdeaLikeCount.setInt(1, likeDelta);
                mUpdateIdeaLikeCount.setInt(2, id);
                res = mUpdateIdeaLikeCount.executeUpdate();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return res;
    }

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
}