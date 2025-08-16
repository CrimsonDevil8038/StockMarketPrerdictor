package Stock_Predictor;


import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;

public class Test {
    public static void main(String[] args) {
        JDBC_Manager jdbcManager = new JDBC_Manager();

        AccountManager accountManager = new AccountManager();
        accountManager.data();
        accountManager.check_credentials();


        CSV_Manager csvManager = new CSV_Manager();
//        jdbcManager.listTables();

//        csvManager.readCSV("C:\\Users\\patel\\Downloads\\Stock_Files\\Reilance_1.csv", "Reliance");

    }
}
