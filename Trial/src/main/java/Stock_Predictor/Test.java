package Stock_Predictor;

public class Test {
    public static void main(String[] args) {

        CSV_Manager csvManager = new CSV_Manager();
        csvManager.readCSV("C:\\Users\\patel\\Downloads\\Stock_Files\\543940.csv", "Stock_Unknown");
        // csvManager.readCSV("C:\\Users\\patel\\Downloads\\Stock_Files\\NIFTY 50-13-07-2024-to-13-07-2025.csv","Stock_Unknown1");

        csvManager.viewStockTimePeriod("Stock_Unknown");
        //csvManager.viewStock("Stock_Unknown");


    }
}
