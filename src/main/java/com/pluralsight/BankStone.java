package com.pluralsight;


import java.io.*;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.util.*;

public class BankStone {
    public static Scanner myScanner = new Scanner(System.in);

    public static ArrayList<Transaction> ledger = new ArrayList<>();

    public static void main(String[] args) {
        preload(); // preload the ArrayList ledger with data already existing in csv file
        home(); // start application and display home screen
    }


    public static void home() {
        boolean isRunning = true;
        while (isRunning) {
            System.out.println("""
                    HOME
                    
                    Welcome!
                    """);

            String response = askUser("""
                    Select an option below:
                    D) Make a Deposit
                    P) Make a Payment (Debit)
                    L) View Ledger
                    X) Exit
                    """);

            if (response.isEmpty()) {
                System.out.println("Response not valid. Please try again");
            } else {
                switch (response.toLowerCase()) {
                    case "d": {
                        makeTransaction("deposit");
                        break;
                    }

                    case "p": {
                        makeTransaction("charge");
                        break;
                    }

                    case "l": {
                        ledger();
                        break;
                    }

                    case "x": {
                        isRunning = false;
                        break;
                    }

                }
            }

        }
    }

    public static void makeTransaction(String type) {
        String description = askUser("Provide a description for this " + type);
        String vendor = askUser("Provide the name of the vendor");
        double amount;
        if (type.equals("deposit")) {
            amount = Math.abs(askUserDouble("How much money would you like to deposit?"));

        } else {
            amount = -Math.abs(askUserDouble("How much was the charge?"));
        }

        Transaction transaction = new Transaction(description, vendor, amount);

        ledger.add(transaction);

        storeInCSV(transaction);

    }

    public static void storeInCSV(Transaction transaction) {

        LocalDateTime dateTime = transaction.getDateTime();

        LocalDate date = dateTime.toLocalDate();

        LocalTime rawTime = dateTime.toLocalTime();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm:ss");
        String time = rawTime.format(formatter);

        String description = transaction.getDescription();
        String vendor = transaction.getVendor();
        double amount = transaction.getAmount();

        String line = date + "|" + time + "|" + description + "|" + vendor + "|" + amount;

        try (
                FileWriter fileWriter = new FileWriter("transactions.csv", true); // pass in true to enable append mode (to not overwrite the whole file)
                BufferedWriter bufferedWriter = new BufferedWriter(fileWriter);
        ) {
            bufferedWriter.newLine();
            bufferedWriter.write(line);
        } catch (IOException e) {

            System.out.println("Error writing file");

        }

    }

    public static void ledger() {
        boolean isRunning = true;
        while (isRunning) {
            String response = askUser("""
                    
                    LEDGER
                    
                    A) Display all entries
                    D) Display deposits
                    P) Display payments
                    R) Go to Reports
                    H) Home
                    
                    Please select an option:
                    """);
            switch (response.toLowerCase()) {
                case "a":
                    viewAll();
                    break;

                case "d":
                    viewDeposits();
                    break;

                case "p":
                    viewCharges();
                    break;

                case "r":
                    reports();
                    break;

                case "h":
                    isRunning = false;
                    break;
            }
        }
    }

    // method for viewing all transactions
    public static void viewAll() {
        for (Transaction transaction : ledger) {
            transaction.display();
        }
    }

    // method for viewing all deposits
    public static void viewDeposits() {
        List<Transaction> filtered = ledger.stream()
                .filter(transaction -> transaction.getAmount() > 0)
                .toList();

        for (Transaction transaction : filtered) {
            transaction.display();
        }
    }

    // method for viewing all charges/payments
    public static void viewCharges() {
        List<Transaction> filtered = ledger.stream()
                .filter(transaction -> transaction.getAmount() < 0)
                .toList();

        for (Transaction transaction : filtered) {
            transaction.display();
        }
    }

