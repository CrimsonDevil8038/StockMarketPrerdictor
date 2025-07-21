package Stock_Predictor;

import java.io.*;

public class Test {
    public static void main(String[] args) {

        String path = "S:\\Shlok\\LEARNING\\Java\\Sem_2_Project\\StockMarketPrerdictor\\Trial\\src\\main\\java\\ObjectSerialzation";
        File objectfile  = new File(path,"Name.ser");
        CSV_Manager csvManager = new CSV_Manager();

        ObjectManager objectManager = new ObjectManager();

        //objectManager.objectOutputStream(objectfile,csvManager);

        csvManager = (CSV_Manager) objectManager.objectInputStream(objectfile,csvManager);

        csvManager .CSV("S:\\Shlok\\LEARNING\\Java\\Sem_2_Project\\StockMarketPrerdictor\\Trial\\src\\main\\resources\\CSV_1.csv","CSV_1");

        csvManager.viewStock("CSV_1");

        objectManager.objectOutputStream(objectfile,csvManager);






    }
}
