package Stock_Predictor.Predict_And_Analysis;

import Stock_Predictor.JDBC.JDBC_Manager;

import java.util.LinkedList;
import java.util.List;

public class Stock {

    private final LinkedList<Stock_Data> stock_data = new LinkedList<>();
    private String name;
    private JDBC_Manager jdbcManager = new JDBC_Manager();

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
        for (int i = 0; i < stock_data.size(); i++) {
            Stock_Data currentDayData = stock_data.get(i);
            List<Stock_Data> historicalData = stock_data.subList(0, i + 1);

            // Typical Price
            currentDayData.setTypicalPrice(DerivedIndicators.getTypicalPrice(currentDayData));

            // VWAP-20 Days
            int vwapPeriod = Math.min(i + 1, 20);
            List<Stock_Data> vwapPeriodData = stock_data.subList(i - vwapPeriod + 1, i + 1);
            currentDayData.setVwap(DerivedIndicators.calculateVWAP(vwapPeriodData));

            // SMAs
            currentDayData.setSma_5(DerivedIndicators.calculateSMA(historicalData, 5));
            currentDayData.setSma_10(DerivedIndicators.calculateSMA(historicalData, 10));
            currentDayData.setSma_15(DerivedIndicators.calculateSMA(historicalData, 15));
            currentDayData.setSma_50(DerivedIndicators.calculateSMA(historicalData, 50));
            currentDayData.setSma_100(DerivedIndicators.calculateSMA(historicalData, 100));
            currentDayData.setSma_200(DerivedIndicators.calculateSMA(historicalData, 200));

            // *** ADDED: SMA 20 Volume Calculation ***
            currentDayData.setSma_20volume(DerivedIndicators.calculateSMA_volume(historicalData, 20));

            // EMAs
            currentDayData.setEma_5(DerivedIndicators.calculateEMA(historicalData, 5));
            currentDayData.setEma_10(DerivedIndicators.calculateEMA(historicalData, 10));
            currentDayData.setEma_15(DerivedIndicators.calculateEMA(historicalData, 15));
            currentDayData.setEma_50(DerivedIndicators.calculateEMA(historicalData, 50));
            currentDayData.setEma_100(DerivedIndicators.calculateEMA(historicalData, 100));
            currentDayData.setEma_200(DerivedIndicators.calculateEMA(historicalData, 200));

            // RSIs
            currentDayData.setRsi_14(DerivedIndicators.calculateRSI(historicalData, 14));
            currentDayData.setRsi_30(DerivedIndicators.calculateRSI(historicalData, 30));

            // MACD
            MACDResult macdResult = DerivedIndicators.calculateMACD(historicalData);
            currentDayData.setMacdline(macdResult.getMacdLine());
            currentDayData.setSignalline(macdResult.getSignalLine());

            // Bollinger Bands
            BollingerBands bollingerBands = DerivedIndicators.calculateBollingerBands(historicalData, 20, 2);
            currentDayData.setUpperband(bollingerBands.getUpperBand());
            currentDayData.setMiddleband(bollingerBands.getMiddleBand());
            currentDayData.setLowerband(bollingerBands.getLowerBand());

            // Stochastic Oscillator
            currentDayData.setStochastic(DerivedIndicators.calculateStochasticOscillator(historicalData, 14));


            //Stock Deviation
            currentDayData.setVolDevNorm(DerivedIndicators.calculateNormalizedVolumeDeviation(historicalData, 20, 20));

            // Set official SQL Date
            currentDayData.setOfficial_date(jdbcManager.toCall_Dataformatter(currentDayData.getDate()));
        }
    }

    void toPostgreSQL() {
        jdbcManager.create_table_GeneralTable(name);
        for (Stock_Data data : stock_data) {
            jdbcManager.insert_StockData(name, data);
        }
    }
}
