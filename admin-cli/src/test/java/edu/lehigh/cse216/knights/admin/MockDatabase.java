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

    
}
