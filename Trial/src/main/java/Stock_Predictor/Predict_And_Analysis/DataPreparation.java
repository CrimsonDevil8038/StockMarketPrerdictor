package Stock_Predictor.Predict_And_Analysis;

import Stock_Predictor.JDBC.JDBC_Manager;
import java.sql.Date;
import java.util.Calendar;

public class DataPreparation {

    public double[][] x_train, x_val, x_test;
    public double[] y_train, y_val, y_test;
    public double bias = 0.0;
    public double[] weights;
    public double learningRate = 0.0001;
    public double l2Lambda = 0.01; // L2 regularization parameter

    public double[][] x_train_long, x_val_long, x_test_long;
    public double[] y_train_long, y_val_long, y_test_long;
    public double longTermBias = 0.0;
    public double[] longTermWeights;
    public double longTermLearningRate = 0.0001;
    public double longTermL2Lambda = 0.01;

    JDBC_Manager jdbcManager = new JDBC_Manager();


    double[] prepare_short_data(String tableName, Date date) {
        double data[] = new double[14];
        Date predata1 = jdbcManager.getPreviousDate(date, tableName);
        if (predata1 == null) return data; // Not enough history

        double typicalPrice = jdbcManager.getTypicalPriceOnDate(tableName, date);
        double preTypicalPrice = jdbcManager.getTypicalPriceOnDate(tableName, predata1);
        data[0] = (preTypicalPrice == 0 || Double.isNaN(typicalPrice) || Double.isNaN(preTypicalPrice)) ? 0.0 : ((typicalPrice - preTypicalPrice) / preTypicalPrice);

        double close = jdbcManager.getCloseOnDate(tableName, date);
        if (Double.isNaN(close) || close == 0) return data; // Cannot proceed without a valid close price

        data[1] = (jdbcManager.getSMA5OnDate(tableName, date) / close) - 1.0;
        data[2] = (jdbcManager.getSMA10OnDate(tableName, date) / close) - 1.0;
        data[3] = (jdbcManager.getSMA15OnDate(tableName, date) / close) - 1.0;
        data[4] = (jdbcManager.getEMA5OnDate(tableName, date) / close) - 1.0;
        data[5] = (jdbcManager.getEMA10OnDate(tableName, date) / close) - 1.0;
        data[6] = (jdbcManager.getEMA15OnDate(tableName, date) / close) - 1.0;
        data[7] = jdbcManager.getMACDLineOnDate(tableName, date) / close;
        data[8] = jdbcManager.getSignalLineOnDate(tableName, date) / close;

        double rsi = jdbcManager.getRSI14OnDate(tableName, date);
        data[9] = Double.isNaN(rsi) ? 0.0 : (rsi - 50.0) / 50.0;

        double stochastic = jdbcManager.getStochasticOnDate(tableName, date);
        data[10] = Double.isNaN(stochastic) ? 0.0 : (stochastic - 50.0) / 50.0;

        double vwap = jdbcManager.getVWAPOnDate(tableName, date);
        data[11] = (vwap == 0 || Double.isNaN(vwap)) ? 0.0 : ((close - vwap) / vwap);

        double upper = jdbcManager.getUpperBandOnDate(tableName, date);
        double middle = jdbcManager.getMiddleBandOnDate(tableName, date);
        double lower = jdbcManager.getLowerBandOnDate(tableName, date);
        data[12] = (middle == 0 || Double.isNaN(upper) || Double.isNaN(lower) || Double.isNaN(middle)) ? 0.0 : ((upper - lower) / middle);

        double volumeDeviation = jdbcManager.getVolDevNormOnDate(tableName, date);
        data[13] = Double.isNaN(volumeDeviation) ? 0.0 : Math.max(-3.0, Math.min(3.0, volumeDeviation));

        return data;
    }

