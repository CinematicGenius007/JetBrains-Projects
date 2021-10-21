package tictactoe;

public class GameInterface {
    private final Board b;

    public GameInterface() {
        b = new Board();
    }

    public char TakeTurn(int i) {
        char curr = b.getTurn();
        b.putMove(i);
        return curr;
    }

    public String checkForWinner() {
        char winner;
        if ((winner = b.getWinner()) != 'n')
            if (winner == 'D')
                return "Draw";
            else
                return "" + winner;
        return "";
    }

    public void resetBoard() {
        b.newBoard();
    }
    public Board getBoard() { return b; }
    public Character getTurn() { return b.getTurn(); }
}
