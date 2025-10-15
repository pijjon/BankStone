package com.pluralsight;


import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.InputMismatchException;
import java.util.NoSuchElementException;
import java.util.Scanner;

public class BankStone {
    public static Scanner myScanner = new Scanner(System.in);

    public static ArrayList<Transaction> ledger = new ArrayList<>();

    public static void main(String[] args) {
        home(); // start application and display home screen
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
                continue;
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


    public static void viewLedger() {

    }

    public static void makePayment() {

    }

    public static void preload() {

    }
}
