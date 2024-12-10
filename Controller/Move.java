package Controller;

import part.Board;
import part.Pieces;

public class Move {
    
    public int oldCol;
    public int oldRow;
    public int newCol;
    public int newRow;

    public Pieces pieces;
    public Pieces capture;

    public Move(Board board, Pieces pieces, int newCol, int newRow) {
        this.oldCol = pieces.col;
        this.oldRow = pieces.row;
        this.newCol = newCol;
        this.newRow = newRow;

        this.pieces = pieces;
        this.capture = board.getPieces(newCol, newRow);
    }

}
