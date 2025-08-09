package Stock_Predictor;

import java.sql.*;

public class JDBC_Manager {


    private final Connection connection = new JDBC_Connection().SQLConnection();

    private final String create_Database = "Create Database Sem_2Pro;";


    String getCreate_generalTable(String stockname) {
        String tableName = stockname;
        String create_generalTable = "CREATE TABLE IF NOT EXISTS " + tableName + " (" +
                " Date DATE PRIMARY KEY," +
                " Open NUMERIC(10,3)," +
                " High NUMERIC(10,3)," +
                " Low NUMERIC(10,3)," +
                " Close NUMERIC(10,3)," +
                " Volume NUMERIC(15,2)," +
                " VWAP NUMERIC(10,3)," +
                " TypicalPrice NUMERIC(10,3)," +
                " SMA_5 NUMERIC(10,3)," +
                " SMA_10 NUMERIC(10,3)," +
                " SMA_15 NUMERIC(10,3)," +
                " SMA_50 NUMERIC(10,3)," +
                " SMA_100 NUMERIC(10,3)," +
                " SMA_200 NUMERIC(10,3)," +
                " EMA_5 NUMERIC(10,3)," +
                " EMA_10 NUMERIC(10,3)," +
                " EMA_15 NUMERIC(10,3)," +
                " EMA_50 NUMERIC(10,3)," +
                " EMA_100 NUMERIC(10,3)," +
                " EMA_200 NUMERIC(10,3)," +
                " RSI_14 NUMERIC(10,3)," +
                " RSI_30 NUMERIC(10,3)," +
                " MACDLine NUMERIC(10,3)," +
                " SignalLine NUMERIC(10,3)," +
                " upperBand NUMERIC(10,3)," +
                " middleBand NUMERIC(10,3)," +
                " lowerBand NUMERIC(10,3)," +
                " Stochastic NUMERIC(10,3)" +
                ");";

        return create_generalTable;
    }

    String getUser_data(String username) {
        String User_data = "Create Table IF NOT EXISTS " + username + "(" +
                "UserId serial PRIMARY KEY,Stock varchar(50),Quantity Integer,Purchase_Price Numeric" +
                ");";
        return User_data;
    }

    boolean create_table_GeneralTable(String stockName) {
        try {
            Statement statement = connection.createStatement();
            return !statement.execute(getCreate_generalTable(stockName));
        } catch (Exception e) {
            return false;
        }
    }

    boolean create_User(String Name) {
        try {
            Statement statement = connection.createStatement();
            return !statement.execute(getUser_data(Name));
        } catch (Exception e) {
            return false;
        }
    }

    boolean create_Rest() {
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
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public Date toCall_Dataformatter(String dateString) {
        /*
        CREATE OR REPLACE FUNCTION DateFormatter(conversionDate TEXT)
            RETURNS DATE
            LANGUAGE plpgsql AS $$
        DECLARE

        BEGIN
            RETURN TO_DATE(conversionDate, 'DD-Mon-YY');
        END;
        $$

         */
        try {
            PreparedStatement preparedStatement = connection.prepareStatement("SELECT DateFormatter(?)");
            preparedStatement.setString(1, dateString);
            ResultSet resultSet = preparedStatement.executeQuery();
            resultSet.next();
            return resultSet.getDate(1);

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    boolean insert_StockData(String name,Stock_Data stockData){

        try{
            String sql = "Insert into "+name+" values(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)ON CONFLICT DO NOTHING";
            // Assuming 'stockData' is your data object and 'connection' is your SQL connection
            PreparedStatement preparedStatement = connection.prepareStatement(sql);

            preparedStatement.setDate(1, stockData.getOfficial_date());
            preparedStatement.setDouble(2, stockData.getOpen());
            preparedStatement.setDouble(3, stockData.getHigh());
            preparedStatement.setDouble(4, stockData.getLow());
            preparedStatement.setDouble(5, stockData.getClose());
            preparedStatement.setDouble(6, stockData.getVolume());
            preparedStatement.setDouble(7, stockData.getVwap());
            preparedStatement.setDouble(8, stockData.getTypicalPrice());
            preparedStatement.setDouble(9, stockData.getSma_5());
            preparedStatement.setDouble(10, stockData.getSma_10());
            preparedStatement.setDouble(11, stockData.getSma_15());
            preparedStatement.setDouble(12, stockData.getSma_50());
            preparedStatement.setDouble(13, stockData.getSma_100());
            preparedStatement.setDouble(14, stockData.getSma_200());
            preparedStatement.setDouble(15, stockData.getEma_5());
            preparedStatement.setDouble(16, stockData.getEma_10());
            preparedStatement.setDouble(17, stockData.getEma_15());
            preparedStatement.setDouble(18, stockData.getEma_50());
            preparedStatement.setDouble(19, stockData.getEma_100());
            preparedStatement.setDouble(20, stockData.getEma_200());
            preparedStatement.setDouble(21, stockData.getRsi_14());
            preparedStatement.setDouble(22, stockData.getRsi_30());
            preparedStatement.setDouble(23, stockData.getMacdline());
            preparedStatement.setDouble(24, stockData.getSignalline());
            preparedStatement.setDouble(25, stockData.getUpperband());
            preparedStatement.setDouble(26, stockData.getMiddleband());
            preparedStatement.setDouble(27, stockData.getLowerband());
            preparedStatement.setDouble(28, stockData.getStochastic());

            if(preparedStatement.executeUpdate()>=0){
                return  true;
            }else{
                return  false;
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

}

