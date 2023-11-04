package edu.lehigh.cse216.knights.admin;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.IOException;

import java.util.ArrayList;
import java.util.Map;

/**
 * App is our basic admin app.  For now, it is a demonstration of the six key 
 * operations on a database: connect, insert, update, query, delete, disconnect
 */
public class App {

    /** Database object to communicate with the PostgreSQL database. */
    static Database db;

    /** The actions prompted to the admin from the App menu. */
    static final String MENU_ACTIONS = "TD1*-+~q?";

    /** Options for the four tables in the database: users, ideas, comments, likes. Or for all */
    static final String TABLE_OPTIONS = "UICL*";

    /**
     * Print the menu for our program
     */
    static void menu() {
        System.out.println("Main Menu");
        System.out.println("  [T] Create ideas");
        System.out.println("  [D] Drop ideas");
        System.out.println("  [1] Query for a specific row");
        System.out.println("  [*] Query for all rows");
        System.out.println("  [-] Delete a row");
        System.out.println("  [+] Insert a new row");
        // System.out.println("  [~] Update a row");
        System.out.println("  [q] Quit Program");
        System.out.println("  [?] Help (this message)");
    }

    /**
     * Ask the user to enter an option; repeat until we get a valid option
     * 
     * @param in A BufferedReader, for reading from the keyboard
     * @param actions The valid actions for the current screen
     * 
     * @return The character corresponding to the chosen menu option
     */
    static char prompt(BufferedReader in, String actions) {
        // We repeat until a valid single-character option is selected        
        while (true) {
            System.out.print("[" + actions + "] :> ");
            String action;
            try {
                action = in.readLine();
            } catch (IOException e) {
                e.printStackTrace();
                continue;
            }
            if (action.length() != 1)
                continue;
            if (actions.contains(action)) {
                return action.charAt(0);
            }
            System.out.println("Invalid Command");
        }
    }

    /**
     * Ask the user to enter a String message
     * 
     * @param in A BufferedReader, for reading from the keyboard
     * @param message A message to display when asking for input
     * 
     * @return The string that the user provided.  May be "".
     */
    static String getString(BufferedReader in, String message) {
        String s;
        try {
            System.out.print(message + " :> ");
            s = in.readLine();
        } catch (IOException e) {
            e.printStackTrace();
            return "";
        }
        return s;
    }

    /**
     * Ask the user to enter an integer
     * 
     * @param in A BufferedReader, for reading from the keyboard
     * @param message A message to display when asking for input
     * 
     * @return The integer that the user provided.  On error, it will be -1
     */
    static int getInt(BufferedReader in, String message) {
        int i = -1;
        try {
            System.out.print(message + " :> ");
            i = Integer.parseInt(in.readLine());
        } catch (IOException e) {
            e.printStackTrace();
        } catch (NumberFormatException e) {
            e.printStackTrace();
        }
        return i;
    }

    /**
     * The main routine runs a loop that gets a request from the user and
     * processes it
     * 
     * @param argv Command-line options.  Ignored by this program.
     */
    public static void main(String[] argv) {
        // Get a fully-configured connection to the database, or exit 
        // immediately
        db = getDatabaseConnection();
        if (db == null)
            return;

        // Start our basic command-line interpreter:
        BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
        while (true) {
            // Get the user's request, and do it
            //
            // NB: for better testability, each action should be a separate
            //     function call
            char action = prompt(in, MENU_ACTIONS);
            if (action == '?') {
                menu();
            } else if (action == 'q') {
                break;
            } else if (action == 'T') {
                createTable(in);
            } else if (action == 'D') {
                dropTable(in);
            } else if (action == '1') {
                int id = getInt(in, "Enter the row ID");
                if (id == -1)
                    continue;
                Database.RowData res = db.selectOne(id);
                if (res != null) {
                    System.out.println("  [" + res.mId + "] " + res.mContent);
                    System.out.println("  --> " + res.mLikeCount);
                }
            } else if (action == '*') {
                ArrayList<Database.RowData> res = db.selectAll();
                if (res == null)
                    continue;
                System.out.println("  Current Database Contents");
                System.out.println("  -------------------------");
                for (Database.RowData rd : res) {
                    System.out.println("  [" + rd.mId + "] " + rd.mContent);
                }
            } else if (action == '-') {
                int id = getInt(in, "Enter the row ID");
                if (id == -1)
                    continue;
                int res = db.deleteRow(id);
                if (res == -1)
                    continue;
                System.out.println("  " + res + " rows deleted");
            } else if (action == '+') {
                String content = getString(in, "Enter the content");
                // String message = getString(in, "Enter the message");
                // if (subject.equals("") || message.equals(""))
                //     continue;
                int res = db.insertRow(content);
                System.out.println(res + " rows added");
            }
            // } else if (action == '~') {
            //     int id = getInt(in, "Enter the row ID :> ");
            //     if (id == -1)
            //         continue;
            //     String newMessage = getString(in, "Enter the new message");
            //     int res = db.updateOne(id, newMessage);
            //     if (res == -1)
            //         continue;
            //     System.out.println("  " + res + " rows updated");
            // }
        }
        // Always remember to disconnect from the database when the program 
        // exits
        db.disconnect();
    }

    private static void createTable(BufferedReader in) {
        System.out.println("Create a table");
        System.out.println("  [U] 'users'");
        System.out.println("  [I] 'ideas'");
        System.out.println("  [C] 'comments'");
        System.out.println("  [L] 'likes'");
        System.out.println("  [*] Create all tables");
        char action = prompt(in, App.TABLE_OPTIONS);
        if(action == 'U'){
            db.createTable("users");
        } else if (action == 'I'){
            db.createTable("ideas");
        } else if (action == 'C'){
            db.createTable("comments");
        } else if (action == 'L'){
            db.createTable("likes");
        } else if (action == '*'){
            db.createTable("users");
            db.createTable("ideas");
            db.createTable("comments");
            db.createTable("likes");
        }
    }

    private static void dropTable(BufferedReader in) {
        System.out.println("Drop a table (warning: data cannot be recovered)");
        System.out.println("  [U] 'users'");
        System.out.println("  [I] 'ideas'");
        System.out.println("  [C] 'comments'");
        System.out.println("  [L] 'likes'");
        System.out.println("  [*] Drop all tables");
        char action = prompt(in, App.TABLE_OPTIONS);
        if(action == 'U'){
            db.dropTable("users");
        } else if (action == 'I'){
            db.dropTable("ideas");
        } else if (action == 'C'){
            db.dropTable("comments");
        } else if (action == 'L'){
            db.dropTable("likes");
        } else if (action == '*'){
            db.dropTable("users");
            db.dropTable("ideas");
            db.dropTable("comments");
            db.dropTable("likes");
        }
    }

    private static final String DEFAULT_PORT_DB = "5432";

    /**
    * Get a fully-configured connection to the database, or exit immediately
    * Uses the Postgres configuration from environment variables.
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