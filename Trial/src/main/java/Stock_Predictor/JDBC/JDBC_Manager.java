package Stock_Predictor.JDBC;

import Stock_Predictor.Predict_And_Analysis.Stock_Data;

import java.sql.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.util.Calendar;

import static Stock_Predictor.Color.*;

public class JDBC_Manager {


    private final Connection connection = JDBC_Connection.SQLConnection();


    public boolean create_Rest() {
        try {
            String table_Users = "Create Table If Not Exists USERS(" +
                    "UserId serial PRIMARY KEY,Username varchar(50),Password varchar(50)," +
                    "Pancard varchar(10) NOT NULL UNIQUE ,AadharCard varchar(12) NOT NULL UNIQUE,Mobile varchar(10) NOT NULL UNIQUE" +
                    ",lastlogin TIMESTAMP);";


            String portfolio_history = "CREATE TABLE IF NOT EXISTS portfolio_history (" +
                    " history_id SERIAL PRIMARY KEY," +
                    " user_id  INTEGER NOT NULL," +
                    "stock_name VARCHAR(50) NOT NULL," +
                    " action VARCHAR(10) NOT NULL," +
                    " quantity_change INTEGER," +
                    " price_per_share NUMERIC(20, 4)," +
                    " transaction_time TIMESTAMP WITH TIME ZONE DEFAULT NOW()" +
                    ");";

            Statement statement = connection.createStatement();
            statement.execute(table_Users);
            statement.execute(portfolio_history);
            return true;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }





    public Date toCall_Dataformatter(String dateString) {

        try {
            PreparedStatement preparedStatement = connection.prepareStatement("SELECT DateFormatter(?)");
            preparedStatement.setString(1, dateString);
            ResultSet resultSet = preparedStatement.executeQuery();
            resultSet.next();
            return resultSet.getDate(1);

        } catch (Exception e) {
            System.out.println(RED + "Unable to Format Date" + RESET);
            return null;
        }
    }


    public boolean check(String name) {
        DatabaseMetaData dbMeta = null;
        try {
            dbMeta = connection.getMetaData();
            ResultSet rs = dbMeta.getTables(connection.getCatalog(), "", null, new String[]{"TABLE"});
            while (rs.next()) {
                String tableName = rs.getString("TABLE_NAME");
                if (tableName.equalsIgnoreCase(name)) {
                    return true;
                }
            }
            return false;
        } catch (SQLException e) {
            System.out.println(RED + "SQL EXCEPTION " +e.getMessage()+" "+ e.getCause() + RESET);
            return true;
        }
    }

    public void Stock_tables() {
        DatabaseMetaData dbMeta = null;
        try {
            dbMeta = connection.getMetaData();
            ResultSet rs = dbMeta.getTables(connection.getCatalog(), "", null, new String[]{"TABLE"});
            while (rs.next()) {
                String tableName = rs.getString("TABLE_NAME");
                if (tableName.contains("stock_")) {
                    String[] name = tableName.split("stock_");
                    System.out.println("Table: " + BLUE + name[1] + RESET);
                }
            }
        } catch (SQLException e) {
            System.out.println(RED + "SQL Exception : Unable to list tables " + RESET);
            return;
        }
    }


    String getCreate_generalTable(String stockname) {
        String tableName = "STOCK_" + stockname;

        return "CREATE TABLE IF NOT EXISTS " + tableName + " (" +
                " Date DATE PRIMARY KEY," +
                " Open NUMERIC(20,4)," +
                " High NUMERIC(20,4)," +
                " Low NUMERIC(20,4)," +
                " Close NUMERIC(20,4)," +
                " Volume NUMERIC(25,0)," +
                " VWAP NUMERIC(20,4)," +
                " TypicalPrice NUMERIC(20,4)," +
                " SMA_5 NUMERIC(20,4)," +
                " SMA_10 NUMERIC(20,4)," +
                " SMA_15 NUMERIC(20,4)," +
                " SMA_50 NUMERIC(20,4)," +
                " SMA_100 NUMERIC(20,4)," +
                " SMA_200 NUMERIC(20,4)," +
                " EMA_5 NUMERIC(20,4)," +
                " EMA_10 NUMERIC(20,4)," +
                " EMA_15 NUMERIC(20,4)," +
                " EMA_50 NUMERIC(20,4)," +
                " EMA_100 NUMERIC(20,4)," +
                " EMA_200 NUMERIC(20,4)," +
                " RSI_14 NUMERIC(20,4)," +
                " RSI_30 NUMERIC(20,4)," +
                " MACDLine NUMERIC(20,4)," +
                " SignalLine NUMERIC(20,4)," +
                " upperBand NUMERIC(20,4)," +
                " middleBand NUMERIC(20,4)," +
                " lowerBand NUMERIC(20,4)," +
                " Stochastic NUMERIC(20,4)," +
                " SMA_20Volume NUMERIC(25,4)," +
                " VolDevNorm NUMERIC(20,3)" +
                ");";
    }

    public boolean create_table_GeneralTable(String stockName) {
        try (Statement statement = connection.createStatement()) {
            statement.execute(getCreate_generalTable(stockName));
            return true;
        } catch (SQLException e) {
            System.out.println(RED + "SQL EXCEPTION " +e.getMessage()+" "+ e.getCause() + RESET);
            return false;
        }
    }

    public boolean insert_StockData(String name, Stock_Data stockData) {
        String tableName = "STOCK_" + name;

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
            System.out.println(RED + "SQL EXCEPTION " +e.getMessage()+" "+ e.getCause() + RESET);
            return  false;
        }
    }


