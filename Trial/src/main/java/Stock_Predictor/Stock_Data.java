package Stock_Predictor;

public class Stock_Data {

    private final String date;
    private final double open;
    private final double high;
    private final double low;
    private final double close;
    private final double volume;
    private final double vwap;

    public Stock_Data(String date, double open, double high, double low, double close, double volume, double vwap) {
        this.date = date;
        this.open = open;
        this.high = high;
        this.low = low;
        this.close = close;
        this.volume = volume;
        this.vwap = vwap;
    }

    public String getDate() {
        return date;
    }

    public double getOpen() {
        return open;
    }

    public double getHigh() {
        return high;
    }

    public double getLow() {
        return low;
    }

    public double getClose() {
        return close;
    }

    public double getVolume() {
        return volume;
    }

    public double getVwap() {
        return vwap;
    }


    @Override
    public String toString() {
        String print = String.format("%-12s %12.2f %12.2f %12.2f %12.2f %12.2f %12.2f", date, open, high, low, close, volume, vwap);

        return print;
    }
}
