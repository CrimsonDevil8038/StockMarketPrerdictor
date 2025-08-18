package Stock_Predictor.Predict_And_Analysis;

public class BollingerBands {

    private final double upperBand;
    private final double middleBand;
    private final double lowerBand;

    public BollingerBands(double upperBand, double middleBand, double lowerBand) {
        this.upperBand = upperBand;
        this.middleBand = middleBand;
        this.lowerBand = lowerBand;
    }

    public double getUpperBand() {
        return upperBand;
    }

    public double getMiddleBand() {
        return middleBand;
    }

    public double getLowerBand() {
        return lowerBand;
    }

    @Override
    public String toString() {
        return "Upper: " + String.format("%.2f", upperBand) +
                ", Middle: " + String.format("%.2f", middleBand) +
                ", Lower: " + String.format("%.2f", lowerBand);
    }
}