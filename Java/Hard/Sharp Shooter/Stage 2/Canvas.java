package shooter;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.ArrayList;
import java.util.List;

public class Canvas extends JPanel implements KeyListener {
    private final List<Point> holes = new ArrayList<>();
    private int sightX = 350;
    private int sightY = 350;

    public Canvas() {
        setName("Canvas");
        setVisible(true);
        setFocusable(true);
        setPreferredSize(new Dimension(700, 700));
        setBackground(Color.DARK_GRAY);
        addKeyListener(this);

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

        holes.forEach(p -> {
            g.setColor(Color.ORANGE);
            g.fillOval(p.x - 5, p.y - 5, 10, 10);
        });

        // Gun Sight
        g.setColor(Color.RED);
        Point sight = new Point(sightX, sightY);
        g.drawLine(sight.x - 40, sight.y, sight.x - 10, sight.y);
        g.drawLine(sight.x + 10, sight.y, sight.x + 40, sight.y);
        g.drawLine(sight.x, sight.y - 40, sight.x, sight.y - 10);
        g.drawLine(sight.x, sight.y + 10, sight.x, sight.y + 40);
        ((Graphics2D) g).setStroke(new BasicStroke(4f));
        g.drawOval(sight.x - 40, sight.y - 40, 80, 80);
    }

    @Override
    public void keyTyped(KeyEvent e) {}

    @Override
    public void keyPressed(KeyEvent e) {
        boolean flag = true;
        int key = e.getKeyCode();
        if (key == 37)
            if (sightX >= 50)
                sightX -= 10;
            else
                flag = false;
        else if (key == 38)
            if (sightY >= 50)
                sightY -= 10;
            else
                flag = false;
        else if (key == 39)
            if (sightX <= 650)
                sightX += 10;
            else
                flag = false;
        else if (key == 40)
            if (sightY <= 650)
                sightY += 10;
            else
                flag = false;
        else if (key == 32)
            holes.add(new Point(sightX, sightY));

        if (flag)
            repaint();
    }

    @Override
    public void keyReleased(KeyEvent e) {}

    // space bar - 32, left - 37, up - 38, right - 39, down - 40
}
