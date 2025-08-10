package Stock_Predictor;


import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;

public class Test {
    public static void main(String[] args) {
        JDBC_Manager jdbcManager = new JDBC_Manager();
        CSV_Manager csvManager = new CSV_Manager();
//        jdbcManager.listTables();

        csvManager.readCSV("C:\\Users\\patel\\Downloads\\Reilance_1.csv", "Reliance");

    }
}
