package com.example.puzzlegame;

public class SavedState {
    public int getNumRightPieces() {
        return numRightPieces;
    }

    public void setNumRightPieces(int numRightPieces) {
        this.numRightPieces = numRightPieces;
    }

    private int numRightPieces;

    public int getScore() {
        return score;
    }

    public void setScore(int score) {
        this.score = score;
    }

    private int score;

    public SavedState(int numRightPieces, int score) {
        this.numRightPieces = numRightPieces;
        this.score = score;
    }
}
