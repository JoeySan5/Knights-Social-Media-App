package edu.lehigh.cse216.knights.backend;

// Import the Spark package, so that we can make use of the "get" function to 
// create an HTTP GET route
import spark.Spark;

import java.util.Collections;
import java.util.Hashtable;
import java.util.Map;
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



/**
 * App creates an HTTP server capable of interacting with the Database.
 */
public class App 
{   
    /**
     * Sets up the database and server ports.
     * @param args The command line arguments, (unused)
     */
    public static void main( String[] args )
    {
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
        // NB: Gson is thread-safe.  See 
        // https://stackoverflow.com/questions/10380835/is-it-ok-to-use-gson-instance-as-a-static-field-in-a-model-bean-reuse
        final Gson gson = new Gson();

        /**
         * Key = sessionKey
         * String = userId
         */
        Hashtable<String, String> sessionKeyTable = new Hashtable<>();
        sessionKeyTable.put("lGaJjDO8kdNq", "112569610817039937158");
        sessionKeyTable.put("k0kyOGwPlod5", "107106171889739877350");
        sessionKeyTable.put("YMtxeMIRXi5o","101136375578726959533");
        // Set the port on which to listen for requests from the environment
        Spark.port(getIntFromEnv("PORT", DEFAULT_PORT_SPARK));

        // Set up the location for serving static files.  If the STATIC_LOCATION
        // environment variable is set, we will serve from it.  Otherwise, serve
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
            final String supportedRequestHeaders = "Content-Type,Authorization,X-Requested-With,Content-Length,Accept,Origin";
            enableCORS(acceptCrossOriginRequestsFrom, acceptedCrossOriginRoutes, supportedRequestHeaders);
        }

        // Set up a route for serving the main page
        // tjp: this 'main page' of 'index.html' is just a placeholder. It also leads
        //      to a 404 error since the page doesn't exist. Maybe in future phases we should
        //      go to some specific page.
        Spark.get("/", (req, res) -> {
            res.redirect("/index.html");
            return "";
        });

        // GET route that returns all ideas with their id, content, and likeCount.
        // All we do is get the data, embed it in a StructuredResponse, turn it into JSON, and 
        // return it.  If there's no data, we return "[]", so there's no need 
        // for error handling.
        Spark.get("/ideas", (request, response) -> {
            // ensure status 200 OK, with a MIME type of JSON
            response.status(200);
            response.type("application/json");
            return gson.toJson(new StructuredResponse("ok", null, db.selectAllIdeas()));
        });

        // GET route that returns everything for a single idea.
        // The ":id" suffix in the first parameter to get() becomes 
        // request.params("id"), so that we can get the requested row ID.  If 
        // ":id" isn't a number, Spark will reply with a status 500 Internal
        // Server Error.  Otherwise, we have an integer, and the only possible 
        // error is that it doesn't correspond to a row with data.
        Spark.get("/ideas/:id", (request, response) -> {
            int idx = Integer.parseInt(request.params("id"));
            // ensure status 200 OK, with a MIME type of JSON
            response.status(200);
            response.type("application/json");
            Idea.ExtendedIdea idea = db.selectOneIdea(idx);
            if (idea == null) {
                return gson.toJson(new StructuredResponse("error", idx + " not found", null));
            } else {
                return gson.toJson(new StructuredResponse("ok", null, idea));
            }
        });

        // TODO - we might need to implement this route specifically for newly-created users to be re-reouted to
        //       already logged in users should be routed to /ideas
        //         /users route should act like /users/:id but with self's own id only.
        //       This is a backlog item and possibly is completely unnecessary.
        //         
        // Spark.get("/users", (request, response) -> {
        //     //verify session key
        //     // function the same as GET /users/:id
        // }

        // POST route for adding a new idea to the Database.  This will read
        // JSON from the body of the request, turn it into a IdeaRequest 
        // object, extract the title and content, insert them, and return the 
        // ID of the newly created row.
        Spark.post("/ideas", (request, response) -> {
            // NB: if gson.Json fails, Spark will reply with status 500 Internal 
            // Server Error
            Request.IdeaRequest req = gson.fromJson(request.body(), Request.IdeaRequest.class);
            // ensure status 200 OK, with a MIME type of JSON
            // NB: even on error, we return 200, but with a JSON object that
            //     describes the error.
            response.status(200);
            response.type("application/json");
            int rowsInserted = db.insertIdea(req.mContent, req.mUserId);
            if (rowsInserted <= 0) {
                return gson.toJson(new StructuredResponse("error", "error creating idea", null));
            } else {
                return gson.toJson(new StructuredResponse("ok", "created " + rowsInserted + " idea(s)", null));
            }
        });

