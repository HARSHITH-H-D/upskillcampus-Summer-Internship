package com.example.apiprograms;
import javafx.application.Application;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;
import java.util.HashMap;
import java.util.Map;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.FileReader;
class Account {
    private String name;
    private int pin;
    private long aadhaarno;
    private String address;
    private int accountNumber;
    private int balance;

    public Account(String name, int pin, long aadhaarno, String address, int accountNumber, int balance) {
        this.name = name;
        this.pin = pin;
        this.aadhaarno = aadhaarno;
        this.address = address;
        this.accountNumber = accountNumber;
        this.balance = balance;
    }

    public int getAccountNumber() {
        return accountNumber;
    }

    public int getBalance() {
        return balance;
    }

    public void deposit(int amount) {
        balance += amount;
    }

    public boolean withdraw(int amount) {
        if (balance >= amount) {
            balance -= amount;
            return true;
        }
        return false;
    }

    public String getName() {
        return name;
    }

    public boolean authenticate(int enteredPin) {
        return this.pin == enteredPin;
    }

    public String toCSV() {
        return name + "," + accountNumber + "," + pin + "," + aadhaarno + "," + address + "," + balance;
    }
}
class Bank {

    private Map<Integer, Account> accounts = new HashMap<>();
    private String fileName;

    public Bank(String fileName) {
        this.fileName = fileName;
        loadAccountsFromFile();
    }

    // Load accounts from CSV file
    private void loadAccountsFromFile() {
        try (BufferedReader reader = new BufferedReader(new FileReader(fileName))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] details = line.split(",");
                String name = details[0];
                int accountNumber = Integer.parseInt(details[1]);
                int pin = Integer.parseInt(details[2]);
                long aadhaarno = Long.parseLong(details[3]);
                String address = details[4];
                int balance = Integer.parseInt(details[5]);
                accounts.put(accountNumber, new Account(name, pin, aadhaarno, address, accountNumber, balance));
            }

        } catch (IOException e) {
            System.out.println("Error loading accounts from file.");
        }
    }

    // Save all accounts to CSV file
    public void saveAccountsToFile() {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter("Accounts.csv"))) {
            for (Account account : accounts.values()) {
                writer.write(account.toCSV());
                writer.newLine();
            }
        } catch (IOException e) {
            System.out.println("Error saving accounts to file.");
        }
    }

    public Account createAccount(String name, int pin, long aadhaarno, String address) {
        int accountNumber = accounts.size() + 1001; // Simple account number generator
        Account newAccount = new Account(name, pin, aadhaarno, address, accountNumber, 0);
        accounts.put(accountNumber, newAccount);
        saveAccountsToFile(); // Save after creating account
        System.out.println("Account created successfully. Account number: " + accountNumber);
        return newAccount;
    }

    public Account getAccount(int accountNumber) {
        return accounts.get(accountNumber);
    }

    public boolean transfer(int fromAccountNumber, int toAccountNumber, int amount, int pin) {
        Account fromAccount = getAccount(fromAccountNumber);
        Account toAccount = getAccount(toAccountNumber);

        if (fromAccount == null || toAccount == null) {
            System.out.println("One or both account numbers are invalid.");
            return false;
        }

        if (!fromAccount.authenticate(pin)) {
            System.out.println("Incorrect PIN for account " + fromAccountNumber);
            return false;
        }

        if (fromAccount.withdraw(amount)) {
            toAccount.deposit(amount);
            saveAccountsToFile(); // Save after transaction
            System.out.println("Transfer of " + amount + " from Account " + fromAccountNumber + " to Account " + toAccountNumber + " successful.");
            return true;
        } else {
            System.out.println("Transfer failed due to insufficient balance.");
            return false;
        }
    }
}

public class BankApp extends Application {
    private Bank bank = new Bank("accounts.csv");

    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("Banking System");

        // Create UI components
        Label welcomeLabel = new Label("Welcome to the Bank System");

        Button createAccountButton = new Button("Create Account");
        Button viewBalanceButton = new Button("View Balance");
        Button withdrawButton = new Button("Withdraw");
        Button depositButton = new Button("Deposit");
        Button transferButton = new Button("Transfer");

