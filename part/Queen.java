package part;

import java.awt.image.BufferedImage;

public class Queen extends Pieces{
    public Queen(Board board, int col, int row, boolean isWhite) {
        super(board);
        this.col = col;
        this.row = row;
        this.xPos = col * board.tileSize;
        this.yPos = row * board.tileSize;
        
        this.isWhite = isWhite;
        this.name = "Queen";

        this.sprite = sheet.getSubimage(1 * sheetScale, isWhite ? 0 : sheetScale, sheetScale, sheetScale).getScaledInstance(board.tileSize, board.tileSize, BufferedImage.SCALE_SMOOTH);
    }

    public boolean isValidMovement(int col, int row) {
        return this.col == col || this.row == row || Math.abs(this.col - col) == Math.abs(this.row - row);
    }

    public boolean moveCollidesWithPiece(int col, int row) {
        if(this.col == col || this.row == row) {
            // left
            if(this.col > col)
                for(int c = this.col - 1; c > col; c--)
                    if(board.getPieces(c, this.row) != null) 
                        return true;

            // right
            if(this.col < col)
                for(int c = this.col + 1; c < col; c++)
                    if(board.getPieces(c, this.row) != null) 
                        return true;

            // up
            if(this.row > row)
                for(int r = this.row - 1; r > row; r--)
                    if(board.getPieces(this.col, r) != null) 
                        return true;

            // down
            if(this.row < row)
                for(int r = this.row + 1; r < row; r++)
                    if(board.getPieces(this.col, r) != null) 
                        return true;
        } else {
            // up left
            if(this.col > col && this.row > row)
                for(int i = 1;i < Math.abs(this.col - col);i++) 
                    if(board.getPieces(this.col - i, this.row - i) != null)
                        return true;

            // up right
            if(this.col < col && this.row > row)
                for(int i = 1;i < Math.abs(this.col - col);i++) 
                    if(board.getPieces(this.col + i, this.row - i) != null)
                        return true;

            // down left
            if(this.col > col && this.row < row)
                for(int i = 1;i < Math.abs(this.col - col);i++) 
                    if(board.getPieces(this.col - i, this.row + i) != null)
                        return true;

            // down right
            if(this.col < col && this.row < row)
                for(int i = 1;i < Math.abs(this.col - col);i++) 
                    if(board.getPieces(this.col + i, this.row + i) != null)
                        return true;
        }

        return false;
    }
}
