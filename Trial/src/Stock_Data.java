public class Stock_Data {

   private String date;
   private double open;
   private double close;
   private double high;
   private double low;
   private long sharesTraded;
   private double turnover;

    public Stock_Data(String date, double open, double close, double high, double low , double turnover,long sharesTraded) {
        this.date = date;
        this.open = open;
        this.close = close;
        this.high = high;
        this.low = low;
        this.sharesTraded = sharesTraded;
        this.turnover = turnover;
    }

    public String getDate() {
        return date;
    }

    public double getOpen() {
        return open;
    }

    public double getClose() {
        return close;
    }

    public double getHigh() {
        return high;
    }

    public double getLow() {
        return low;
    }

    public long getSharesTraded() {
        return sharesTraded;
    }

    public double getTurnover() {
        return turnover;
    }



    @Override
    public String toString() {
        String data =String.format(
                "%-12s %15.5f %15.5f %15.5f %15.5f %15.5f %15d\n",date,open,close,high,low,turnover,sharesTraded);

        return data;
    }
}
