package Stock_Predictor.PortFolioManagment;

import Stock_Predictor.JDBC.JDBC_Manager;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.LinkedList; // Switched to standard Java LinkedList
import java.util.Scanner;

public class Portfolio {
    // Static members to be initialized from Main to use the same instances
    private static JDBC_Manager jdbcManager;
    private static Scanner scanner;

    // Instance variables for a single holding
    String stockname;
    int quantity;
    double purchase_price;
    double current_price;
    double profit_loss;
    double total_value;

    /**
     * Initializes the Portfolio manager with necessary components from Main.
     * This is crucial to ensure we use the same database connection and scanner everywhere.
     */
    public static void init(JDBC_Manager manager, Scanner sc) {
        jdbcManager = manager;
        scanner = sc;
    }

    // Constructor for creating a portfolio holding object
    public Portfolio(String stockname, int quantity, double purchase_price, double current_price) {
        this.stockname = stockname;
        this.quantity = quantity;
        this.purchase_price = purchase_price;
        this.current_price = current_price;
        this.total_value = current_price * quantity;
        this.profit_loss = (current_price - purchase_price) * quantity;
    }

    // Default constructor
    public Portfolio() {}

    @Override
    public String toString() {
        // Formats the output for a neat table, with colors for profit/loss
        String profitColor = profit_loss >= 0 ? "\u001B[32m" : "\u001B[31m"; // Green for profit, Red for loss
        String resetColor = "\u001B[0m";
        return String.format("| %-15s | %-10d | %-18.2f | %-15.2f | %-18.2f | %s%-15.2f%s |",
                stockname, quantity, purchase_price, current_price, total_value, profitColor, profit_loss, resetColor);
    }

    /**
     * Fetches the most recent closing price for a given stock.
     * @param stockName The simple name of the stock (e.g., "TCS").
     * @return The latest closing price, or 0.0 if not found.
     */
    public double getCurrentPrice(String stockName) {
        String tableName = "stock_" + stockName.toLowerCase();
        if (!jdbcManager.check(tableName)) {
            return 0.0; // Stock data doesn't exist in the system
        }
        try (Connection conn = jdbcManager.getConnection()) {
            String sql = "SELECT \"close\" FROM " + tableName + " ORDER BY \"date\" DESC LIMIT 1";
            try (Statement st = conn.createStatement(); ResultSet rs = st.executeQuery(sql)) {
                if (rs.next()) {
                    return rs.getDouble("close");
                }
            }
        } catch (Exception e) {
            System.err.println("Error fetching current price for " + stockName + ": " + e.getMessage());
        }
        return 0.0;
    }

 LinkedList<Portfolio> getPortfolios(String username) {
        LinkedList<Portfolio> portfolios = new LinkedList<>();
        String userTable = "user_" + username.toLowerCase();

        try (Connection conn = jdbcManager.getConnection()) {
            String sql = "SELECT \"stock\", \"quantity\", \"purchase_price\" FROM " + userTable;
            try (Statement st = conn.createStatement(); ResultSet rs = st.executeQuery(sql)) {
                while (rs.next()) {
                    String stockName = rs.getString("stock");
                    int quantity = rs.getInt("quantity");
                    double purchasePrice = rs.getDouble("purchase_price");
                    double currentPrice = getCurrentPrice(stockName);
                    // Create a new Portfolio object for each row and add it to the list
                    portfolios.add(new Portfolio(stockName, quantity, purchasePrice, currentPrice));
                }
            }
        } catch (Exception e) {
            // This error is normal if the user has never bought a stock before.
        }
        return portfolios;
    }

    public void displayPortfolios(String username) {
        LinkedList<Portfolio> portfolio = getPortfolios(username);
        if (portfolio.isEmpty()) {
            System.out.println("\nYour portfolio is empty.");
            return;
        }
        System.out.println("\n--- Your Portfolio ---");
        System.out.println("+-----------------+------------+--------------------+-----------------+--------------------+-----------------+");
        System.out.println("| Stock           | Quantity   | Avg. Purchase Price| Current Price   | Total Value        | Profit/Loss     |");
        System.out.println("+-----------------+------------+--------------------+-----------------+--------------------+-----------------+");
        double totalPortfolioValue = 0;
        double totalProfitLoss = 0;
        for (Portfolio p : portfolio) {
            System.out.println(p.toString());
            totalPortfolioValue += p.total_value;
            totalProfitLoss += p.profit_loss;
        }
        System.out.println("+-----------------+------------+--------------------+-----------------+--------------------+-----------------+");
        String totalProfitColor = totalProfitLoss >= 0 ? "\u001B[32m" : "\u001B[31m";
        String resetColor = "\u001B[0m";
        System.out.printf("Total Portfolio Value: %.2f\n", totalPortfolioValue);
        System.out.printf("Total Profit/Loss: %s%.2f%s\n", totalProfitColor, totalProfitLoss, resetColor);
    }