        // Create grid pane
        GridPane grid = new GridPane();
        grid.setAlignment(Pos.CENTER);
        grid.setVgap(10);
        grid.setHgap(10);
        grid.add(welcomeLabel, 0, 0, 2, 1);
        grid.add(createAccountButton, 0, 1);
        grid.add(viewBalanceButton, 1, 1);
        grid.add(withdrawButton, 0, 2);
        grid.add(depositButton, 1, 2);
        grid.add(transferButton, 0, 3, 2, 1);

        // Create scenes for different operations
        createAccountButton.setOnAction(e -> showCreateAccountScreen(primaryStage));
        viewBalanceButton.setOnAction(e -> showViewBalanceScreen(primaryStage));
        withdrawButton.setOnAction(e -> showWithdrawScreen(primaryStage));
        depositButton.setOnAction(e -> showDepositScreen(primaryStage));
        transferButton.setOnAction(e -> showTransferScreen(primaryStage));

        // Main Scene
        Scene scene = new Scene(grid, 400, 300);
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    private void showCreateAccountScreen(Stage primaryStage) {
        Stage createAccountStage = new Stage();
        createAccountStage.setTitle("Create Account");

        GridPane grid = new GridPane();
        grid.setAlignment(Pos.CENTER);
        grid.setVgap(10);

        // UI components for creating account
        TextField nameField = new TextField();
        TextField pinField = new TextField();
        TextField aadhaarField = new TextField();
        TextArea addressField = new TextArea();
        Button createAccountButton = new Button("Create Account");

        grid.add(new Label("Name:"), 0, 0);
        grid.add(nameField, 1, 0);
        grid.add(new Label("PIN:"), 0, 1);
        grid.add(pinField, 1, 1);
        grid.add(new Label("Aadhaar No:"), 0, 2);
        grid.add(aadhaarField, 1, 2);
        grid.add(new Label("Address:"), 0, 3);
        grid.add(addressField, 1, 3);
        grid.add(createAccountButton, 1, 4);

        // Create account logic
        createAccountButton.setOnAction(e -> {
            int a=1000;
            String name = nameField.getText();

            int pin = Integer.parseInt(pinField.getText());
            long aadhaarNo = Long.parseLong(aadhaarField.getText());
            String address = addressField.getText();
            bank.createAccount(name, pin, aadhaarNo, address);

            showAlert(Alert.AlertType.INFORMATION, "Success", "Account created successfully!");
        });

        Scene scene = new Scene(grid, 400, 300);
        createAccountStage.setScene(scene);
        createAccountStage.show();
    }

    private void showViewBalanceScreen(Stage primaryStage) {
        Stage balanceStage = new Stage();
        balanceStage.setTitle("View Balance");

        GridPane grid = new GridPane();
        grid.setAlignment(Pos.CENTER);
        grid.setVgap(10);

        TextField accountNumberField = new TextField();
        Button checkBalanceButton = new Button("Check Balance");

        grid.add(new Label("Account Number:"), 0, 0);
        grid.add(accountNumberField, 1, 0);
        grid.add(checkBalanceButton, 1, 1);

        checkBalanceButton.setOnAction(e -> {
            int accountNumber = Integer.parseInt(accountNumberField.getText());
            Account account = bank.getAccount(accountNumber);
            if (account != null) {
                showAlert(Alert.AlertType.INFORMATION, "Balance", "Balance: " + account.getBalance());
            } else {
                showAlert(Alert.AlertType.ERROR, "Error", "Account not found!");
            }
        });

        Scene scene = new Scene(grid, 400, 200);
        balanceStage.setScene(scene);
        balanceStage.show();
    }