    // method for displaying the reports screen
    public static void reports() {
        boolean isRunning = true;
        while (isRunning) {
            String response = askUser("""
                    REPORTS
                    
                    1) Month To Date
                    2) Previous Month
                    3) Year To Date
                    4) Previous Year
                    5) Search by Vendor
                    0) Back
                    """);
            switch (response) {
                case "1":
                    monthToDate();
                    break;
                case "2":
                    previousMonth();
                    break;
                case "3":
                    yearToDate();
                    break;
                case "4":
                    previousYear();
                    break;
                case "5":
                    searchByVendor();
                    break;
                case "0":
                    isRunning = false;
            }
        }
    }

    // preload ArrayList
    public static void preload() {
        // initialize FileReader and Buffered Reader in try-with-resources for auto closing
        try (FileReader fileReader = new FileReader("transactions.csv");
             BufferedReader bufferedReader = new BufferedReader(fileReader);) {

            // iterate through each line of csv file to parse data
            String line = bufferedReader.readLine(); // this is just the header line, not needed

            while ((line = bufferedReader.readLine()) != null) {
                Transaction transaction = getTransaction(line);

                // add each new object to ledger ArrayList
                ledger.add(transaction);
            }

        } catch (IOException e) {
            System.out.println("Error reading csv file");
        }
    }

    // convert lines of text from csv into Transaction objects and return the object
    private static Transaction getTransaction(String line) {
        String[] parts = line.split("\\|");
        LocalDate date = LocalDate.parse(parts[0]);
        LocalTime time = LocalTime.parse(parts[1]);
        LocalDateTime dateTime = LocalDateTime.of(date, time);

        String description = parts[2];

        String vendor = parts[3];

        double amount = Double.parseDouble(parts[4]);

        // create new objects with overloaded constructor function, passing in data
        return new Transaction(dateTime, description, vendor, amount);
    }

    public static String askUser(String question) {
        try {
            System.out.println(question);
            return myScanner.nextLine().trim();
        } catch (Exception e) { // usually only catches if there is something wrong with the scanner
            System.out.println("Error prompting for user input");
            return "";
        }
    }

    public static double askUserDouble(String question) {
        while (true) { // keep looping indefinitely until we get a correct input
            try {
                System.out.println(question);
                double response = myScanner.nextDouble();
                myScanner.nextLine(); // eat the line
                return response; // until a return statement is reached (return breaks the while loop)
            } catch (InputMismatchException e) {
                System.out.println("Incorrect input type. Try again!");
                myScanner.nextLine(); // clear the buffer if wrong input type
            } catch (NoSuchElementException | IllegalStateException e) {
                e.printStackTrace();
                System.out.println("Something went wrong. Try again!");
            }
        }
    }

    public static int askUserInt(String question) {
        while (true) { // keep looping indefinitely until we get a correct input
            try {
                System.out.println(question);
                int response = myScanner.nextInt();
                myScanner.nextLine(); // eat the line
                return response; // until a return statement is reached (return breaks the while loop)
            } catch (InputMismatchException e) {
                System.out.println("Incorrect input type. Try again!");
                myScanner.nextLine(); // clear the buffer if wrong input type
            } catch (NoSuchElementException | IllegalStateException e) {
                e.printStackTrace();
                System.out.println("Something went wrong. Try again!");
            }
        }
    }

    public static void monthToDate() {
        List<Transaction> filtered = ledger.stream()
                .filter(transaction ->
                                transaction.getDateTime().getMonth() == LocalDateTime.now().getMonth() &&
                                transaction.getDateTime().getYear() == LocalDateTime.now().getYear())
                .toList();

        for (Transaction transaction : filtered) {
            transaction.display();
        }
    }

    public static void previousMonth() {
        List<Transaction> filtered = ledger.stream()
                .filter(transaction ->
                        transaction.getDateTime().getMonth() == LocalDateTime.now().minusMonths(1).getMonth() &&
                                transaction.getDateTime().getYear() == LocalDateTime.now().getYear())
                .toList();

        for (Transaction transaction : filtered) {
            transaction.display();
        }
    }

