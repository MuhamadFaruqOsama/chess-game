package Controller;

import java.util.ArrayList;
import java.util.List;

import part.Board;
import part.Pieces;

public class CheckScanner {
    Board board;

    public CheckScanner(Board board) {
        this.board = board;
    }

    public boolean isKingChecked(Move move) {
        Pieces king = board.findKing(move.pieces.isWhite);

        if (king == null) return false;

        int kingCol = king.col;
        int kingRow = king.row;

        // Jika raja yang sedang dipindahkan, gunakan posisi tujuan
        if (board.selectedPiece != null && board.selectedPiece.name.equals("King")) {
            kingCol = move.newCol;
            kingRow = move.newRow;
        }

        return hitByLinearPiece(king, kingCol, kingRow, 0, 1) || // Ke atas
               hitByLinearPiece(king, kingCol, kingRow, 1, 0) || // Ke kanan
               hitByLinearPiece(king, kingCol, kingRow, 0, -1) || // Ke bawah
               hitByLinearPiece(king, kingCol, kingRow, -1, 0) || // Ke kiri
               hitByDiagonalPiece(king, kingCol, kingRow, 1, 1) || // Diagonal kanan atas
               hitByDiagonalPiece(king, kingCol, kingRow, 1, -1) || // Diagonal kanan bawah
               hitByDiagonalPiece(king, kingCol, kingRow, -1, 1) || // Diagonal kiri atas
               hitByDiagonalPiece(king, kingCol, kingRow, -1, -1) || // Diagonal kiri bawah
               hitByKnight(king, kingCol, kingRow) || // Ancaman kuda
               hitByPawn(king, kingCol, kingRow) || // Ancaman bidak
               hitByKing(king, kingCol, kingRow); // Ancaman raja lain
    }

    private boolean hitByLinearPiece(Pieces king, int kingCol, int kingRow, int colStep, int rowStep) {
        for (int i = 1; i < 8; i++) {
            int col = kingCol + (i * colStep);
            int row = kingRow + (i * rowStep);

            Pieces piece = board.getPieces(col, row);
            if (piece != null) {
                if (!board.sameTeam(piece, king) && (piece.name.equals("Rook") || piece.name.equals("Queen"))) {
                    return true;
                }
                break; // Hentikan jika bertemu piece
            }
        }
        return false;
    }

    private boolean hitByDiagonalPiece(Pieces king, int kingCol, int kingRow, int colStep, int rowStep) {
        for (int i = 1; i < 8; i++) {
            int col = kingCol + (i * colStep);
            int row = kingRow + (i * rowStep);

            Pieces piece = board.getPieces(col, row);
            if (piece != null) {
                if (!board.sameTeam(piece, king) && (piece.name.equals("Bishop") || piece.name.equals("Queen"))) {
                    return true;
                }
                break; // Hentikan jika bertemu piece
            }
        }
        return false;
    }

    private boolean hitByKnight(Pieces king, int kingCol, int kingRow) {
        int[][] knightMoves = {
            {-2, -1}, {-1, -2}, {1, -2}, {2, -1},
            {2, 1}, {1, 2}, {-1, 2}, {-2, 1}
        };

        for (int[] move : knightMoves) {
            Pieces piece = board.getPieces(kingCol + move[0], kingRow + move[1]);
            if (piece != null && !board.sameTeam(piece, king) && piece.name.equals("Knight")) {
                return true;
            }
        }
        return false;
    }

    private boolean hitByKing(Pieces king, int kingCol, int kingRow) {
        int[][] kingMoves = {
            {-1, -1}, {0, -1}, {1, -1},
            {-1, 0},         {1, 0},
            {-1, 1}, {0, 1}, {1, 1}
        };

        for (int[] move : kingMoves) {
            Pieces piece = board.getPieces(kingCol + move[0], kingRow + move[1]);
            if (piece != null && !board.sameTeam(piece, king) && piece.name.equals("King")) {
                return true;
            }
        }
        return false;
    }

    private boolean hitByPawn(Pieces king, int kingCol, int kingRow) {
        int direction = king.isWhite ? -1 : 1;
        Pieces leftPawn = board.getPieces(kingCol - 1, kingRow + direction);
        Pieces rightPawn = board.getPieces(kingCol + 1, kingRow + direction);

        return (leftPawn != null && !board.sameTeam(leftPawn, king) && leftPawn.name.equals("Pawn")) ||
               (rightPawn != null && !board.sameTeam(rightPawn, king) && rightPawn.name.equals("Pawn"));
    }

    public boolean isGameOver(Pieces king) {
        if (!isKingChecked(new Move(board, king, king.col, king.row))) {
            return false; // Raja tidak dalam check
        }

        for (Pieces piece : board.pieceList) {
            if (board.sameTeam(piece, king)) {
                board.selectedPiece = piece;

                for (int row = 0; row < board.rows; row++) {
                    for (int col = 0; col < board.cols; col++) {
                        Move move = new Move(board, piece, col, row);
                        if (board.isValidMove(move) && !isKingChecked(move)) {
                            return false; // Masih ada gerakan valid
                        }
                    }
                }
            }
        }

        // Cek apakah ada bidak yang bisa menghalangi ancaman
        for (Move threateningMove : getThreateningMoves(king)) {
            if (canPieceBlockCheck(king, threateningMove)) {
                return false; // Ada bidak yang bisa menyelamatkan
            }
        }

        return true; // Tidak ada gerakan valid, game over
    }

    private boolean canPieceBlockCheck(Pieces king, Move threateningMove) {
        Pieces threateningPiece = threateningMove.pieces;

        if (threateningPiece.name.equals("Knight")) {
            return canAnyPieceCapture(king, threateningPiece.col, threateningPiece.row);
        }

        int colStep = Integer.compare(threateningMove.newCol, king.col);
        int rowStep = Integer.compare(threateningMove.newRow, king.row);

        int col = threateningMove.newCol;
        int row = threateningMove.newRow;

        while (col != king.col || row != king.row) {
            if (canAnyPieceMoveTo(king, col, row)) {
                return true;
            }
            col -= colStep;
            row -= rowStep;
        }

        return false;
    }

    private List<Move> getThreateningMoves(Pieces king) {
        List<Move> threateningMoves = new ArrayList<>();

        for (Pieces piece : board.pieceList) {
            if (!board.sameTeam(piece, king)) {
                Move move = new Move(board, piece, king.col, king.row);
                if (board.isValidMove(move)) {
                    threateningMoves.add(move);
                }
            }
        }

        return threateningMoves;
    }

    private boolean canAnyPieceCapture(Pieces king, int targetCol, int targetRow) {
        for (Pieces piece : board.pieceList) {
            if (board.sameTeam(piece, king)) {
                board.selectedPiece = piece;
                Move move = new Move(board, piece, targetCol, targetRow);

                if (board.isValidMove(move) && !isKingChecked(move)) {
                    return true;
                }
            }
        }
        return false;
    }

    private boolean canAnyPieceMoveTo(Pieces king, int targetCol, int targetRow) {
        for (Pieces piece : board.pieceList) {
            if (board.sameTeam(piece, king)) {
                board.selectedPiece = piece;
                Move move = new Move(board, piece, targetCol, targetRow);

                if (board.isValidMove(move) && !isKingChecked(move)) {
                    return true;
                }
            }
        }
        return false;
    }
}
