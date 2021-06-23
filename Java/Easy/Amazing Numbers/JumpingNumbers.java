package amazingNumbers;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;

public class JumpingNumbers {
	private static final Scanner scanner = new Scanner(System.in);
	private static final NumberFormat myFormat = NumberFormat.getInstance();
	private static final Class<JumpingNumbers> cl = JumpingNumbers.class;

	
	public static void main(String[] args) throws NoSuchMethodException, SecurityException, IllegalAccessException, IllegalArgumentException, InvocationTargetException {
		//Greetings
		print("Welcome to Amazing Numbers!\n", true);
		
		print(instructions(), true);
		
		while (true) {
			long n = 0L;
			int range = 1;
			List<Property> list = new ArrayList<>();
			
			print("\nEnter a request: ", false);
			
			String[] response = scanner.nextLine().split(" ");
			print("", true);
			
			if (response.length == 0) {
				print(instructions(), true); 
				continue;
			}
			
			if (response[0].charAt(0) == '0') break;
			
			if ((n = checkNumber(response[0])) < 0L) continue;
			
			if (response.length > 1 
					&& (range = checkRange(response[1])) < 0) continue;
			
			if (response.length > 2
					&& (list = checkProps(response)) == null) continue;
			
			if (range == 1) singleProp(n);
			else if (list.size() == 0) for (int i = 0; i < range; i++) printPropList(n + i);
			else if (list.size() > 0 && range > -1) {
				for (int i = 0, j = 0; i < range; j++) {
					boolean isValid = true;
					for (int k = 0; k < list.size(); k++) {
						Method m = cl.getDeclaredMethod(list.get(k).getName(), long.class);
						m.setAccessible(true);
						isValid = isValid && (boolean) m.invoke(null, n + j);
						if (!isValid) break;
					}
					if (isValid && ++i >= 0) printPropList(n + j);
				}
			}
			
		}
		
		print("Goodbye!\r\n"
				+ "\r\n"
				+ "Process finished with exit code 0", true);
		
	}
	
	private static long checkNumber(String no) {
		long n = -1L;
		try {
			n = Long.parseLong(no);
			if (n < 0L) throw new Exception();
		} catch (Exception e) {
			n = -1L;
			error1();
		}
		return n;
	}
	
	private static int checkRange(String no) {
		int n = -1;
		try {
			n = Integer.parseInt(no);
			if (n < 0L) throw new Exception();
		} catch (Exception e) {
			n = -1;
			error2();
		}
		return n;
	}
	
	private static List<Property> checkProps(String[] res) {
		List<Property> list = new ArrayList<>();
		List<String> errors = new ArrayList<>();
		for (int i = 2; i < res.length; i++) {
			Property p = Property.isProp(res[i].toLowerCase());
			if (p == null) {
				errors.add(res[i].toUpperCase());
				list = null;
			} else if (list != null && p != null) {
				list.add(p);
			}
		}
		if (list == null || errors.size() > 0) error4(errors);
		if (list != null && errors.size() == 0) {
			if (list.contains(Property.EVEN) && list.contains(Property.ODD)) {
				Property p1 = list.indexOf(Property.EVEN) > list.indexOf(Property.ODD) ? Property.ODD : Property.EVEN;
				Property p2 = p1 == Property.EVEN ? Property.ODD : Property.EVEN;
				list = null;
				error6(p1, p2);
			} else if (list.contains(Property.DUCK) && list.contains(Property.SPY)) {
				Property p1 = list.indexOf(Property.DUCK) > list.indexOf(Property.SPY) ? Property.SPY : Property.DUCK;
				Property p2 = p1 == Property.DUCK ? Property.SPY : Property.DUCK;
				list = null; 
				error6(p1, p2);
			} else if (list.contains(Property.SQUARE) && list.contains(Property.SUNNY)) {
				Property p1 = list.indexOf(Property.SQUARE) > list.indexOf(Property.SUNNY) ? Property.SUNNY : Property.SQUARE;
				Property p2 = p1 == Property.SQUARE ? Property.SUNNY : Property.SQUARE;
				list = null; 
				error6(p1, p2);
			}
		}
		return list;
	}
	
	private static void singleProp(long n) {
		myFormat.setGroupingUsed(true);
		print("Properties of " + myFormat.format(n) + "\r\n"
				+ "        buzz: " + isBuzz(n) + "\r\n"
				+ "        duck: " + isDuck(n) + "\r\n"
				+ " palindromic: " + isPalindrome(n) + "\r\n"
				+ "      gapful: " + isGapful(n) + "\r\n"
				+ "         spy: " + isSpy(n) + "\r\n"
				+ "      square: " + isSquare(n) + "\r\n"
				+ "       sunny: " + isSunny(n) + "\r\n"
				+ "     jumping: " + isJumping(n) + "\r\n"
				+ "        even: " + isEven(n) + "\r\n"
				+ "         odd: " + isOdd(n), true);
	}
	
