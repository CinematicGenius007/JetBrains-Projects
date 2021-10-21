package tictactoe.display;

import tictactoe.backend.*;

import java.util.ArrayList;
import java.util.List;

public class GameInterface {
    private enum State { CHOOSE_PLAYER, CHOOSE_MOVE }
    private static State state = State.CHOOSE_PLAYER;

    private enum Player {
        NONE(null),
        USER(null),
        EASY(new EasyMove()),
        MEDIUM(new MediumMove()),
        HARD(new HardMove());

        ComputerMove cm;

        Player(ComputerMove cm) {
            this.cm = cm;
        }

    }

    private enum Turn { PLAYER1, PLAYER2	}

    public static final Board b = new Board();

    private static Player player1 = Player.NONE;
    private static Player player2 = Player.NONE;

    private static Turn turn = Turn.PLAYER1;

    private static int cxy = 0;

    public final static List<Character> MATCHES = new ArrayList<>();

    public static void takeInput(String[] input) {
        int status = checkInput(input);
        if (status == 200)
            start();
        else if (status == 201)
            nextMove();
        else if (status == 400)
            System.out.print("Bad parameters!\nInput command: ");
        else if (status == 401)
            System.out.println("You should enter numbers!");
        else if (status == 402)
            System.out.println("Coordinates should be from 1 to 3!");
        else if (status == 404)
            System.out.println("This cell is occupied! Choose another one!");

        if (status > 400 && status <= 404)
            System.out.print("Enter the coordinates: ");
    }

    /**
     * {@summary This is the method that takes care of the game. }
     *
     */
    private static void gameState() {
        char winner;
        if ((winner = b.getWinner()) != 'n') {
            if (winner == 'D')
                System.out.println("Draw");
            else
                System.out.println("" + winner + " wins");

            MATCHES.add(winner);
            state = State.CHOOSE_PLAYER;
            turn = Turn.PLAYER1;
            System.out.print("Input command: ");
            return;
        }

        Player currP;
        if (turn == Turn.PLAYER1)
            currP = player1;
        else
            currP = player2;

        if (currP == Player.USER)
            System.out.print("Enter the coordinates: ");
        else
            nextMoveByComputer(currP.cm.getMove(b), currP.toString().toLowerCase());
    }

    private static void start() {
        b.newBoard();
        b.print();
        state = State.CHOOSE_MOVE;
        gameState();
    }

    private static void nextMove() {
        b.putMove(cxy);
        b.print();
        flipTurn();
        gameState();
    }

    private static void nextMoveByComputer(int move, String str) {
        b.putMove(move);
        System.out.printf("Making move level \"%s\"\n", str);
        b.print();
        flipTurn();
        gameState();
    }

    private static int checkInput(String[] input) {
        if (state == State.CHOOSE_PLAYER && (input.length == 3
                && input[0].equals("start")
                && areValidPlayers(input[1], input[2])))
            return 200;
        else if(state == State.CHOOSE_MOVE && (input.length == 2))
            return isValidMove(input[0], input[1]);
        return 400;
    }

    private static int isValidMove(String string1, String string2) {
        try {
            int x = Integer.parseInt(string1);
            int y = Integer.parseInt(string2);
            if (x < 1 || x > 3 || y < 1 || y > 3)
                return 402;
            else if (isImpossibleMove(((x - 1) * 3) + (y - 1)))
                return 404;
            cxy = ((x - 1) * 3) + (y - 1);
        } catch (NumberFormatException ne) {
            return 401;
        }
        return 201;
    }

    private static boolean areValidPlayers(String p1, String p2) {
        for (Player pl : Player.values()) {
            if (p1.equals(pl.toString().toLowerCase()))
                player1 = pl;
            if (p2.equals(pl.toString().toLowerCase()))
                player2 = pl;
        }
        return player1 != Player.NONE && player2 != Player.NONE;
    }

    private static boolean isImpossibleMove(int x) {
        return !b.isPosEmpty(x);
    }

    private static void flipTurn() {
        if (turn == Turn.PLAYER1)
            turn = Turn.PLAYER2;
        else
            turn = Turn.PLAYER1;
    }
}
