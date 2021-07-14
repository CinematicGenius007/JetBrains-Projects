package coffeMaker;

import java.util.Scanner;

public class Main {
	static final Scanner inp = new Scanner(System.in);
	static int W = 400;
	static int M = 540;
	static int B = 120;
	static int C = 9;
	static int D = 550;
	
	public static void main(String[] args) {
		boolean end = false;
		while (!end) {
			System.out.println("Write action (buy, fill, take, remaining, exit):");
			String res = inp.next();
			
			switch (res) {
				case "buy":
					buy();
					break;
				case "fill":
					fill();
					break;
				case "take":
					take();
					break;
				case "remaining":
					pd();
					break;
				case "exit":
					end = true;
					break;
				default:
					break;
			}
		}
	}
	
	static void buy() {
		System.out.println("\nWhat do you want to buy? 1 - espresso, "
				+ "2 - latte, 3 - cappuccino, back - to main menu:");
		String _input = inp.next().trim();
		int input = 0;
		if ("back".equals(_input)) {
			System.out.println();
			return;
		} else {
			input = Integer.parseInt(_input);
		}
		String[] available = {"true"};
		switch (input) {
			case 1:
				available = check(new int[] {250, 0, 16, 1, 4});
				break;
			case 2:
				available = check(new int[] {350, 75, 20, 1, 7});
				break;
			case 3:
				available = check(new int[] {200, 100, 12, 1, 6});
				break;
			default:
				break;
		}
		
		if (available[0].contains("true")) {
			System.out.println("I have enough resources, making you a coffee!\n");
		} else if (available[0].contains("false")) {
			System.out.printf("Sorry, not enough %s!%n%n", available[1]);
		}
		
	}
	
	static void fill() {
		System.out.println("\nWrite how many ml of water you want to add:");
		W += inp.nextInt();
		System.out.println("Write how many ml of milk you want to add:");
		M += inp.nextInt();
		System.out.println("Write how many grams of coffee beans you want to add:");
		B += inp.nextInt();
		System.out.println("Write how many disposable cups of coffee you want to add:");
		C += inp.nextInt();
		System.out.println();
	}
	
	static void take() {
		System.out.printf("%nI gave you $%d%n%n", D);
		D = 0;
	}
	
	static String[] check(int[] ingredient) {
		if (ingredient[0] > W) {
			return new String[] {"false", "water"};
		} else if (ingredient[1] > M) {
			return new String[] {"false", "milk"};
		} else if (ingredient[2] > B) {
			return new String[] {"false", "coffee beans"};
		} else if (ingredient[3] > C) {
			return new String[] {"false", "cups"};
		} 
		
		makeChange(ingredient);
		return new String[] {"true"};
	}
	
	static void makeChange(int[] ing) {
		W -= ing[0];
		M -= ing[1];
		B -= ing[2];
		C -= ing[3];
		D += ing[4];
	}
	
	static void pd() {
		System.out.println("\nThe coffee machine has:\r\n"
				+ W + " ml of water\r\n"
				+ M + " ml of milk\r\n"
				+ B + " g of coffee beans\r\n"
				+ C + " disposable cups\r\n"
				+ "$" + D + " of money\n");
	}

}
