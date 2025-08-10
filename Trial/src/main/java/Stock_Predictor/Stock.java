package Stock_Predictor;

import java.sql.*;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

public class Stock {

    private final LinkedList<Stock_Data> stock_data = new LinkedList<Stock_Data>();
    private String name;
    private  JDBC_Manager jdbcManager = new JDBC_Manager();

    public Stock(String name) {
        this.name = name;
    }

    public Stock() {
    }

    public String getName() {
        return name;
    }

    public LinkedList<Stock_Data> getStock_data() {
        return stock_data;
    }

    void calculate_() {
        DerivedIndicators derivedIndicators = new DerivedIndicators();

        for (int i = 0; i < stock_data.size(); i++) {
            Stock_Data currentDayData = stock_data.get(i);

            List<Stock_Data> historicalData = stock_data.subList(0, i + 1);

            //Typical Price
            currentDayData.setTypicalPrice(DerivedIndicators.getTypicalPrice(currentDayData));

            //VWAP-20 Days
            int vwapPeriod = Math.min(i + 1, 20);
            List<Stock_Data> vwapPeriodData = stock_data.subList(i - vwapPeriod + 1, i + 1);
            currentDayData.setVwap(DerivedIndicators.calculateVWAP(vwapPeriodData));

            //SMA_5
            currentDayData.setSma_5(DerivedIndicators.calculateSMA(historicalData, 5));

            //SMA_10
            currentDayData.setSma_10(DerivedIndicators.calculateSMA(historicalData, 10));

            //SMA_15
            currentDayData.setSma_15(DerivedIndicators.calculateSMA(historicalData, 15));

            //SMA_50
            currentDayData.setSma_50(DerivedIndicators.calculateSMA(historicalData, 50));

            //SMA_100
            currentDayData.setSma_100(DerivedIndicators.calculateSMA(historicalData, 100));

            //SMA_200
            currentDayData.setSma_200(DerivedIndicators.calculateSMA(historicalData, 200));

            //EMA_5
            currentDayData.setEma_5(DerivedIndicators.calculateEMA(historicalData, 5));

            //EMA_10
            currentDayData.setEma_10(DerivedIndicators.calculateEMA(historicalData, 10));

            //EMA_15
            currentDayData.setEma_15(DerivedIndicators.calculateEMA(historicalData, 15));

            //EMA_50
            currentDayData.setEma_50(DerivedIndicators.calculateEMA(historicalData, 50));

            //EMA_100
            currentDayData.setEma_100(DerivedIndicators.calculateEMA(historicalData, 100));

            //EMA_200
            currentDayData.setEma_200(DerivedIndicators.calculateEMA(historicalData, 200));

            //RSI_14
            currentDayData.setRsi_14(DerivedIndicators.calculateRSI(historicalData, 14));

            //RSI_30
            currentDayData.setRsi_30(DerivedIndicators.calculateRSI(historicalData, 30));

            //MACDLine and SignalLine
            MACDResult macdResult = DerivedIndicators.calculateMACD(historicalData);
            currentDayData.setMacdline(macdResult.getMacdLine());
            currentDayData.setSignalline(macdResult.getSignalLine());

            //UpperBand and MiddleBand and LowerBand
            BollingerBands bollingerBands = DerivedIndicators.calculateBollingerBands(historicalData, 20, 2);
            currentDayData.setUpperband(bollingerBands.getUpperBand());
            currentDayData.setMiddleband(bollingerBands.getMiddleBand());
            currentDayData.setLowerband(bollingerBands.getLowerBand());

            //Stochastic
            currentDayData.setStochastic(DerivedIndicators.calculateStochasticOscillator(historicalData, 14));

            currentDayData.setOfficial_date(jdbcManager.toCall_Dataformatter(currentDayData.getDate()));
        }


    }


    void toPostgreSQL(){


        jdbcManager.create_table_GeneralTable(name);
        for (int i = 0; i< stock_data.size();i++) {
            Stock_Data stockData = stock_data.get(i);
            jdbcManager.insert_StockData(name,stockData);
        }
    }

    void to_recalculate(){

        try(Connection connection = jdbcManager.getConnection();
            Statement statement = connection.createStatement()
        ){
            String sql = "Select Date,Open,High,Low,Close,Volume from "+name+" Order By Date  Asc";
            stock_data.clear();
            ResultSet resultSet = statement.executeQuery(sql);
            while (resultSet.next()){
                Stock_Data stockData = new Stock_Data(resultSet.getString(1), resultSet.getDouble(2),
                resultSet.getDouble(3), resultSet.getDouble(4), resultSet.getDouble(5),
                resultSet.getDouble(6));
                stock_data.add(stockData);
                stockData.setOfficial_date(jdbcManager.toCall_Dataformatter(stockData.getDate()));
                System.out.println(stock_data.toString());
            }

            calculate_();
            for (int i = 0; i < stock_data.size(); i++) {
                Stock_Data stockData = stock_data.get(i);
                jdbcManager.update_recalculatedData(name,stockData);
            }

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

    }
}
