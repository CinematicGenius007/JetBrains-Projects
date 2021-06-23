package amazingNumbers;

import java.util.Arrays;
import java.util.Scanner;

public class PalindromicNumbers {
	private static final Scanner scan = new Scanner(System.in);
	
	public static void main(String[] args) {
        System.out.printf("%s%n%n%s%n%s%n%s%n%n", 
            "Welcome to Amazing Numbers!",
            "Supported requests:",
            "- enter a natural number to know its properties;",
            "- enter 0 to exit.");
        
        
        while (true) {
            System.out.print("Enter a request: ");
            String[] input = scan.nextLine().split(" ");
            long no = 0;
            try {
            	System.out.println(Arrays.toString(input));
            	System.out.printf("%s%s%n", "", "12");
            	no = Long.parseLong(input.length > 1 ? input[1] : input[0]);
            } catch (Exception e) {
            	no = -1L;
            }
            
            System.out.println();
            if (no == 0) {
                System.out.println("Goodbye!");
                return;
            } else if (!(no > 0)) {
                System.out.println("The first parameter should be a natural number or zero.\n");
                continue;
            } 
            
            System.out.printf("Properties of %,d%n", no);
            
            System.out.printf("%12s: %b%n", "even", no % 2 == 0);
            System.out.printf("%12s: %b%n", "odd", no % 2 != 0);
            System.out.printf("%12s: %b%n", "buzz", no % 7 == 0 || no % 10 == 7);
            System.out.printf("%12s: %b%n", "duck", getDuck(no));
            System.out.printf("%12s: %b%n", "palindromic", getPalindrome(String.valueOf(no)));
            
            System.out.println();
        }
        
        
    }
    
    private static boolean getPalindrome(String n) {
        return n.equals(new StringBuilder(n).reverse().toString());
    }
    
    private static boolean getDuck(long n) {
        while (n > 0) {
            if (n % 10L == 0) 
                return true;
            n /= 10;
        }
        return false;
    }
}
