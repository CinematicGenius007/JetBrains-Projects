package tictactoe;

import javax.swing.*;
import java.awt.*;

public class MenuItem extends JMenuItem {

    public MenuItem(String text, String name) {
        super(text);
        setName(name);
        setFont(new java.awt.Font("Courier", Font.BOLD, 15));
    }
}
