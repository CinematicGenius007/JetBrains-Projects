package carsharing;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.*;

public class Main {

    private static final String FOLDER_PATH = "src/carsharing/db/";
    private static final Scanner INPUT = new Scanner(System.in);
    private static int companyI = 1;
    private static int csI = 1;
    private static String database = "carsharing";
    static Connection conn;

    public static void main(String[] args) {
        if (args.length >= 2 && args[0].equals("-databaseFileName"))
            database = args[1];


        try {
            Class.forName("org.h2.Driver");

            conn = DriverManager.getConnection("jdbc:h2:./"
                    + FOLDER_PATH
                    + database);
            conn.setAutoCommit(true);
            Statement stmt = conn.createStatement();
            stmt.executeUpdate("CREATE TABLE IF NOT EXISTS COMPANY(" +
                    "ID INTEGER PRIMARY KEY, " +
                    "NAME VARCHAR(20) UNIQUE NOT NULL" +
                    ");");

            stmt.executeUpdate("CREATE TABLE IF NOT EXISTS CAR(" +
                    "ID INTEGER PRIMARY KEY AUTO_INCREMENT, " +
                    "NAME VARCHAR(20) UNIQUE NOT NULL, " +
                    "COMPANY_ID INTEGER NOT NULL, " +
                    "FOREIGN KEY (COMPANY_ID) REFERENCES COMPANY (ID)" +
                    ");");

            stmt.executeUpdate("CREATE TABLE IF NOT EXISTS CUSTOMER(" +
                    "ID INTEGER PRIMARY KEY AUTO_INCREMENT, " +
                    "NAME VARCHAR(20) UNIQUE NOT NULL, " +
                    "RENTED_CAR_ID INTEGER, " +
                    "FOREIGN KEY (RENTED_CAR_ID) REFERENCES CAR (ID)" +
                    ");");


            while (true) {
                System.out.println("1. Log in as a manager\n" +
                        "2. Log in as a customer\n" +
                        "3. Create a customer\n" +
                        "0. Exit");
                int choice = Integer.parseInt(INPUT.nextLine());
                if (choice == 0)
                    break;
                else if (choice == 1)
                    companyMenu();
                else if (choice == 2)
                    printCustomerList();
                else if (choice == 3)
                    addToCustomer();

                System.out.println();
            }



        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void companyMenu() {
        while (true) {
            System.out.println("\n" +
                    "1. Company list\n" +
                    "2. Create a company\n" +
                    "0. Back");
            int choice = Integer.parseInt(INPUT.nextLine());
            if (choice == 0)
                break;
            else if (choice == 1)
                printCompanyList();
            else if (choice == 2)
                addToCompany();
        }
    }

    static void carMenu(int companyID) {
        while (true) {
            System.out.println("1. Car list\n" +
                    "2. Create a car\n" +
                    "0. Back");
            int choice = Integer.parseInt(INPUT.nextLine());
            if (choice == 0)
                break;
            else if (choice == 1)
                printCarList(companyID);
            else if (choice == 2)
                addToCar(companyID);
        }
    }

    static void customerMenu(int customerID) {
        while (true) {
            System.out.println("\n" +
                    "1. Rent a car\n" +
                    "2. Return a rented car\n" +
                    "3. My rented car\n" +
                    "0. Back");
            int choice = Integer.parseInt(INPUT.nextLine());
            if (choice == 0)
                break;
            else if (choice == 1)
                rentCar(customerID);
            else if (choice == 2)
                returnRentedCar(customerID);
            else if (choice == 3)
                printRentedCars(customerID);
        }
    }

    static void rentCar(int customerID) {
        try {
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery("SELECT * FROM CUSTOMER" +
                    " WHERE ID = " +
                    customerID + ";");

            rs.next();
            int id = rs.getInt("RENTED_CAR_ID");

            ResultSet rss = stmt.executeQuery("SELECT * FROM CUSTOMER ORDER BY ID;");
            List<Integer> rentedCars = new ArrayList<>();
            while (rss.next()) {
                rentedCars.add(rss.getInt("RENTED_CAR_ID"));
            }


            if (id != 0) {
                System.out.println("You've already rented a car!");
            } else {
                ResultSet rst = stmt.executeQuery("SELECT * FROM COMPANY ORDER BY ID;");

                Map<Integer, String> list = new TreeMap<>();
                while (rst.next()) {
                    list.put(rst.getInt("ID"),
                            rst.getString("NAME"));
                }

                if (list.isEmpty()) {
                    System.out.println("The company list is empty!");
                } else {

                    System.out.println("Choose the company:");
                    list.forEach((k, v) -> System.out.println(k + ". " + v));
                    System.out.println("0. Back");

                    int choice = Integer.parseInt(INPUT.nextLine());
                    if (choice != 0) {
                        ResultSet rst2 = stmt.executeQuery("SELECT * FROM CAR " +
                                "WHERE COMPANY_ID = " + choice +
                                " ORDER BY ID;");


                        List<Integer> ids = new ArrayList<>();
                        List<String> names = new ArrayList<>();
                        while (rst2.next()) {
                            int idd = rst2.getInt("ID");
                            if (!rentedCars.contains(idd)) {
                                ids.add(idd);
                                names.add(rst2.getString("NAME"));
                            }
                        }

                        if (ids.isEmpty()) {
                            System.out.println("The car list is empty!");
                        } else {

                            System.out.println("Choose a car:");
                            for (int i = 0; i < names.size(); i++) {
                                System.out.println((i + 1) + ". " + names.get(i));
                            }
                            System.out.println("0. Back");

                            int choice1 = Integer.parseInt(INPUT.nextLine());
                            if (choice1 != 0) {
                                stmt.executeUpdate("UPDATE CUSTOMER " +
                                        "SET RENTED_CAR_ID = " + ids.get(choice1 - 1) + ";");
                                System.out.println("You rented '" + names.get(choice1 - 1) + "'");

                            }
                        }
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    static void returnRentedCar(int customerID) {
        try {
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery("SELECT * FROM CUSTOMER " +
                    "WHERE ID = " + customerID +
                    ";");
            rs.next();
            int n = rs.getInt("RENTED_CAR_ID");


            if (n == 0) {
                System.out.println("You didn't rent a car!\n");
            } else {
                stmt.executeUpdate("UPDATE CUSTOMER SET RENTED_CAR_ID = NULL" +
                        " WHERE ID = " +
                        customerID + ";");
                System.out.println("You've returned a rented car!\n");

            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    static void printRentedCars(int customerID) {
        try {
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery("SELECT * FROM CUSTOMER" +
                    " WHERE ID = " +
                    customerID + ";");

            rs.next();
            int id = rs.getInt("RENTED_CAR_ID");
            if (id == 0) {
                System.out.println("You didn't rent a car!");
            } else {
                System.out.println("Your rented car:");
                ResultSet rs1 = stmt.executeQuery("SELECT * FROM CAR WHERE ID = " +
                        "" + id + ";");
                rs1.next();
                String car = rs1.getString("NAME");
                int carID = rs1.getInt("COMPANY_ID");
                System.out.println(car + "\nCompany:");
                ResultSet rs2 = stmt.executeQuery("SELECT * FROM COMPANY WHERE ID = " +
                        "" + carID + ";");
                rs2.next();
                String compName = rs2.getString("NAME");
                System.out.println(compName + "\n");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    static void addToCustomer() {
        System.out.println("\nEnter the customer name:");
        try {
            Statement stmt = conn.createStatement();
            String input = INPUT.nextLine().trim();
            int count = stmt.executeUpdate(String.format("INSERT INTO CUSTOMER (ID, NAME) " +
                    "VALUES (%d, '%s');", csI++, input));
            if (count != 0) {
                System.out.println("The customer was added!");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    static void addToCar(int companyID) {
        System.out.println("\nEnter the car name:");
        try {
            Statement stmt = conn.createStatement();
            String input = INPUT.nextLine().trim();
            int count = stmt.executeUpdate(String.format("INSERT INTO CAR (NAME, COMPANY_ID) " +
                    "VALUES ('%s', %d);", input, companyID));
            if (count != 0) {
                System.out.println("The car was added!");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    static void printCarList(int companyID) {
        try {
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery("SELECT * FROM CAR WHERE COMPANY_ID = " +
                    companyID + ";");
            Map<Integer, String> carList = new TreeMap<>();
            while (rs.next()) {
                carList.put(rs.getInt("ID"),
                        rs.getString("NAME"));
            }

            if (carList.isEmpty()) {
                System.out.println("The car list is empty!");
            } else {
                System.out.println("Car list:");
                int i = 1;
                for (String str : carList.values()) {
                    System.out.println(i++ + ". " + str);
                }
                System.out.println();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    static void addToCompany() {
        System.out.println("\nEnter the company name:");
        try {
            Statement stmt = conn.createStatement();
            String input = INPUT.nextLine().trim();
            int count = stmt.executeUpdate(String.format("INSERT INTO COMPANY " +
                    "VALUES (%d, '%s');", companyI++, input));
            if (count != 0) {
                System.out.println("The company was created!");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    static void printCustomerList() {
        try {
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery("SELECT * FROM CUSTOMER;");
            Map<Integer, String> carList = new TreeMap<>();
            while (rs.next()) {
                carList.put(rs.getInt("ID"),
                        rs.getString("NAME"));
            }

            if (carList.isEmpty()) {
                System.out.println("The customer list is empty!");
            } else {
                System.out.println("Customer list:");
                carList.forEach((k, v) -> System.out.println(k + ". " + v));
                System.out.println("0. Back");
                int choice = Integer.parseInt(INPUT.nextLine());
                customerMenu(choice);
                System.out.println();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    static void printCompanyList() {
        System.out.println();
        try {
            Statement stmt = conn.createStatement();
            ResultSet rst = stmt.executeQuery("SELECT * FROM COMPANY ORDER BY ID;");

            Map<Integer, String> list = new TreeMap<>();
            while (rst.next()) {
                list.put(rst.getInt("ID"),
                        rst.getString("NAME"));
            }

            if (list.isEmpty()) {
                System.out.println("The company list is empty!");
            } else {

                System.out.println("Choose the company:");
                list.forEach((k, v) -> System.out.println(k + ". " + v));
                System.out.println("0. Back");

                int choice = Integer.parseInt(INPUT.nextLine());
                if (choice != 0) {
                    System.out.println("'" + list.get(choice) + "' company");
                    carMenu(choice);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}