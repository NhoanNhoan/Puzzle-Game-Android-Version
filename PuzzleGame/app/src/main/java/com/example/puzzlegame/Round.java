package com.example.puzzlegame;

public class Round {
    int numRound;
    int numPiecesInRow;
    int numPiecesInColumn;
    int additionPoint;
    int minusPoint;
    int image;

    public Round(int numRound, int numPieceInRow, int numPieceInColumn, int additionPoint, int minusPoint, int image) {
        this.numRound = numRound;
        this.numPiecesInRow = numPieceInRow;
        this.numPiecesInColumn = numPieceInColumn;
        this.additionPoint = additionPoint;
        this.minusPoint = minusPoint;
        this.image = image;
    }
}
