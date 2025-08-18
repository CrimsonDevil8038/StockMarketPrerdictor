package Stock_Predictor;


import Stock_Predictor.Account.AccountManager;
import Stock_Predictor.JDBC.JDBC_Manager;
import Stock_Predictor.Predict_And_Analysis.CSV_Manager;
import Stock_Predictor.Predict_And_Analysis.DataPreparation;

import java.sql.Date;

public class Test {
    public static void main(String[] args) {
        JDBC_Manager jdbcManager = new JDBC_Manager();
        CSV_Manager csvManager = new CSV_Manager();
        DataPreparation dataPreparation = new DataPreparation();
        AccountManager accountManager = new AccountManager();

        jdbcManager.create_User("Shlok");
        csvManager.readCSV("C:\\Users\\patel\\Downloads\\Data\\Tata Motors.csv","TataMotors");

    }
}