    public void data(String stockName, int features) {
        Date startDate = jdbcManager.get_minDate(stockName);
        int duration = jdbcManager.getTradingDayCount(stockName);

        int test_data = (int) (Math.round(0.2 * duration));
        int val_data = (int) (Math.round(0.2 * duration));
        int train_data_size = duration - test_data - val_data;

        x_train = new double[train_data_size - 40][features];
        y_train = new double[train_data_size - 40];
        x_val = new double[val_data][features];
        y_val = new double[val_data];
        x_test = new double[test_data][features];
        y_test = new double[test_data];

        weights = new double[features];
        for (int i = 0; i < weights.length; i++) {
            weights[i] = (Math.random() - 0.5) * 0.001;
        }

        System.out.println("Min Date: " + jdbcManager.get_minDate(stockName) + "\nMax Date: " + jdbcManager.get_maxDate(stockName));
        System.out.println("Total duration: " + duration);
        System.out.println("Training samples: " + (train_data_size - 40));
        System.out.println("Validation samples: " + val_data);
        System.out.println("Test samples: " + test_data);

        if (features == 14) {
            Date cursorDate = addBusinessDays(startDate, train_data_size);
            for (int i = x_train.length - 1; i >= 0; i--) {
                Date nextDayDate = cursorDate;
                cursorDate = jdbcManager.getPreviousDate(cursorDate, stockName);

                double nextDayClose = jdbcManager.getCloseOnDate(stockName, nextDayDate);
                double currentDayClose = jdbcManager.getCloseOnDate(stockName, cursorDate);

                if (currentDayClose != 0 && !Double.isNaN(currentDayClose) && !Double.isNaN(nextDayClose)) {
                    y_train[i] = (nextDayClose - currentDayClose) / currentDayClose;
                } else {
                    y_train[i] = 0.0;
                }
                x_train[i] = prepare_short_data(stockName, cursorDate);
            }

            cursorDate = addBusinessDays(startDate, train_data_size + val_data);
            for (int i = x_val.length - 1; i >= 0; i--) {
                Date nextDayDate = cursorDate;
                cursorDate = jdbcManager.getPreviousDate(cursorDate, stockName);

                double nextDayClose = jdbcManager.getCloseOnDate(stockName, nextDayDate);
                double currentDayClose = jdbcManager.getCloseOnDate(stockName, cursorDate);

                if (currentDayClose != 0 && !Double.isNaN(currentDayClose) && !Double.isNaN(nextDayClose)) {
                    y_val[i] = (nextDayClose - currentDayClose) / currentDayClose;
                } else {
                    y_val[i] = 0.0;
                }
                x_val[i] = prepare_short_data(stockName, cursorDate);
            }

            cursorDate = jdbcManager.get_maxDate(stockName);
            for (int i = x_test.length - 1; i >= 0; i--) {
                Date nextDayDate = cursorDate;
                if (cursorDate == null) break;
                cursorDate = jdbcManager.getPreviousDate(cursorDate, stockName);
                if (cursorDate == null) break;

                double nextDayClose = jdbcManager.getCloseOnDate(stockName, nextDayDate);
                double currentDayClose = jdbcManager.getCloseOnDate(stockName, cursorDate);

                if (currentDayClose != 0 && !Double.isNaN(currentDayClose) && !Double.isNaN(nextDayClose)) {
                    y_test[i] = (nextDayClose - currentDayClose) / currentDayClose;
                } else {
                    y_test[i] = 0.0;
                }
                x_test[i] = prepare_short_data(stockName, cursorDate);
            }
        }
    }

