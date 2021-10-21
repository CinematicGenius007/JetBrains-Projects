package tictactoe.backend;

import java.util.Random;

public class EasyMove implements ComputerMove {
    @Override
    public int getMove(Board b) {
        Random r = new Random();
        while (true) {
            int x = r.nextInt(9);
            if (b.isPosEmpty(x))
                return x;
        }
    }
}
