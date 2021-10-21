package tictactoe;

import java.util.*;

public class RobotMove {
    char[] tb;
    char max = 'X';
    char min = 'O';


    public int getMove(Board b) {
        Board bh = new Board(b.getClone(), b.getTurn());
        max = b.getTurn();
        min = b.getNextTurn();
        tb = bh.getClone();
        return minimax(bh, true).getKey();
    }


    private Map.Entry<Integer, Long> minimax(Board b, boolean isMax) {
        char winner = b.getWinner();
        if (winner == max)
            return new AbstractMap.SimpleEntry<>(0, 10L);
        else if (winner == min)
            return new AbstractMap.SimpleEntry<>(0, -10L);
        else if (winner == 'D')
            return new AbstractMap.SimpleEntry<>(0, 0L);

        Map<Integer, Long> bestMove = new HashMap<>();

        for (int i = 0; i < 9; i++) {
            if (b.isPosEmpty(i)) {
                b.setValue(i, isMax ? max : min);
                Map.Entry<Integer, Long> temp =
                        new AbstractMap.SimpleEntry<>(i,
                                minimax(b, !isMax).getValue() - 1L);
                bestMove.put(temp.getKey(), temp.getValue());
                b.setValue(i, '\u0000');
            }
        }

        return Collections.max(bestMove.entrySet(), (e1, e2) -> isMax ? e1.getValue().compareTo(e2.getValue())
                : e2.getValue().compareTo(e1.getValue()));
    }
}
