package Stock_Predictor;

public class Test {
    public static void main(String[] args) {

        CSV_Manager csvManager = new CSV_Manager();
       // csvManager.readCSV("D:\\543940.csv", "Stock_Unknown");
        csvManager.readCSV("C:\\Users\\patel\\Downloads\\Stock_Files\\543940.csv","Stock_Unknown1");
        csvManager.viewStock("Stock_Unknown1");
        //csvManager.viewStock("Stock_Unknown");


    }
}
