package Stock_Predictor.PortFolioManagment;

import Stock_Predictor.JDBC.JDBC_Manager;
import Stock_Predictor.Predict_And_Analysis.DataPreparation;

import java.sql.*;

import static Stock_Predictor.Color.*;


public class Portfolio {
    private int userid;
    private final LinkedList linkedList = new LinkedList();
    private final JDBC_Manager jdbcManager;
    private final DataPreparation dataPreparation;
    private final Connection conn;

    public Portfolio(JDBC_Manager jdbcManager, DataPreparation dataPreparation) {
        this.jdbcManager = jdbcManager;
        this.dataPreparation = dataPreparation;
        this.conn = jdbcManager.getConnection();
    }

    public void setUserid(int userid) {
        this.userid = userid;
        createUserPortfolioTable();
        retrieveData();
    }

    public void retrieveData() {
        this.linkedList.head = null;
        String sql = "SELECT stock_name, quantity, purchase_price FROM User_Portfolio WHERE userid = ?";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, this.userid);
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                String stockName = rs.getString("stock_name");
                long quantity = rs.getLong("quantity");
                double purchasePrice = rs.getDouble("purchase_price");

                String tableName = "stock_" + stockName.toLowerCase();
                Date maxDate = jdbcManager.get_maxDate(tableName);
                if (maxDate != null) {
                    double currentPrice = jdbcManager.getCloseOnDate(tableName, maxDate);
                    dataPreparation.data_short_term(tableName);
                    dataPreparation.gradientDescentWithRegularization(500);
                    double predictedPrice = dataPreparation.predictSilent(tableName);
                    Date predictionDate = jdbcManager.calculateNextBusinessDay(maxDate);
                    linkedList.addLast(stockName, purchasePrice, currentPrice, predictedPrice, quantity, predictionDate, maxDate);
                }
            }
            System.out.println(GREEN + "Portfolio loaded successfully." + RESET);
        } catch (SQLException e) {
            System.err.println("Error retrieving portfolio data: " + e.getMessage());
        }
    }

    public void addPortfolio(String stockName, long quantity, double purchasePrice) {
        String sql = "INSERT INTO User_Portfolio (userid, stock_name, quantity, purchase_price) VALUES (?, ?, ?, ?) " +
                "ON CONFLICT (userid, stock_name) DO UPDATE SET quantity = User_Portfolio.quantity + EXCLUDED.quantity";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, userid);
            pstmt.setString(2, stockName);
            pstmt.setLong(3, quantity);
            pstmt.setDouble(4, purchasePrice);
            int affectedRows = pstmt.executeUpdate();
            if (affectedRows > 0) {
                System.out.println(GREEN + "Successfully added/updated " + quantity + " shares of " + stockName + " to your portfolio." + RESET);
                retrieveData();
            }
        } catch (SQLException e) {
            System.err.println("Error adding to portfolio: " + e.getMessage());
        }
    }

    public void updatePortfolio(String stockName, long quantityToSell) {
        long newQuantity = linkedList.sellShares(stockName, quantityToSell);

        if (newQuantity < 0) {
            return;
        }

        String sql;
        if (newQuantity == 0) {
            sql = "DELETE FROM User_Portfolio WHERE userid = ? AND stock_name = ?";
        } else {
            sql = "UPDATE User_Portfolio SET quantity = ? WHERE userid = ? AND stock_name = ?";
        }
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            if (newQuantity > 0) {
                pstmt.setLong(1, newQuantity);
                pstmt.setInt(2, this.userid);
                pstmt.setString(3, stockName);
            } else {
                pstmt.setInt(1, this.userid);
                pstmt.setString(2, stockName);
            }
            pstmt.executeUpdate();
            System.out.println("Database updated successfully.");
        } catch (SQLException e) {
            System.err.println("Error updating database portfolio: " + e.getMessage());
        }
    }

    public void displayPortfolio() {
        linkedList.display();
    }

    private void createUserPortfolioTable() {
        String createTableSQL = "CREATE TABLE IF NOT EXISTS User_Portfolio (" +
                "userid INTEGER NOT NULL," +
                "stock_name VARCHAR(50) NOT NULL," +
                "quantity BIGINT NOT NULL," +
                "purchase_price DECIMAL(15,3) NOT NULL," +
                "PRIMARY KEY (userid, stock_name)," +
                "FOREIGN KEY (userid) REFERENCES users(userid) ON DELETE CASCADE" +
                ")";
        try (Statement stmt = conn.createStatement()) {
            stmt.execute(createTableSQL);
        } catch (SQLException e) {
            System.err.println("Error creating portfolio table: " + e.getMessage());
        }
    }
}