        // PUT route for modifying a likeCount of an idea. This is almost 
        // exactly the same as POST
        Spark.put("/ideas/:id/likes", (request, response) -> {
            // If we can't get an ID or can't parse the JSON, Spark will send
            // a status 500
            int idx = Integer.parseInt(request.params("id"));
            Request.LikeRequest req = gson.fromJson(request.body(), Request.LikeRequest.class);

            String key = req.sessionKey;
            System.out.println("sessionKey: " + key);

            if(!sessionKeyTable.containsKey(key)){
                return gson.toJson(new StructuredResponse("error", "Invalid session key", null));
            }
            String userID = sessionKeyTable.get(key);
            System.out.println("userID: " + userID);
            
            int likeIncrement = req.value;
            if(likeIncrement == 0){
                // Specific error response to say that the request was formatted incorrectly
                // Occurs when 'value' is missing from request (or value is 0)
                return gson.toJson(new StructuredResponse("error", "could not find mLikeIncrement field from request", null));
            }
            // ensure status 200 OK, with a MIME type of JSON
            response.status(200);
            response.type("application/json");
            
            System.out.println("sessionKey: " + key + "userID: " + userID + "likeIncrement: " + likeIncrement);

            int rowsUpdated = db.updateIdeaLikeCount(userID, idx, likeIncrement);

            if (rowsUpdated <= 0) {
                return gson.toJson(new StructuredResponse("error", "unable to change likes on idea #" + idx + " by " + likeIncrement, null));
            } else {
                return gson.toJson(new StructuredResponse("ok", null, null));
            }
        });
        
        
        // DELETE route for removing an idea from the Database.
        Spark.delete("/ideas/:id", (request, response) -> {
            // If we can't get an ID, Spark will send a status 500
            int idx = Integer.parseInt(request.params("id"));

            // ensure status 200 OK, with a MIME type of JSON
            response.status(200);
            response.type("application/json");
            // NB: we won't concern ourselves too much with the quality of the 
            //     message sent on a successful delete
            int rowsDeleted = db.deleteIdea(idx);
            if (rowsDeleted <= 0) {
                return gson.toJson(new StructuredResponse("error", "unable to delete idea #" + idx, null));
            } else {
                return gson.toJson(new StructuredResponse("ok", null, null));
            }
        });


