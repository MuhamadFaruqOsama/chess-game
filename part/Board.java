package part;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.util.ArrayList;
import java.util.stream.Collectors;

import javax.swing.*;
import javax.swing.plaf.DimensionUIResource;

import Controller.CheckScanner;
import Controller.Input;
import Controller.Move;

public class Board extends JPanel {

    public int tileSize = 85;

    public int cols = 8;
    public int rows = 8;

    public ArrayList<Pieces> pieceList = new ArrayList<>();

    public CheckScanner checkScanner = new CheckScanner(this);

    public Pieces selectedPiece;
    Input input = new Input(this);

    public int enPassantTile = -1;

    private boolean isWhiteToMove = true;
    private boolean isGameOver = false;

    public Board() {
        this.setPreferredSize(new DimensionUIResource(cols * tileSize, rows * tileSize));

        this.addMouseListener(input);
        this.addMouseMotionListener(input);
        
        addPieces();
    }

    public Pieces getPieces(int col, int row) {
        for(Pieces pieces : pieceList) {
            if(pieces.col == col && pieces.row == row) {
                return pieces;
            }
        }

        return null;
    }

    public void makeMove(Move move) {
        if (move.pieces.name.equals("Pawn")){
            movePawn(move);
        } else if(move.pieces.name.equals("King")) {
            moveKing(move);
        }

        move.pieces.col = move.newCol;
        move.pieces.row = move.newRow;

        move.pieces.xPos = move.newCol * tileSize;
        move.pieces.yPos = move.newRow * tileSize;

        move.pieces.isFirstMove = false;

        capture(move.capture);

        isWhiteToMove = !isWhiteToMove;

        updateGameState();
    }

    private void moveKing(Move move) {
        if(Math.abs(move.pieces.col - move.newCol) == 2) {
            Pieces rook;
            if(move.pieces.col < move.newCol) {
                rook = getPieces(7, move.pieces.row);
                rook.col = 5; 
            } else {
                rook = getPieces(0, move.pieces.row);
                rook.col = 3;
            }
            rook.xPos = rook.col * tileSize;
        
        }
        
    }

    private void movePawn(Move move){
            //en passant
            int colorIndex = move.pieces.isWhite ? 1 :-1;

            if (getTileNum(move.newCol, move.newRow)==enPassantTile){
                move.capture = getPieces(move.newCol, move.newRow + colorIndex);
            }
            if(Math.abs(move.pieces.row - move.newRow)==2){
                enPassantTile = getTileNum(move.newCol, move.newRow + colorIndex);
            } else {
                enPassantTile = -1;
            }
            
            //promotions    
            colorIndex = move.pieces.isWhite ? 0 : 7;
            if (move.newRow == colorIndex){
                promotePawn(move);
            }
    }

    private void promotePawn(Move move) {
        // Menampilkan dialog pilihan promosi
        String[] options = {"Queen", "Rook", "Bishop", "Knight"};
        int choice = JOptionPane.showOptionDialog(
            this,
            "Choose promotion piece:",
            "Pawn Promotion",
            JOptionPane.DEFAULT_OPTION,
            JOptionPane.INFORMATION_MESSAGE,
            null,
            options,
            options[0]
        );
    
        // Membuat bidak baru berdasarkan pilihan pemain
        Pieces newPiece;
        switch (choice) {
            case 1: // Rook
                newPiece = new Rock(this, move.newCol, move.newRow, move.pieces.isWhite);
                break;
            case 2: // Bishop
                newPiece = new Bishop(this, move.newCol, move.newRow, move.pieces.isWhite);
                break;
            case 3: // Knight
                newPiece = new Knight(this, move.newCol, move.newRow, move.pieces.isWhite);
                break;
            default: // Queen
                newPiece = new Queen(this, move.newCol, move.newRow, move.pieces.isWhite);
                break;
        }
    
        // Mengganti pion dengan bidak yang baru dipilih
        pieceList.add(newPiece);
        capture(move.pieces);
    }

    public void capture(Pieces piece) {
        pieceList.remove(piece);
    }

    public boolean isValidMove(Move move) {

        if(isGameOver){
            return false;
        }

        if(move.pieces.isWhite != isWhiteToMove){
            return false;
        }

        if(sameTeam(move.pieces, move.capture)) {
            return false;
        }

        if(!move.pieces.isValidMovement(move.newCol, move.newRow)) {
            return false;
        }

        if(move.pieces.moveCollidesWithPiece(move.newCol, move.newRow)) {
            return false;
        }

        if(checkScanner.isKingChecked(move)) {
            return false;
        }

        return true;
    }

