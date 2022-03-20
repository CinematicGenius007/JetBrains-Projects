package shooter;

import javax.swing.*;
import java.awt.*;

public class SharpShooter extends JFrame {
    public SharpShooter() {
        super("Sharp shooter");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setVisible(true);
        setLayout(new BorderLayout());

        // Setting Canvas
        Canvas canvas = new Canvas();
        add(canvas, BorderLayout.NORTH);
        canvas.requestFocus();

        // Setting Statusbar
        JLabel nameLabel = new JLabel();
        nameLabel.setName("Statusbar");
        nameLabel.setText("You are welcome!");
        nameLabel.setPreferredSize(new Dimension(700, 30));
        nameLabel.setBackground(Color.WHITE);
        nameLabel.setForeground(Color.BLACK);
        add(nameLabel, BorderLayout.SOUTH);

        pack();
    }
}
