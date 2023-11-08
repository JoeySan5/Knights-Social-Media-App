package edu.lehigh.cse216.knights.backend;

public class Like {
    
    public final String userId;

    public final int IdeaId;

    public int value;

    public Like(String userId, int IdeaId, int value) {
        this.userId = userId;
        this.IdeaId = IdeaId;
        this.value = value;
    }

}
