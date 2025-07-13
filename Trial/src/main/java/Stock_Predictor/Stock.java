package Stock_Predictor;

import java.util.LinkedList;

public class Stock {
    private String name;
    private LinkedList<Stock_Data> stock_data = new LinkedList<Stock_Data>();
    DateFormatter dateFormatter = new DateFormatter();

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

    public void showStockData(){
        System.out.println("%-12Date %12.2Open %12.2High %12.2Low %12.2Close %12.2Volume %12.2VAMP");
        for(Stock_Data e: stock_data){
            System.out.println(e.toString());
        }
    }

    public  void  showData_timeperiod(){
        int time = dateFormatter.input();
        System.out.println("%-12Date %12.2Open %12.2High %12.2Low %12.2Close %12.2Volume %12.2VAMP");
        for (int i = 0; i < time; i++) {
                System.out.println(stock_data.get(i).toString());
        }
    }



}
