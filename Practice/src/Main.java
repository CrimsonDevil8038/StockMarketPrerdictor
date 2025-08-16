package Stock_Predictor;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.LinkedList;

//TIP To <b>Run</b> code, press <shortcut actionId="Run"/> or
// click the <icon src="AllIcons.Actions.Execute"/> icon in the gutter.
public class Main {

}
class Portfolio {
    static Stock_Predictor.JDBC_Manager JDBC_Manager;
    static Stock_Predictor.AccountManager accountManager;
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

    double getCurrent_price(String stockname) {
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

    LinkedList<Portfolio> getPortfolios(String username) {
        LinkedList<Portfolio> portfolios = new LinkedList<>();
        if (!accountManager.check_credentials()) {
            System.out.println("Authentication failed for user: " + username);
            return portfolios;
        }
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
            portfolios.add(new Portfolio(stockname, quantity, purchase_price, current_price));
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
        return portfolios;
    }

    void displayportfolios(String username) {
        LinkedList<Portfolio> portfolio = getPortfolios(username);
        if (portfolio.isEmpty()) {
            System.out.println("No holdings found for user: " + username);
            return;
        }
        System.out.println("Holdings found for user: " + username);
        System.out.println(portfolio.toString());
    }
}