    private void showWithdrawScreen(Stage primaryStage) {
        Stage withdrawStage = new Stage();
        withdrawStage.setTitle("Withdraw Money");

        GridPane grid = new GridPane();
        grid.setAlignment(Pos.CENTER);
        grid.setVgap(10);

        TextField accountNumberField = new TextField();
        TextField pinField = new TextField();
        TextField amountField = new TextField();
        Button withdrawButton = new Button("Withdraw");

        grid.add(new Label("Account Number:"), 0, 0);
        grid.add(accountNumberField, 1, 0);
        grid.add(new Label("PIN:"), 0, 1);
        grid.add(pinField, 1, 1);
        grid.add(new Label("Amount:"), 0, 2);
        grid.add(amountField, 1, 2);
        grid.add(withdrawButton, 1, 3);

        withdrawButton.setOnAction(e -> {
            int accountNumber = Integer.parseInt(accountNumberField.getText());
            int pin = Integer.parseInt(pinField.getText());
            int amount = Integer.parseInt(amountField.getText());
            Account account = bank.getAccount(accountNumber);
            if (account != null && account.authenticate(pin)) {
                if (account.withdraw(amount)) {
                    bank.saveAccountsToFile();
                    showAlert(Alert.AlertType.INFORMATION, "Success", "Withdrawal successful!");
                } else {
                    showAlert(Alert.AlertType.ERROR, "Error", "Insufficient balance.");
                }
            } else {
                showAlert(Alert.AlertType.ERROR, "Error", "Invalid account or PIN.");
            }
        });

        Scene scene = new Scene(grid, 400, 300);
        withdrawStage.setScene(scene);
        withdrawStage.show();
    }

    private void showDepositScreen(Stage primaryStage) {
        Stage depositStage = new Stage();
        depositStage.setTitle("Deposit Money");

        GridPane grid = new GridPane();
        grid.setAlignment(Pos.CENTER);
        grid.setVgap(10);

        TextField accountNumberField = new TextField();
        TextField amountField = new TextField();
        Button depositButton = new Button("Deposit");

        grid.add(new Label("Account Number:"), 0, 0);
        grid.add(accountNumberField, 1, 0);
        grid.add(new Label("Amount:"), 0, 1);
        grid.add(amountField, 1, 1);
        grid.add(depositButton, 1, 2);

        depositButton.setOnAction(e -> {
            int accountNumber = Integer.parseInt(accountNumberField.getText());
            int amount = Integer.parseInt(amountField.getText());
            Account account = bank.getAccount(accountNumber);
            if (account != null) {
                account.deposit(amount);
                bank.saveAccountsToFile();
                showAlert(Alert.AlertType.INFORMATION, "Success", "Deposit successful!");
            } else {
                showAlert(Alert.AlertType.ERROR, "Error", "Account not found.");
            }
        });

        Scene scene = new Scene(grid, 400, 300);
        depositStage.setScene(scene);
        depositStage.show();
    }

    private void showTransferScreen(Stage primaryStage) {
        Stage transferStage = new Stage();
        transferStage.setTitle("Transfer Money");

        GridPane grid = new GridPane();
        grid.setAlignment(Pos.CENTER);
        grid.setVgap(10);

        TextField fromAccountField = new TextField();
        TextField toAccountField = new TextField();
        TextField pinField = new TextField();
        TextField amountField = new TextField();
        Button transferButton = new Button("Transfer");

        grid.add(new Label("From Account:"), 0, 0);
        grid.add(fromAccountField, 1, 0);
        grid.add(new Label("To Account:"), 0, 1);
        grid.add(toAccountField, 1, 1);
        grid.add(new Label("PIN:"), 0, 2);
        grid.add(pinField, 1, 2);
        grid.add(new Label("Amount:"), 0, 3);
        grid.add(amountField, 1, 3);
        grid.add(transferButton, 1, 4);

        transferButton.setOnAction(e -> {
            int fromAccount = Integer.parseInt(fromAccountField.getText());
            int toAccount = Integer.parseInt(toAccountField.getText());
            int pin = Integer.parseInt(pinField.getText());
            int amount = Integer.parseInt(amountField.getText());
            if (bank.transfer(fromAccount, toAccount, amount, pin)) {
                showAlert(Alert.AlertType.INFORMATION, "Success", "Transfer successful!");
            } else {
                showAlert(Alert.AlertType.ERROR, "Error", "Transfer failed.");
            }
        });

        Scene scene = new Scene(grid, 400, 300);
        transferStage.setScene(scene);
        transferStage.show();
    }

    private void showAlert(Alert.AlertType alertType, String title, String message) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
