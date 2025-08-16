package Stock_Predictor.Predict_And_Analysis;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

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

    /**
     * Calculates the Simple Moving Average (SMA) of the closing price.
     * This is a safer and more efficient implementation that does not modify the original list.
     */
    public static double calculateSMA(List<Stock_Data> data, int period) {
        if (data == null || data.size() < period) {
            return 0.0; // Not enough data
        }

        // Get the most recent data points without modifying the original list
        List<Stock_Data> recentData = data.subList(data.size() - period, data.size());

        double sum = 0.0;
        for (Stock_Data stockDay : recentData) {
            sum += stockDay.getClose();
        }

        return sum / period;
    }

    /**
     * Calculates the Simple Moving Average (SMA) of the trading volume.
     * This method is safe and efficient as it does not modify the original list.
     */
    public static double calculateSMA_volume(List<Stock_Data> data, int period) {
        if (data == null || data.size() < period) {
            return 0.0; // Not enough data
        }

        // Get the most recent data points without modifying the original list
        List<Stock_Data> recentData = data.subList(data.size() - period, data.size());

        double sum = 0.0;
        for (Stock_Data stockDay : recentData) {
            sum += stockDay.getVolume();
        }

        return sum / period;
    }

    /**
     * Calculates the Exponential Moving Average (EMA).
     * This is a safer and more efficient implementation that does not modify the original list.
     */
    public static double calculateEMA(List<Stock_Data> data, int period) {
        if (data == null || data.size() < period) {
            return 0.0;
        }

        // Calculate initial SMA for the first period
        double initialSum = 0.0;
        for (int i = data.size() - period; i < data.size(); i++) {
            initialSum += data.get(i).getClose();
        }
        double sma = initialSum / period;

        // If the data size is exactly the period, EMA is the SMA
        if (data.size() == period) {
            return sma;
        }

        // Calculate EMA for the rest of the data
        double multiplier = 2.0 / (period + 1.0);
        double ema = sma; // Start with SMA as the first EMA

        // This loop is simplified as we only need the final EMA value
        // For a full EMA series, this would be different.
        for (int i = data.size() - period + 1; i < data.size(); i++) {
            ema = (data.get(i).getClose() - ema) * multiplier + ema;
        }

        // A more correct approach for a single EMA value on a series
        List<Double> closePrices = data.stream().map(Stock_Data::getClose).collect(Collectors.toList());
        double finalEma = calculateEMAFromPrices(closePrices, period);

        return finalEma;
    }

    // Helper for EMA calculation on a list of prices
    private static double calculateEMAFromPrices(List<Double> prices, int period) {
        if (prices.size() < period) return 0.0;

        double sum = 0;
        for(int i = 0; i < period; i++) {
            sum += prices.get(i);
        }
        double ema = sum / period;
        double multiplier = 2.0 / (period + 1.0);

        for (int i = period; i < prices.size(); i++) {
            ema = (prices.get(i) - ema) * multiplier + ema;
        }
        return ema;
    }

    public static double calculateRSI(List<Stock_Data> data, int period) {
        if (data == null || data.size() < period + 1) {
            return 0.0;
        }

        double totalGain = 0;
        double totalLoss = 0;

        // Calculate initial average gain and loss
        for (int i = data.size() - period; i < data.size(); i++) {
            double change = data.get(i).getClose() - data.get(i - 1).getClose();
            if (change > 0) {
                totalGain += change;
            } else {
                totalLoss -= change;
            }
        }

        double avgGain = totalGain / period;
        double avgLoss = totalLoss / period;

        // This simplified RSI calculation is for the most recent period.
        // A full implementation would smooth the averages over the entire dataset.

        if (avgLoss == 0) {
            return 100.0; // Avoid division by zero
        }

        double rs = avgGain / avgLoss;
        return 100.0 - (100.0 / (1.0 + rs));
    }

    public static MACDResult calculateMACD(List<Stock_Data> data) {
        if (data == null || data.size() < 26) {
            return new MACDResult(0.0, 0.0); // Not enough data
        }

        List<Double> closePrices = data.stream().map(Stock_Data::getClose).collect(Collectors.toList());

        double ema12 = calculateEMAFromPrices(closePrices, 12);
        double ema26 = calculateEMAFromPrices(closePrices, 26);
        double macdLine = ema12 - ema26;

        // To calculate the signal line, we need a history of MACD values
        ArrayList<Double> macdHistory = new ArrayList<>();
        if (data.size() >= 35) { // 26 for first MACD + 9 for first signal line
            for (int i = 26; i <= data.size(); i++) {
                List<Double> sublist = closePrices.subList(0, i);
                double shortEma = calculateEMAFromPrices(sublist, 12);
                double longEma = calculateEMAFromPrices(sublist, 26);
                macdHistory.add(shortEma - longEma);
            }
        }

        if (macdHistory.size() < 9) {
            return new MACDResult(macdLine, 0.0);
        }

        double signalLine = calculateEMAFromPrices(macdHistory, 9);

        return new MACDResult(macdLine, signalLine);
    }

    public static BollingerBands calculateBollingerBands(List<Stock_Data> data, int period, double stdDevMultiplier) {
        if (data == null || data.size() < period) {
            return new BollingerBands(0, 0, 0);
        }

        double middleBand = calculateSMA(data, period);
        List<Stock_Data> recentData = data.subList(data.size() - period, data.size());

        double sumOfSquares = 0.0;
        for (Stock_Data stockDay : recentData) {
            sumOfSquares += Math.pow(stockDay.getClose() - middleBand, 2);
        }
        double stdDev = Math.sqrt(sumOfSquares / period);

        double upperBand = middleBand + (stdDev * stdDevMultiplier);
        double lowerBand = middleBand - (stdDev * stdDevMultiplier);

        return new BollingerBands(upperBand, middleBand, lowerBand);
    }

    public static double calculateStochasticOscillator(List<Stock_Data> data, int period) {
        if (data == null || data.size() < period) {
            return 0.0;
        }

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
            return 50.0; // Avoid division by zero, return neutral value
        }

        return 100 * ((currentClose - lowestLow) / (highestHigh - lowestLow));
    }


    public static double calculateVolumeDeviation(List<Stock_Data> data, int period) {
        if (data == null || data.size() < period) {
            return 0.0;
        }

        // 1. Calculate the SMA of the volume for the specified period.
        double smaVolume = calculateSMA_volume(data, period);

        // 2. If SMA is zero, deviation is undefined. Return 0 to avoid errors.
        if (smaVolume == 0) {
            return 0.0;
        }

        // 3. Get the volume for the most recent day (Vt).
        double currentVolume = data.get(data.size() - 1).getVolume();

        // 4. Apply the formula and return the result.
        return (currentVolume - smaVolume) / smaVolume;
    }


    public static double calculateNormalizedVolumeDeviation(List<Stock_Data> data, int smaPeriod, int normalizationPeriod) {
        // We need enough data for the first SMA calculation plus the full normalization window.
        if (data == null || data.size() < smaPeriod + normalizationPeriod) {
            return 0.0; // Return a default value if data is insufficient.
        }

        List<Double> volDevHistory = new ArrayList<>();
        // 1. Calculate the Volume Deviation for each day in the normalization window.
        for (int i = 0; i < normalizationPeriod; i++) {
            int endIndex = data.size() - normalizationPeriod + 1 + i;
            List<Stock_Data> historicalSublist = data.subList(0, endIndex);

            if (historicalSublist.size() >= smaPeriod) {
                double volDev = calculateVolumeDeviation(historicalSublist, smaPeriod);
                volDevHistory.add(volDev);
            }
        }

        if (volDevHistory.isEmpty()) {
            return 0.0;
        }

        // 2. Find the minimum and maximum deviation in the recent history.
        double minVolDev = Collections.min(volDevHistory);
        double maxVolDev = Collections.max(volDevHistory);

        // 3. Get the most recent deviation value to be normalized.
        double currentVolDev = volDevHistory.get(volDevHistory.size() - 1);

        // 4. Calculate the range. If range is zero, return a neutral value (0.5).
        double range = maxVolDev - minVolDev;
        if (range == 0) {
            return 0.5;
        }

        // 5. Apply the normalization formula and return the result.
        return (currentVolDev - minVolDev) / range;
    }
}
