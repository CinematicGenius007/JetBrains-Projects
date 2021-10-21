package tictactoe;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.util.Timer;
import java.util.TimerTask;

public class TicTacToe extends JFrame {

    private enum State { START, RESET }
    private enum Turn {
        P1(null),
        P2(null);

        RobotMove rm;
        Turn(RobotMove rm) {
            this.rm = rm;
        }
        void setMove(RobotMove rm) {
            this.rm = rm;
        }
        RobotMove getMove() { return rm; }
    }

    JLabel log;
    JButton reset;
    JPanel jPanel;
    JButton player1;
    JButton player2;
    State state = State.START;
    Turn turn = Turn.P1;
    GameInterface gi;

    public TicTacToe() {
        setTitle("Tic Tae Toe");
        setSize(314, 466);
        setLayout(null);
        setResizable(false);
        setBackground(new java.awt.Color(0, 0, 204));

        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        gi = new GameInterface();

        log = new JLabel();
        log.setName("LabelStatus");
        log.setFocusable(false);
        log.setText("Game is not started");
        log.setBounds(0, 350, 300, 50);
        log.setFont(new java.awt.Font("Consolas", Font.PLAIN, 16));
        log.setForeground(Color.RED);

        add(log);

        reset = new JButton();
        reset.setName("ButtonStartReset");
        reset.setText("Start");
        reset.setBackground(new java.awt.Color(104, 205, 55));
        reset.setFont(new java.awt.Font("Consolas", Font.PLAIN, 14));
        reset.setForeground(new java.awt.Color(0, 0, 102));
        reset.setBounds(100, 0, 100, 50);
        reset.setFocusPainted(false);

        add(reset);

        player1 = new JButton();
        player1.setName("ButtonPlayer1");
        player1.setText("Human");
        player1.setBackground(new java.awt.Color(104, 205, 55));
        player1.setFont(new java.awt.Font("Consolas", Font.PLAIN, 14)); // NOI18N
        player1.setForeground(new java.awt.Color(0, 0, 102));
        player1.setBounds(0, 0, 100, 50);
        player1.setFocusPainted(false);

        add(player1);

        player2 = new JButton();
        player2.setName("ButtonPlayer2");
        player2.setText("Human");
        player2.setBackground(new java.awt.Color(104, 205, 55));
        player2.setFont(new java.awt.Font("Consolas", Font.PLAIN, 14)); // NOI18N
        player2.setForeground(new java.awt.Color(0, 0, 102));
        player2.setBounds(200, 0, 100, 50);
        player2.setFocusPainted(false);

        add(player2);

        add(jPanel = new GameBoard());

        JMenuBar menuBar = new JMenuBar();

        JMenu menu = new JMenu("Game");
        menu.setEnabled(true);
        menu.setName("MenuGame");
        menu.setMnemonic(KeyEvent.VK_F);
        menu.setFont(new java.awt.Font("Consolas", Font.BOLD, 15));

        JMenuItem menuHumanHuman = new MenuItem("Human vs Human", "MenuHumanHuman");
        JMenuItem menuHumanRobot = new MenuItem("Human vs Robot", "MenuHumanRobot");
        JMenuItem menuRobotHuman = new MenuItem("Robot vs Human", "MenuRobotHuman");
        JMenuItem menuRobotRobot = new MenuItem("Robot vs Robot", "MenuRobotRobot");
        JMenuItem menuExit = new MenuItem("Exit", "MenuExit");

        menuExit.addActionListener(e -> System.exit(0));

        menu.add(menuHumanHuman);
        menu.add(menuHumanRobot);
        menu.add(menuRobotHuman);
        menu.add(menuRobotRobot);
        menu.addSeparator();
        menu.add(menuExit);

        menuBar.add(menu);
        setJMenuBar(menuBar);

        setVisible(true);

        addCells();

        reset.addActionListener(e -> {
            if (state == State.START) {
                reset.setText("Reset");
                log.setText(String.format("The turn of %s Player (X)"
                        , getCurrentPlayer()));
                state = State.RESET;
                player1.setEnabled(false);
                player2.setEnabled(false);
                System.out.println(log.getText());
                makeButtonsAvailable();
                moveNext();
            } else {
                reset.setText("Start");
                player1.setText("Human");
                player2.setText("Human");
                player1.setEnabled(true);
                player2.setEnabled(true);
                turn = Turn.P1;
                Turn.P1.setMove(null);
                Turn.P2.setMove(null);
                resetGameBoard();
                state = State.START;
                log.setText("Game is not started");
            }
        });

        player1.addActionListener(e -> {
            if (state == State.START) {
                if (Turn.P1.rm == null) {
                    player1.setText("Robot");
                    Turn.P1.setMove(new RobotMove());
                } else {
                    player1.setText("Human");
                    Turn.P1.setMove(null);
                }
            }
        });

        player2.addActionListener(e -> {
            if (state == State.START) {
                if (Turn.P2.rm == null) {
                    player2.setText("Robot");
                    Turn.P2.setMove(new RobotMove());
                } else {
                    player2.setText("Human");
                    Turn.P2.setMove(null);
                }
            }
        });

        menuHumanHuman.addActionListener(e -> {
            if (state == State.RESET)
                reset.doClick();
            if (!"Human".equals(player1.getText()))
                player1.doClick();

            if (!"Human".equals(player2.getText()))
                player2.doClick();

            reset.doClick();
        });

        menuHumanRobot.addActionListener(e -> {
            if (state == State.RESET)
                reset.doClick();
            if (!"Human".equals(player1.getText()))
                player1.doClick();

            if (!"Robot".equals(player2.getText()))
                player2.doClick();

            reset.doClick();

        });

        menuRobotHuman.addActionListener(e -> {
            if (state == State.RESET)
                reset.doClick();
            if (!"Robot".equals(player1.getText()))
                player1.doClick();

            if (!"Human".equals(player2.getText()))
                player2.doClick();

            reset.doClick();
        });

        menuRobotRobot.addActionListener(e -> {
            if (state == State.RESET)
                reset.doClick();
            if (!"Robot".equals(player1.getText()))
                player1.doClick();

            if (!"Robot".equals(player2.getText()))
                player2.doClick();

            reset.doClick();
        });
    }

