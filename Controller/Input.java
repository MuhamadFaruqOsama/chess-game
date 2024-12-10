package Controller;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import part.Board;
import part.Pieces;

public class Input extends MouseAdapter {

    Board board;

    public Input (Board board) {
        this.board = board;
    }

    @Override
    public void mousePressed(MouseEvent e) {
        int col = e.getX() / board.tileSize;
        int row = e.getY() / board.tileSize;

        Pieces piecesXY = board.getPieces(col, row);
        if(piecesXY != null) {
            board.selectedPiece = piecesXY;
        }
    }

    @Override
    public void mouseDragged(MouseEvent e) {
        if(board.selectedPiece != null) {
            board.selectedPiece.xPos = e.getX() - board.tileSize / 2;
            board.selectedPiece.yPos = e.getY() - board.tileSize / 2;

            board.repaint();
        }
    }

    @Override
    public void mouseReleased(MouseEvent e) {
        int col = e.getX() / board.tileSize;
        int row = e.getY() / board.tileSize;

        if(board.selectedPiece != null) {
            Move move = new Move(board, board.selectedPiece, col, row);

            if(board.isValidMove(move)) {
                board.makeMove(move);
            } else {
                board.selectedPiece.xPos = board.selectedPiece.col * board.tileSize;
                board.selectedPiece.yPos = board.selectedPiece.row * board.tileSize;
            }
        }

        board.selectedPiece = null;
        board.repaint();
    }
}