    public Date addBusinessDays(Date startDate, int daysToAdd) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(startDate);
        int addedDays = 0;
        while (addedDays < daysToAdd) {
            cal.add(Calendar.DAY_OF_MONTH, 1);
            int dayOfWeek = cal.get(Calendar.DAY_OF_WEEK);
            if (dayOfWeek != Calendar.SATURDAY && dayOfWeek != Calendar.SUNDAY) {
                addedDays++;
            }
        }
        return new java.sql.Date(cal.getTimeInMillis());
    }

    public void gradientDescentWithRegularization(int epochs) {
        int m = x_train.length;
        int n = x_train[0].length;

        double bestValLoss = Double.MAX_VALUE;
        double[] bestWeights = new double[n];
        double bestBias = 0.0;
        int patienceCounter = 0;
        int patience = 50;

        for (int epoch = 0; epoch < epochs; epoch++) {
            double[] predictions = new double[m];
            for (int i = 0; i < m; i++) {
                predictions[i] = bias;
                for (int j = 0; j < n; j++) {
                    predictions[i] += x_train[i][j] * weights[j];
                }
            }

            double[] gradients = new double[n];
            double biasGradient = 0.0;
            for (int i = 0; i < m; i++) {
                double error = predictions[i] - y_train[i];
                biasGradient += error;
                for (int j = 0; j < n; j++) {
                    gradients[j] += error * x_train[i][j] + l2Lambda * weights[j];
                }
            }

            for (int j = 0; j < n; j++) {
                weights[j] -= learningRate * (gradients[j] / m);
            }
            bias -= learningRate * (biasGradient / m);

            if (epoch % 25 == 0) {
                double trainMse = calculateMSE(x_train, y_train, weights, bias);
                double valMse = calculateMSE(x_val, y_val, weights, bias);
//                System.out.println("Epoch " + epoch + " | Train MSE: " + String.format("%.8f", trainMse) + " | Val MSE: " + String.format("%.8f", valMse));

                if (valMse < bestValLoss) {
                    bestValLoss = valMse;
                    System.arraycopy(weights, 0, bestWeights, 0, n);
                    bestBias = bias;
                    patienceCounter = 0;
                } else {
                    patienceCounter++;
                    if (patienceCounter >= patience) {
                        System.out.println("Early stopping at epoch " + epoch);
                        break;
                    }
                }
            }
        }

        System.arraycopy(bestWeights, 0, weights, 0, n);
        bias = bestBias;
        double testMse = calculateMSE(x_test, y_test, weights, bias);
        System.out.println("Final Test MSE (on price % change): " + testMse);
    }

    public double predict(String stockName, Date date) {
        double[] features = prepare_short_data(stockName, date);
        double lastKnownClose = jdbcManager.getCloseOnDate(stockName, date);
        if (Double.isNaN(lastKnownClose)) return 0.0;

        double predictedChange = bias;
        for (int i = 0; i < features.length; i++) {
            predictedChange += features[i] * weights[i];
        }
        return lastKnownClose * (1 + predictedChange);
    }

    public double[] prepare_long_data(String tableName, Date date) {
        double[] data = new double[10];
        double close = jdbcManager.getCloseOnDate(tableName, date);

        if (Double.isNaN(close) || close == 0) {
            return data;
        }


        data[0] = (jdbcManager.getSMA50OnDate(tableName, date) / close) - 1.0;
        data[1] = (jdbcManager.getSMA100OnDate(tableName, date) / close) - 1.0;
        data[2] = (jdbcManager.getSMA200OnDate(tableName, date) / close) - 1.0;


        data[3] = (jdbcManager.getEMA50OnDate(tableName, date) / close) - 1.0;
        data[4] = (jdbcManager.getEMA100OnDate(tableName, date) / close) - 1.0;
        data[5] = (jdbcManager.getEMA200OnDate(tableName, date) / close) - 1.0;


        double rsi30 = jdbcManager.getRSI30OnDate(tableName, date);


        data[6] = Double.isNaN(rsi30) ? 0.0 : (rsi30 - 50.0) / 50.0;


        double sma50 = jdbcManager.getSMA50OnDate(tableName, date);
        double sma200 = jdbcManager.getSMA200OnDate(tableName, date);


        data[7] = (Double.isNaN(sma50) || Double.isNaN(sma200)) ? 0.0 : (sma50 > sma200 ? 1.0 : -1.0);


        data[8] = jdbcManager.getMACDLineOnDate(tableName, date) / close;


        double upperBand = jdbcManager.getUpperBandOnDate(tableName, date);
        double lowerBand = jdbcManager.getLowerBandOnDate(tableName, date);
        double middleBand = jdbcManager.getMiddleBandOnDate(tableName, date);

        data[9] = (middleBand == 0 || Double.isNaN(upperBand) || Double.isNaN(lowerBand) || Double.isNaN(middleBand)) ? 0.0 : (upperBand - lowerBand) / middleBand;


        for (int i = 0; i < data.length; i++) {
            if (Double.isNaN(data[i])) {
                data[i] = 0.0;
            }
        }

        return data;
    }

    public void data_long_term(String stockName, int predictionHorizon) {

        String tableName = "stock_" + stockName.toLowerCase();
        Date startDate = jdbcManager.get_minDate(tableName);
        int duration = jdbcManager.getTradingDayCount(tableName);

        if (duration < 200 + predictionHorizon) {
            System.err.println("Not enough data for a long-term prediction with a " + predictionHorizon + "-day horizon.");
            return;
        }

        int numFeatures = 10;
        longTermWeights = new double[numFeatures];
        for (int i = 0; i < longTermWeights.length; i++) {
            longTermWeights[i] = (Math.random() - 0.5) * 0.001;
        }

        int test_size = (int) (0.2 * duration);
        int val_size = (int) (0.2 * duration);
        int train_size = duration - test_size - val_size;

        train_size -= predictionHorizon;
        val_size -= predictionHorizon;
        test_size -= predictionHorizon;

        x_train_long = new double[train_size][numFeatures];
        y_train_long = new double[train_size];
        x_val_long = new double[val_size][numFeatures];
        y_val_long = new double[val_size];
        x_test_long = new double[test_size][numFeatures];
        y_test_long = new double[test_size];

        Date cursorDate = startDate;
        for (int i = 0; i < train_size; i++) {
            Date futureDate = addBusinessDays(cursorDate, predictionHorizon);
            double currentClose = jdbcManager.getCloseOnDate(tableName, cursorDate);
            double futureClose = jdbcManager.getCloseOnDate(tableName, futureDate);


            if (currentClose != 0 && !Double.isNaN(currentClose) && !Double.isNaN(futureClose)) {
                y_train_long[i] = (futureClose - currentClose) / currentClose;
            } else {
                y_train_long[i] = 0.0;
            }
            x_train_long[i] = prepare_long_data(tableName, cursorDate);
            cursorDate = addBusinessDays(cursorDate, 1);
        }

        for (int i = 0; i < val_size; i++) {
            Date futureDate = addBusinessDays(cursorDate, predictionHorizon);
            double currentClose = jdbcManager.getCloseOnDate(tableName, cursorDate);
            double futureClose = jdbcManager.getCloseOnDate(tableName, futureDate);

            if (currentClose != 0 && !Double.isNaN(currentClose) && !Double.isNaN(futureClose)) {
                y_val_long[i] = (futureClose - currentClose) / currentClose;
            } else {
                y_val_long[i] = 0.0;
            }
            x_val_long[i] = prepare_long_data(tableName, cursorDate);
            cursorDate = addBusinessDays(cursorDate, 1);
        }

        for (int i = 0; i < test_size; i++) {
            Date futureDate = addBusinessDays(cursorDate, predictionHorizon);
            double currentClose = jdbcManager.getCloseOnDate(tableName, cursorDate);
            double futureClose = jdbcManager.getCloseOnDate(tableName, futureDate);

            if (currentClose != 0 && !Double.isNaN(currentClose) && !Double.isNaN(futureClose)) {
                y_test_long[i] = (futureClose - currentClose) / currentClose;
            } else {
                y_test_long[i] = 0.0;
            }
            x_test_long[i] = prepare_long_data(tableName, cursorDate);
            cursorDate = addBusinessDays(cursorDate, 1);
        }
    }

    public void gradientDescent_long_term(int epochs) {
        System.out.println("\n--- Training Long-Term Model ---");
        int m = x_train_long.length;
        if (m == 0) {
            System.err.println("No training data available for long-term model.");
            return;
        }
        int n = longTermWeights.length;

        double bestValLoss = Double.MAX_VALUE;
        double[] bestWeights = new double[n];
        double bestBias = 0.0;
        int patienceCounter = 0;
        int patience = 50;

        for (int epoch = 0; epoch < epochs; epoch++) {
            double[] predictions = new double[m];
            for (int i = 0; i < m; i++) {
                predictions[i] = longTermBias;
                for (int j = 0; j < n; j++) {
                    predictions[i] += x_train_long[i][j] * longTermWeights[j];
                }
            }

            double[] gradients = new double[n];
            double biasGradient = 0.0;
            for (int i = 0; i < m; i++) {
                double error = predictions[i] - y_train_long[i];
                biasGradient += error;
                for (int j = 0; j < n; j++) {
                    gradients[j] += error * x_train_long[i][j] + longTermL2Lambda * longTermWeights[j];
                }
            }

            for (int j = 0; j < n; j++) {
                longTermWeights[j] -= longTermLearningRate * (gradients[j] / m);
            }
            longTermBias -= longTermLearningRate * (biasGradient / m);

            if (epoch % 50 == 0) {
                double trainMse = calculateMSE(x_train_long, y_train_long, longTermWeights, longTermBias);
                double valMse = calculateMSE(x_val_long, y_val_long, longTermWeights, longTermBias);
//                System.out.println("Epoch " + epoch + " | Train MSE: " + String.format("%.8f", trainMse) + " | Val MSE: " + String.format("%.8f", valMse));

                if (valMse < bestValLoss) {
                    bestValLoss = valMse;
                    System.arraycopy(longTermWeights, 0, bestWeights, 0, n);
                    bestBias = longTermBias;
                    patienceCounter = 0;
                } else {
                    patienceCounter++;
                    if (patienceCounter >= patience) {
                        System.out.println("Early stopping at epoch " + epoch);
                        break;
                    }
                }
            }
        }

        System.arraycopy(bestWeights, 0, longTermWeights, 0, n);
        longTermBias = bestBias;

        double testMse = calculateMSE(x_test_long, y_test_long, longTermWeights, longTermBias);
        System.out.println("Final Long-Term Test MSE: " + testMse);
        System.out.println("--- Long-Term Model Training Complete ---");
    }

    public double predict_long_term(String stockName, Date date) {
        // FIX: Table name should be consistent
        String tableName = "stock_" + stockName.toLowerCase();
        double[] features = prepare_long_data(tableName, date);
        double lastKnownClose = jdbcManager.getCloseOnDate(tableName, date);
        if (Double.isNaN(lastKnownClose)) return 0.0;

        double predictedChange = longTermBias;
        for (int i = 0; i < features.length; i++) {
            predictedChange += features[i] * longTermWeights[i];
        }
        return lastKnownClose * (1 + predictedChange);
    }

    private double calculateMSE(double[][] X, double[] y, double[] w, double b) {
        if (X == null || X.length == 0) return 0.0;
        double mse = 0.0;
        int count = 0;
        for (int i = 0; i < X.length; i++) {

            if (X[i] == null) continue;

            double prediction = b;
            for (int j = 0; j < X[i].length; j++) {
                prediction += X[i][j] * w[j];
            }


            if (Double.isNaN(prediction) || Double.isNaN(y[i])) {
                continue;
            }

            double error = prediction - y[i];
            mse += error * error;
            count++;
        }

        return count == 0 ? 0.0 : mse / count;
    }
}
