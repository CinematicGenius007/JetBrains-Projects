package tictactoe.backend;

public class Board {
    private final char[] BD = new char[9];
    private char turn = 'X';

    public Board() {}
    public Board(char[] bd, char turn) {
        setTurn(turn);
        System.arraycopy(bd, 0, BD, 0, 9);
    }

    public char getTurn() {
        return turn;
    }

    public void setTurn(char turn) {
        this.turn = turn;
    }

    public char getNextTurn() {
        if (turn == 'X')
            return 'O';
        return 'X';
    }

    public void changeTurn() {
        if (turn == 'X')
            turn = 'O';
        else
            turn = 'X';
    }

    public char[] getClone() {
        char[] temp = new char[9];
        for (int i = 0; i < 9; i++)
            temp[i] = BD[i];
        return temp;
    }

    public void newBoard() {
        for (int i = 0; i < 9; i++) {
            BD[i] = '\u0000';
        }
        setTurn('X');
    }

    public boolean isPosEmpty(int x) {
        return BD[x] == '\u0000';
    }

    public void print() {
        System.out.println("---------");
        for (int i = 0; i < 3; i++) {
            System.out.print("| ");
            for (int k = 0; k < 3; k++) {
                if (BD[i * 3 + k] != '\u0000')
                    System.out.printf("%s ", BD[i * 3 + k]);
                else
                    System.out.print("  ");
            }
            System.out.println("|");
        }

        System.out.println("---------");
    }

    public void putMove(int x) {
        BD[x] = turn;
        changeTurn();
    }

    public void setValue(int x, char c) {
        BD[x] = c;
    }

    public boolean isFull() {
        for (Character c : BD)
            if (c == '\u0000')
                return false;
        return true;
    }

    public int movesLeft() {
        int count = 0;
        for (int i = 0; i < 9; i++)
            if (BD[i] == '\u0000')
                count++;
        return count;
    }

    public int getFirstEmpty() {
        for (int i = 0; i < 9; i++)
            if (BD[i] == '\u0000')
                return i;
        return -1;
    }

    public Character getWinner() {
        for (int i = 0; i < 3; i++) {
            if (BD[i * 3] == BD[i * 3 + 1]
                    && BD[i * 3 + 1] == BD[i * 3 + 2]
                    && BD[i * 3 + 2] != '\u0000') return BD[i * 3];
            else if (BD[i] == BD[i + 3]
                    && BD[i + 3] == BD[i + 6]
                    && BD[i + 6] != '\u0000') return BD[i];
        }
        if (BD[0] == BD[4]
                && BD[4] == BD[8]
                && BD[8] != '\u0000') return BD[0];
        else if (BD[2] == BD[4]
                && BD[4] == BD[6]
                && BD[6] != '\u0000') return BD[2];
        else if (isFull()) return 'D';
        return 'n';
    }
}
