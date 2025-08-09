package Stock_Predictor;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class JDBC_Connection {

    Connection SQLConnection() {
        String URL = "jdbc:postgresql://localhost:5432/sem_2pro";
        String USER = "postgres";
        String PASSWORD = "Shlok@0812";

        Connection connection = null;
        try {
            connection = DriverManager.getConnection(URL, USER, PASSWORD);

            if (connection != null && !connection.isClosed()) {
                System.out.println("\u001B[32mConnected to the database successfully.\u001B[0m");

            }

        } catch (SQLException e) {
            System.err.println(" Database connection failed.");
            e.printStackTrace();
        }

        return connection;
    }

}
