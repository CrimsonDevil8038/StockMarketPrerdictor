package Stock_Predictor;

import Stock_Predictor.JDBC.JDBC_Manager;

import java.util.Scanner;

public class Test {
    public static void main(String[] args) {
        JDBC_Manager jdbcManager = new JDBC_Manager();
        Scanner scanner = new Scanner(System.in);
        jdbcManager.Stock_tables();
        System.out.print("Enter Name: ");
        String name = scanner.next();
        jdbcManager.displayMinAndMaxDateAndClose(name);

    }


}
