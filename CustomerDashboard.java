package Java.Sales_Management_System;
import java.sql.*;
import java.util.*;

public class CustomerDashboard {
    private static final Scanner scanner = new Scanner(System.in);

    // Database connection details
    private static final String URL = "jdbc:mysql://localhost:3306/sales";
    private static final String USER = "root";
    private static final String PASSWORD = "2000"; // Replace with your MySQL password

    private Map<String, Integer> selectedProducts = new HashMap<>();
    private int currentCustomerId = -1;

    public void start() {
        System.out.println("Welcome to the Sales Management System");

        while (true) {
            System.out.println("1. Register");
            System.out.println("2. Login");
            System.out.println("3. Exit");
            System.out.print("Enter your choice: ");
            int choice = scanner.nextInt();

            switch (choice) {
                case 1:
                    register();
                    break;
                case 2:
                    if (login()) {
                        showDashboard();
                    }
                    break;
                case 3:
                    System.out.println("Exiting...");
                    return;
                default:
                    System.out.println("Invalid choice. Please try again.");
            }
        }
    }

    private void register() {
        System.out.print("Enter name: ");
        String name = scanner.next();
        System.out.print("Enter phone: ");
        String phone = scanner.next();
        System.out.print("Enter address: ");
        String address = scanner.next();

        try (Connection connection = DriverManager.getConnection(URL, USER, PASSWORD);
             PreparedStatement statement = connection.prepareStatement("INSERT INTO customers (name, phone, address) VALUES (?, ?, ?)")) {
            statement.setString(1, name);
            statement.setString(2, phone);
            statement.setString(3, address);
            statement.executeUpdate();
            System.out.println("Registration successful.");
        } catch (SQLException e) {
            System.err.println("Error during registration: " + e.getMessage());
        }
    }

    private boolean login() {
        System.out.print("Enter phone: ");
        String phone = scanner.next();

        try (Connection connection = DriverManager.getConnection(URL, USER, PASSWORD);
             PreparedStatement statement = connection.prepareStatement("SELECT id FROM customers WHERE phone = ?")) {
            statement.setString(1, phone);
            ResultSet resultSet = statement.executeQuery();

            if (resultSet.next()) {
                currentCustomerId = resultSet.getInt("id");
                System.out.println("Login successful.");
                return true;
            } else {
                System.out.println("Invalid phone number.");
                return false;
            }
        } catch (Exception e) {
            System.err.println("Error Occured " + e.getMessage());
            return false;
        }
    }

    private void showDashboard() {
        System.out.println("Customer Dashboard");
        boolean exit = false;
        while (!exit) {
            System.out.println("1. View Products");
            System.out.println("2. Add Product and Quantity");
            System.out.println("3. Get Bill");
            System.out.println("4. Back to Main Menu");
            System.out.print("Enter your choice: ");
            int choice = scanner.nextInt();
            switch (choice) {
                case 1:
                    viewProducts();
                    break;
                case 2:
                    addProductAndQuantity();
                    break;
                case 3:
                    generateBill();
                    break;
                case 4:
                    exit = true;
                    break;
                default:
                    System.out.println("Invalid choice. Please enter a valid option.");
            }
        }
    }

    private void viewProducts() {
        try (Connection connection = DriverManager.getConnection(URL, USER, PASSWORD);
             Statement statement = connection.createStatement()) {
            String query = "SELECT name, market_price FROM products";
            ResultSet resultSet = statement.executeQuery(query);
            System.out.println("\n\tProducts:");
            while (resultSet.next()) {
                String productName = resultSet.getString("name");
                double productPrice = resultSet.getDouble("market_price");
                System.out.print(productName + " - ");
                System.out.printf( " %.2f Rs.\n",productPrice);
            }
            System.out.println();
        } catch (SQLException e) {
            System.err.println("Error fetching products: " + e.getMessage());
        }
    }

    private void addProductAndQuantity() {
        System.out.print("Enter product name: ");
        String productName = scanner.next();
        System.out.print("Enter quantity: ");
        int quantity = scanner.nextInt();

        try (Connection connection = DriverManager.getConnection(URL, USER, PASSWORD);
        PreparedStatement statement = connection.prepareStatement("SELECT quantity FROM products WHERE name = ?")) {
            statement.setString(1, productName);
            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                int availableQuantity = resultSet.getInt("quantity");
                // Check if the product availability
                if (availableQuantity >= quantity) {
                    // Add product to the selected list
                    selectedProducts.put(productName, selectedProducts.getOrDefault(productName, 0) + quantity);

                    // Update product quantity
                    try (PreparedStatement updateStatement = connection.prepareStatement("UPDATE products SET quantity = quantity - ? WHERE name = ?")) {
                        updateStatement.setInt(1, quantity);
                        updateStatement.setString(2, productName);
                        updateStatement.executeUpdate();
                    }
                    System.out.println("Added " + quantity + " of '" + productName + "' to your selection.");
                } else {
                    System.out.println("Insufficient quantity available. Available: " + availableQuantity);
                }
            } else {
                System.out.println("Product '" + productName + "' not found.");
            }
        } catch (SQLException e) {
            System.err.println("Error adding product: " + e.getMessage());
        }
    }

    private void generateBill() {
        if (selectedProducts.isEmpty()) {
            System.out.println("No products selected.");
            return;
        }
    
        double totalBill = 0.0;
    
        try (Connection connection = DriverManager.getConnection(URL, USER, PASSWORD);
             PreparedStatement selectStatement = connection.prepareStatement("SELECT market_price, actual_price FROM products WHERE name = ?");
             PreparedStatement updateStatement = connection.prepareStatement("UPDATE products SET quantity = quantity - ?, total_profit = total_profit + ? WHERE name = ?")) {
    
            System.out.println("\n\tBill Details:");
            System.out.println("Product Name\tQuantity\tTotal Price");
    
            for (Map.Entry<String, Integer> entry : selectedProducts.entrySet()) {
                String productName = entry.getKey();
                int quantity = entry.getValue();
                selectStatement.setString(1, productName);
                ResultSet resultSet = selectStatement.executeQuery();
    
                if (resultSet.next()) {
                    double marketPrice = resultSet.getDouble("market_price");
                    double actualPrice = resultSet.getDouble("actual_price");
                    double totalPrice = marketPrice * quantity;
                    totalBill += totalPrice;
    
                    updateStatement.setInt(1, quantity);
                    updateStatement.setDouble(2, (marketPrice - actualPrice) * quantity);
                    updateStatement.setString(3, productName);
                    updateStatement.executeUpdate();
    
                    System.out.println(productName + "\t\t" + quantity + "\t\t" + totalPrice + "Rs.");
                } else {
                    System.out.println("Product '" + productName + "' not found for billing.");
                }
            }
    
            System.out.printf("\nTotal Bill: %.2fRs.\n\n" , totalBill);
        } catch (SQLException e) {
            System.err.println("Error generating bill: " + e.getMessage());
        }
    
        selectedProducts.clear();
    }
}
