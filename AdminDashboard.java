package Java.Sales_Management_System;

import java.sql.*;
import java.util.Scanner;

public class AdminDashboard {
    private static final Scanner scanner = new Scanner(System.in);

    private static final String URL = "jdbc:mysql://localhost:3306/sales";
    private static final String USER = "root";
    private static final String PASSWORD = "2000";

    private boolean loggedIn = false;

    public void showDashboard() {
        if (!login()) {
            System.out.println("Login failed. Exiting admin dashboard.");
            return;
        }

        System.out.println("\tAdmin Dashboard");
        boolean exit = false;
        while (!exit) {
            System.out.println("1. Modify Product");
            System.out.println("2. Get Customer List");
            System.out.println("3. Profit Status");
            System.out.println("4. Exit");
            System.out.print("Enter your choice: ");
            int choice = scanner.nextInt();
            switch (choice) {
                case 1:
                    modifyProduct();
                    break;
                case 2:
                    getCustomerList();
                    break;
                case 3:
                    profitStatus();
                    break;
                case 4:
                    exit = true;
                    break;
                default:
                    System.out.println("Invalid choice. Please enter a valid option.");
            }
        }
    }

    private boolean login() {
        System.out.println("\n\tAdmin Login");
        System.out.print("Enter name: ");
        String name = scanner.next();
        System.out.print("Enter phone: ");
        String phone = scanner.next();

        try (Connection connection = DriverManager.getConnection(URL, USER, PASSWORD);
             PreparedStatement statement = connection.prepareStatement("SELECT * FROM admins WHERE name = ? AND phone = ?")) {
            statement.setString(1, name);
            statement.setString(2, phone);
            ResultSet resultSet = statement.executeQuery();

            if (resultSet.next()) {
                loggedIn = true;
                System.out.println("Login successful.");
                return true;
            } else {
                System.out.println("Invalid credentials. Login failed.");
                return false;
            }
        } catch (SQLException e) {
            System.err.println("Error during login: " + e.getMessage());
            return false;
        }
    }

    private void modifyProduct() {
        if (!loggedIn) {
            System.out.println("Login required to access this feature.");
            return;
        }

        boolean exit = false;
        while (!exit) {
            System.out.println("Modify Product");
            System.out.println("1. Add Product");
            System.out.println("2. Remove Product");
            System.out.println("3. Change Quantity");
            System.out.println("4. Change Price");
            System.out.println("5. Exit");
            System.out.print("Enter your choice: ");
            int choice = scanner.nextInt();
            switch (choice) {
                case 1:
                    addProduct();
                    break;
                case 2:
                    removeProduct();
                    break;
                case 3:
                    changeQuantity();
                    break;
                case 4:
                    changePrice();
                    break;
                case 5:
                    exit = true;
                    break;
                default:
                    System.out.println("Invalid choice. Please enter a valid option.");
            }
        }
    }

    private void addProduct() {
        if (!loggedIn) {
            System.out.println("Login required to access this feature.");
            return;
        }

        System.out.println("Adding Product");
        System.out.print("Enter product name: ");
        String name = scanner.next();
        System.out.print("Enter market price: ");
        double marketPrice = scanner.nextDouble();
        System.out.print("Enter actual price: ");
        double actualPrice = scanner.nextDouble();
        System.out.print("Enter quantity: ");
        int quantity = scanner.nextInt();

        try (Connection connection = DriverManager.getConnection(URL, USER, PASSWORD);
             PreparedStatement statement = connection.prepareStatement("INSERT INTO products (name, market_price, actual_price, quantity) VALUES (?, ?, ?, ?)")) {
            statement.setString(1, name);
            statement.setDouble(2, marketPrice);
            statement.setDouble(3, actualPrice);
            statement.setInt(4, quantity);
            int rowsAffected = statement.executeUpdate();
            if (rowsAffected > 0) {
                System.out.println("Product added successfully.");
            } else {
                System.out.println("Failed to add product.");
            }
        } catch (SQLException e) {
            System.err.println("Error adding product: " + e.getMessage());
        }
    }

