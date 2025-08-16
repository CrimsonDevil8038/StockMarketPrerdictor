package Stock_Predictor.Predict_And_Analysis;

import java.sql.Date;

public class Stock_Data {
    /*
Error:
Minor value mismatches (e.g., X.0000000002 instead of expected X) observed in computed results.

Cause:
Floating-point precision errors caused by binary representation of decimal numbers in programming languages. Exact decimal fractions cannot always be stored in float/double, leading to small rounding artifacts during arithmetic operations.

Solution:
Use decimal-safe types (e.g., Java BigDecimal, Python Decimal) for parsing and computation when precision is important.
Apply explicit rounding to a fixed number of decimal places before storing or displaying results.
     */
    private Date official_date;
    private String date;
    private double open;
    private double high;
    private double low;
    private double close;
    private double volume;
    private double vwap;
    private double typicalPrice;
    private double sma_5;
    private double sma_10;
    private double sma_15;
    private double sma_50;
    private double sma_100;
    private double sma_200;
    private double ema_5;
    private double ema_10;
    private double ema_15;
    private double ema_50;
    private double ema_100;
    private double ema_200;
    private double rsi_14;
    private double rsi_30;
    private double macdline;
    private double signalline;
    private double upperband;
    private double middleband;
    private double lowerband;
    private double stochastic;
    private double sma_20volume;
    private double volDevNorm;


    public Stock_Data(String date, double open, double high, double low, double close, double volume) {
        this.date = date;
        this.open = open;
        this.high = high;
        this.low = low;
        this.close = close;
        this.volume = volume;

        vwap = -1;
        typicalPrice = sma_5 = sma_10 = sma_15 = sma_50 = sma_100 = sma_200 = vwap;
        rsi_14 = rsi_30 = ema_5 = ema_10 = ema_15 = ema_50 = ema_100 = ema_200 = vwap;
        macdline = signalline = upperband = middleband = lowerband = stochastic = sma_20volume = volDevNorm = vwap;
    }

    public Stock_Data(Date date, double open, double high, double low, double close, double volume) {
        this.official_date = date;
        this.open = open;
        this.high = high;
        this.low = low;
        this.close = close;
        this.volume = volume;
        // Initialize all derived fields to 0
        vwap = 0;
        typicalPrice = sma_5 = sma_10 = sma_15 = sma_50 = sma_100 = sma_200 = vwap;
        rsi_14 = rsi_30 = ema_5 = ema_10 = ema_15 = ema_50 = ema_100 = ema_200 = vwap;
        macdline = signalline = upperband = middleband = lowerband = stochastic = sma_20volume = vwap;
    }

    // --- Getters and Setters ---

    public Date getOfficial_date() {
        return official_date;
    }

    public void setOfficial_date(Date official_date) {
        this.official_date = official_date;
    }

    public double getRsi_14() {
        return rsi_14;
    }

    public void setRsi_14(double rsi_14) {
        this.rsi_14 = rsi_14;
    }

    public double getRsi_30() {
        return rsi_30;
    }

    public void setRsi_30(double rsi_30) {
        this.rsi_30 = rsi_30;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public double getOpen() {
        return open;
    }

    public void setOpen(double open) {
        this.open = open;
    }

    public double getHigh() {
        return high;
    }

    public void setHigh(double high) {
        this.high = high;
    }

    public double getLow() {
        return low;
    }

    public void setLow(double low) {
        this.low = low;
    }

    public double getClose() {
        return close;
    }

    public void setClose(double close) {
        this.close = close;
    }

    public double getVolume() {
        return volume;
    }

    public void setVolume(double volume) {
        this.volume = volume;
    }

    public double getVwap() {
        return vwap;
    }

    public void setVwap(double vwap) {
        this.vwap = vwap;
    }

    public double getTypicalPrice() {
        return typicalPrice;
    }

    public void setTypicalPrice(double typicalPrice) {
        this.typicalPrice = typicalPrice;
    }

    public double getSma_5() {
        return sma_5;
    }

    public void setSma_5(double sma_5) {
        this.sma_5 = sma_5;
    }

    public double getSma_10() {
        return sma_10;
    }

    public void setSma_10(double sma_10) {
        this.sma_10 = sma_10;
    }

    public double getSma_15() {
        return sma_15;
    }

    public void setSma_15(double sma_15) {
        this.sma_15 = sma_15;
    }

    public double getSma_50() {
        return sma_50;
    }

    public void setSma_50(double sma_50) {
        this.sma_50 = sma_50;
    }

    public double getSma_100() {
        return sma_100;
    }

    public void setSma_100(double sma_100) {
        this.sma_100 = sma_100;
    }

    public double getSma_200() {
        return sma_200;
    }

    public void setSma_200(double sma_200) {
        this.sma_200 = sma_200;
    }

    public double getEma_5() {
        return ema_5;
    }

    public void setEma_5(double ema_5) {
        this.ema_5 = ema_5;
    }

    public double getEma_10() {
        return ema_10;
    }

    public void setEma_10(double ema_10) {
        this.ema_10 = ema_10;
    }

    public double getEma_15() {
        return ema_15;
    }

    public void setEma_15(double ema_15) {
        this.ema_15 = ema_15;
    }

    public double getEma_50() {
        return ema_50;
    }

    public void setEma_50(double ema_50) {
        this.ema_50 = ema_50;
    }

    public double getEma_100() {
        return ema_100;
    }

    public void setEma_100(double ema_100) {
        this.ema_100 = ema_100;
    }

    public double getEma_200() {
        return ema_200;
    }

    public void setEma_200(double ema_200) {
        this.ema_200 = ema_200;
    }

    public double getMacdline() {
        return macdline;
    }

    public void setMacdline(double macdline) {
        this.macdline = macdline;
    }

    public double getSignalline() {
        return signalline;
    }

    public void setSignalline(double signalline) {
        this.signalline = signalline;
    }

    public double getUpperband() {
        return upperband;
    }

    public void setUpperband(double upperband) {
        this.upperband = upperband;
    }

    public double getMiddleband() {
        return middleband;
    }

    public void setMiddleband(double middleband) {
        this.middleband = middleband;
    }

    public double getLowerband() {
        return lowerband;
    }

    public void setLowerband(double lowerband) {
        this.lowerband = lowerband;
    }

    public double getStochastic() {
        return stochastic;
    }

    public void setStochastic(double stochastic) {
        this.stochastic = stochastic;
    }

    public double getSma_20volume() {
        return sma_20volume;
    }

    public void setSma_20volume(double sma_20volume) {
        this.sma_20volume = sma_20volume;
    }
    public double getVolDevNorm() {
        return volDevNorm;
    }

    public void setVolDevNorm(double volDevNorm) {
        this.volDevNorm = volDevNorm;
    }

    @Override
    public String toString() {
        // Updated to include sma_20volume
        return String.format(
                "%s,%.3f,%.3f,%.3f,%.3f,%.3f,%.3f,%.3f,%.3f,%.3f,%.3f,%.3f,%.3f,%.3f,%.3f,%.3f,%.3f,%.3f,%.3f,%.3f,%.3f,%.3f,%.3f,%.3f,%.3f,%.3f,%.3f,%.3f,%.3f",
                official_date, open, high, low, close, volume, vwap, typicalPrice,
                sma_5, sma_10, sma_15, sma_50, sma_100, sma_200,
                ema_5, ema_10, ema_15, ema_50, ema_100, ema_200,
                rsi_14, rsi_30, macdline, signalline,
                upperband, middleband, lowerband, stochastic, sma_20volume
        );
    }
}