    public static void yearToDate() {
        List<Transaction> filtered = ledger.stream()
                .filter(transaction -> transaction.getDateTime().getYear() == LocalDateTime.now().getYear())
                .toList();

        for (Transaction transaction : filtered) {
            transaction.display();
        }
    }

    public static void previousYear() {
        List<Transaction> filtered = ledger.stream()
                .filter(transaction -> transaction.getDateTime().getYear() == LocalDateTime.now().minusYears(1).getYear())
                .toList();

        for (Transaction transaction : filtered) {
            transaction.display();
        }
    }

    public static void searchByVendor() {
        String vendor = askUser("What vendor would you like to search for in transactions?");
        List<Transaction> filtered = ledger.stream()
                .filter(transaction -> transaction.getVendor().equalsIgnoreCase(vendor))
                .toList();

        for (Transaction transaction : filtered) {
            transaction.display();
        }
    }

    public static void customSearch() {
        String startDateStr = null;
        String endDateStr = null;
        LocalDate startDate = null;
        LocalDate endDate = null;
        String vendor = null;
        String description = null;
        String type = null;
        Double startAmount = null;
        Double endAmount = null;

        while (true) {
            String question = String.format("""
                    CUSTOM REPORT
                    
                    Please select a filter below to change:
                    
                    1) Start Date:             %s
                    2) End Date:               %s
                    3) Vendor:                 %s
                    4) Description:            %s
                    5) Type (Deposit/Payment): %s
                    6) Start Amount:           %.2f
                    7) End Amount:             %.2f
                    
                    G) Generate Report
                    X) Exit to Reports
                    
                    """, startDateStr, endDateStr, vendor, description, type, startAmount, endAmount);

            String response = askUser(question);

            switch (response.toLowerCase()) {

                case "1":
                    String update1 = askUser("What start date would you like to search by? (Leave blank to skip)");
                    if (!update1.isEmpty()) {
                        startDate = LocalDate.parse(update1);
                        startDateStr = startDate.toString();
                    }
                    break;

                case "2":
                    String update2 = askUser("What end date would you like to search by? (Leave blank to skip)");
                    if (!update2.isEmpty()) {
                        endDate = LocalDate.parse(update2);
                        endDateStr = endDate.toString();
                    }
                    break;

                case "3":
                    String update3 = askUser("What vendor would you like to search by? (Leave blank to skip)");
                    if (!update3.isEmpty()) {
                        vendor = update3;
                    }
                    break;

                case "4":
                    String update4 = askUser("What description would you like to search by? (Leave blank to skip)");
                    if (!update4.isEmpty()) {
                        description = update4;
                    }
                    break;

                case "5":
                    while (true) {
                        String update5 = askUser("What transaction type would you like to search by? (Leave blank to skip)\nD) Deposits or P)Payments/Charges ");
                        if (!update5.isEmpty()) {
                            if (update5.equalsIgnoreCase("d")) {
                                type = "deposits";
                                break;
                            }
                            else if (update5.equalsIgnoreCase("p")) {
                                type = "payments";
                                break;
                            }
                            else {
                                System.out.println("Improper input!");
                                continue;
                            }
                        }
                        break;

                    }

                case "6":
                    String update6 = askUser("What minimum amount would you like to filter for? (Leave blank to skip)\nNote: Negative values are payments");
                    if (!update6.isEmpty()) {
                        startAmount = Double.parseDouble(update6);
                    }
                    break;

                case "7":
                    String update7 = askUser("What maximum amount would you like to filter for? (Leave blank to skip)\nNote: Negative values are payments");
                    if (!update7.isEmpty()) {
                        endAmount = Double.parseDouble(update7);
                    }
                    break;

                case "g":
                    customFilter(
                            startDateStr,
                            endDateStr,
                            startDate,
                            endDate,
                            vendor,
                            description,
                            type,
                            startAmount,
                            endAmount);


                case "x":

            }
        }


    }
}
