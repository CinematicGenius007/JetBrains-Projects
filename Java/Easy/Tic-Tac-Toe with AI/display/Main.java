package tictactoe.display;

import java.util.Scanner;
public class Main {
    private static final Scanner scanner = new Scanner(System.in);

    public static void main(String[] args) {
        System.out.print("Input command: ");
        while (true) {
            String[] input = scanner.nextLine().split("\\s+");
            if (input[0].equals("exit"))
                break;
            else
                GameInterface.takeInput(input);
        }
    }
}
