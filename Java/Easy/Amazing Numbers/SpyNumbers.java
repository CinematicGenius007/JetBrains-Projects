package amazingNumbers;

import java.text.NumberFormat;
import java.util.*;


public class SpyNumbers {
	private static final Scanner scanner = new Scanner(System.in);
	private static final NumberFormat myFormat = NumberFormat.getInstance();
	private static final String[] props = {"BUZZ", "DUCK", "PALINDROMIC", "GAPFUL", "SPY", "SQUARE", "SUNNY", "EVEN", "ODD"};

	public static void main(String[] args) {
        myFormat.setGroupingUsed(true);
		print("Welcome to Amazing Numbers!\n", true);
		
		print(instructions(), true);
		
		long n = 0L;
		int range = 1;
		boolean extend = false;
		String[] prop = new String[2];
		boolean specialProp = false;
		int[] propIndex = {-1, -1};
		boolean specialPropEx = false;
		
		while (true) {
			n = 0L;
			range = 1;
			extend = false;
			prop = new String[2];
			specialProp = false;
			propIndex[0] = propIndex[1] = -1;
			specialPropEx = false;
			
			print("\nEnter a request: ", false);
			
			String[] response = scanner.nextLine().split(" ");
			print("", true);
			
			if (response.length == 0) {
				print(instructions(), true);
				continue;
			}
			
			try {
				n = Long.parseLong(response[0]);
				if (!(n >= 0L))
					throw new Exception();
			} catch (Exception e) {error1(); continue;}
			
			//Very Important exit
			if (n == 0L)
				break;
			
			if (response.length > 1) {
				try {
					extend = true;
					range = Integer.parseInt(response[1]);
					if (!(range > 0))
						throw new Exception();
				} catch (Exception e) {error2(); continue;}
			}
			
			if (response.length > 2) {
				try {
					specialProp = true;
					prop[0] = response[2];
					propIndex[0] =  arraySearch(prop[0].toUpperCase());
					if (response.length > 3) {
						specialPropEx = true;
						prop[1] = response[3];
						propIndex[1] =  arraySearch(prop[1].toUpperCase());
//						print(propIndex[0] + " " + propIndex[1], true);
						if (propIndex[0] < 0 && propIndex[1] < 0) {
							error4(prop[0], prop[1]); 
							throw new Exception();
						} else if (propIndex[0] < 0 || propIndex[1] < 0) {
							error3(propIndex[0] < 0 ? prop[0] : prop[1]);
							throw new Exception();
						} else if (propIndex[0] == 7 && propIndex[1] == 8
								|| propIndex[1] == 7 && propIndex[0] == 8
								|| propIndex[0] == 1 && propIndex[1] == 4
								|| propIndex[1] == 1 && propIndex[0] == 4
								|| propIndex[0] == 5 && propIndex[1] == 6
								|| propIndex[1] == 5 && propIndex[0] == 6) {
							error5(propIndex[0], propIndex[1]); 
							throw new Exception();
						}
					} else {
						if (propIndex[0] < 0) {
							error3(prop[0]); 
							throw new Exception();
						}
					}
					
				} catch (Exception e) {continue;}
			}
			
			
			if (!extend && !specialProp) {
				print("Properties of " + myFormat.format(n) + "\r\n"
						+ "        buzz: " + getBuzz(n) + "\r\n"
						+ "        duck: " + getDuck(n) + "\r\n"
						+ " palindromic: " + getPalindrome(n) + "\r\n"
						+ "      gapful: " + getGapful(n) + "\r\n"
						+ "         spy: " + getSpy(n) + "\r\n"
						+ "      square: " + getSquare(n) + "\r\n"
						+ "       sunny: " + getSunny(n) + "\r\n"
						+ "        even: " + getEven(n) + "\r\n"
						+ "         odd: " + getOdd(n), true);
			} else if (extend && !specialProp) {
				for (int i = 0; i < range; i++) {
					long curr = n + i;
					printPropList(curr);
				}
			} else if (extend && specialProp) {
				for (int i = 0, j = 0; i < range; j++) {
					long curr = n + j;
					if (findProp(propIndex[0], curr)) {
						printPropList(curr);
						i++;
					}
				}
			} else if (extend && specialProp && specialPropEx) {
				for (int i = 0, j = 0; i < range; j++) {
					long curr = n + j;
					if (findProp(propIndex[0], curr) && findProp(propIndex[1], curr)) {
						printPropList(curr);
						i++;
					}
				}
			}
			
			
			
			
		}
		
		print("Goodbye!\r\n"
				+ "\r\n"
				+ "Process finished with exit code 0", true);
		
//		print("  " + (getEven(1L) ? "hi" : ""), true);
	}
	