    public void buy(String username) {
        System.out.println("\n--- Buy Stock ---");
        System.out.print("Enter Stock Name to Buy (e.g., TCS): ");
        String stockName = scanner.next().toUpperCase();

        double currentPrice = getCurrentPrice(stockName);
        if (currentPrice == 0.0) {
            System.out.println("Error: Stock '" + stockName + "' not found or has no price data. Please import its data first.");
            return;
        }
        System.out.printf("Current price of %s is %.2f\n", stockName, currentPrice);

        System.out.print("Enter quantity to buy: ");
        int quantityToBuy = scanner.nextInt();
        if (quantityToBuy <= 0) {
            System.out.println("Error: Quantity must be positive.");
            return;
        }

        String userTable = "user_" + username.toLowerCase();
        Portfolio existingHolding = jdbcManager.getHolding(userTable, stockName);

        if (existingHolding != null) {
            // User already owns this stock, so we update the holding
            int currentQuantity = existingHolding.quantity;
            double currentAvgPrice = existingHolding.purchase_price;

            int newQuantity = currentQuantity + quantityToBuy;
            // Calculate new weighted average price
            double newAvgPrice = ((currentAvgPrice * currentQuantity) + (currentPrice * quantityToBuy)) / newQuantity;

            if (jdbcManager.updateHolding(userTable, stockName, newQuantity, newAvgPrice)) {
                System.out.printf("Successfully bought %d shares of %s. You now own %d shares.\n", quantityToBuy, stockName, newQuantity);
            } else {
                System.out.println("Error: Failed to update your portfolio.");
            }
        } else {
            // User does not own this stock, so we insert a new holding
            if (jdbcManager.insertHolding(userTable, stockName, quantityToBuy, currentPrice)) {
                System.out.printf("Successfully bought %d shares of %s.\n", quantityToBuy, stockName);
            } else {
                System.out.println("Error: Failed to add stock to your portfolio.");
            }
        }
    }


    public void sell(String username) {
        System.out.println("\n--- Sell Stock ---");
        displayPortfolios(username); // Show user what they own first

        LinkedList<Portfolio> portfolios = getPortfolios(username);
        if (portfolios.isEmpty()) {
            return; // Nothing to sell
        }

        System.out.print("\nEnter Stock Name to Sell: ");
        String stockName = scanner.next().toUpperCase();

        String userTable = "user_" + username.toLowerCase();
        Portfolio holdingToSell = jdbcManager.getHolding(userTable, stockName);

        if (holdingToSell == null) {
            System.out.println("Error: You do not own any shares of '" + stockName + "'.");
            return;
        }

        System.out.printf("You own %d shares of %s.\n", holdingToSell.quantity, stockName);
        System.out.print("Enter quantity to sell: ");
        int quantityToSell = scanner.nextInt();

        if (quantityToSell <= 0 || quantityToSell > holdingToSell.quantity) {
            System.out.println("Error: Invalid quantity. You can sell between 1 and " + holdingToSell.quantity + " shares.");
            return;
        }

        if (quantityToSell == holdingToSell.quantity) {
            // Selling all shares of this stock
            if (jdbcManager.deleteHolding(userTable, stockName)) {
                System.out.printf("Successfully sold all %d shares of %s.\n", quantityToSell, stockName);
            } else {
                System.out.println("Error: Failed to sell stock.");
            }
        } else {
            // Selling a partial amount
            int newQuantity = holdingToSell.quantity - quantityToSell;
            if (jdbcManager.updateHoldingQuantity(userTable, stockName, newQuantity)) {
                System.out.printf("Successfully sold %d shares of %s. You have %d shares remaining.\n", quantityToSell, stockName, newQuantity);
            } else {
                System.out.println("Error: Failed to sell stock.");
            }
        }
    }
}
