package edu.lehigh.cse216.knights.backend;

import java.util.ArrayList;

/**
 * A mock database that stores a list of Ideas in memory.
 * Used for unit testing in a controlled environment.
 * 
 * NB: The methods of MockDatabase are synchronized, since they will be used from a 
 * web framework and there may be multiple concurrent accesses to the MockDatabase.
 * 
 * Currently adjusted so that the first entry
 * in the table has ID = 1. (not 0).
 */
public class MockDatabase {

    /**
     * Rows in the MockDatabase, where each row is an Idea entry
     */
    private ArrayList<Idea> mRows;

    /**
     * A counter for keeping track of the next ID to assign to a new Idea
     */
    private int mCounter;

    /**
     * Construct the MockDatabase by resetting its counter and creating the
     * ArrayList for the rows of data.
     */
    MockDatabase() {
        mCounter = 0;
        mRows = new ArrayList<>();
    }

    /**
     * Add a new row to the DataStore. Currently adjusted so that the first entry
     * in the table has ID = 1. (not 0).
     * 
     * @param content The content for the new Idea
     * @return the ID associated with the row, or -1 if no row was created
     */
    public synchronized int createEntry(String content) {
        if (content == null)
            return -1;
        // id will be unique since mCounter is incremented on every successful call
        mCounter++;
        int id = mCounter;
        Idea idea = new Idea(id, content);
        mRows.add(id-1, idea);
        return id;
    }

     /**
     * Get one complete row from the MockDatabase using its ID to select it
     * 
     * @param id The ID of the row to select
     * @return A copy of the data in the row, if it exists, or null otherwise
     */
    public synchronized Idea readOne(int id) {
        if (id > mRows.size() || id <= 0)
            return null;
        Idea idea = mRows.get(id-1);
        if (idea == null)
            return null;
        return new Idea(idea);
    }

    /**
     * Get all of the Ideas that are present in the MockDatabase
     * @return An ArrayList with all of the data
     */
    public synchronized ArrayList<Idea> readAll() {
        ArrayList<Idea> data = new ArrayList<>();
        for (Idea row : mRows) {
            if (row != null)
                data.add(new Idea(row));
        }
        return data;
    }

     /**
     * Update the likeCount of an Idea in the MockDatabase
     * @param id The ID of the row to update
     * @param likeDelta the requested amount to change likes by; must be 1 or -1 to be successful
     * @return the amount of posts affected by the given likeCountged.  -1 indicates an error.
     */
    public synchronized int updateOneLikeCount(int id, int likeDelta) {
        // Do not update if we don't a valid likeDelta
        if (!(likeDelta == 1 || likeDelta == -1))
            return -1; // default error return value
        // only update if the current entry is valid (not null)
        if (id > mRows.size() || id <= 0)
            return 0;   // implying 0 rows updated
        Idea data = mRows.get(id-1);
        if (data == null || id > mCounter)
            return 0;   // implying 0 rows updated
        // Update and then return 1, since 1 row is updated
        data.mLikeCount += likeDelta;
        // We can test this method by accessing mRows.get(id-1)
        return 1;
    }

    /**
     * Delete a row from the DataStore
     * 
     * @param id The ID of the row to delete
     * @return true if the row was deleted, false otherwise
     */
    public synchronized boolean deleteOne(int id) {
        // Deletion fails for an invalid Id or an Id that has already been deleted
        if (id > mRows.size() || id <= 0)
            return false;
        if (mRows.get(id-1) == null)
            return false;
        mRows.set(id-1, null);
        return true;
    }

}
