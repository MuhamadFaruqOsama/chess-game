package part;

import java.awt.image.BufferedImage;

import Controller.Move;

public class King extends Pieces {
    public King(Board board, int col, int row, boolean isWhite) {
        super(board);
        this.col = col;
        this.row = row;
        this.xPos = col * board.tileSize;
        this.yPos = row * board.tileSize;
    
        this.isWhite = isWhite;
        this.name = "King";

        this.sprite = sheet.getSubimage(0 * sheetScale, isWhite ? 0 : sheetScale, sheetScale, sheetScale).getScaledInstance(board.tileSize, board.tileSize, BufferedImage.SCALE_SMOOTH);
    }

    public boolean isValidMovement(int col, int row) {
        return Math.abs((col - this.col) * (row - this.row)) == 1 || Math.abs(col - this.col) + Math.abs(row - this.row) == 1 || canCastle(col, row);
    }

    public boolean canCastle(int col, int row) {
        if(this.row == row) {
            if(col == 6) {
                Pieces rook = board.getPieces(7, row);
                if(rook != null && rook.isFirstMove && isFirstMove) {
                    return board.getPieces(5, row) == null &&
                            board.getPieces(6, row) == null &&
                            !board.checkScanner.isKingChecked(new Move(board, this, 5, row));
                }
            } else if(col == 2) {
                Pieces rook = board.getPieces(0, row);
                if(rook != null && rook.isFirstMove && isFirstMove) {
                    return board.getPieces(3, row) == null &&
                            board.getPieces(2, row) == null &&
                            board.getPieces(1, row) == null &&
                            !board.checkScanner.isKingChecked(new Move(board, this, 3, row));
                }
            }
        }
        
        return false;
    }
}