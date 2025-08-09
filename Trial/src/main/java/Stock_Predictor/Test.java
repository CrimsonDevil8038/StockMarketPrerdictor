package Stock_Predictor;


public class Test {
    public static void main(String[] args) {
        JDBC_Manager jdbcManager = new JDBC_Manager();
        CSV_Manager csvManager = new CSV_Manager();

        csvManager.readCSV("C:\\Users\\patel\\Downloads\\Stock_Files\\543940.csv", "Data1");

    }
}
