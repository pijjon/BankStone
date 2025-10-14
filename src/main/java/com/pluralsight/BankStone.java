package com.pluralsight;

import java.time.LocalDateTime;
import java.util.InputMismatchException;
import java.util.NoSuchElementException;
import java.util.Scanner;

public class BankStone {
    public static Scanner myScanner = new Scanner(System.in);

    public static void main(String[] args) {
        home(); // start application and display home screen
    }

    public static String askUser(String question) {
        try {
            return myScanner.nextLine();
        } catch (Exception e) { // usually only catches if there is something wrong with the scanner
            System.out.println("Error prompting for user input");
            return "";
        }
    }

    public static double askUserDouble(String question) {
        while (true) { // keep looping indefinitely
            try {
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
                    }

                    case "p": {
                        makeTransaction("charge");
                    }

                    case "l": {
                        viewLedger();
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
        if (type.equals("deposit")) {
            double amount = askUserDouble("How much money would you like to deposit?");

        }
    }

    public static void viewLedger() {

    }

    public static void makePayment() {

    }

    public static void preload() {

    }
}
