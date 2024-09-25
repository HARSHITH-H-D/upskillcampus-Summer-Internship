import java.io.*;
import java.util.*;

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

public class App {
    private static Bank bank = new Bank("accounts.csv");
    private static Scanner sc = new Scanner(System.in);

    public static void main(String[] args) {
        while (true) {
            System.out.println("Welcome to the Bank System");
            System.out.println("1.CREATE ACCOUNT");
            System.out.println("2.VIEW BALANCE");
            System.out.println("3.WITHDRAW");
            System.out.println("4.DEPOSIT");
            System.out.println("5.TRANSACTION");
            System.out.println("6.EXIT");

            System.out.println("Enter the choice:");
            int choice = sc.nextInt();

            switch (choice) {
                case 1:
                    createAccount();
                    break;
                case 2:
                    viewBalance();
                    break;
                case 3:
                    withdraw();
                    break;
                case 4:
                    deposit();
                    break;
                case 5:
                    transfer();
                    break;
                case 6:
                    System.out.println("Exiting...");
                    System.exit(0);
                default:
                    System.out.println("Invalid choice");
            }
        }
    }

    private static void createAccount() {
        System.out.println("Enter your name:");
        String name = sc.next();
        System.out.println("Enter pin:");
        int pin = sc.nextInt();
        System.out.println("Enter aadhaar no:");
        long aadhaarno = sc.nextLong();
        sc.nextLine(); // consume newline left-over
        System.out.println("Enter address:");
        String address = sc.nextLine();

        Account account = bank.createAccount(name, pin, aadhaarno, address);
        System.out.println("Account created: " + account.getAccountNumber());
    }

    private static void viewBalance() {
        System.out.println("Enter account number:");
        int accountNumber = sc.nextInt();
        Account account = bank.getAccount(accountNumber);

        if (account != null) {
            System.out.println("Account Balance: " + account.getBalance());
        } else {
            System.out.println("Account not found.");
        }
    }

    private static void withdraw() {
        System.out.println("Enter account number:");
        int accountNumber = sc.nextInt();
        System.out.println("Enter amount to withdraw:");
        int amount = sc.nextInt();
        System.out.println("Enter PIN:");
        int pin = sc.nextInt();

        Account account = bank.getAccount(accountNumber);

        if (account != null && account.authenticate(pin)) {
            if (account.withdraw(amount)) {
                bank.saveAccountsToFile(); // Save after withdrawal
                System.out.println("Withdrawal successful.");
            } else {
                System.out.println("Insufficient balance.");
            }
        } else {
            System.out.println("Invalid account number or PIN.");
        }
    }

    private static void deposit() {
        System.out.println("Enter account number:");
        int accountNumber = sc.nextInt();
        System.out.println("Enter amount to deposit:");
        int amount = sc.nextInt();

        Account account = bank.getAccount(accountNumber);

        if (account != null) {
            account.deposit(amount);
            bank.saveAccountsToFile(); // Save after deposit
            System.out.println("Deposit successful.");
        } else {
            System.out.println("Account not found.");
        }
    }

    private static void transfer() {
        System.out.println("Enter your account number:");
        int fromAccount = sc.nextInt();
        System.out.println("Enter recipient's account number:");
        int toAccount = sc.nextInt();
        System.out.println("Enter amount to transfer:");
        int amount = sc.nextInt();
        System.out.println("Enter your PIN:");
        int pin = sc.nextInt();

        bank.transfer(fromAccount, toAccount, amount, pin);
    }
}
