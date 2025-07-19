package Stock_Predictor;

public class DerivedIndicators {

    double averagePrice(double high, double low, double close) {
        return ((high + low + close) / 3);
    }

    double medianPrice(double high, double low) {
        return ((high + low) / 2);
    }

    double priceRange(double high, double low) {
        return (high - low);
    }

    double bodySize(double open, double closs) {
        return (closs - open);
    }

    double simpleMovingAverage(double[] close) {

        double sum = 0;
        for (int i = 0; i < close.length; i++) {
            sum += close[i];
        }
        return (sum / close.length);
    }

    double exponentialMovingAverage(double[] close) {

        double sum = 0;
        for (int i = close.length - 1; i > 1; i++) {
            sum += close[i];
        }
        double sma = (sum / (close.length - 1));

        double a = 2 / (close.length);

        return ((a * close[0]) + ((1 - a) * sma));
    }

    double movingAverageConvergenceDivergence(double[] close_1, double[] close_2) {
        return Math.abs(exponentialMovingAverage(close_1) - exponentialMovingAverage(close_2));
    }

    double vwap(double[] high, double[] low, double[] close, double[] volume) {

        double sum = 0;
        double vsum = 0;
        for (int i = 0; i < high.length; i++) {
            double sma = (averagePrice(high[i], low[i], close[i]) * volume[i]);
            sum += sma;
            vsum += volume[i];
        }

        return (sum / vsum);
    }
    //vwap
}