	private static String instructions() {
		return "Supported requests:\r\n"
				+ "- enter a natural number to know its properties;\r\n"
				+ "- enter two natural numbers to obtain the properties of the list:\r\n"
				+ "  * the first parameter represents a starting number;\r\n"
				+ "  * the second parameter shows how many consecutive numbers are to be processed;\r\n"
				+ "- two natural numbers and properties to search for;\r\n"
				+ "- a property preceded by minus must not be present in numbers;\r\n"
				+ "- separate the parameters with one space;\r\n"
				+ "- enter 0 to exit.";
	}
	
	private static void printPropList(long n) {
		myFormat.setGroupingUsed(true);
		System.out.printf("%16s", myFormat.format(n));
		print(" is "
				+ (isBuzz(n) ? "buzz, " : "")
				+ (isDuck(n) ? "duck, " : "")
				+ (isPalindrome(n) ? "palindromic, " : "")
				+ (isGapful(n) ? "gapful, " : "")
				+ (isSpy(n) ? "spy, " : "")
				+ (isSquare(n) ? "square, " : "")
				+ (isSunny(n) ? "sunny, " : "")
				+ (isJumping(n) ? "jumping, " : "")
				+ (isEven(n) ? "even" : "")
				+ (isOdd(n) ? "odd" : "")
				+ "", true);
	}
	
	//Properties
	private static boolean isOdd(long n)  {return n % 2 != 0;}
	private static boolean isEven(long n)  {return n % 2 == 0;}
	private static boolean isBuzz(long n)  {return n % 7 == 0 || n % 10 == 7;}
	private static boolean isDuck(long n)  {return ("" + n).indexOf("0") >= 0;}
	private static boolean isPalindrome(long n)  {
		return ("" + n).equals(new StringBuilder("" + n).reverse().toString());
	}
	private static boolean isGapful(long n)  {
		return Long.toString(n).length() > 2 
				&& n % Long.parseLong(Long.toString(n).charAt(0) + Long.toString(n % 10L)) == 0;
	}
	private static boolean isSpy(long n) {
		long sum = 0;
		long product = 1;
		while (n > 0L) {
			sum += n % 10;
			product *= n % 10;
			n /= 10;
		}
		return sum == product;
	}
	private static boolean isSquare(long n) {
		return Math.sqrt(n) - Math.floor(Math.sqrt(n)) == 0;
	}
	private static boolean isSunny(long n) {
		return Math.sqrt(n + 1L) - Math.floor(Math.sqrt(n + 1L)) == 0;
	}
	private static boolean isJumping(long n) {
		while (n > 10) {
			if (Math.abs((n % 10) - ((n / 10) % 10)) != 1) {
				return false;
			}
			n /= 10;
		}
		return true;
	}
	
	
	private static void print(String str, boolean newLine) {
		if (str != null) {
			if (newLine)
				System.out.println(str);
			else 
				System.out.print(str);
		} else if (newLine) {
			System.out.println();
		}
	}
	
	private static void error1() {
		print("The first parameter should be a natural number or zero.", true);
	}
	
	private static void error2() {
		print("The second parameter should be a natural number.", true);
	}
	
	private static void error4(List<String> errors) {
		print("The propert" + (errors.size() > 1 ? "ies " : "y ")
				+ errors
				+ (errors.size() > 1 ? " are " : " is ")
				+ "wrong.\r\n"
				+ "Available properties: "
				+ Arrays.toString(Property.values()), true);
	}
	
	private static void error6(Property p1, Property p2) {
		print("The request contains mutually exclusive properties: [" 
				+ p1.name() + ", " + p2.name() + "]\r\n"
				+ "There are no numbers with these properties.", true);
	}
	

}


enum Property {
	EVEN("getEven"), 
	ODD("getOdd"), 
	BUZZ("getBuzz"), 
	DUCK("getDuck"), 
	PALINDROMIC("getPalindrome"), 
	GAPFUL("getGapful"), 
	SPY("getSpy"), 
	SQUARE("getSquare"), 
	SUNNY("getSunny"), 
	JUMPING("getJumping");
	
	private final String name;
//	private boolean val;
	
	Property(String name) {
		this.name = name;
//		this.val = val;
	}
	
	public String getName() {
		return this.name;
	}
	
//	public boolean getVal() {
//		return this.val;
//	}
	
//	public void setVal(boolean val) {
//		this.val = val;
//	}
	
	public static Property isProp(String s) {
		switch (s) {
		case "even": 		return EVEN;
		case "odd": 		return ODD;
		case "buzz": 		return BUZZ;
		case "duck": 		return DUCK;
		case "palindromic": return PALINDROMIC;
		case "gapful": 		return GAPFUL;
		case "spy": 		return SPY;
		case "square": 		return SQUARE;
		case "sunny": 		return SUNNY;
		case "jumping": 	return JUMPING;
		default: 			return null;
		}
	}
	
}


