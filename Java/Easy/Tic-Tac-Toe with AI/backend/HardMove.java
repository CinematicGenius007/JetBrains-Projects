package tictactoe.backend;

import java.util.*;

public class HardMove implements ComputerMove {

    char max = 'X';
    char min = 'O';

    @Override
    public int getMove(Board b) {
        Board bh = new Board(b.getClone(), b.getTurn());
        max = b.getTurn();
        min = b.getNextTurn();
        return minimax(bh, bh.getFirstEmpty(), true).getKey();
    }

    private Map.Entry<Integer, Long> minimax(Board b, int depth, boolean isMax) {
        char winner = b.getWinner();
        if (winner == max)
            return new AbstractMap.SimpleEntry<>(depth, 10L);
        else if (winner == min)
            return new AbstractMap.SimpleEntry<>(depth, -10L);
        else if (winner == 'D')
            return new AbstractMap.SimpleEntry<>(depth, 0L);

        Map<Integer, Long> bestMove = new HashMap<>();

        for (int i = 0; i < 9; i++) {
            if (b.isPosEmpty(i)) {
                b.setValue(i, isMax ? max : min);
                Map.Entry<Integer, Long> temp =
                        new AbstractMap.SimpleEntry<>(i,
                                minimax(b, i, !isMax).getValue() - 1L);
                bestMove.put(temp.getKey(), temp.getValue());
                b.setValue(i, '\u0000');
            }
        }

        return Collections.max(bestMove.entrySet(), new Comparator<Map.Entry<Integer, Long>>() {
            public int compare(Map.Entry<Integer, Long> e1, Map.Entry<Integer, Long> e2) {
                return isMax ? e1.getValue().compareTo(e2.getValue())
                        : e2.getValue().compareTo(e1.getValue());
            }
        });
    }
}
