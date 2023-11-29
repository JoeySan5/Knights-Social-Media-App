package edu.lehigh.cse216.knights.backend;

// Import the Spark package, so that we can make use of the "get" function to 
// create an HTTP GET route
import spark.Spark;

import java.util.Collections;
import java.util.Hashtable;
import java.util.Map;
import java.util.Set;
import static spark.Spark.*;

// Import Google's JSON library
import com.google.gson.*;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken.Payload;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.http.javanet.NetHttpTransport;

//For memeCachier
import net.rubyeye.xmemcached.MemcachedClient;
import net.rubyeye.xmemcached.MemcachedClientBuilder;
import net.rubyeye.xmemcached.XMemcachedClientBuilder;
import net.rubyeye.xmemcached.auth.AuthInfo;
import net.rubyeye.xmemcached.command.BinaryCommandFactory;
import net.rubyeye.xmemcached.exception.MemcachedException;
import net.rubyeye.xmemcached.utils.AddrUtil;

import java.lang.InterruptedException;
import java.net.InetSocketAddress;
import java.io.IOException;
import java.util.List;
import java.util.concurrent.TimeoutException;

/**
 * App creates an HTTP server capable of interacting with the Database.
 */
public class App {

    /**
     * Sets up the database and server ports.
     * 
     * @param args The command line arguments, (unused)
     */
    public static void main(String[] args) {

        staticFiles.location("/public");

        // Get a fully-configured connection to the database, or exit immediately
        Database db = getDatabaseConnection();
        if (db == null)
            return;

        // gson provides us with a way to turn JSON into objects, and objects
        // into JSON.
        //
        // NB: it must be final, so that it can be accessed from our lambdas
        //
        // NB: Gson is thread-safe. See
        //
        // https://stackoverflow.com/questions/10380835/is-it-ok-to-use-gson-instance-as-a-static-field-in-a-model-bean-reuse
        final Gson gson = new Gson();

        // Set the port on which to listen for requests from the environment
        Spark.port(getIntFromEnv("PORT", DEFAULT_PORT_SPARK));

        // Set up the location for serving static files. If the STATIC_LOCATION
        // environment variable is set, we will serve from it. Otherwise, serve
        // from "/web"
        String static_location_override = System.getenv("STATIC_LOCATION");
        if (static_location_override == null) {
            Spark.staticFileLocation("/web");
        } else {
            Spark.staticFiles.externalLocation(static_location_override);
        }

        // enable CORS if the environment specifies to do so
        if ("True".equalsIgnoreCase(System.getenv("CORS_ENABLED"))) {
            final String acceptCrossOriginRequestsFrom = "*";
            final String acceptedCrossOriginRoutes = "GET,PUT,POST,DELETE,OPTIONS";
            final String supportedRequestHeaders = "Content-Type,Authorization,X-Requested-With,Content-Length,Accept,Origin,Access-Control-Allow-Origin";
            enableCORS(acceptCrossOriginRequestsFrom, acceptedCrossOriginRoutes,
                    supportedRequestHeaders);
        }

        // Set up a route for serving the main page
        // tjp: this 'main page' of 'index.html' is just a placeholder. It also leads
        // to a 404 error since the page doesn't exist. Maybe in future phases weshould
        // go to some specific page.
        Spark.get("/", (req, res) -> {
            res.redirect("/index.html");
            return "";
        });

        // GET route that returns all ideas with their id, content, and likeCount.
        // The session key should be vaild to get the ideas
        // All we do is get the data, embed it in a StructuredResponse, turn it into
        // JSON, and
        // return it. If there's no data, we return "[]", so there's no need
        // for error handling.
        Spark.get("/ideas", (request, response) -> {

            String key = request.queryParams("sessionKey");
            System.out.println("here is sess key:" + key);

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

            // ensure status 200 OK, with a MIME type of JSON

            // check if stored in cache
            String sesskey = mc.get(key);
            System.out.println("here is mc getting key" + "\n" + mc.get(key) + "\n");
            if (sesskey == null) {
                return gson.toJson(new StructuredResponse("error", "Invalid session key",
                        null));
            }
            response.status(200);
            response.type("application/json");
            return gson.toJson(new StructuredResponse("ok", null, db.selectAllIdeas()));
        });

        // GET route that returns everything for a single idea.
        // The ":id" suffix in the first parameter to get() becomes
        // request.params("id"), so that we can get the requested row ID. If
        // ":id" isn't a number, Spark will reply with a status 500 Internal
        // Server Error. Otherwise, we have an integer, and the only possible
        // error is that it doesn't correspond to a row with data.
        Spark.get("/ideas/:id", (request, response) -> {

            int idx = Integer.parseInt(request.params("id"));
            // ensure status 200 OK, with a MIME type of JSON
            String key = request.queryParams("sessionKey");

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
            if (mc.get(key) == null) {
                return gson.toJson(new StructuredResponse("error", "Invalid session key",
                        null));
            }

            response.status(200);
            response.type("application/json");
            Idea.ExtendedIdea idea = db.selectOneIdea(idx);
            if (idea == null) {
                return gson.toJson(new StructuredResponse("error", idx + " not found",
                        null));
            } else {
                return gson.toJson(new StructuredResponse("ok", null, idea));
            }
        });

        // POST route for adding a new idea to the Database. This will read
        // JSON from the body of the request, turn it into a IdeaRequest
        // object, extract the title and content, insert them, and return the
        // ID of the newly created row.
        Spark.post("/ideas", (request, response) -> {

            // NB: if gson.Json fails, Spark will reply with status 500 Internal
            // Server Error
            Request.IdeaRequest req = gson.fromJson(request.body(),
                    Request.IdeaRequest.class);
            // ensure status 200 OK, with a MIME type of JSON
            // NB: even on error, we return 200, but with a JSON object that
            // describes the error.
            String key = req.sessionKey;
            String userID = null;

            FileObject file = req.mFile;

            String fileid = db.parseFileid(file);
            System.out.println("fileid: " + fileid);

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

            if (mc.get(key) == null) {
                return gson.toJson(new StructuredResponse("error", "Invalid session key",
                        null));
            } else {
                userID = mc.get(key);
                if (userID == null) {
                    return gson.toJson(new StructuredResponse("error", "authentication failed",
                            null));
                }
            }

            response.status(200);
            response.type("application/json");
            int rowsInserted = db.insertIdea(req.mContent, userID, fileid, req.mLink);
            if (rowsInserted <= 0) {
                return gson.toJson(new StructuredResponse("error", "error creating idea",
                        null));
            } else {
                return gson.toJson(new StructuredResponse("ok", "created " + rowsInserted + "idea(s)", null));
            }
        });

        // PUT route for modifying a likeCount of an idea. This is almost
        // exactly the same as POST
        Spark.put("/ideas/:id/likes", (request, response) -> {
            // If we can't get an ID or can't parse the JSON, Spark will send
            // a status 500
            int idx = Integer.parseInt(request.params("id"));
            Request.LikeRequest req = gson.fromJson(request.body(),
                    Request.LikeRequest.class);

            String key = req.sessionKey;
            System.out.println("sessionKey: " + key);

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

            if (mc.get(key) == null) {
                return gson.toJson(new StructuredResponse("error", "Invalid session key",
                        null));
            }
            String userID = mc.get(key);
            System.out.println("userID: " + userID);

            int likeIncrement = req.value;
            if (likeIncrement == 0) {
                // Specific error response to say that the request was formatted incorrectly
                // Occurs when 'value' is missing from request (or value is 0)
                return gson.toJson(
                        new StructuredResponse("error", "could not find mLikeIncrement field from request", null));
            }
            // ensure status 200 OK, with a MIME type of JSON
            response.status(200);
            response.type("application/json");

            System.out.println("sessionKey: " + key + "userID: " + userID +
                    "likeIncrement: " + likeIncrement);

            int rowsUpdated = db.updateIdeaLikeCount(userID, idx, likeIncrement);

            if (rowsUpdated <= 0) {
                return gson.toJson(new StructuredResponse("error",
                        "unable to change likes on idea #" + idx + " by " + likeIncrement, null));
            } else {
                return gson.toJson(new StructuredResponse("ok", null, null));
            }
        });

        // DELETE route for removing an idea from the Database.
        // This is actually an unused feature. There are no implementation instructions
        // for this feature in phase 2
        Spark.delete("/ideas/:id", (request, response) -> {
            // If we can't get an ID, Spark will send a status 500
            int idx = Integer.parseInt(request.params("id"));

            // ensure status 200 OK, with a MIME type of JSON
            response.status(200);
            response.type("application/json");
            // NB: we won't concern ourselves too much with the quality of the
            // message sent on a successful delete
            int rowsDeleted = db.deleteIdea(idx);
            if (rowsDeleted <= 0) {
                return gson.toJson(new StructuredResponse("error", "unable to delete idea #"
                        + idx, null));
            } else {
                return gson.toJson(new StructuredResponse("ok", null, null));
            }
        });

        // Post route for login authentication with Google OAuth
        // If the authentication is successful, return a session key
        // Also, add the session key and userId to the sessionKeyTable
        // If the authentication is failed, return an error message
        // If the user is not in the database, add the user to the database
        // Client ID is stored in the environment variable CLIENT_ID
        Spark.post("/login", (request, response) -> {

            String CLIENT_ID = System.getenv("CLIENT_ID");

            if (CLIENT_ID == null) {
                System.out.println("CLIENT_ID is not set, please check your mvn exec command");
                return gson.toJson(new StructuredResponse("error",
                        "CLIENT_ID is not set, please check your mvn exec command", null));
            }

            // Need to verify that types are correct
            // Originally NetHttpTransport was type HttpTransport
            NetHttpTransport transport = GoogleNetHttpTransport.newTrustedTransport();
            JsonFactory jsonFactory = JacksonFactory.getDefaultInstance();

            GoogleIdTokenVerifier verifier = new GoogleIdTokenVerifier.Builder(transport,
                    jsonFactory)
                    // Specify the CLIENT_ID of the app that accesses the backend:
                    .setAudience(Collections.singletonList(CLIENT_ID))
                    // Or, if multiple clients access the backend:
                    // .setAudience(Arrays.asList(CLIENT_ID_1, CLIENT_ID_2, CLIENT_ID_3))
                    .build();

            // (Receive idTokenString by HTTPS POST)
            // System.out.println("heres req" + request.headers());

            System.out.println(request);
            // System.out.println(request.body());
            Request.LoginRequest req = gson.fromJson(request.body(),
                    Request.LoginRequest.class);

            // String idTokenString = request.params();
            Set<String> paramSet = request.queryParams();
            System.out.println("number of params found=" + paramSet.size());
            GoogleIdToken idToken = verifier.verify(req.credential);

            String userId = null;
            if (idToken != null) {
                Payload payload = idToken.getPayload();

                // Print user identifier
                userId = payload.getSubject();
                String email = payload.getEmail();
                System.out.println("User ID: " + userId);
                System.out.println("Email: " + email);
                if (email != null && !email.endsWith("@lehigh.edu")) {
                    return gson.toJson(new StructuredResponse("error",
                            "authentication failed, only approve @lehigh.edu domain", null));
                }
                // Get profile information from payload
                // String email = payload.getEmail();
                // System.out.println("The email is: " + email);
                // tjp: Knights app does not need this data:
                // boolean emailVerified = Boolean.valueOf(payload.getEmailVerified());
                // String name = (String) payload.get("name");
                // String pictureUrl = (String) payload.get("picture");
                // String locale = (String) payload.get("locale");
                // String familyName = (String) payload.get("family_name");
                // String givenName = (String) payload.get("given_name");

                // Use or store profile information
                // ...

            } else {
                // Case for authentication failed
                System.out.println("Invalid ID token. please check the Client_ID or process of getting token");
                return gson.toJson(new StructuredResponse("error", "authentication failed",
                        null));
            }
            // This case should never occur, but it might if authentication is successful
            // but provides no subjectID
            if (userId == null) {
                System.out.println("For testing backend - UserId is null on 'successful' authentication");
                return gson.toJson(new StructuredResponse("error", "authentication failed",
                        null));
            }

            // If no user exists, add a new user to the users table in the database
            User res = db.selectOneUser(userId, true);
            if (res == null) {
                int rowsInserted = db.insertNewUser(userId);
                if (rowsInserted <= 0) {
                    return gson.toJson(new StructuredResponse("error", "error creating user",
                            null));
                }
            } else if (res.mValid == false) {
                return gson.toJson(new StructuredResponse("error", "user has been invalidated to login", null));
            }

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

            // generate a new session key-a random string.
            String sessionKey = SessionKeyGenerator.generateRandomString(12);
            mc.set(sessionKey, 0, userId);
            // show up the sessionKey and sessionKeyTable
            System.out.println("session Key is " + sessionKey);
            System.out.println("sessionKey value in cache: " + mc.get(sessionKey));
            return gson.toJson(new StructuredResponse("ok", "authentication success",
                    sessionKey));
        });

        // Post route for adding a new user to the database
        // The new user only has a userId and a valid status
        // The other information will be 'unknown'
        Spark.post("/users", (request, response) -> {
            Request.UserRequest req = gson.fromJson(request.body(),
                    Request.UserRequest.class);

            response.status(200);
            response.type("application/json");

            int rowsInserted = db.insertNewUser(req.mId);
            if (rowsInserted <= 0) {
                return gson.toJson(new StructuredResponse("error", "error creating user",
                        null));
            } else {
                return gson.toJson(new StructuredResponse("ok", "created " + rowsInserted + " user(s)", null));
            }
        });

        // Put route for updating a user's information
        // The user can only update his/her own information
        Spark.put("/users", (request, response) -> {
            Request.UserRequest req = gson.fromJson(request.body(),
                    Request.UserRequest.class);
            response.status(200);
            response.type("application/json");

            // Check the session key
            String key = req.sessionKey;
            String userId;
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
            if (mc.get(key) == null) {
                return gson.toJson(new StructuredResponse("error", "Invalid session key",
                        null));
            } else {
                userId = mc.get(key);
                if (userId == null) {
                    return gson.toJson(new StructuredResponse("error", "authentication failed",
                            null));
                }
            }

            req.mId = userId;

            int rowsUpdated = db.updateOneUser(req);
            if (rowsUpdated <= 0) {
                return gson.toJson(new StructuredResponse("error", "error updating user",
                        null));
            } else {
                return gson.toJson(new StructuredResponse("ok", "updated " + rowsUpdated + " user(s)", null));
            }
        });

        // Get route for getting a user's information
        // The user can only get his/her own information (GI and SO)
        // If the user request to get other user's information, the server will return
        // information with restricted information (without GI and SO)
        Spark.get("/users/:id", (request, response) -> {
            String requestedUserId = request.params("id");
            // ensure status 200 OK, with a MIME type of JSON
            response.status(200);
            response.type("application/json");

            // Question: why we can't use this way to get sessionKey
            // Request.UserRequest req = gson.fromJson(request.body(),
            // Request.UserRequest.class);
            // String key = req.sessionKey;

            // Answer:
            // -d sends the data as the request body. GET requests typically do not have a
            // request body, and the route handler in your server code is not set up to
            // parse a JSON body for a GET request. It is expecting a query parameter
            // instead.

            // For the server code to accept the sessionKey from the request body, you would
            // need to parse the JSON from the request body with
            // gson.fromJson(request.body(), ...) within the route handler. But this is not
            // standard practice for GET requests, which is why the -d option with a GET
            // request is not working.

            // In RESTful API design, it's more common to use query parameters or path
            // variables for GET requests and reserve the request body for POST or PUT
            // requests where a resource is being created or updated.
            // So we are using query parameter to get sessionKey

            String key = request.queryParams("sessionKey");
            System.out.println("Requested sessionKey: " + key);
            System.out.println("Reqeusted userId: " + requestedUserId);

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

            if (mc.get(key) == null) {
                return gson.toJson(new StructuredResponse("error", "Invalid session key",
                        null));
            }
            String userId = mc.get(key);
            boolean restrictInfo = !(userId.equals(requestedUserId));

            User user = db.selectOneUser(requestedUserId, restrictInfo);
            if (user == null) {
                return gson.toJson(new StructuredResponse("error", requestedUserId + " not found", null));
            } else {
                return gson.toJson(new StructuredResponse("ok", null, user));
            }
        });

        // Same function as above, but without the userId parameter
        // Only use the session key, and get the user's information
        // This function is used for getting the user's own information
        Spark.get("/users", (request, response) -> {
            // ensure status 200 OK, with a MIME type of JSON
            response.status(200);
            response.type("application/json");

            // See other get Users function for more detailed comments

            String key = request.queryParams("sessionKey");
            System.out.println("Requested sessionKey: " + key);

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

            if (mc.get(key) == null) {
                return gson.toJson(new StructuredResponse("error", "Invalid session key",
                        null));
            }

            if (mc.get(key) == null) {
                return gson.toJson(new StructuredResponse("error", "Invalid session key",
                        null));
            }
            String userId = mc.get(key);
            boolean restrictInfo = false;

            User user = db.selectOneUser(userId, restrictInfo);
            if (user == null) {
                return gson.toJson(new StructuredResponse("error", userId + " not found",
                        null));
            } else {
                return gson.toJson(new StructuredResponse("ok", null, user));
            }
        });

        // POST route for adding a new comment to the Database. This will read
        // JSON from the body of the request, turn it into a CommentRequest
        // object, extract the (content, UserID, IdeaID) insert them, and return the
        // ID of the newly created row.
        Spark.post("/comments", (request, response) -> {
            Request.CommentRequest req = gson.fromJson(request.body(),
                    Request.CommentRequest.class);
            response.status(200);
            response.type("application/json");

            String key = req.sessionKey;
            String userID = null;
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

            if (mc.get(key) == null) {
                return gson.toJson(new StructuredResponse("error", "Invalid session key",
                        null));
            }
            if (mc.get(key) == null) {
                return gson.toJson(new StructuredResponse("error", "Invalid session key",
                        null));
            } else {
                userID = mc.get(key);
                if (userID == null) {
                    return gson.toJson(new StructuredResponse("error", "authentication failed",
                            null));
                }
            }

            int rowsInserted = db.insertNewComment(req.mContent, userID, req.mIdeaId);
            if (rowsInserted <= 0) {
                return gson.toJson(new StructuredResponse("error", "error creating comment",
                        null));
            } else {
                return gson.toJson(new StructuredResponse("ok", "created " + rowsInserted + "comment(s)", null));
            }
        });

        // Put route for updating a comment's content
        // The user can only update his/her own comment
        Spark.put("/comments", (request, response) -> {
            Request.CommentRequest req = gson.fromJson(request.body(),
                    Request.CommentRequest.class);
            response.status(200);
            response.type("application/json");

            String key = req.sessionKey;
            String userID = null;
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

            if (mc.get(key) == null) {
                return gson.toJson(new StructuredResponse("error", "Invalid session key",
                        null));
            }
            if (mc.get(key) == null) {
                return gson.toJson(new StructuredResponse("error", "Invalid session key",
                        null));
            } else {
                userID = mc.get(key);
                if (userID == null) {
                    return gson.toJson(new StructuredResponse("error", "authentication failed",
                            null));
                }
            }
            System.out.println("mid: " + req.mId);

            String CommenterID = db.getCommenterUserID(req.mId);
            System.out.println("CommenterID: " + CommenterID);
            System.out.println("userID: " + userID);

            if (!userID.equals(CommenterID)) {
                return gson.toJson(new StructuredResponse("error", "You can only edit your own comment", null));
            }

            int rowsUpdated = db.updateOneComment(req.mContent, req.mId);
            if (rowsUpdated <= 0) {
                return gson.toJson(new StructuredResponse("error", "error updating comment",
                        null));
            } else {
                return gson.toJson(new StructuredResponse("ok", "updated " + rowsUpdated + "comment(s)", null));
            }
        });

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

    private static final String DEFAULT_PORT_DB = "5432";
    private static final int DEFAULT_PORT_SPARK = 4567;

    /**
     * Get a fully-configured connection to the database, or exit immediately
     * Uses the Postgres configuration from environment variables
     * 
     * NB: now when we shutdown the server, we no longer lose all data
     * 
     * @return null on failure, otherwise configured database object
     */
    private static Database getDatabaseConnection() {
        System.out.println("db url = " + System.getenv("DATABASE_URL"));
        if (System.getenv("DATABASE_URL") != null) {
            return Database.getDatabase(System.getenv("DATABASE_URL"), DEFAULT_PORT_DB);
        }

        Map<String, String> env = System.getenv();
        String ip = env.get("POSTGRES_IP");
        String port = env.get("POSTGRES_PORT");
        String user = env.get("POSTGRES_USER");
        String pass = env.get("POSTGRES_PASS");
        return Database.getDatabase(ip, port, "", user, pass);
    }

    /**
     * Get an integer environment variable if it exists, and otherwise return the
     * default value.
     * 
     * @envar The name of the environment variable to get.
     * @defaultVal The integer value to use as the default if envar isn't found
     * 
     * @returns The best answer we could come up with for a value for envar
     */
    static int getIntFromEnv(String envar, int defaultVal) {
        ProcessBuilder processBuilder = new ProcessBuilder();
        if (processBuilder.environment().get(envar) != null) {
            return Integer.parseInt(processBuilder.environment().get(envar));
        }
        return defaultVal;
    }

    /**
     * Set up CORS headers for the OPTIONS verb, and for every response that the
     * server sends. This only needs to be called once.
     * 
     * @param origin  The server that is allowed to send requests to this server
     * @param methods The allowed HTTP verbs from the above origin
     * @param headers The headers that can be sent with a request from the above
     *                origin
     */
    private static void enableCORS(String origin, String methods, String headers) {
        // Create an OPTIONS route that reports the allowed CORS headers and methods
        Spark.options("/*", (request, response) -> {
            String accessControlRequestHeaders = request.headers("Access-Control-Request-Headers");
            if (accessControlRequestHeaders != null) {
                response.header("Access-Control-Allow-Headers", accessControlRequestHeaders);
            }
            String accessControlRequestMethod = request.headers("Access-Control-Request-Method");
            if (accessControlRequestMethod != null) {
                response.header("Access-Control-Allow-Methods", accessControlRequestMethod);
            }
            return "OK";
        });

        // 'before' is a decorator, which will run before any
        // get/post/put/delete. In our case, it will put three extra CORS
        // headers into the response
        Spark.before((request, response) -> {
            response.header("Access-Control-Allow-Origin", origin);
            response.header("Access-Control-Request-Method", methods);
            response.header("Access-Control-Allow-Headers", headers);
        });
    }
}