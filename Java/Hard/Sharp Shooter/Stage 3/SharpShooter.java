package shooter;

import javax.swing.*;
import java.awt.*;

public class SharpShooter extends JFrame {
    public SharpShooter() {
        super("Sharp shooter");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setVisible(true);
        setLayout(new BorderLayout());

        JLabel nameLabel = new JLabel();
        nameLabel.setName("Statusbar");
        nameLabel.setText("Press the SPACE bar to start the game");
        nameLabel.setPreferredSize(new Dimension(700, 30));

        // Setting Canvas
        Canvas canvas = new Canvas(nameLabel);
        add(canvas, BorderLayout.NORTH);
        canvas.requestFocus();

        // Setting Statusbar
        add(nameLabel, BorderLayout.SOUTH);

        pack();
    }
}
