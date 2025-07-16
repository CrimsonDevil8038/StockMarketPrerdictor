package Stock_Predictor;

import java.util.LinkedList;

public class Stock {
    private final LinkedList<Stock_Data> stock_data = new LinkedList<Stock_Data>();
    DateFormatter dateFormatter = new DateFormatter();
    private String name;

    public Stock(String name) {
        this.name = name;
    }

    public Stock() {
    }

    public String getName() {
        return name;
    }

    public LinkedList<Stock_Data> getStock_data() {
        return stock_data;
    }

    public void showStockData() {
        System.out.printf("%-12s %12s %12s %12s %12s %12s %12s\n", "Date", "Open", "High", "Low", "Close", "Volume", "VWAP");

        for (Stock_Data e : stock_data) {
            System.out.println(e.toString());
        }
    }

    public void showData_timeperiod() {

        Object[] dataretrived = dateFormatter.input();
        int time = (int) dataretrived[0];
        String start = (String) dataretrived[1];
        int remaining = time;

        System.out.println("%-12Date %12.2Open %12.2High %12.2Low %12.2Close %12.2Volume %12.2VAMP");
        for (int i = 0; i < stock_data.size(); i++) {

            if (start.equalsIgnoreCase(stock_data.get(i).getDate()) || (remaining < time)) {
                remaining--;
                System.out.println(stock_data.get(i).toString());
            } else if (remaining == 0) {
                break;
            }
        }
    }


}
