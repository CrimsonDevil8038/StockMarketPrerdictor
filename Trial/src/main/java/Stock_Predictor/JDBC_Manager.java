package Stock_Predictor;
import java.sql.*;

public class JDBC_Manager {


    private Connection connection = new JDBC_Connection().SQLConnection();

    private String  create_Database = "Create Database Sem_2Pro;";


    String getCreate_generalTable(String stockname){
        String tableName = stockname;
        String  create_generalTable = "CREATE TABLE IF NOT EXISTS "+tableName+"(" +
                "    Date DATE,Open NUMERIC(10,3),High NUMERIC(10,3),Low NUMERIC(10,3),Close NUMERIC(10,3),Volume NUMERIC(10,3),VWAP NUMERIC(10,3),TypicalPrice NUMERIC(10,3)," +
                "    SMA_5 NUMERIC(10,3),SMA_10 NUMERIC(10,3),SMA_15 NUMERIC(10,3),SMA_50 NUMERIC(10,3),SMA_100 NUMERIC(10,3),SMA_200 NUMERIC(10,3)," +
                "    EMA_5 NUMERIC(10,3),EMA_10 NUMERIC(10,3),EMA_15 NUMERIC(10,3),EMA_50 NUMERIC(10,3),EMA_100 NUMERIC(10,3),EMA_200 NUMERIC(10,3)," +
                "    MACDLine NUMERIC(10,3),SignalLine NUMERIC(10,3)," +
                "    upperBand NUMERIC(10,3),middleBand NUMERIC(10,3),lowerBand NUMERIC(10,3)," +
                "    Stochastic NUMERIC(10,3)" +
                ");";

        return  create_generalTable;
    }

    String getUser_data(String username){
        String User_data = "Create Table IF NOT EXISTS "+username+"(" +
                "UserId serial PRIMARY KEY,Stock varchar(50),Quantity Integer,Purchase_Price Numeric" +
                ");";
        return User_data;
    }

    boolean create_table_GeneralTable(String stockName){
        try{
            Statement statement = connection.createStatement();
            return  !statement.execute(getCreate_generalTable(stockName));
        } catch (Exception e) {
            return  false;
        }
    }

    boolean create_User(String Name){
        try{
            Statement statement = connection.createStatement();
            return  !statement.execute(getUser_data(Name));
        } catch (Exception e) {
            return  false;
        }
    }

    boolean create_Rest(){
        try {
            String table_Users = "Create Table If Not Exists USERS(" +
                    "UserId serial PRIMARY KEY,Username varchar(50),Password varchar(50)," +
                    "Pancard varchar(10) NOT NULL UNIQUE ,AadharCard varchar(12) NOT NULL UNIQUE,Mobile varchar(10) NOT NULL UNIQUE" +
                    ");";

            String prediction_table = "Create Table If not Exists PredictionAcc(" +
                    "Stock varchar(50),Prediction_Short numeric(3,2),Prediction_Long numeric(3,2)" +
                    ");";

            Statement statement = connection.createStatement();

            statement.execute(table_Users);
            statement.execute(prediction_table);
            return true;
        }
        catch (Exception e){
            throw new RuntimeException(e);
        }
    }



}