    private void removeProduct() {
        if (!loggedIn) {
            System.out.println("Login required to access this feature.");
            return;
        }

        System.out.println("Removing Product");
        System.out.print("Enter product name: ");
        String name = scanner.next();

        try (Connection connection = DriverManager.getConnection(URL, USER, PASSWORD);
             PreparedStatement statement = connection.prepareStatement("DELETE FROM products WHERE name = ?")) {
            statement.setString(1, name);
            int rowsAffected = statement.executeUpdate();
            if (rowsAffected > 0) {
                System.out.println("Product removed successfully.");
            } else {
                System.out.println("No such product found.");
            }
        } catch (SQLException e) {
            System.err.println("Error removing product: " + e.getMessage());
        }
    }

    private void changeQuantity() {
        if (!loggedIn) {
            System.out.println("Login required to access this feature.");
            return;
        }

        System.out.println("Changing Quantity");
        System.out.print("Enter product name: ");
        String name = scanner.next();
        System.out.print("Enter new quantity: ");
        int newQuantity = scanner.nextInt();

        try (Connection connection = DriverManager.getConnection(URL, USER, PASSWORD);
             PreparedStatement statement = connection.prepareStatement("UPDATE products SET quantity = ? WHERE name = ?")) {
            statement.setInt(1, newQuantity);
            statement.setString(2, name);
            int rowsAffected = statement.executeUpdate();
            if (rowsAffected > 0) {
                System.out.println("Quantity changed successfully.");
            } else {
                System.out.println("No such product found.");
            }
        } catch (SQLException e) {
            System.err.println("Error changing quantity: " + e.getMessage());
        }
    }

    private void changePrice() {
        if (!loggedIn) {
            System.out.println("Login required to access this feature.");
            return;
        }

        System.out.println("Changing Price");
        System.out.print("Enter product name: ");
        String name = scanner.next();
        System.out.print("Enter new market price: ");
        double newMarketPrice = scanner.nextDouble();
        System.out.print("Enter new actual price: ");
        double newActualPrice = scanner.nextDouble();

        try (Connection connection = DriverManager.getConnection(URL, USER, PASSWORD);
             PreparedStatement statement = connection.prepareStatement("UPDATE products SET market_price = ?, actual_price = ? WHERE name = ?")) {
            statement.setDouble(1, newMarketPrice);
            statement.setDouble(2, newActualPrice);
            statement.setString(3, name);
            int rowsAffected = statement.executeUpdate();
            if (rowsAffected > 0) {
                System.out.println("Price changed successfully.");
            } else {
                System.out.println("No such product found.");
            }
        } catch (SQLException e) {
            System.err.println("Error changing price: " + e.getMessage());
        }
    }

    private void getCustomerList() {
        if (!loggedIn) {
            System.out.println("Login required to access this feature.");
            return;
        }

        System.out.println("\tCustomer List");

        try (Connection connection = DriverManager.getConnection(URL, USER, PASSWORD);
             Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery("SELECT * FROM customers ORDER BY name ASC")) {
            while (resultSet.next()) {
                // int id = resultSet.getInt("id");
                String name = resultSet.getString("name");
                String phone = resultSet.getString("phone");
                String address = resultSet.getString("address");
                System.out.println("Name: " + name + ", Phone: " + phone + ", Address: " + address);
            }
            System.out.println();
        } catch (SQLException e) {
            System.err.println("Error fetching customer list: " + e.getMessage());
        }
    }

    private void profitStatus() {
        if (!loggedIn) {
            System.out.println("Login required to access this feature.");
            return;
        }
    
        System.out.println("\tProfit Status");
    
        try (Connection connection = DriverManager.getConnection(URL, USER, PASSWORD);
             Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery("SELECT name, SUM(total_profit) AS profit FROM products WHERE total_profit > 0 GROUP BY name ORDER BY profit DESC")) {
            double totalProfit = 0.0;
            System.out.println("Product Name\tTotal Profit");
            while (resultSet.next()) {
                String productName = resultSet.getString("name");
                double profit = resultSet.getDouble("profit");
                totalProfit += profit;
                System.out.print(productName + "\t\t");
                System.out.printf("%.2fRs.\n" , profit);
            }
            System.out.printf("\nTotal Profit: %.2fRs.\n\n" , totalProfit);
        } catch (SQLException e) {
            System.err.println("Error fetching profit status: " + e.getMessage());
        }
    }
    
    
}
