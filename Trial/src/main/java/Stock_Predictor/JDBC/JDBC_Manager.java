package Stock_Predictor.JDBC;

import Stock_Predictor.Predict_And_Analysis.Stock_Data;

import java.sql.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Instant;

public class JDBC_Manager {


    private final Connection connection = new JDBC_Connection().SQLConnection();

    private final String create_Database = "Create Database Sem_2Pro;";



    String getUser_data(String username) {
        username = "USER_"+username;
        String User_data = "Create Table IF NOT EXISTS " + username + "(" +
                "UserId serial PRIMARY KEY,Stock varchar(50),Quantity Integer,Purchase_Price Numeric,PurchaseTime TimeStamp" +
                ");";
        return User_data;
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



    public Connection getConnection() {
        return connection;
    }

    public void listTables() {
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

    String getCreate_generalTable(String stockname) {
        String tableName = "STOCK_" + stockname;
        // CORRECTED: Added a comma before SMA_20Volume and set correct data type.
        return "CREATE TABLE IF NOT EXISTS " + tableName + " (" +
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
                " Stochastic NUMERIC(10,3)," +
                " SMA_20Volume NUMERIC(15,2)," +
                " VolDevNorm NUMERIC(10,3)" +
                ");";
    }

    public boolean create_table_GeneralTable(String stockName) {
        try (Statement statement = connection.createStatement()) {
            statement.execute(getCreate_generalTable(stockName));
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean insert_StockData(String name, Stock_Data stockData) {
        String tableName = "STOCK_" + name;
        // Updated to 29 columns
        String sql = "INSERT INTO " + tableName + " VALUES(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?) ON CONFLICT (Date) DO NOTHING";

        try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
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
            preparedStatement.setDouble(29, stockData.getSma_20volume());
            preparedStatement.setDouble(30, stockData.getVolDevNorm());

            return preparedStatement.executeUpdate() >= 0;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }



    public void insert_user(String username, String password, String pancard, String aadharcard,
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

    public boolean check_data(String username, String password) {
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


    public double get_minOpen(String stock_name, int timeperiod, Date date) {
        return getMinValue(stock_name, timeperiod, date, "Open");
    }

    public double get_minHigh(String stock_name, int timeperiod, Date date) {
        return getMinValue(stock_name, timeperiod, date, "High");
    }

    public double get_minLow(String stock_name, int timeperiod, Date date) {
        return getMinValue(stock_name, timeperiod, date, "Low");
    }

    public double get_minClose(String stock_name, int timeperiod, Date date) {
        return getMinValue(stock_name, timeperiod, date, "Close");
    }

    public double get_minVolume(String stock_name, int timeperiod, Date date) {
        return getMinValue(stock_name, timeperiod, date, "Volume");
    }

    public double get_minVWAP(String stock_name, int timeperiod, Date date) {
        return getMinValue(stock_name, timeperiod, date, "VWAP");
    }

    public double get_minTypicalPrice(String stock_name, int timeperiod, Date date) {
        return getMinValue(stock_name, timeperiod, date, "TypicalPrice");
    }

    public double get_minSMA_5(String stock_name, int timeperiod, Date date) {
        return getMinValue(stock_name, timeperiod, date, "SMA_5");
    }

    public double get_minSMA_10(String stock_name, int timeperiod, Date date) {
        return getMinValue(stock_name, timeperiod, date, "SMA_10");
    }

    public double get_minSMA_15(String stock_name, int timeperiod, Date date) {
        return getMinValue(stock_name, timeperiod, date, "SMA_15");
    }

    public double get_minSMA_50(String stock_name, int timeperiod, Date date) {
        return getMinValue(stock_name, timeperiod, date, "SMA_50");
    }

    public double get_minSMA_100(String stock_name, int timeperiod, Date date) {
        return getMinValue(stock_name, timeperiod, date, "SMA_100");
    }

    public double get_minSMA_200(String stock_name, int timeperiod, Date date) {
        return getMinValue(stock_name, timeperiod, date, "SMA_200");
    }

    public double get_minEMA_5(String stock_name, int timeperiod, Date date) {
        return getMinValue(stock_name, timeperiod, date, "EMA_5");
    }

    public double get_minEMA_10(String stock_name, int timeperiod, Date date) {
        return getMinValue(stock_name, timeperiod, date, "EMA_10");
    }

    public double get_minEMA_15(String stock_name, int timeperiod, Date date) {
        return getMinValue(stock_name, timeperiod, date, "EMA_15");
    }

    public double get_minEMA_50(String stock_name, int timeperiod, Date date) {
        return getMinValue(stock_name, timeperiod, date, "EMA_50");
    }

    public double get_minEMA_100(String stock_name, int timeperiod, Date date) {
        return getMinValue(stock_name, timeperiod, date, "EMA_100");
    }

    public double get_minEMA_200(String stock_name, int timeperiod, Date date) {
        return getMinValue(stock_name, timeperiod, date, "EMA_200");
    }

    public double get_minRSI_14(String stock_name, int timeperiod, Date date) {
        return getMinValue(stock_name, timeperiod, date, "RSI_14");
    }

    public double get_minRSI_30(String stock_name, int timeperiod, Date date) {
        return getMinValue(stock_name, timeperiod, date, "RSI_30");
    }

    public double get_minMACDLine(String stock_name, int timeperiod, Date date) {
        return getMinValue(stock_name, timeperiod, date, "MACDLine");
    }

    public double get_minSignalLine(String stock_name, int timeperiod, Date date) {
        return getMinValue(stock_name, timeperiod, date, "SignalLine");
    }

    public double get_minUpperBand(String stock_name, int timeperiod, Date date) {
        return getMinValue(stock_name, timeperiod, date, "upperBand");
    }

    public double get_minMiddleBand(String stock_name, int timeperiod, Date date) {
        return getMinValue(stock_name, timeperiod, date, "middleBand");
    }

    public double get_minLowerBand(String stock_name, int timeperiod, Date date) {
        return getMinValue(stock_name, timeperiod, date, "lowerBand");
    }

    public double get_minStochastic(String stock_name, int timeperiod, Date date) {
        return getMinValue(stock_name, timeperiod, date, "Stochastic");
    }


    public double get_maxOpen(String stock_name, int timeperiod, Date date) {
        return getMaxValue(stock_name, timeperiod, date, "Open");
    }

    public double get_maxHigh(String stock_name, int timeperiod, Date date) {
        return getMaxValue(stock_name, timeperiod, date, "High");
    }

    public double get_maxLow(String stock_name, int timeperiod, Date date) {
        return getMaxValue(stock_name, timeperiod, date, "Low");
    }

    public double get_maxClose(String stock_name, int timeperiod, Date date) {
        return getMaxValue(stock_name, timeperiod, date, "Close");
    }

    public double get_maxVolume(String stock_name, int timeperiod, Date date) {
        return getMaxValue(stock_name, timeperiod, date, "Volume");
    }

    public double get_maxVWAP(String stock_name, int timeperiod, Date date) {
        return getMaxValue(stock_name, timeperiod, date, "VWAP");
    }

    public double get_maxTypicalPrice(String stock_name, int timeperiod, Date date) {
        return getMaxValue(stock_name, timeperiod, date, "TypicalPrice");
    }

    public double get_maxSMA_5(String stock_name, int timeperiod, Date date) {
        return getMaxValue(stock_name, timeperiod, date, "SMA_5");
    }

    public double get_maxSMA_10(String stock_name, int timeperiod, Date date) {
        return getMaxValue(stock_name, timeperiod, date, "SMA_10");
    }

    public double get_maxSMA_15(String stock_name, int timeperiod, Date date) {
        return getMaxValue(stock_name, timeperiod, date, "SMA_15");
    }

    public double get_maxSMA_50(String stock_name, int timeperiod, Date date) {
        return getMaxValue(stock_name, timeperiod, date, "SMA_50");
    }

    public double get_maxSMA_100(String stock_name, int timeperiod, Date date) {
        return getMaxValue(stock_name, timeperiod, date, "SMA_100");
    }

    public double get_maxSMA_200(String stock_name, int timeperiod, Date date) {
        return getMaxValue(stock_name, timeperiod, date, "SMA_200");
    }

    public double get_maxEMA_5(String stock_name, int timeperiod, Date date) {
        return getMaxValue(stock_name, timeperiod, date, "EMA_5");
    }

    public double get_maxEMA_10(String stock_name, int timeperiod, Date date) {
        return getMaxValue(stock_name, timeperiod, date, "EMA_10");
    }

    public double get_maxEMA_15(String stock_name, int timeperiod, Date date) {
        return getMaxValue(stock_name, timeperiod, date, "EMA_15");
    }

    public double get_maxEMA_50(String stock_name, int timeperiod, Date date) {
        return getMaxValue(stock_name, timeperiod, date, "EMA_50");
    }

    public double get_maxEMA_100(String stock_name, int timeperiod, Date date) {
        return getMaxValue(stock_name, timeperiod, date, "EMA_100");
    }

    public double get_maxEMA_200(String stock_name, int timeperiod, Date date) {
        return getMaxValue(stock_name, timeperiod, date, "EMA_200");
    }

    public double get_maxRSI_14(String stock_name, int timeperiod, Date date) {
        return getMaxValue(stock_name, timeperiod, date, "RSI_14");
    }

    public double get_maxRSI_30(String stock_name, int timeperiod, Date date) {
        return getMaxValue(stock_name, timeperiod, date, "RSI_30");
    }

    public double get_maxMACDLine(String stock_name, int timeperiod, Date date) {
        return getMaxValue(stock_name, timeperiod, date, "MACDLine");
    }

    public double get_maxSignalLine(String stock_name, int timeperiod, Date date) {
        return getMaxValue(stock_name, timeperiod, date, "SignalLine");
    }

    public double get_maxUpperBand(String stock_name, int timeperiod, Date date) {
        return getMaxValue(stock_name, timeperiod, date, "upperBand");
    }

    public double get_maxMiddleBand(String stock_name, int timeperiod, Date date) {
        return getMaxValue(stock_name, timeperiod, date, "middleBand");
    }

    public double get_maxLowerBand(String stock_name, int timeperiod, Date date) {
        return getMaxValue(stock_name, timeperiod, date, "lowerBand");
    }

    public double get_maxStochastic(String stock_name, int timeperiod, Date date) {
        return getMaxValue(stock_name, timeperiod, date, "Stochastic");
    }


    private double getMinValue(String stock_name, int timeperiod, Date date, String column) {
        try {
            String sql = "SELECT MIN(" + column + ") FROM " + stock_name +
                    " WHERE date <= ? AND date >= ? - INTERVAL '" + timeperiod + " day'";
            PreparedStatement ps = connection.prepareStatement(sql);
            ps.setDate(1, date);
            ps.setDate(2, date);
            ResultSet rs = ps.executeQuery();
            rs.next();
            return rs.getDouble(1);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private double getMaxValue(String stock_name, int timeperiod, Date date, String column) {
        try {
            String sql = "SELECT MAX(" + column + ") FROM " + stock_name +
                    " WHERE date <= ? AND date >= ? - INTERVAL '" + timeperiod + " day'";
            PreparedStatement ps = connection.prepareStatement(sql);
            ps.setDate(1, date);
            ps.setDate(2, date);
            ResultSet rs = ps.executeQuery();
            rs.next();
            return rs.getDouble(1);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public Date getFormattedDate(String dateString) {


        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        try {

            java.util.Date parsedDate = format.parse(dateString);

            return new java.sql.Date(parsedDate.getTime());
        } catch (ParseException e) {

            System.err.println("Error parsing date string: " + dateString + ". Please use 'yyyy-MM-dd' format.");
            throw new RuntimeException("Failed to parse date: " + dateString, e);
        }
    }

    public Date get_minDate(String stock_name) {
        try {
            String sql = "SELECT MIN(date) FROM " + stock_name;
            PreparedStatement preparedStatement = connection.prepareStatement(sql);

            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                return resultSet.getDate(1);
            }
            return null;
        } catch (SQLException e) {
            throw new RuntimeException("Error retrieving min date", e);
        }
    }

    public Date get_maxDate(String stock_name) {
        try {
            String sql = "SELECT MAX(date) FROM " + stock_name;
            PreparedStatement preparedStatement = connection.prepareStatement(sql);

            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                return resultSet.getDate(1);
            }
            return null;
        } catch (SQLException e) {
            throw new RuntimeException("Error retrieving max date", e);
        }
    }

    public int get_daysBetween(Date date1,Date date2) {
        try {
            String sql = "SELECT ABS(EXTRACT(DAY FROM AGE(? , ?)))";
            PreparedStatement preparedStatement = connection.prepareStatement(sql);

            preparedStatement.setDate(1, date1);
            preparedStatement.setDate(2, date2);

            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                return resultSet.getInt(1);
            }
            return 0;
        } catch (SQLException e) {
            throw new RuntimeException("Error calculating days between dates", e);
        }
    }

    public Date getPreviousDate(Date inputDate) {
        try {
            String sql = "{ ? = call get_previous_date(?) }";
            CallableStatement callableStatement = connection.prepareCall(sql);

            callableStatement.registerOutParameter(1, java.sql.Types.DATE);
            callableStatement.setDate(2, inputDate);

            callableStatement.execute();

            return callableStatement.getDate(1);

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    // Generic method to avoid repetitive code
    private double getColumnValueOnDate(String stockName, String columnName, Date date) {
        try {
            String sql = "SELECT " + columnName + " FROM " + stockName + " WHERE date = ?";
            PreparedStatement ps = connection.prepareStatement(sql);
            ps.setDate(1, date);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return rs.getDouble(1);
            }
            return Double.NaN; // No value found
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
    

    public double getOpenOnDate(String stockName, Date date) {
        return getColumnValueOnDate(stockName, "Open", date);
    }

    public double getHighOnDate(String stockName, Date date) {
        return getColumnValueOnDate(stockName, "High", date);
    }

    public double getLowOnDate(String stockName, Date date) {
        return getColumnValueOnDate(stockName, "Low", date);
    }

    public double getCloseOnDate(String stockName, Date date) {
        return getColumnValueOnDate(stockName, "Close", date);
    }

    public double getVolumeOnDate(String stockName, Date date) {
        return getColumnValueOnDate(stockName, "Volume", date);
    }

    public double getVWAPOnDate(String stockName, Date date) {
        return getColumnValueOnDate(stockName, "VWAP", date);
    }

    public double getTypicalPriceOnDate(String stockName, Date date) {
        return getColumnValueOnDate(stockName, "TypicalPrice", date);
    }

    public double getSMA5OnDate(String stockName, Date date) {
        return getColumnValueOnDate(stockName, "SMA_5", date);
    }

    public double getSMA10OnDate(String stockName, Date date) {
        return getColumnValueOnDate(stockName, "SMA_10", date);
    }

    public double getSMA15OnDate(String stockName, Date date) {
        return getColumnValueOnDate(stockName, "SMA_15", date);
    }

    public double getSMA50OnDate(String stockName, Date date) {
        return getColumnValueOnDate(stockName, "SMA_50", date);
    }

    public double getSMA100OnDate(String stockName, Date date) {
        return getColumnValueOnDate(stockName, "SMA_100", date);
    }

    public double getSMA200OnDate(String stockName, Date date) {
        return getColumnValueOnDate(stockName, "SMA_200", date);
    }

    public double getEMA5OnDate(String stockName, Date date) {
        return getColumnValueOnDate(stockName, "EMA_5", date);
    }

    public double getEMA10OnDate(String stockName, Date date) {
        return getColumnValueOnDate(stockName, "EMA_10", date);
    }

    public double getEMA15OnDate(String stockName, Date date) {
        return getColumnValueOnDate(stockName, "EMA_15", date);
    }

    public double getEMA50OnDate(String stockName, Date date) {
        return getColumnValueOnDate(stockName, "EMA_50", date);
    }

    public double getEMA100OnDate(String stockName, Date date) {
        return getColumnValueOnDate(stockName, "EMA_100", date);
    }

    public double getEMA200OnDate(String stockName, Date date) {
        return getColumnValueOnDate(stockName, "EMA_200", date);
    }

    public double getRSI14OnDate(String stockName, Date date) {
        return getColumnValueOnDate(stockName, "RSI_14", date);
    }

    public double getRSI30OnDate(String stockName, Date date) {
        return getColumnValueOnDate(stockName, "RSI_30", date);
    }

    public double getMACDLineOnDate(String stockName, Date date) {
        return getColumnValueOnDate(stockName, "MACDLine", date);
    }

    public double getSignalLineOnDate(String stockName, Date date) {
        return getColumnValueOnDate(stockName, "SignalLine", date);
    }

    public double getUpperBandOnDate(String stockName, Date date) {
        return getColumnValueOnDate(stockName, "upperBand", date);
    }

    public double getMiddleBandOnDate(String stockName, Date date) {
        return getColumnValueOnDate(stockName, "middleBand", date);
    }

    public double getLowerBandOnDate(String stockName, Date date) {
        return getColumnValueOnDate(stockName, "lowerBand", date);
    }

    public double getStochasticOnDate(String stockName, Date date) {
        return getColumnValueOnDate(stockName, "Stochastic", date);
    }

    public double get_minSMA_20Volume(String stock_name, int timeperiod, Date date) {
        return getMinValue(stock_name, timeperiod, date, "SMA_20Volume");
    }

    public double get_maxSMA_20Volume(String stock_name, int timeperiod, Date date) {
        return getMaxValue(stock_name, timeperiod, date, "SMA_20Volume");
    }

    public double getSMA20VolumeOnDate(String stockName, Date date) {
        return getColumnValueOnDate(stockName, "SMA_20Volume", date);
    }

    public double get_minVolDevNorm(String stock_name, int timeperiod, Date date) {
        return getMinValue(stock_name, timeperiod, date, "VolDevNorm");
    }

    public double get_maxVolDevNorm(String stock_name, int timeperiod, Date date) {
        return getMaxValue(stock_name, timeperiod, date, "VolDevNorm");
    }

    public double getVolDevNormOnDate(String stockName, Date date) {
        return getColumnValueOnDate(stockName, "VolDevNorm", date);
    }



}




