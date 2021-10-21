package tictactoe;

import javax.swing.*;
import java.awt.*;


@SuppressWarnings("unused")
public class Cell extends JButton {

    public Cell(String name) {
        super();
        this.setName(name);
        this.setBackground(new java.awt.Color(204, 255, 255));
        this.setFont(new java.awt.Font("Consolas", Font.PLAIN, 24));
        this.setForeground(new java.awt.Color(0, 0, 102));
        this.setSize(100, 100);
        this.setText(" ");
        this.setEnabled(false);
        this.setFocusPainted(false);
    }
}
