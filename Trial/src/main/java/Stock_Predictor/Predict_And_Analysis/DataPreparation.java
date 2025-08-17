package Stock_Predictor.Predict_And_Analysis;

import Stock_Predictor.JDBC.JDBC_Manager;

import java.time.LocalDate;
import java.sql.Date;

public class DataPreparation {

    double[][] x_train;
    double[] y_train;
    double bias = 0.0;
    double prediction = 0.0;
    double[] features;
    double[] weights;
    double learningRate = 0.01;

    JDBC_Manager jdbcManager = new JDBC_Manager();

    void start_prediction(String Stock) {
        try {
            int duration = jdbcManager.get_daysBetween(jdbcManager.get_minDate(Stock), jdbcManager.get_maxDate(Stock));
            if (duration >= 30) {
                System.out.println("Please enter date in Format 'DD-Mon-YY'");
                System.out.println("Time Period : " + jdbcManager.get_minDate(Stock) + "- " + jdbcManager.get_maxDate(Stock));
                System.out.println("Enter The Prediction Date:");
            } else {
                System.out.println("Minimum Data  Required of 30 Days");
                return;
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    double[] prepare_short_data(String stock, Date date) {
        double data[] = new double[14];
        int days = 30;
        Date predata1 = jdbcManager.getPreviousDate(date);
        Date predate2 = jdbcManager.getPreviousDate(predata1);

        // %typicalPrice:
        double typicalPrice = jdbcManager.getTypicalPriceOnDate(stock, predata1);
        double preTypicalPrice = jdbcManager.getTypicalPriceOnDate(stock, predate2);
        // Added a check to prevent division by zero
        data[0] = (preTypicalPrice == 0) ? 0.0 : ((typicalPrice - preTypicalPrice) / preTypicalPrice);

        // 5SMA
        double sma5 = jdbcManager.getSMA5OnDate(stock, predata1);
        double min = jdbcManager.get_minSMA_5(stock, days, predata1);
        double max = jdbcManager.get_maxSMA_5(stock, days, predata1);
        data[1] = scale(sma5, min, max);

        // 10SMA
        double sma10 = jdbcManager.getSMA10OnDate(stock, predata1);
        min = jdbcManager.get_minSMA_10(stock, days, predata1);
        max = jdbcManager.get_maxSMA_10(stock, days, predata1);
        data[2] = scale(sma10, min, max);

        // 15SMA
        double sma15 = jdbcManager.getSMA15OnDate(stock, predata1);
        min = jdbcManager.get_minSMA_15(stock, days, predata1);
        max = jdbcManager.get_maxSMA_15(stock, days, predata1);
        data[3] = scale(sma15, min, max); // CORRECTED: Was sma5

        // 5EMA
        double ema5 = jdbcManager.getEMA5OnDate(stock, predata1);
        min = jdbcManager.get_minEMA_5(stock, days, predata1);
        max = jdbcManager.get_maxEMA_5(stock, days, predata1);
        data[4] = scale(ema5, min, max); // CORRECTED: Was sma5

        // 10EMA
        double ema10 = jdbcManager.getEMA10OnDate(stock, predata1);
        min = jdbcManager.get_minEMA_10(stock, days, predata1);
        max = jdbcManager.get_maxEMA_10(stock, days, predata1);
        data[5] = scale(ema10, min, max); // CORRECTED: Was sma10

        // 15EMA
        double ema15 = jdbcManager.getEMA15OnDate(stock, predata1);
        min = jdbcManager.get_minEMA_15(stock, days, predata1);
        max = jdbcManager.get_maxEMA_15(stock, days, predata1);
        data[6] = scale(ema15, min, max); // CORRECTED: Was sma5

        // MACD line
        double macd = jdbcManager.getMACDLineOnDate(stock, predata1);
        min = jdbcManager.get_minMACDLine(stock, days, predata1);
        max = jdbcManager.get_maxMACDLine(stock, days, predata1);
        data[7] = scale(macd, min, max); // CORRECTED: Was sma10

        // Signal Line
        double signal = jdbcManager.getSignalLineOnDate(stock, predata1);
        min = jdbcManager.get_minSignalLine(stock, days, predata1);
        max = jdbcManager.get_maxSignalLine(stock, days, predata1);
        data[8] = scale(signal, min, max); // CORRECTED: Was sma5

        // RSI_14
        double rsi = jdbcManager.getRSI14OnDate(stock, predata1);
        data[9] = (rsi / 100);

        // Stochastic
        double stochastic = jdbcManager.getStochasticOnDate(stock, predata1);
        data[10] = (stochastic / 100);

        // Distance from VWAP
        double vwap = jdbcManager.getVWAPOnDate(stock, predata1);
        double close = jdbcManager.getCloseOnDate(stock, predata1);
        // Added a check to prevent division by zero
        data[11] = (vwap == 0) ? 0.0 : ((close - vwap) / vwap);

        // BollingerBand Width
        double upper = jdbcManager.getUpperBandOnDate(stock, predata1);
        double middle = jdbcManager.getMiddleBandOnDate(stock, predata1);
        double lower = jdbcManager.getLowerBandOnDate(stock, predata1);
        // Added a check to prevent division by zero
        data[12] = (middle == 0) ? 0.0 : ((upper - lower) / middle);

        // Volume Deviation
        double volumeDeviation = jdbcManager.getVolDevNormOnDate(stock, predata1);
        data[13] = volumeDeviation; // CORRECTED: Assigned value to the array

        return data;
    }

    double scale(double current, double min, double max) {
        double range = max - min;
        if (range == 0) {
            return 0.5; // Return a neutral value if there's no range
        }
        return (current - min) / range;
    }



}
