package tictactoe;

import javax.swing.*;
import java.awt.*;


@SuppressWarnings("unused")
public class GameBoard extends JPanel {



    public GameBoard() {
        super();
        this.setName("Board");
        this.setBackground(new java.awt.Color(0, 0, 204));
        this.setBounds(0, 50, 300, 300);
        this.setLayout(new GridLayout(3, 3));
        for (int i = 3, k = 0; i > 0; i--)
            for (int j = 'A'; j <= 'C'; j++)
                this.add(new Cell("Button" + Character.toString(j) + i));



    }
}
