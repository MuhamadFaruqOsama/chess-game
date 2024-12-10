package part;

import java.awt.image.BufferedImage;

public class Pawn extends Pieces {
    public Pawn(Board board, int col, int row, boolean isWhite) {
        super(board);
        this.col = col;
        this.row = row;
        this.xPos = col * board.tileSize;
        this.yPos = row * board.tileSize;
        
        this.isWhite = isWhite;
        this.name = "Pawn";

        this.sprite = sheet.getSubimage(5 * sheetScale, isWhite ? 0 : sheetScale, sheetScale, sheetScale).getScaledInstance(board.tileSize, board.tileSize, BufferedImage.SCALE_SMOOTH);
    }

    public boolean isValidMovement(int col, int row) {
        int colorIndex = isWhite ? 1 : -1;

        // push pawn 1
        if(this.col == col && row == this.row - colorIndex && board.getPieces(col, row) == null)
            return true;

        // push pawn 2
        if(isFirstMove && this.col == col && row == this.row - colorIndex * 2 && board.getPieces(col, row) == null && board.getPieces(col, row + colorIndex) == null)
            return true;

        // capture the left
        if(col == this.col - 1 && row == this.row - colorIndex && board.getPieces(col, row) != null)
            return true;

        // capture the right
        if(col == this.col + 1 && row == this.row - colorIndex && board.getPieces(col, row) != null)
            return true;
        
        // enPassant left
        if (board.getTileNum(col, row)== board.enPassantTile && row == this.row - colorIndex && board.getPieces(col, row + colorIndex)!= null){
            return true;
        }
        // enpassant right
        if (board.getTileNum(col, row)== board.enPassantTile && row == this.row - colorIndex && board.getPieces(col, row + colorIndex)!= null){
            return true;
        }
        return false;
    }
}
