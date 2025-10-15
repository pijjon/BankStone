package com.pluralsight;


import java.io.*;
import java.time.LocalDateTime;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.InputMismatchException;
import java.util.NoSuchElementException;
import java.util.Scanner;

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

            if (response == null || response.isEmpty()) {
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
                        viewLedger();
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

        }
        else {
            amount = -Math.abs(askUserDouble("How much was the charge?"));
        }

        Transaction transaction = new Transaction(description, vendor, amount);

        ledger.add(transaction);

        storeInCSV(transaction);

    }

    public static void storeInCSV (Transaction transaction) {

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
        }
        catch (IOException e) {

            System.out.println("Error writing file");

        }

    }

    public static void viewLedger() {

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

        }
        catch (IOException e) {
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
            return myScanner.nextLine();
        } catch (Exception e) { // usually only catches if there is something wrong with the scanner
            System.out.println("Error prompting for user input");
            return "";
        }
    }

    public static double askUserDouble(String question) {
        while (true) { // keep looping indefinitely
            try {
                System.out.println(question);
                double response = myScanner.nextDouble();
                myScanner.nextLine(); // eat the line
                return response; // until a return statement is reached (return breaks the while loop)
            } catch (InputMismatchException e) {
                System.out.println("Incorrect input type. Try again!");
            } catch (NoSuchElementException | IllegalStateException e) {
                e.printStackTrace();
                System.out.println("Something went wrong. Try again!");
            }
        }
    }
}
