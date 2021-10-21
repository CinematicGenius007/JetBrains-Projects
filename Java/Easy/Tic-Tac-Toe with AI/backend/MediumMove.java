package tictactoe.backend;

public class MediumMove implements ComputerMove {
    @Override
    public int getMove(Board b) {
        Board bm = new Board(b.getClone(), b.getTurn());
        for (int i = 0; i < 9; i++) {
            if (bm.isPosEmpty(i)) {
                bm.setValue(i, bm.getTurn());
                if (bm.getWinner() != 'n')
                    return i;
                else
                    bm.setValue(i, '\u0000');

                bm.setValue(i, bm.getNextTurn());
                if (bm.getWinner() != 'n')
                    return i;
                else
                    bm.setValue(i, '\u0000');
            }
        }
        return new EasyMove().getMove(b);
    }
}
