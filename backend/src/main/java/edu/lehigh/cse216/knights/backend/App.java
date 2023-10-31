package edu.lehigh.cse216.knights.backend;

// Import the Spark package, so that we can make use of the "get" function to 
// create an HTTP GET route
import spark.Spark;

import java.util.Map;

// Import Google's JSON library
import com.google.gson.*;

import edu.lehigh.cse216.knights.backend.Idea;
import edu.lehigh.cse216.knights.backend.IdeaRequest;

import edu.lehigh.cse216.knights.backend.User;
import edu.lehigh.cse216.knights.backend.UserRequest;


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
            Idea idea = db.selectOneIdea(idx);
            if (idea == null) {
                return gson.toJson(new StructuredResponse("error", idx + " not found", null));
            } else {
                return gson.toJson(new StructuredResponse("ok", null, idea));
            }
        });

        // POST route for adding a new idea to the Database.  This will read
        // JSON from the body of the request, turn it into a IdeaRequest 
        // object, extract the title and content, insert them, and return the 
        // ID of the newly created row.
        Spark.post("/ideas", (request, response) -> {
            // NB: if gson.Json fails, Spark will reply with status 500 Internal 
            // Server Error
            IdeaRequest req = gson.fromJson(request.body(), IdeaRequest.class);
            // ensure status 200 OK, with a MIME type of JSON
            // NB: even on error, we return 200, but with a JSON object that
            //     describes the error.
            response.status(200);
            response.type("application/json");
            int rowsInserted = db.insertIdea(req.mContent);
            if (rowsInserted <= 0) {
                return gson.toJson(new StructuredResponse("error", "error creating idea", null));
            } else {
                return gson.toJson(new StructuredResponse("ok", "created " + rowsInserted + " idea(s)", null));
            }
        });

        // PUT route for modifying a likeCount of an idea. This is almost 
        // exactly the same as POST
        Spark.put("/ideas/:id", (request, response) -> {
            // If we can't get an ID or can't parse the JSON, Spark will send
            // a status 500
            int idx = Integer.parseInt(request.params("id"));
            IdeaRequest req = gson.fromJson(request.body(), IdeaRequest.class);
            int likeIncrement = req.mLikeIncrement;
            if(likeIncrement == 0){
                // Specific error response to say that the request was formatted incorrectly
                // Occurs when 'mLikeIncrement' is missing from request (or value is 0)
                return gson.toJson(new StructuredResponse("error", "could not find mLikeIncrement field from request", null));
            }
            // ensure status 200 OK, with a MIME type of JSON
            response.status(200);
            response.type("application/json");
            int rowsUpdated = db.updateIdeaLikeCount(idx, likeIncrement);
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


        Spark.post("/users", (request, response) -> {
            UserRequest req = gson.fromJson(request.body(), UserRequest.class);
            
            response.status(200);
            response.type("application/json");
            
            int rowsInserted = db.insertNewUser(req.mEmail);
            if (rowsInserted <= 0) {
                return gson.toJson(new StructuredResponse("error", "error creating user", null));
            } else {
                return gson.toJson(new StructuredResponse("ok", "created " + rowsInserted + " user(s)", null));
            }
        });

        Spark.put("/users", (request, response) -> {
            UserRequest req = gson.fromJson(request.body(), UserRequest.class);
            response.status(200);
            response.type("application/json");
        
            int rowsUpdated = db.updateOneUser(req);
            if (rowsUpdated <= 0) {
                return gson.toJson(new StructuredResponse("error", "error updating user", null));
            } else {
                return gson.toJson(new StructuredResponse("ok", "updated " + rowsUpdated + " user(s)", null));
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