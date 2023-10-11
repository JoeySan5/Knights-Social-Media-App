package edu.lehigh.cse216.knights.backend;

// Import the Spark package, so that we can make use of the "get" function to 
// create an HTTP GET route
import spark.Spark;

import java.util.Map;

// Import Google's JSON library
import com.google.gson.*;

/**
 * For now, our app creates an HTTP server that can only get and add data.
 */
public class App 
{
    public static void main( String[] args )
    {
        // Get a fully-configured connection to the database, or exit 
        // immediately
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

        // Set up a route for serving the main page
        Spark.get("/", (req, res) -> {
            res.redirect("/index.html");
            return "";
        });

        // GET route that returns all message titles and Ids.  All we do is get 
        // the data, embed it in a StructuredResponse, turn it into JSON, and 
        // return it.  If there's no data, we return "[]", so there's no need 
        // for error handling.
        Spark.get("/posts", (request, response) -> {
            // ensure status 200 OK, with a MIME type of JSON
            response.status(200);
            response.type("application/json");
            return gson.toJson(new StructuredResponse("ok", null, db.selectAll()));
        });

        /**
         * Phase 1 does not have any specific GET route, only a route to get all posts
         */
        // // GET route that returns everything for a single row in the Database.
        // // The ":id" suffix in the first parameter to get() becomes 
        // // request.params("id"), so that we can get the requested row ID.  If 
        // // ":id" isn't a number, Spark will reply with a status 500 Internal
        // // Server Error.  Otherwise, we have an integer, and the only possible 
        // // error is that it doesn't correspond to a row with data.
        // Spark.get("/posts/:id", (request, response) -> {
        //     int idx = Integer.parseInt(request.params("id"));
        //     // ensure status 200 OK, with a MIME type of JSON
        //     response.status(200);
        //     response.type("application/json");
        //     DataRow data = db.selectOne(idx);
        //     if (data == null) {
        //         return gson.toJson(new StructuredResponse("error", idx + " not found", null));
        //     } else {
        //         return gson.toJson(new StructuredResponse("ok", null, data));
        //     }
        // });

        // POST route for adding a new element to the Database.  This will read
        // JSON from the body of the request, turn it into a SimpleRequest 
        // object, extract the title and content, insert them, and return the 
        // ID of the newly created row.
        Spark.post("/posts", (request, response) -> {
            // NB: if gson.Json fails, Spark will reply with status 500 Internal 
            // Server Error
            SimpleRequest req = gson.fromJson(request.body(), SimpleRequest.class);
            // ensure status 200 OK, with a MIME type of JSON
            // NB: even on error, we return 200, but with a JSON object that
            //     describes the error.
            response.status(200);
            response.type("application/json");
            int rowsInserted = db.insertRow(req.mTitle, req.mContent);
            if (rowsInserted <= 0) {
                return gson.toJson(new StructuredResponse("error", "error creating post", null));
            } else {
                return gson.toJson(new StructuredResponse("ok", "created " + rowsInserted + " post(s)", null));
            }
        });

        // PUT route for modifying a likeCount of a post. This is almost 
        // exactly the same as POST
        Spark.put("/posts/:id", (request, response) -> {
            // If we can't get an ID or can't parse the JSON, Spark will send
            // a status 500
            int idx = Integer.parseInt(request.params("id"));
            // int likeModifier = Integer.parseInt(request.params("likeChange")); // want to get integer directly, but this doesn't follow formatting/protocol?
            SimpleRequest req = gson.fromJson(request.body(), SimpleRequest.class);
            int likeModifier = Integer.parseInt(req.mContent);
            // ensure status 200 OK, with a MIME type of JSON
            response.status(200);
            response.type("application/json");
            int rowsUpdated = db.updateOne(idx, likeModifier);
            if (rowsUpdated <= 0) {
                return gson.toJson(new StructuredResponse("error", "unable to change likes on post #" + idx, null));
            } else {
                return gson.toJson(new StructuredResponse("ok", null, null));
            }
        });

        /**
         * DELETE not yet decided on in phase 1
         */
        // // DELETE route for removing a row from the Database.
        // Spark.delete("/messages/:id", (request, response) -> {
        //     // If we can't get an ID, Spark will send a status 500
        //     int idx = Integer.parseInt(request.params("id"));

        //     // ensure status 200 OK, with a MIME type of JSON
        //     response.status(200);
        //     response.type("application/json");
        //     // NB: we won't concern ourselves too much with the quality of the 
        //     //     message sent on a successful delete
        //     int rowsDeleted = db.deleteRow(idx);
        //     if (rowsDeleted <= 0) {
        //         return gson.toJson(new StructuredResponse("error", "unable to delete row " + idx, null));
        //     } else {
        //         return gson.toJson(new StructuredResponse("ok", null, null));
        //     }
        // });

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
}