    public boolean sameTeam(Pieces p1, Pieces p2) {
        if(p1 == null || p2 == null) {
            return false;
        }

        return p1.isWhite == p2.isWhite;
    }

    public int getTileNum(int col, int row) {
        return row * rows + col;
    }
    

    public Pieces findKing(boolean isWhite) {
        for (Pieces piece : pieceList) {
            if (isWhite == piece.isWhite && piece.name.equals("King")) {
                return piece;
            }
        }
        return null;
    }


    public void addPieces() {
        // // BLACK OPONENTS
        pieceList.add(new Rock(this, 0, 0, false));
        pieceList.add(new Knight(this, 1, 0, false));
        pieceList.add(new Bishop(this, 2, 0, false));
        pieceList.add(new Queen(this, 3, 0, false));
        pieceList.add(new King(this, 4, 0, false));
        pieceList.add(new Bishop(this, 5, 0, false));
        pieceList.add(new Knight(this, 6, 0, false));
        pieceList.add(new Rock(this, 7, 0, false));

        pieceList.add(new Pawn(this, 0, 1, false));
        pieceList.add(new Pawn(this, 1, 1, false));
        pieceList.add(new Pawn(this, 2, 1, false));
        pieceList.add(new Pawn(this, 3, 1, false));
        pieceList.add(new Pawn(this, 4, 1, false));
        pieceList.add(new Pawn(this, 5, 1, false));
        pieceList.add(new Pawn(this, 6, 1, false));
        pieceList.add(new Pawn(this, 7, 1, false));

    //     // // WHITE OPONENTS
        pieceList.add(new Rock(this, 0, 7, true));
        pieceList.add(new Knight(this, 1, 7, true));
        pieceList.add(new Bishop(this, 2, 7, true));
        pieceList.add(new Queen(this, 3, 7, true));
        pieceList.add(new King(this, 4, 7, true));
        pieceList.add(new Bishop(this, 5, 7, true));
        pieceList.add(new Knight(this, 6, 7, true));
        pieceList.add(new Rock(this, 7, 7, true));

        pieceList.add(new Pawn(this, 0, 6, true));
        pieceList.add(new Pawn(this, 1, 6, true));
        pieceList.add(new Pawn(this, 2, 6, true));
        pieceList.add(new Pawn(this, 3, 6, true));
        pieceList.add(new Pawn(this, 4, 6, true));
        pieceList.add(new Pawn(this, 5, 6, true));
        pieceList.add(new Pawn(this, 6, 6, true));
        pieceList.add(new Pawn(this, 7, 6, true));
    }

    private void updateGameState(){
        Pieces king = findKing(isWhiteToMove);
        if (checkScanner.isGameOver(king)) {
            if (checkScanner.isKingChecked(new Move(this, king, king.col, king.row))) {
                JOptionPane.showMessageDialog(null, isWhiteToMove ? "Black Wins!" : "White Wins!");
            } else {
                JOptionPane.showMessageDialog(null, "Stalemate");
            }
            isGameOver = true;
            restartGame();
        }else if(insufficientMaterial(true) && insufficientMaterial(false)){
            JOptionPane.showMessageDialog(null, "Insufficient Material!");
            isGameOver = true;
        }
    }

    private void restartGame() {
        pieceList.clear();
        addPieces();
    }

    private boolean insufficientMaterial(boolean isWhite){
        ArrayList<String> names = pieceList.stream()
            .filter(p -> p.isWhite == isWhite)
            .map(p -> p.name)
            .collect(Collectors.toCollection(ArrayList::new));
        if(names.contains("Queen") || names.contains("Rook") || names.contains("Pawn") || names.contains("Bishop") || names.contains("Knight")){
            return false;
        }
        return names.size() < 3;
    }

    public void paintComponent(Graphics g) {
        Graphics2D g2d = (Graphics2D) g;

        // paint board
        for (int r = 0; r < rows; r++) 
            for (int c = 0; c < cols; c++) {
                g2d.setColor((r+c) % 2 == 0 ? new Color(237, 215, 167) : new Color(114, 65, 39));
                g2d.fillRect(c * tileSize, r * tileSize, tileSize, tileSize);
            }

        // paint valid movement 
        if(selectedPiece != null)
        for (int r = 0; r < rows; r++) 
            for (int c = 0; c < cols; c++) {
                if(isValidMove(new Move(this, selectedPiece, c, r))) {
                    g2d.setColor(new Color(107, 224, 65, 120));
                    g2d.fillRect(c * tileSize, r * tileSize, tileSize, tileSize);
                }
            }

        // paint pieces 
        for(Pieces piece: pieceList) {
            piece.paint(g2d);
        }
    }
}
