package Stock_Predictor.Predict_And_Analysis;

import Stock_Predictor.JDBC.JDBC_Manager;

import java.sql.Date;
import java.util.Calendar;

import static Stock_Predictor.Color.RED;
import static Stock_Predictor.Color.RESET;

public class DataPreparation {

    public double[][] x_train, x_val, x_test;
    public double[] y_train, y_val, y_test;
    public double bias = 0.0;
    public double[] weights;
    public double learningRate = 0.01;
    public double l2Lambda = 0.01;
    private double[] featureMeans;
    private double[] featureStdDevs;

    JDBC_Manager jdbcManager = new JDBC_Manager();


    double[] prepare_short_data(String tableName, Date date) {
        double[] data = new double[16];

        try {
            Date preDate1 = jdbcManager.getPreviousDate(date, tableName);
            Date preDate2 = preDate1 != null ? jdbcManager.getPreviousDate(preDate1, tableName) : null;

            if (preDate1 == null) return data;

            double close = jdbcManager.getCloseOnDate(tableName, date);
            if (Double.isNaN(close) || close == 0) return data;

            // 1. Price momentum (2-day)
            if (preDate2 != null) {
                double close2 = jdbcManager.getCloseOnDate(tableName, preDate2);
                double close1 = jdbcManager.getCloseOnDate(tableName, preDate1);
                if (close2 > 0 && close1 > 0) {
                    data[0] = ((close - close2) / close2);
                    data[1] = ((close - close1) / close1);
                }
            }

            // 2. Moving average ratios
            data[2] = (jdbcManager.getSMA5OnDate(tableName, date) / close) - 1.0;
            data[3] = (jdbcManager.getSMA10OnDate(tableName, date) / close) - 1.0;
            data[4] = (jdbcManager.getSMA50OnDate(tableName, date) / close) - 1.0;

            // 3. EMA ratios
            data[5] = (jdbcManager.getEMA5OnDate(tableName, date) / close) - 1.0;
            data[6] = (jdbcManager.getEMA10OnDate(tableName, date) / close) - 1.0;
            data[7] = (jdbcManager.getEMA50OnDate(tableName, date) / close) - 1.0;

            // 4. Technical indicators
            double rsi = jdbcManager.getRSI14OnDate(tableName, date);
            data[8] = Double.isNaN(rsi) ? 0.0 : (rsi - 50.0) / 50.0;

            double stochastic = jdbcManager.getStochasticOnDate(tableName, date);
            data[9] = Double.isNaN(stochastic) ? 0.0 : (stochastic - 50.0) / 50.0;

            // 5. MACD
            data[10] = jdbcManager.getMACDLineOnDate(tableName, date) / close;
            data[11] = jdbcManager.getSignalLineOnDate(tableName, date) / close;

            // 6. Bollinger Bands
            double upper = jdbcManager.getUpperBandOnDate(tableName, date);
            double middle = jdbcManager.getMiddleBandOnDate(tableName, date);
            double lower = jdbcManager.getLowerBandOnDate(tableName, date);
            if (middle > 0 && !Double.isNaN(upper) && !Double.isNaN(lower)) {
                data[12] = (close - middle) / middle;
                data[13] = (upper - lower) / middle;
            }

            // 7. Volume indicators
            double volumeDeviation = jdbcManager.getVolDevNormOnDate(tableName, date);
            data[14] = Double.isNaN(volumeDeviation) ? 0.0 : Math.max(-3.0, Math.min(3.0, volumeDeviation));

            // 8. VWAP relative position
            double vwap = jdbcManager.getVWAPOnDate(tableName, date);
            data[15] = (vwap > 0 && !Double.isNaN(vwap)) ? ((close - vwap) / vwap) : 0.0;

            // Replace NaN values with 0
            for (int i = 0; i < data.length; i++) {
                if (Double.isNaN(data[i])) {
                    data[i] = 0.0;
                }
            }

            return data;

        } catch (Exception e) {
            System.out.println(RED + "Error preparing features: " + e.getMessage() + RESET);
            return data;
        }
    }

