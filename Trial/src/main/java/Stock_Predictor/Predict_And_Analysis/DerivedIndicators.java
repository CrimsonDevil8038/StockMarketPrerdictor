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

    public static double calculateSMA(List<Stock_Data> data, int period) {
        if (data == null || data.size() < period) {
            return 0.0;
        }

        List<Stock_Data> recentData = data.subList(data.size() - period, data.size());

        double sum = 0.0;
        for (Stock_Data stockDay : recentData) {
            sum += stockDay.getClose();
        }

        return sum / period;
    }


    public static double calculateSMA_volume(List<Stock_Data> data, int period) {
        if (data == null || data.size() < period) {
            return 0.0;
        }


        List<Stock_Data> recentData = data.subList(data.size() - period, data.size());

        double sum = 0.0;
        for (Stock_Data stockDay : recentData) {
            sum += stockDay.getVolume();
        }

        return sum / period;
    }


    public static double calculateEMA(List<Stock_Data> data, int period) {
        if (data == null || data.size() < period) {
            return 0.0;
        }


        double initialSum = 0.0;
        for (int i = data.size() - period; i < data.size(); i++) {
            initialSum += data.get(i).getClose();
        }
        double sma = initialSum / period;


        if (data.size() == period) {
            return sma;
        }


        double multiplier = 2.0 / (period + 1.0);
        double ema = sma;


        for (int i = data.size() - period + 1; i < data.size(); i++) {
            ema = (data.get(i).getClose() - ema) * multiplier + ema;
        }


        List<Double> closePrices = data.stream().map(Stock_Data::getClose).collect(Collectors.toList());
        double finalEma = calculateEMAFromPrices(closePrices, period);

        return finalEma;
    }


    private static double calculateEMAFromPrices(List<Double> prices, int period) {
        if (prices.size() < period) return 0.0;

        double sum = 0;
        for (int i = 0; i < period; i++) {
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


        if (avgLoss == 0) {
            return 100.0;
        }

        double rs = avgGain / avgLoss;
        return 100.0 - (100.0 / (1.0 + rs));
    }

    public static MACDResult calculateMACD(List<Stock_Data> data) {
        if (data == null || data.size() < 26) {
            return new MACDResult(0.0, 0.0);
        }

        List<Double> closePrices = data.stream().map(Stock_Data::getClose).collect(Collectors.toList());

        double ema12 = calculateEMAFromPrices(closePrices, 12);
        double ema26 = calculateEMAFromPrices(closePrices, 26);
        double macdLine = ema12 - ema26;


        ArrayList<Double> macdHistory = new ArrayList<>();
        if (data.size() >= 35) {
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
            return 50.0;
        }

        return 100 * ((currentClose - lowestLow) / (highestHigh - lowestLow));
    }


    public static double calculateVolumeDeviation(List<Stock_Data> data, int period) {
        if (data == null || data.size() < period) {
            return 0.0;
        }

        double smaVolume = calculateSMA_volume(data, period);


        if (smaVolume == 0) {
            return 0.0;
        }


        double currentVolume = data.get(data.size() - 1).getVolume();


        return (currentVolume - smaVolume) / smaVolume;
    }


    public static double calculateNormalizedVolumeDeviation(List<Stock_Data> data, int smaPeriod, int normalizationPeriod) {

        if (data == null || data.size() < smaPeriod + normalizationPeriod) {
            return 0.0;
        }

        List<Double> volDevHistory = new ArrayList<>();

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


        double minVolDev = Collections.min(volDevHistory);
        double maxVolDev = Collections.max(volDevHistory);


        double currentVolDev = volDevHistory.get(volDevHistory.size() - 1);


        double range = maxVolDev - minVolDev;
        if (range == 0) {
            return 0.5;
        }


        return (currentVolDev - minVolDev) / range;
    }
}
