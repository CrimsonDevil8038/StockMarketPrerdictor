package Stock_Predictor;

import java.io.Serializable;
import java.util.LinkedList;

public class Stock implements Serializable {
    

    private final LinkedList<Stock_Data> stock_data = new LinkedList<Stock_Data>();

    DateFormatter dateFormatter = new DateFormatter();
    private String name;
    private String fileLocation = "";

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


    public String getFileLocation() {
        return fileLocation;
    }

    public void setFileLocation(String fileLocation) {
        this.fileLocation = fileLocation;
    }

    public void showStockData() {
        System.out.printf("%-12s %12s %12s %12s %12s %12s %12s\n", "Date", "Open", "High", "Low", "Close", "Volume", "VWAP");

        for (Stock_Data e : stock_data) {
            System.out.println(e.toString());
        }
    }

    public void showData_timeperiod() {

        Object[] dataretrived = dateFormatter.input();
        String start = (String) dataretrived[0];
        String end = (String) dataretrived[1];
        int count = 0;
        System.out.printf("%-12s %12s %12s %12s %12s %12s %12s\n", "Date", "Open", "High", "Low", "Close", "Volume", "VWAP");
        for (int i = 0; i < stock_data.size(); i++) {

            if (end.equalsIgnoreCase(stock_data.get(i).getDate()) || count > 0) {
                System.out.println(stock_data.get(i).toString());
                count++;
            }
            if (start.equalsIgnoreCase(stock_data.get(i).getDate())) {
                break;
            }
        }
    }


}
