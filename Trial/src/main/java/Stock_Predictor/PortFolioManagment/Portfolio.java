package Stock_Predictor.PortFolioManagment;
import Stock_Predictor.Account.AccountManager;
import Stock_Predictor.JDBC.JDBC_Manager;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;

public class Portfolio {
    static JDBC_Manager JDBC_Manager;
    static AccountManager accountManager;
    String stockname;
    int quantity;
    double purchase_price;
    double current_price;
    double profit;

    public Portfolio(String stockname, int quantity, double purchase_price, double current_price) {
        this.stockname = stockname;
        this.quantity = quantity;
        this.purchase_price = purchase_price;
        this.current_price = current_price;
        this.profit = (current_price - purchase_price) * quantity;
    }

    public Portfolio() {

    }

    @Override
    public String toString() {
        return "Portfolio{" +
                "stockname='" + stockname + '\'' +
                ", quantity=" + quantity +
                ", purchase_price=" + purchase_price +
                ", current_price=" + current_price +
                ", profit=" + profit +
                '}';
    }

    public double getCurrent_price(String stockname) {
        stockname += "Stock_"+stockname;
        try (Connection conn = JDBC_Manager.getConnection()) {
            String sql = "SELECT Close FROM " + stockname + " ORDER BY Date DESC LIMIT 1";
            Statement st = conn.createStatement();
            ResultSet rs = st.executeQuery(sql);
            if (rs.next()) {
                return rs.getDouble("Close");
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
        return 0.0;
    }

    public LinkedList getPortfolios(String username) {
        LinkedList portfolios = new LinkedList();
        try (Connection conn = JDBC_Manager.getConnection()) {
            String sql = "SELECT Stock, Quantity, Purchase_Price FROM " + username;
            Statement st = conn.createStatement();
            ResultSet rs = st.executeQuery(sql);
            while (rs.next()) {
                String stockName = rs.getString("Stock");
                int quantity = rs.getInt("Quantity");
                double purchasePrice = rs.getDouble("Purchase_Price");
                double currentPrice = getCurrent_price(stockName);
            }
            portfolios.addLast(new Portfolio(stockname, quantity, purchase_price, current_price));
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
        return portfolios;
    }

    public void displayportfolios(String username) {
        username += "USER_"+username;
        LinkedList portfolio = getPortfolios(username);
        if (portfolio.isEmpty()) {
            System.out.println("No holdings found for user: " + username);
            return;
        }
        System.out.println("Holdings found for user: " + username);
        System.out.println(portfolio.toString());
    }

    public void buy(String username){
        username += "USER_"+username;
        LinkedList portfolio = getPortfolios(username);
    }

    public  void sell(String username){
        LinkedList portfolio = getPortfolios(username);

        //portfolio.deleteValue();

    }
}