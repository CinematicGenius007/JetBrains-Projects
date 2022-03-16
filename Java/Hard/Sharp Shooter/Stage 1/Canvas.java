package shooter;

import javax.swing.*;
import java.awt.*;

public class Canvas extends JPanel {
    public Canvas() {
        setName("Canvas");
        setVisible(true);
        setFocusable(true);
        setPreferredSize(new Dimension(700, 700));
        setBackground(Color.DARK_GRAY);

        repaint();
    }

    @Override
    public void paint(Graphics g) {
        super.paint(g);
        g.setColor(Color.BLACK);
        g.fillOval(50, 50, 600, 600);
        for (int i = 50, j = 600; i <= 230; i += 30, j -= 60) {
            g.setColor(Color.WHITE);
            g.drawOval(i, i, j, j);
        }
        g.setColor(Color.BLACK);
        g.drawOval(230, 230, 240, 240);
        g.setColor(Color.WHITE);
        g.fillOval(230, 230, 240, 240);
        g.setColor(Color.BLACK);
        g.drawOval(260, 260, 180, 180);
        g.drawOval(290, 290, 120, 120);
        g.fillOval(320, 320, 60, 60);

        g.setColor(Color.CYAN);
        g.fillOval(295, 495, 10, 10);
    }
}