	private static String instructions() {
		return "Supported requests:\r\n"
				+ "- enter a natural number to know its properties;\r\n"
				+ "- enter two natural numbers to obtain the properties of the list:\r\n"
				+ "  * the first parameter represents a starting number;\r\n"
				+ "  * the second parameters show how many consecutive numbers are to be processed;\r\n"
				+ "- two natural numbers and two properties to search for;\r\n"
				+ "- separate the parameters with one space;\r\n"
				+ "- enter 0 to exit.";
	}
	
	private static void printPropList(long n) {
		myFormat.setGroupingUsed(true);
		System.out.printf("%16s", myFormat.format(n));
		print(" is "
				+ (getBuzz(n) ? "buzz, " : "")
				+ (getDuck(n) ? "duck, " : "")
				+ (getPalindrome(n) ? "palindromic, " : "")
				+ (getGapful(n) ? "gapful, " : "")
				+ (getSpy(n) ? "spy, " : "")
				+ (getSquare(n) ? "square, " : "")
				+ (getSunny(n) ? "sunny, " : "")
				+ (getEven(n) ? "even" : "")
				+ (getOdd(n) ? "odd" : "")
				+ "", true);
	}
	
	private static boolean findProp(int i, Long n) {
		if (i == 0) 
			return getBuzz(n);
		else if (i == 1) 
			return getDuck(n);
		else if (i == 2) 
			return getPalindrome(n);
		else if (i == 3) 
			return getGapful(n);
		else if (i == 4) 
			return getSpy(n);
		else if (i == 5) 
			return getSquare(n);
		else if (i == 6) 
			return getSunny(n);
		else if (i == 7) 
			return getEven(n);
		else if (i == 8) 
			return getOdd(n);
		else
			return false;
	}
	
	
	//Properties
	private static boolean getOdd(long n)  {return n % 2 != 0;}
	private static boolean getEven(long n)  {return n % 2 == 0;}
	private static boolean getBuzz(long n)  {return n % 7 == 0 || n % 10 == 7;}
	private static boolean getDuck(long n)  {return ("" + n).indexOf("0") >= 0;}
	private static boolean getPalindrome(long n)  {
		return ("" + n).equals(new StringBuilder("" + n).reverse().toString());
	}
	private static boolean getGapful(long n)  {
		return Long.toString(n).length() > 2 
				&& n % Long.parseLong(Long.toString(n).charAt(0) + Long.toString(n % 10L)) == 0;
	}
	private static boolean getSpy(long n) {
		long sum = 0;
		long product = 1;
		while (n > 0L) {
			sum += n % 10;
			product *= n % 10;
			n /= 10;
		}
		return sum == product;
	}
	private static boolean getSquare(long n) {
		return Math.sqrt(n) - Math.floor(Math.sqrt(n)) == 0;
	}
	private static boolean getSunny(long n) {
		return Math.sqrt(n + 1L) - Math.floor(Math.sqrt(n + 1L)) == 0;
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
	
	private static void error3(String prop) {
		print("The property [" + prop.toUpperCase() + "] is wrong.\r\n"
				+ "Available properties: Available properties: [EVEN, ODD, BUZZ, DUCK, PALINDROMIC, GAPFUL, SPY, SQUARE, SUNNY]", true);
	}
	
	private static void error4(String prop1, String prop2) {
		print("The properties [" + prop1.toUpperCase() + ", " + prop2.toUpperCase() + "] are wrong.\r\n"
				+ "Available properties: Available properties: [EVEN, ODD, BUZZ, DUCK, PALINDROMIC, GAPFUL, SPY, SQUARE, SUNNY]", true);
	}
	
	private static void error5(int prop1, int prop2) {
		print("The request contains mutually exclusive properties: [" 
				+ props[prop1] + ", " + props[prop2] + "]\r\n"
				+ "There are no numbers with these properties.", true);
	}
	
	
	private static int arraySearch(String value) {
		for (int i = 0; i < props.length; i++) {
			if (value.equals(props[i]))
				return i;
		}
		return -1;
	}

}
