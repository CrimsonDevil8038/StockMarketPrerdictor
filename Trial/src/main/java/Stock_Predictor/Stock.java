package Stock_Predictor;

import java.util.LinkedList;
import java.util.List;

public class Stock {


    private final LinkedList<Stock_Data> stock_data = new LinkedList<Stock_Data>();


    private String name;
    private String fileLocation = "";

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


    public String getFileLocation() {
        return fileLocation;
    }

    public void setFileLocation(String fileLocation) {
        this.fileLocation = fileLocation;
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


        }


    }

}