    public void data_short_term(String stockName) {
        try {
            Date startDate = jdbcManager.get_minDate(stockName);
            Date endDate = jdbcManager.get_maxDate(stockName);
            int duration = jdbcManager.getTradingDayCount(stockName);
            int features = 16;


            int test_data = (int) (Math.round(0.2 * duration));
            int val_data = (int) (Math.round(0.2 * duration));
            int train_data_size = duration - test_data - val_data;


            x_train = new double[train_data_size][features];
            y_train = new double[train_data_size];
            x_val = new double[val_data - 1][features];
            y_val = new double[val_data - 1];
            x_test = new double[test_data - 1][features];
            y_test = new double[test_data - 1];

            weights = new double[features];
            for (int i = 0; i < weights.length; i++) {
                weights[i] = (Math.random() - 0.5) * 0.001;
            }

//            System.out.println("Training next-day prediction model...");
//            System.out.println("Training samples: " + (train_data_size - 41));
//            System.out.println("Validation samples: " + (val_data - 1));
//            System.out.println("Test samples: " + (test_data - 1));


            Date currentDate = addBusinessDays(startDate, 40);
            for (int i = 0; i < x_train.length; i++) {
                Date nextBusinessDay = jdbcManager.calculateNextBusinessDay(currentDate);

                double currentDayClose = jdbcManager.getCloseOnDate(stockName, currentDate);
                double nextDayClose = jdbcManager.getCloseOnDate(stockName, nextBusinessDay);

                if (currentDayClose != 0 && !Double.isNaN(currentDayClose) &&
                        !Double.isNaN(nextDayClose) && nextDayClose != 0) {
                    y_train[i] = (nextDayClose - currentDayClose) / currentDayClose;
                } else {
                    y_train[i] = 0.0;
                }


                x_train[i] = prepare_short_data(stockName, currentDate);
                currentDate = jdbcManager.calculateNextBusinessDay(currentDate);
            }


            for (int i = 0; i < x_val.length; i++) {
                Date nextBusinessDay = jdbcManager.calculateNextBusinessDay(currentDate);

                double currentDayClose = jdbcManager.getCloseOnDate(stockName, currentDate);
                double nextDayClose = jdbcManager.getCloseOnDate(stockName, nextBusinessDay);

                if (currentDayClose != 0 && !Double.isNaN(currentDayClose) &&
                        !Double.isNaN(nextDayClose) && nextDayClose != 0) {
                    y_val[i] = (nextDayClose - currentDayClose) / currentDayClose;
                } else {
                    y_val[i] = 0.0;
                }

                x_val[i] = prepare_short_data(stockName, currentDate);
                currentDate = jdbcManager.calculateNextBusinessDay(currentDate);
            }


            for (int i = 0; i < x_test.length; i++) {
                Date nextBusinessDay = jdbcManager.calculateNextBusinessDay(currentDate);

                double currentDayClose = jdbcManager.getCloseOnDate(stockName, currentDate);
                double nextDayClose = jdbcManager.getCloseOnDate(stockName, nextBusinessDay);

                if (currentDayClose != 0 && !Double.isNaN(currentDayClose) &&
                        !Double.isNaN(nextDayClose) && nextDayClose != 0) {
                    y_test[i] = (nextDayClose - currentDayClose) / currentDayClose;
                } else {
                    y_test[i] = 0.0;
                }

                x_test[i] = prepare_short_data(stockName, currentDate);
                currentDate = jdbcManager.calculateNextBusinessDay(currentDate);
            }

            normalizeFeatures(features);

        } catch (Exception e) {
            System.out.println(RED + "Exception " + e.getMessage() + ": " + e.getCause() + RESET);
            throw new RuntimeException(e);
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
        try {
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
//                    System.out.println("Epoch " + epoch + " | Train MSE: " + String.format("%.8f", trainMse) + " | Val MSE: " + String.format("%.8f", valMse));

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
//        System.out.println("Final Test MSE (on price % change): " + testMse);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public double predict(String stockName, Date date) {

        double[] features = prepare_short_data(stockName, date);
        double currentClose = jdbcManager.getCloseOnDate(stockName, date);

        if (Double.isNaN(currentClose) || currentClose == 0) {
            System.out.println(RED + "Cannot get close price for date: " + date + RESET);
            return 0.0;
        }

        if (featureMeans != null && featureStdDevs != null) {
            for (int i = 0; i < features.length && i < featureMeans.length; i++) {
                features[i] = (features[i] - featureMeans[i]) / featureStdDevs[i];
            }
        }

        double predictedChange = bias;
        for (int i = 0; i < features.length && i < weights.length; i++) {
            predictedChange += features[i] * weights[i];
        }

        double nextDayPredictedPrice = currentClose * (1 + predictedChange);

        System.out.printf("Using features from %s (close: %.2f) to predict next business day price: %.2f (%.2f%% change)\n",
                date, currentClose, nextDayPredictedPrice, predictedChange * 100);

        return nextDayPredictedPrice;
    }

    public double[] prepare_long_data(String tableName, Date date) {
        double[] data = new double[12]; // Changed from 10 to 12

        try {
            Date preDate1 = jdbcManager.getPreviousDate(date, tableName);
            Date preDate2 = preDate1 != null ? jdbcManager.getPreviousDate(preDate1, tableName) : null;

            double close = jdbcManager.getCloseOnDate(tableName, date);
            if (Double.isNaN(close) || close == 0) {
                return data;
            }

            // 1. Long-term price momentum
            if (preDate2 != null) {
                double close1 = jdbcManager.getCloseOnDate(tableName, preDate1);
                double close2 = jdbcManager.getCloseOnDate(tableName, preDate2);
                if (close1 > 0 && close2 > 0) {
                    data[0] = ((close - close1) / close1);
                    data[1] = ((close - close2) / close2);
                }
            }

            // 2. Long-term moving average ratios
            data[2] = (jdbcManager.getSMA50OnDate(tableName, date) / close) - 1.0;
            data[3] = (jdbcManager.getSMA100OnDate(tableName, date) / close) - 1.0;
            data[4] = (jdbcManager.getSMA200OnDate(tableName, date) / close) - 1.0;

            // 3. Long-term EMA ratios
            data[5] = (jdbcManager.getEMA50OnDate(tableName, date) / close) - 1.0;
            data[6] = (jdbcManager.getEMA100OnDate(tableName, date) / close) - 1.0;
            data[7] = (jdbcManager.getEMA200OnDate(tableName, date) / close) - 1.0;

            // 4. Long-term RSI (30-period for long-term trends)
            double rsi30 = jdbcManager.getRSI30OnDate(tableName, date);
            data[8] = Double.isNaN(rsi30) ? 0.0 : (rsi30 - 50.0) / 50.0;

            // 5. Moving average crossover signal (trend direction)
            double sma50 = jdbcManager.getSMA50OnDate(tableName, date);
            double sma200 = jdbcManager.getSMA200OnDate(tableName, date);
            data[9] = (Double.isNaN(sma50) || Double.isNaN(sma200)) ? 0.0 : (sma50 > sma200 ? 1.0 : -1.0);

            // 6. MACD for long-term momentum
            data[10] = jdbcManager.getMACDLineOnDate(tableName, date) / close;

            // 7. Bollinger Band position for volatility context
            double upperBand = jdbcManager.getUpperBandOnDate(tableName, date);
            double lowerBand = jdbcManager.getLowerBandOnDate(tableName, date);
            double middleBand = jdbcManager.getMiddleBandOnDate(tableName, date);
            data[11] = (middleBand == 0 || Double.isNaN(upperBand) ||
                    Double.isNaN(lowerBand) || Double.isNaN(middleBand)) ?
                    0.0 : (upperBand - lowerBand) / middleBand;


            for (int i = 0; i < data.length; i++) {
                if (Double.isNaN(data[i])) {
                    data[i] = 0.0;
                }
            }

            return data;

        } catch (Exception e) {
            System.out.println(RED + "SQL Exception " + e.getCause() + RESET);
            return data;
        }
    }

    public void data_long_term(String stockName, int predictionHorizon) {
        try {
            String tableName = "stock_" + stockName.toLowerCase();
            Date startDate = jdbcManager.get_minDate(tableName);
            int duration = jdbcManager.getTradingDayCount(tableName);

            if (duration < 200 + predictionHorizon) {
                System.err.println("Not enough data for a long-term prediction with a " + predictionHorizon + "-day horizon.");
                return;
            }

            int numFeatures = 12;
            weights = new double[numFeatures];
            for (int i = 0; i < weights.length; i++) {
                weights[i] = (Math.random() - 0.5) * 0.001;
            }

            int test_size = (int) (0.2 * duration);
            int val_size = (int) (0.2 * duration);
            int train_size = duration - test_size - val_size;


            train_size = Math.max(0, train_size - predictionHorizon);
            val_size = Math.max(0, val_size - predictionHorizon);
            test_size = Math.max(0, test_size - predictionHorizon);

            if (train_size <= 0 || val_size <= 0 || test_size <= 0) {
                System.err.println("Not enough data after accounting for prediction horizon.");
                return;
            }

            x_train = new double[train_size][numFeatures];
            y_train = new double[train_size];
            x_val= new double[val_size][numFeatures];
            y_val= new double[val_size];
            x_test= new double[test_size][numFeatures];
            y_test= new double[test_size];

//            System.out.println("Training long-term prediction model...");
//            System.out.println("Prediction horizon: " + predictionHorizon + " business days");
//            System.out.println("Training samples: " + train_size);
//            System.out.println("Validation samples: " + val_size);
//            System.out.println("Test samples: " + test_size);


            Date currentDate = addBusinessDays(startDate, 200);
            for (int i = 0; i < train_size; i++) {
                Date futureDate = addBusinessDays(currentDate, predictionHorizon);

                double currentClose = jdbcManager.getCloseOnDate(tableName, currentDate);
                double futureClose = jdbcManager.getCloseOnDate(tableName, futureDate);

                if (currentClose != 0 && !Double.isNaN(currentClose) &&
                        !Double.isNaN(futureClose) && futureClose != 0) {
                    y_train[i] = (futureClose - currentClose) / currentClose;
                } else {
                    y_train[i] = 0.0;
                }

                x_train[i] = prepare_long_data(tableName, currentDate);
                currentDate = addBusinessDays(currentDate, 1);
            }

            for (int i = 0; i < val_size; i++) {
                Date futureDate = addBusinessDays(currentDate, predictionHorizon);

                double currentClose = jdbcManager.getCloseOnDate(tableName, currentDate);
                double futureClose = jdbcManager.getCloseOnDate(tableName, futureDate);

                if (currentClose != 0 && !Double.isNaN(currentClose) &&
                        !Double.isNaN(futureClose) && futureClose != 0) {
                    y_val[i] = (futureClose - currentClose) / currentClose;
                } else {
                    y_val[i] = 0.0;
                }

                x_val[i] = prepare_long_data(tableName, currentDate);
                currentDate = addBusinessDays(currentDate, 1);
            }

            for (int i = 0; i < test_size; i++) {
                Date futureDate = addBusinessDays(currentDate, predictionHorizon);

                double currentClose = jdbcManager.getCloseOnDate(tableName, currentDate);
                double futureClose = jdbcManager.getCloseOnDate(tableName, futureDate);

                if (currentClose != 0 && !Double.isNaN(currentClose) &&
                        !Double.isNaN(futureClose) && futureClose != 0) {
                    y_test[i] = (futureClose - currentClose) / currentClose;
                } else {
                    y_test[i] = 0.0;
                }

                x_test[i] = prepare_long_data(tableName, currentDate);
                currentDate = addBusinessDays(currentDate, 1);
            }

        } catch (Exception e) {
            System.out.println(RED + "Exception " + e.getMessage() + ": " + e.getCause() + RESET);
            throw new RuntimeException(e);
        }
    }

    public void gradientDescent_long_term(int epochs) {
        int m = x_train.length;
        if (m == 0) {
            System.out.println(RED+"No training data available for long-term model."+RESET);
            return;
        }
        int n = weights.length;

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

            if (epoch % 50 == 0) {
                double trainMse = calculateMSE(x_train, y_train, weights, bias);
                double valMse = calculateMSE(x_val, y_val, weights, bias);
//               System.out.println("Epoch " + epoch + " | Train MSE: " + String.format("%.8f", trainMse) + " | Val MSE: " + String.format("%.8f", valMse));

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

        double testMse = calculateMSE(x_test, y_test, weights,bias);
        System.out.println("Final Long-Term Test MSE: " + testMse);
        System.out.println("--- Long-Term Model Training Complete ---");
    }

    public double predict_long_term(String stockName, Date date) {
        String tableName = "stock_" + stockName.toLowerCase();


        double[] features = prepare_long_data(tableName, date);
        double currentClose = jdbcManager.getCloseOnDate(tableName, date);

        if (Double.isNaN(currentClose) || currentClose == 0) {
            System.out.println(RED + "Cannot get close price for date: " + date + RESET);
            return 0.0;
        }

        double predictedChange = bias;
        for (int i = 0; i < features.length && i < weights.length; i++) {
            predictedChange += features[i] * weights[i];
        }

        double predictedPrice = currentClose * (1 + predictedChange);

        System.out.printf("Long-term: Using features from %s (close: %.2f) to predict future price: %.2f (%.2f%% change)\n",
                date, currentClose, predictedPrice, predictedChange * 100);

        return predictedPrice;
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

    public void normalizeFeatures(int numFeatures) {
        featureMeans = new double[numFeatures];
        featureStdDevs = new double[numFeatures];
        int trainSize = x_train.length;


        for (int j = 0; j < numFeatures; j++) {
            double sum = 0.0;
            for (int i = 0; i < trainSize; i++) {
                sum += x_train[i][j];
            }
            featureMeans[j] = sum / trainSize;

            double sumOfSquares = 0.0;
            for (int i = 0; i < trainSize; i++) {
                sumOfSquares += Math.pow(x_train[i][j] - featureMeans[j], 2);
            }
            featureStdDevs[j] = Math.sqrt(sumOfSquares / trainSize);
            if (featureStdDevs[j] == 0) featureStdDevs[j] = 1;
        }


        applyNormalization(x_train);
        applyNormalization(x_val);
        applyNormalization(x_test);
    }

    private void applyNormalization(double[][] dataset) {
        if (dataset == null || dataset.length == 0) return;
        for (int i = 0; i < dataset.length; i++) {
            for (int j = 0; j < dataset[0].length; j++) {
                dataset[i][j] = (dataset[i][j] - featureMeans[j]) / featureStdDevs[j];
            }
        }
    }


    public double predictSilent(String stockName) {

        Date date = jdbcManager.get_maxDate(stockName);

        double[] features = prepare_short_data(stockName, date);
        double currentClose = jdbcManager.getCloseOnDate(stockName, date);

        if (Double.isNaN(currentClose) || currentClose == 0) {
            return 0.0;
        }

        if (featureMeans != null && featureStdDevs != null) {
            for (int i = 0; i < features.length && i < featureMeans.length; i++) {
                features[i] = (features[i] - featureMeans[i]) / featureStdDevs[i];
            }
        }


        double predictedChange = bias;
        for (int i = 0; i < features.length && i < weights.length; i++) {
            predictedChange += features[i] * weights[i];
        }


        double nextDayPredictedPrice = currentClose * (1 + predictedChange);

        return nextDayPredictedPrice;
    }
}
