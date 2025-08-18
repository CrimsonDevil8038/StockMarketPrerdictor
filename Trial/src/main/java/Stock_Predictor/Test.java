package Stock_Predictor;


import Stock_Predictor.Predict_And_Analysis.CSV_Manager;

public class Test {
    public static void main(String[] args) {
//        JDBC_Manager jdbcManager = new JDBC_Manager();
        CSV_Manager csvManager = new CSV_Manager();





//        AccountManager accountManager = new AccountManager();
//        accountManager.data();
//        accountManager.check_credentials();



//        jdbcManager.listTables();
        csvManager.readCSV("D:\\Download\\Tata Motors.csv", "Tatamotors");



    }
}
