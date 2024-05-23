package Java.Sales_Management_System;

import java.util.Scanner;

public class Dashboard {
    private static final Scanner scanner = new Scanner(System.in);

    public static void main(String[] args) {
        boolean exit = false;
        while (!exit) {
            System.out.println("Welcome to the Sales Management System");
            System.out.println("1. Customer Dashboard");
            System.out.println("2. Admin Dashboard");
            System.out.println("3. Exit");
            System.out.print("Enter your choice: ");
            int choice = scanner.nextInt();
            switch (choice) {
                case 1:
                    CustomerDashboard customerDashboard = new CustomerDashboard();
                    customerDashboard.start();
                    break;
                case 2:
                    AdminDashboard adminDashboard = new AdminDashboard();
                    adminDashboard.showDashboard();
                    break;
                case 3:
                    exit = true;
                    break;
                default:
                    System.out.println("Invalid choice. Please enter a valid option.");
            }
        }
        System.out.println("Thank you for using the Sales Management System. Goodbye!");
    }
}