    public boolean insert_user(String username, String password, String pancard, String aadharcard,
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

            return preparedStatement.executeUpdate() >= 0;

        } catch (Exception e) {
            System.out.println(RED + "SQL EXCEPTION " +e.getMessage()+" "+ e.getCause() + RESET);
            throw new RuntimeException();
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
            System.out.println(RED + "EXCEPTION " +e.getMessage()+" "+ e.getCause() + RESET);
            return false;
        }
    }


    public double getMinValue(String stock_name, int timeperiod, Date endDate, String column) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(endDate);
        cal.add(Calendar.DAY_OF_MONTH, -timeperiod);
        Date startDate = new java.sql.Date(cal.getTimeInMillis());

        String sql = "SELECT MIN(" + column + ") FROM " + stock_name +
                " WHERE date <= ? AND date >= ?";

        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setDate(1, endDate);
            pstmt.setDate(2, startDate);

            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getDouble(1);
                }
            }
        } catch (SQLException e) {
            System.out.println(RED + "SQL EXCEPTION " +e.getMessage()+" "+ e.getCause() + RESET);
            throw new RuntimeException(e);
        }

        return 0.0;
    }

    private double getMaxValue(String stock_name, int timeperiod, Date date, String column) {

        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        cal.add(Calendar.DAY_OF_MONTH, -timeperiod);
        Date startDate = new java.sql.Date(cal.getTimeInMillis());

        String sql = "SELECT MAX(" + column + ") FROM " + stock_name +
                " WHERE date <= ? AND date >= ?";

        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setDate(1, date);
            ps.setDate(2, startDate);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getDouble(1);
                }
            }
        } catch (Exception e) {
            System.out.println(RED + "SQL EXCEPTION " +e.getMessage()+" "+ e.getCause() + RESET);
            throw new RuntimeException(e);
        }

        return 0.0;
    }

    public Date getFormattedDate(String dateString) {


        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        try {

            java.util.Date parsedDate = format.parse(dateString);

            return new java.sql.Date(parsedDate.getTime());
        } catch (ParseException e) {

            System.out.println(RED + "Error parsing date string: " + dateString + ". Please use 'yyyy-MM-dd' format." + RESET);
            throw new RuntimeException();
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
            System.out.println(RED + "SQL EXCEPTION " +e.getMessage()+" "+ e.getCause() + RESET);
            return null;
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
            System.out.println(RED + "SQL EXCEPTION " +e.getMessage()+" "+ e.getCause() + RESET);
            return null;
        }
    }


    public Date getPreviousDate(Date currentDate, String stockTableName) {

        String sql = "SELECT MAX(date) FROM " + stockTableName + " WHERE date < ?";

        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {

            pstmt.setDate(1, currentDate);

            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getDate(1);
                }
            }
            return null;

        } catch (SQLException e) {
            throw new RuntimeException("Error getting previous date from table " + stockTableName, e);
        }
    }


    private double getColumnValueOnDate(String stockName, String columnName, Date date) {
        try {
            String sql = "SELECT " + columnName + " FROM " + stockName + " WHERE date = ?";
            PreparedStatement ps = connection.prepareStatement(sql);
            ps.setDate(1, date);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return rs.getDouble(1);
            }
            return Double.NaN;
        } catch (Exception e) {
            System.out.println(RED + "SQL EXCEPTION " +e.getMessage()+" "+ e.getCause() + RESET);
            return 0.0;
        }
    }


    public double getCloseOnDate(String stockName, Date date) {
        return getColumnValueOnDate(stockName, "Close", date);
    }


    public double getVWAPOnDate(String stockName, Date date) {
        return getColumnValueOnDate(stockName, "VWAP", date);
    }


    public double getSMA5OnDate(String stockName, Date date) {
        return getColumnValueOnDate(stockName, "SMA_5", date);
    }

    public double getSMA10OnDate(String stockName, Date date) {
        return getColumnValueOnDate(stockName, "SMA_10", date);
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


    public double getVolDevNormOnDate(String stockName, Date date) {
        return getColumnValueOnDate(stockName, "VolDevNorm", date);
    }

    public int getTradingDayCount(String stock) {
        String sql = "SELECT get_trading_day_count(?)";

        try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            preparedStatement.setString(1, stock);
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                if (resultSet.next()) {
                    return resultSet.getInt(1);
                }
            }
            return 0;
        } catch (SQLException e) {
            System.out.println(RED+"Unable to Count Trading Days"+RESET);
            throw new RuntimeException();
        }
    }

        /*
        CREATE TABLE IF NOT EXISTS portfolio_history (
                                                 history_id SERIAL PRIMARY KEY,
                                                 user_id VARCHAR(50) NOT NULL,
                                                 stock VARCHAR(50) NOT NULL,
                                                 action VARCHAR(10) NOT NULL, -- 'BUY', 'SELL', 'UPDATE'
                                                 quantity_change INTEGER,
                                                 price_per_share NUMERIC(20, 4),
                                                 transaction_time TIMESTAMP WITH TIME ZONE DEFAULT NOW());
         */



    public Date calculateNextBusinessDay(Date currentDate) {
        String sql = "SELECT calculate_next_business_day(?)";

        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setDate(1, currentDate);

            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getDate(1);
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error calculating next business day", e);
        }
        return null;
    }

    public Connection getConnection() {
        return  connection;
    }

    public int get_userid(String username) {
        String sql = "SELECT userid FROM users WHERE username = ?";

        // The try-with-resources block is fine, but the logic inside needs a fix.
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) { // Use the existing connection
            pstmt.setString(1, username);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) { // <-- THIS IS THE FIX. Check if a result exists.
                return rs.getInt("userid");
            }

        } catch (SQLException e) {
            System.err.println("Error getting userid: " + e.getMessage());
            e.printStackTrace();
        }
        return -1; // Return an invalid ID if not found
    }


    public void displayMinAndMaxDateAndClose(String stockName) {
        String tablename = "stock_"+stockName.toLowerCase();
       String maxsql = "Select close,date from "+ tablename+" where  close  = (Select max(close) from "+tablename+")";
        String minsql = "Select close,date from "+ tablename+" where  close  = (Select min(close) from "+tablename+")";

        try {
            Statement st = connection.createStatement();
            ResultSet getmax = st.executeQuery(maxsql);
            getmax.next();
            System.out.println("Max Value: "+getmax.getString(1)+" Date: "+getmax.getString(2));

            ResultSet getmin = st.executeQuery(minsql);
            getmin.next();
            System.out.println("Min Value: "+getmin.getString(1)+" Date: "+getmin.getString(2));

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

    }
}
