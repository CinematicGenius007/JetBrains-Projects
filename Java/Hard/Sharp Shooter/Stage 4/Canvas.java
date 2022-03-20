package shooter;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.ArrayList;
import java.util.List;

public class Canvas extends JPanel implements KeyListener {
    private final List<Point> holes = new ArrayList<>();
    private int sightX = 100;
    private int sightY = 100;
    private final JLabel label;
    private boolean start = false;
    private int attempts = 12;
    private int totalScore = 0;

    private enum Direction {
        LEFT, UP, RIGHT, DOWN, NONE;
        static Direction getDirection(int keyCode) {
            switch (keyCode) {
                case 37:
                    return LEFT;
                case 38:
                    return UP;
                case 39:
                    return RIGHT;
                case 40:
                    return DOWN;
                default:
                    return NONE;
            }
        }
    }

    private Direction direction = Direction.NONE;
    private int directionMotionValue = 1;

    public Canvas(JLabel label) {
        setName("Canvas");
        setVisible(true);
        setFocusable(true);
        setPreferredSize(new Dimension(700, 700));
        setBackground(Color.DARK_GRAY);
        addKeyListener(this);

        this.label = label;

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

    private boolean moveSight() {
        switch (direction) {
            case LEFT:
                if (sightX - directionMotionValue >= 40)
                    sightX -= directionMotionValue;
                else
                    return false;
                break;
            case UP:
                if (sightY - directionMotionValue >= 40)
                    sightY -= directionMotionValue;
                else
                    return false;
                break;
            case RIGHT:
                if (sightX + directionMotionValue <= 660)
                    sightX += directionMotionValue;
                else
                    return false;
                break;
            case DOWN:
                if (sightY + directionMotionValue <= 660)
                    sightY += directionMotionValue;
                else
                    return false;
                break;
            default:
                return false;
        }
        return true;
    }

    @Override
    public void keyTyped(KeyEvent e) {}

    @Override
    public void keyPressed(KeyEvent e) {
        int key = e.getKeyCode();
        if (attempts == 0)
            return;
        if (!start) {
            if (key == 32) {
                start = true;
                label.setText("Bullets left: 12, your score: 0 (0)");
            }
            return;
        }
        boolean flag = true;
        if (key == 32) {
            holes.add(new Point(sightX, sightY));
            setLabel();
        }

        Direction newDirection = Direction.getDirection(key);
        if (newDirection != direction && newDirection != Direction.NONE) {
            while (directionMotionValue > 1) {
                directionMotionValue >>= 1;
                if (moveSight())
                    repaint();
            }
            direction = newDirection;
            flag = moveSight();
        } else if (newDirection == direction && newDirection != Direction.NONE) {
            directionMotionValue <<= 1;
            flag = moveSight();
        }

        if (flag)
            repaint();
    }

    @Override
    public void keyReleased(KeyEvent e) {
//        int key = e.getKeyCode();
//        if (key >= 37 && key <= 40) {
//            while (directionMotionValue > 1) {
//                directionMotionValue >>= 1;
//                if (moveSight())
//                    repaint();
//            }
//        }
    }

    private void setLabel() {
        attempts--;
        int score = 10 - ((int) Math.ceil(Math.sqrt(Math.pow(Math.abs(350 - sightX), 2) + Math.pow(Math.abs(350 - sightY), 2)))) / 30;
        totalScore += score;
        if (attempts == 0)
            label.setText(String.format("Game over, your score: %d", totalScore));
        else
            label.setText(String.format("Bullets left: %d, your score: %d (%d)", attempts, totalScore, score));
        System.out.println(label.getText());
    }

    // space bar - 32, left - 37, up - 38, right - 39, down - 40
}
