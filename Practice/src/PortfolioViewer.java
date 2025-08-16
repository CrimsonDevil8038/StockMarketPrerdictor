package Stock_Predictor;

import java.sql.*;
import java.util.LinkedList;

public class PortfolioViewer {
    private final JDBC_Manager jdbcManager;

    public PortfolioViewer() {
        this.jdbcManager = new JDBC_Manager();
    }

    public static class PortfolioHolding {
        private final String stockName;
        private final int quantity;
        private final double purchasePrice;

        public PortfolioHolding(String stockName, int quantity, double purchasePrice) {
            this.stockName = stockName;
            this.quantity = quantity;
            this.purchasePrice = purchasePrice;
        }

        @Override
        public String toString() {
            return String.format("%-20s %-10d $%-12.2f",
                    stockName, quantity, purchasePrice);
        }
    }

    public boolean doesUserTableExist(String username) {
        try (Connection conn = jdbcManager.getConnection();
             Statement stmt = conn.createStatement()) {
            // Check if the user's portfolio table exists
            ResultSet rs = stmt.executeQuery(
                    "SELECT EXISTS (SELECT FROM information_schema.tables " +
                            "WHERE table_name = '" + username.toLowerCase() + "')");
            return rs.next() && rs.getBoolean(1);
        } catch (SQLException e) {
            System.err.println("Error checking table existence: " + e.getMessage());
            return false;
        }
    }

    public LinkedList<PortfolioHolding> getPortfolio(String username) {
        LinkedList<PortfolioHolding> portfolio = new LinkedList<>();

        if (!doesUserTableExist(username)) {
            System.out.println("No portfolio found for user: " + username);
            return portfolio;
        }

        try (Connection conn = jdbcManager.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(
                     "SELECT Stock, Quantity, Purchase_Price FROM " + username)) {

            while (rs.next()) {
                String stockName = rs.getString("Stock");
                int quantity = rs.getInt("Quantity");
                double purchasePrice = rs.getDouble("Purchase_Price");

                portfolio.add(new PortfolioHolding(stockName, quantity, purchasePrice));
            }
        } catch (SQLException e) {
            System.err.println("Database error: " + e.getMessage());
        }
        return portfolio;
    }

    public void displayPortfolio(String username) {
        LinkedList<PortfolioHolding> portfolio = getPortfolio(username);

        if (portfolio.isEmpty()) {
            System.out.println("No holdings found for user: " + username);
            return;
        }

        System.out.println("\nPortfolio for: " + username);
        System.out.println("------------------------------------------");
        System.out.printf("%-20s %-10s %-12s\n",
                "Stock Name", "Quantity", "Buy Price");
        System.out.println("------------------------------------------");

        for (PortfolioHolding holding : portfolio) {
            System.out.println(holding);
        }

        System.out.println("------------------------------------------");

        // Calculate and display total investment
        double totalInvestment = portfolio.stream()
                .mapToDouble(h -> h.purchasePrice * h.quantity)
                .sum();
        System.out.printf("%33s $%.2f\n", "Total Investment:", totalInvestment);
    }

    public boolean addHolding(String username, String stockName,
                              int quantity, double purchasePrice) {
        try (Connection conn = jdbcManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(
                     "INSERT INTO " + username +
                             " (Stock, Quantity, Purchase_Price) VALUES (?, ?, ?)")) {

            stmt.setString(1, stockName);
            stmt.setInt(2, quantity);
            stmt.setDouble(3, purchasePrice);

            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error adding holding: " + e.getMessage());
            return false;
        }
    }

    public static void main(String[] args) {
        PortfolioViewer viewer = new PortfolioViewer();
        viewer.displayPortfolio("john_doe");
    }
}