        Spark.post("/login", (request, response) -> {

            // tjp: TODO maybe set this as environment variable. Hard-coding the client_id goes against 12-factor app guidelines
            String CLIENT_ID = "1019349198762-463i1tt2naq9ipll3f9ade5u7nli7gju.apps.googleusercontent.com";

            // Need to verify that types are correct
            // Originally NetHttpTransport was type HttpTransport
            NetHttpTransport transport = GoogleNetHttpTransport.newTrustedTransport();
            JsonFactory jsonFactory = JacksonFactory.getDefaultInstance();

            GoogleIdTokenVerifier verifier = new GoogleIdTokenVerifier.Builder(transport, jsonFactory)
            // Specify the CLIENT_ID of the app that accesses the backend:
            .setAudience(Collections.singletonList(CLIENT_ID))
            // Or, if multiple clients access the backend:
            //.setAudience(Arrays.asList(CLIENT_ID_1, CLIENT_ID_2, CLIENT_ID_3))
            .build();

            // (Receive idTokenString by HTTPS POST)
            String idTokenString = request.queryParams("credential"); 

            GoogleIdToken idToken = verifier.verify(idTokenString);

            String userId = null;
            if (idToken != null) {
                Payload payload = idToken.getPayload();

                // Print user identifier
                userId = payload.getSubject();
                String email = payload.getEmail();
                System.out.println("User ID: " + userId);
                System.out.println("Email: " + email);
                if(email != null && !email.endsWith("@lehigh.edu")) {
                    return gson.toJson(new StructuredResponse("error", "authentication failed, only approve @lehigh.edu domain", null));
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
                System.out.println("For testing backend - Invalid ID token."); 
                return gson.toJson(new StructuredResponse("error", "authentication failed", null));
            }
            // This case should never occur, but it might if authentication is successful but provides no subjectID
            if (userId == null){
                System.out.println("For testing backend - UserId is null on 'successful' authentication"); 
                return gson.toJson(new StructuredResponse("error", "authentication failed", null));
            }
    
            // If no user exists, add a new user to the users table in the database
            User res = db.selectOneUser(userId, true);
            if(res == null){
                int rowsInserted = db.insertNewUser(userId);
                if (rowsInserted <= 0) {
                    return gson.toJson(new StructuredResponse("error", "error creating user", null));
                }
            } else if(res.mValid == false){
                return gson.toJson(new StructuredResponse("error", "user has been invalidated", null));
            }

            // generate a new session key-a random string.
            String sessionKey =  SessionKeyGenerator.generateRandomString(12);
            sessionKeyTable.put(sessionKey, userId);
            // show up the sessionKeyTable
            System.out.println("sessionKeyTable: " + sessionKeyTable);
            return gson.toJson(new StructuredResponse("ok", "authentication success", sessionKey));
        });

        // Register users profile with default value
        Spark.post("/users", (request, response) -> {
            Request.UserRequest req = gson.fromJson(request.body(), Request.UserRequest.class);
            
            response.status(200);
            response.type("application/json");
            
            int rowsInserted = db.insertNewUser(req.mId);
            if (rowsInserted <= 0) {
                return gson.toJson(new StructuredResponse("error", "error creating user", null));
            } else {
                return gson.toJson(new StructuredResponse("ok", "created " + rowsInserted + " user(s)", null));
            }
        });

        // Edit user's profile
        Spark.put("/users", (request, response) -> {
            Request.UserRequest req = gson.fromJson(request.body(), Request.UserRequest.class);
            response.status(200);
            response.type("application/json");
        
            int rowsUpdated = db.updateOneUser(req);
            if (rowsUpdated <= 0) {
                return gson.toJson(new StructuredResponse("error", "error updating user", null));
            } else {
                return gson.toJson(new StructuredResponse("ok", "updated " + rowsUpdated + " user(s)", null));
            }
        });

        // get user's information
        Spark.get("/users/:id", (request, response) -> {
            String requestedUserId = request.params("id");
            // ensure status 200 OK, with a MIME type of JSON
            response.status(200);
            response.type("application/json");

            // Explain, why we can't use this way to get sessionKey
            // Request.UserRequest req = gson.fromJson(request.body(), Request.UserRequest.class);
            // String key = req.sessionKey;

            String key = request.queryParams("sessionKey");

            if(!sessionKeyTable.containsKey(key)){
                return gson.toJson(new StructuredResponse("error", "Invalid session key", null));
            }
            String userId = sessionKeyTable.get(key);
            boolean restrictInfo = !(userId.equals(requestedUserId));

            User user = db.selectOneUser(requestedUserId, restrictInfo);
            if (user == null) {
                return gson.toJson(new StructuredResponse("error", requestedUserId + " not found", null));
            } else {
                return gson.toJson(new StructuredResponse("ok", null, user));
            }
        });

        // post a comment
        Spark.post("/comments", (request, response) -> {
            Request.CommentRequest req = gson.fromJson(request.body(), Request.CommentRequest.class);
            response.status(200);
            response.type("application/json");
            int rowsInserted = db.insertNewComment(req.mContent, req.mUserId, req.mIdeaId);
            if (rowsInserted <= 0) {
                return gson.toJson(new StructuredResponse("error", "error creating comment", null));
            } else {
                return gson.toJson(new StructuredResponse("ok", "created " + rowsInserted + " comment(s)", null));
            }
        });

        // Edit a comment
        Spark.put("/comments", (request, response) -> {
            Request.CommentRequest req = gson.fromJson(request.body(), Request.CommentRequest.class);
            response.status(200);
            response.type("application/json");
        
            int rowsUpdated = db.updateOneComment(req.mContent, req.mId);
            if (rowsUpdated <= 0) {
                return gson.toJson(new StructuredResponse("error", "error updating comment", null));
            } else {
                return gson.toJson(new StructuredResponse("ok", "updated " + rowsUpdated + " comment(s)", null));
            }
        });

        



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
    private static Database getDatabaseConnection(){
        System.out.println("db url = " + System.getenv("DATABASE_URL"));
        if( System.getenv("DATABASE_URL") != null ){
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
    * @envar      The name of the environment variable to get.
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
     * server sends.  This only needs to be called once.
     * 
     * @param origin The server that is allowed to send requests to this server
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
        // get/post/put/delete.  In our case, it will put three extra CORS
        // headers into the response
        Spark.before((request, response) -> {
            response.header("Access-Control-Allow-Origin", origin);
            response.header("Access-Control-Request-Method", methods);
            response.header("Access-Control-Allow-Headers", headers);
        });
    }
}