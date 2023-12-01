package edu.lehigh.cse216.knights.admin;

import java.util.ArrayList;

/**
 * Mock Database with the same methods as the admin Database, but instead
 * stores data in memory, allowing for testing the database emthods in a controlled
 * environment.
 */
public class MockDatabase{

    private ArrayList<Entity.User> users;

    private ArrayList<Entity.Idea> ideas;

    private ArrayList<Entity.Comment> comments;

    private ArrayList<Entity.Like> likes;

    public MockDatabase(){
        users = new ArrayList<Entity.User>();
        ideas = new ArrayList<Entity.Idea>();
        comments = new ArrayList<Entity.Comment>();
        likes = new ArrayList<Entity.Like>();
    }

    /** methods for inserting entities into mock db */
    public void insertUser(Entity.User user){
        users.add(user);
    }

    public void insertIdea(Entity.Idea idea){
        ideas.add(idea);
    }

    public void insertComment(Entity.Comment comment){
        comments.add(comment);
    }

    public void insertLike(Entity.Like like){
        likes.add(like);
    }

    /** methods for getting entities from mock db */
    public Entity.User getUser(int index){
        return users.get(index);
    }

    public Entity.Idea getIdea(int index){
        return ideas.get(index);
    }

    public Entity.Comment getComment(int index){
        return comments.get(index);
    }

    public Entity.Like getLike(int index){
        return likes.get(index);
    }

    
}
