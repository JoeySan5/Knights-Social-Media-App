package edu.lehigh.cse216.knights.admin;

import java.util.ArrayList;


/**
 * Class used to parse sample data represented as JSON.
 * 
 * Contains a List of all entities in the Database.
 */
public class SampleDataContainer {
    /** List of users read from sample data */
    public ArrayList<Entity.User> sampleUsers;
    /** List of ideas read from sample data */
    public ArrayList<Entity.Idea> sampleIdeas;
    /** List of comments read from sample data */
    public ArrayList<Entity.Comment> sampleComments;
    /** List of likes read from sample data */
    public ArrayList<Entity.Like> sampleLikes;
}