    private void moveNext() {
        String status;
        if ((status = gi.checkForWinner()).length() == 0) {
            log.setText(String.format("The turn of %s Player (%s)",
                    getCurrentPlayer(),
                    gi.getTurn()));
            if (turn.rm != null) {
                Timer timer = new Timer();
                TimerTask setRobotMove = new TimerTask() {
                    @Override public void run() {
                        new Thread(() -> {
                            int move;
                            ((JButton) jPanel.getComponents()[move =
                                    turn.rm.getMove(gi.getBoard())]).setText("" + gi.TakeTurn(move));
                            changeTurns();
                            moveNext();
                        }).start();
                    }
                };
                timer.schedule(setRobotMove, 1000);
            }
        } else {
            log.setText(!status.equals("Draw") ? String.format("The %s Player (%s) wins",
                    getWinnerPlayer(),
                    status)
                    : status);
        }
    }

    private void addCells() {
        int i = 0;
        for (Component c : jPanel.getComponents()) {
            final int j = i++;
            if (c instanceof JButton) {
                ((JButton) c).addActionListener(e -> {
                    if (gi.checkForWinner().length() == 0
                            && " ".equals(((JButton) c).getText())
                            && state == State.RESET
                            && checkLegalTurn()) {
                        ((JButton) c).setText("" + gi.TakeTurn(j));
                        changeTurns();
                        moveNext();
                    }
                });
            }
        }
    }

    private String getCurrentPlayer() {
        return turn.getMove() == null ? "Human" : "Robot";
    }

    private String getWinnerPlayer() {
        if (turn == Turn.P1) {
            return Turn.P2.rm == null ? "Human" : "Robot";
        }
        return Turn.P1.rm == null ? "Human" : "Robot";
    }

    private void resetGameBoard() {
        gi.resetBoard();
        for (Component c : jPanel.getComponents()) {
            if (c instanceof JButton) {
                ((JButton) c).setText(" ");
                c.setEnabled(false);
            }
        }
    }

    private void makeButtonsAvailable() {
        for (Component c : jPanel.getComponents()) {
            if (c instanceof JButton) {
                c.setEnabled(true);
            }
        }
    }

    private void changeTurns() {
        if (turn == Turn.P1)
            turn = Turn.P2;
        else
            turn = Turn.P1;
    }

    private boolean checkLegalTurn() {
        return turn.rm == null;
    }
}