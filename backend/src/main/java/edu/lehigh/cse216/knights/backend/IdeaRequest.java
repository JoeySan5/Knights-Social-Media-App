package edu.lehigh.cse216.knights.backend;

/**
 * SimpleRequest provides a format for clients to present id int and title and content 
 * strings to the server.
 * 
 * NB: since this will be created from JSON, all fields must be public, and we
 *     do not need a constructor.
 */
public class IdeaRequest {
     /**
     * The title being provided by the client.
     */
    public String mTitle;

    /**
     * The content being provided by the client. 
     * Or in the case of like/dislike requests, the amount by which the like total should be changed.
     */
    public String mContent;

    // Should likes by part of requests? tjp says no - likes should be handles by backend
}
