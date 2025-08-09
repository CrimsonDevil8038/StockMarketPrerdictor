package Stock_Predictor;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class DerivedIndicators {


    public static double getTypicalPrice(Stock_Data data) {
        return (data.getHigh() + data.getLow() + data.getClose()) / 3.0;
    }


    public static double calculateVWAP(List<Stock_Data> data) {
        if (data == null || data.isEmpty()) {
            return 0.0;
        }

        double totalValue = 0.0;
        double totalVolume = 0.0;

        for (Stock_Data periodData : data) {
            totalValue += getTypicalPrice(periodData) * periodData.getVolume();
            totalVolume += periodData.getVolume();
        }

        return totalVolume == 0 ? 0.0 : totalValue / totalVolume;
    }


    public static double calculateSMA(List<Stock_Data> data, int period) {
        if (data == null || data.size() < period) {
            return 0.0; // Not enough data
        }


        Collections.reverse(data);

        List<Stock_Data> recentData = data.subList(data.size() - period, data.size());


        double sum = 0.0;
        for (Stock_Data stockDay : recentData) {
            sum += stockDay.getClose();
        }

        Collections.reverse(data);

        return sum / period;
    }


    public static double calculateEMA(List<Stock_Data> data, int period) {
        if (data == null || data.size() < period) {
            return 0.0;
        }

        Collections.reverse(data);

        List<Double> closePrices = new ArrayList<>();
        for (Stock_Data stockDay : data) {
            closePrices.add(stockDay.getClose());
        }


        double initialSum = 0.0;
        for (int i = 0; i < period; i++) {
            initialSum += closePrices.get(i);
        }
        double sma = initialSum / period;

        double multiplier = 2.0 / (period + 1.0);
        double ema = sma;


        for (int i = period; i < closePrices.size(); i++) {
            ema = (closePrices.get(i) - ema) * multiplier + ema;
        }

        Collections.reverse(data);

        return ema;
    }


    public static double calculateRSI(List<Stock_Data> data, int period) {
        if (data == null || data.size() < period + 1) {
            return 0.0;
        }

        Collections.reverse(data);

        double totalGain = 0;
        double totalLoss = 0;


        for (int i = 1; i <= period; i++) {
            double change = data.get(i).getClose() - data.get(i - 1).getClose();
            if (change > 0) {
                totalGain += change;
            } else {
                totalLoss -= change;
            }
        }

        double avgGain = totalGain / period;
        double avgLoss = totalLoss / period;


        for (int i = period + 1; i < data.size(); i++) {
            double change = data.get(i).getClose() - data.get(i - 1).getClose();
            if (change > 0) {
                avgGain = (avgGain * (period - 1) + change) / period;
                avgLoss = (avgLoss * (period - 1)) / period;
            } else {
                avgLoss = (avgLoss * (period - 1) - change) / period;
                avgGain = (avgGain * (period - 1)) / period;
            }
        }

        if (avgLoss == 0) {
            return 100.0;
        }

        Collections.reverse(data);

        double rs = avgGain / avgLoss;
        return 100.0 - (100.0 / (1.0 + rs));
    }


    public static MACDResult calculateMACD(List<Stock_Data> data) {
        if (data == null || data.size() < 26) {
            return new MACDResult(0.0, 0.0); // Not enough data
        }
        Collections.reverse(data);

        double ema12 = calculateEMA(data, 12);
        double ema26 = calculateEMA(data, 26);
        double macdLine = ema12 - ema26;


        ArrayList<Double> macdHistory = new ArrayList<>();
        for (int i = 26; i <= data.size(); i++) {
            List<Stock_Data> sublist = data.subList(0, i);
            double shortEma = calculateEMA(sublist, 12);
            double longEma = calculateEMA(sublist, 26);
            macdHistory.add(shortEma - longEma);
        }


        List<Stock_Data> macdAsStockData = new ArrayList<>();
        for (double macdVal : macdHistory) {

            macdAsStockData.add(new Stock_Data("", 0, 0, 0, macdVal, 0));
        }

        double signalLine = calculateEMA(macdAsStockData, 9);

        Collections.reverse(data);

        return new MACDResult(macdLine, signalLine);
    }


    public static BollingerBands calculateBollingerBands(List<Stock_Data> data, int period, double stdDevMultiplier) {
        if (data == null || data.size() < period) {
            return new BollingerBands(0, 0, 0);
        }

        double middleBand = calculateSMA(data, period);

        Collections.reverse(data);

        List<Stock_Data> recentData = data.subList(data.size() - period, data.size());


        double sumOfSquares = 0.0;
        for (Stock_Data stockDay : recentData) {
            sumOfSquares += Math.pow(stockDay.getClose() - middleBand, 2);
        }
        double stdDev = Math.sqrt(sumOfSquares / period);

        double upperBand = middleBand + (stdDev * stdDevMultiplier);
        double lowerBand = middleBand - (stdDev * stdDevMultiplier);

        Collections.reverse(data);

        return new BollingerBands(upperBand, middleBand, lowerBand);
    }


    public static double calculateStochasticOscillator(List<Stock_Data> data, int period) {
        if (data == null || data.size() < period) {
            return 0.0;
        }
        Collections.reverse(data);

        List<Stock_Data> recentData = data.subList(data.size() - period, data.size());

        double currentClose = data.get(data.size() - 1).getClose();

        double lowestLow = recentData.get(0).getLow();
        double highestHigh = recentData.get(0).getHigh();

        for (int i = 1; i < recentData.size(); i++) {
            if (recentData.get(i).getLow() < lowestLow) {
                lowestLow = recentData.get(i).getLow();
            }
            if (recentData.get(i).getHigh() > highestHigh) {
                highestHigh = recentData.get(i).getHigh();
            }
        }

        if (highestHigh == lowestLow) {
            return 50.0;
        }

        return 100 * ((currentClose - lowestLow) / (highestHigh - lowestLow));
    }
}