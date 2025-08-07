package Stock_Predictor;

import java.io.File;

public class Test {
    public static void main(String[] args) {

       JDBC_Manager jdbcManager = new JDBC_Manager("Shlok@0812");
       jdbcManager.setSpecificStockData("Jio");

    }
}
