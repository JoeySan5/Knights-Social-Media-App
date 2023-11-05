package edu.lehigh.cse216.knights.admin;

import java.util.ArrayList;


/**
 * Class used to parse sample data represented as JSON.
 * 
 * Contains a List of all entities in the Database.
 */
public class SampleDataContainer {
    public ArrayList<Entity.User> sampleUsers;
    public ArrayList<Entity.Idea> sampleIdeas;
    public ArrayList<Entity.Comment> sampleComments;
    public ArrayList<Entity.Like> sampleLikes;
}
