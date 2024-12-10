import java.awt.GridBagLayout;

import javax.swing.*;
import javax.swing.plaf.DimensionUIResource;

import part.Board;

public class Main {
    public static void main(String[] args) {
        JFrame frame = new JFrame();
        frame.setMinimumSize(new DimensionUIResource(1000, 1000));
        frame.setLayout(new GridBagLayout());
        frame.setLocationRelativeTo(null);

        Board board = new Board();
        frame.add(board);

        frame.setVisible(true); 
    }
}