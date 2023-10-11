package edu.lehigh.cse216.knights.backend;

/**
 * SimpleRequest provides a format for clients to present id int and title and content 
 * strings to the server.
 * 
 * NB: since this will be created from JSON, all fields must be public, and we
 *     do not need a constructor.
 */
public class SimpleRequest {
     /**
     * The title being provided by the client.
     */
    public String mTitle;

    /**
     * The content being provided by the client.
     */
    public String mContent;
}
