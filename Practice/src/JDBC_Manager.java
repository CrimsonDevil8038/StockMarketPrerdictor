package Stock_Predictor;

import java.sql.*;
import java.time.Instant;

public class JDBC_Manager {
    private Connection connection;
        public JDBC_Manager() {
            this.connection = createConnection();
        }

        private Connection createConnection() {
            try {
                String url = "jdbc:postgresql://localhost:5432/Sem_2Pro";
                String user = "your_username";
                String password = "your_password";
                return DriverManager.getConnection(url, user, password);
            } catch (SQLException e) {
                throw new RuntimeException("Failed to create database connection", e);
            }
        }

        public Connection getConnection() throws SQLException {
            // Recreate connection if closed
            if (connection == null || connection.isClosed()) {
                connection = createConnection();
            }
            return connection;
        }

        // Add this method to check if a table exists
        public boolean tableExists(String tableName) throws SQLException {
            try (Connection conn = getConnection()) {
                DatabaseMetaData meta = conn.getMetaData();
                try (ResultSet rs = meta.getTables(null, null, tableName, new String[]{"TABLE"})) {
                    return rs.next();
                }
            }
        }


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
                    ",lastlogin TIMESTAMP);";

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
       CREATE OR REPLACE FUNCTION DateFormatter(date_string TEXT)
RETURNS DATE
LANGUAGE plpgsql
AS $$
DECLARE
    -- Variable to hold the successfully converted date.
    converted_date DATE;
BEGIN
    -- Return NULL immediately if input is NULL or empty to avoid unnecessary processing.
    IF date_string IS NULL OR date_string = '' THEN
        RETURN NULL;
    END IF;

    -- Attempt 1: ISO Format (YYYY-MM-DD)
    -- This is the standard and should be tried first.
    BEGIN
        converted_date := TO_DATE(date_string, 'YYYY-MM-DD');
        RETURN converted_date;
    EXCEPTION WHEN others THEN
        -- If it fails, silently continue to the next format.
    END;

    -- Attempt 2: Abbreviated Month (e.g., '1-Jul-24' or '01-Jul-2024')
    BEGIN
        converted_date := TO_DATE(date_string, 'DD-Mon-YY');
        RETURN converted_date;
    EXCEPTION WHEN others THEN
        -- Continue.
    END;

    -- Attempt 3: Full Month Name (e.g., '8-August-2024')
    BEGIN
        converted_date := TO_DATE(date_string, 'DD-Month-YYYY');
        RETURN converted_date;
    EXCEPTION WHEN others THEN
        -- Continue.
    END;

    -- Attempt 4: Common US Format (e.g., '08/25/2024')
    BEGIN
        converted_date := TO_DATE(date_string, 'MM/DD/YYYY');
        RETURN converted_date;
    EXCEPTION WHEN others THEN
        -- Continue.
    END;

    -- Attempt 5: Common European Format (e.g., '25/08/2024')
    BEGIN
        converted_date := TO_DATE(date_string, 'DD/MM/YYYY');
        RETURN converted_date;
    EXCEPTION WHEN others THEN
        -- Continue.
    END;

    -- Attempt 6: Textual Month (e.g., 'August 8, 2024')
    BEGIN
        converted_date := TO_DATE(date_string, 'Month DD, YYYY');
        RETURN converted_date;
    EXCEPTION WHEN others THEN
        -- Continue.
    END;

    -- If all attempts have failed, the format is unrecognized.
    -- Return NULL to indicate failure to parse.
    RETURN NULL;
END;
$$;

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

    boolean insert_StockData(String name, Stock_Data stockData) {

        try {
            String sql = "Insert into " + name + " values(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)ON CONFLICT DO NOTHING";
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

            return preparedStatement.executeUpdate() >= 0;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public Connection getConnection() {
        return connection;
    }

    void listTables() {
        DatabaseMetaData dbMeta = null;
        try {
            dbMeta = connection.getMetaData();
            ResultSet rs = dbMeta.getTables(connection.getCatalog(), "", null, new String[]{"TABLE"});
            while (rs.next()) {
                String tableName = rs.getString("TABLE_NAME");
                System.out.println(tableName);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    void update_recalculatedData(String name, Stock_Data stockData) {

        try {
            String sql = "INSERT INTO  " + name + " VALUES (" +
                    "?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)" +
                    "ON CONFLICT (official_date)" +
                    "DO UPDATE SET" +
                    "    open = EXCLUDED.open," +
                    "    high = EXCLUDED.high," +
                    "    low = EXCLUDED.low," +
                    "    close = EXCLUDED.close," +
                    "    volume = EXCLUDED.volume," +
                    "    vwap = EXCLUDED.vwap," +
                    "    typicalPrice = EXCLUDED.typicalPrice," +
                    "    sma_5 = EXCLUDED.sma_5," +
                    "    sma_10 = EXCLUDED.sma_10," +
                    "    sma_15 = EXCLUDED.sma_15," +
                    "    sma_50 = EXCLUDED.sma_50," +
                    "    sma_100 = EXCLUDED.sma_100," +
                    "    sma_200 = EXCLUDED.sma_200," +
                    "    ema_5 = EXCLUDED.ema_5," +
                    "    ema_10 = EXCLUDED.ema_10," +
                    "    ema_15 = EXCLUDED.ema_15," +
                    "    ema_50 = EXCLUDED.ema_50," +
                    "    ema_100 = EXCLUDED.ema_100," +
                    "    ema_200 = EXCLUDED.ema_200," +
                    "    rsi_14 = EXCLUDED.rsi_14," +
                    "    rsi_30 = EXCLUDED.rsi_30," +
                    "    macdline = EXCLUDED.macdline," +
                    "    signalline = EXCLUDED.signalline," +
                    "    upperband = EXCLUDED.upperband," +
                    "    middleband = EXCLUDED.middleband," +
                    "    lowerband = EXCLUDED.lowerband," +
                    "    stochastic = EXCLUDED.stochastic;";

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

            preparedStatement.executeUpdate();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }


    }

    void insert_user(String username, String password, String pancard, String aadharcard,
                     String mobile, Instant lastlogin) {
        try {
            String sql = "Insert into users(username,password,pancard,aadharcard,mobile,lastlogin) values (?,?,?,?,?,?);";
            PreparedStatement preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setString(1, username);
            preparedStatement.setString(2, password);
            preparedStatement.setString(3, pancard);
            preparedStatement.setString(4, aadharcard);
            preparedStatement.setString(5, mobile);
            preparedStatement.setTimestamp(6, Timestamp.from(lastlogin));

            preparedStatement.executeUpdate();
            create_User(username);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    boolean check_data(String username, String password) {
        try {
            String sql = "Select password from users where username = ?";
            PreparedStatement preparedStatement = connection.prepareStatement(sql);

            preparedStatement.setString(1, username);

            ResultSet resultSet = preparedStatement.executeQuery();
            resultSet.next();
            return password.equals(resultSet.getString(1));

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }


}

