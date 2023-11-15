package edu.lehigh.cse216.knights.backend;

/**
 * Like is a class that represents a single like on a single idea.  It
 * includes the like's unique id, the id of the idea it is liking on,
 * the id of the user who liked the idea, and the like's value.
 */
public class Like {
    /**
     * The userID associated with this like
     */
    public final String userId;

    /**
     * The idea id associated with this like

     */
    public final int IdeaId;

    /**
     * The value of the like
     */
    public int value;

    /**
     * The constructor for the like
     * @param userId The user id associated with this like
     * @param IdeaId The idea id associated with this like
     * @param value The value of the like
     */
    public Like(String userId, int IdeaId, int value) {
        this.userId = userId;
        this.IdeaId = IdeaId;
        this.value = value;
    }

}
