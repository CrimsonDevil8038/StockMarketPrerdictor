package Stock_Predictor.Predict_And_Analysis;

public class MACDResult {
    final double macdLine;
    private final double signalLine;

    public MACDResult(double macdLine, double signalLine) {
        this.macdLine = macdLine;
        this.signalLine = signalLine;
    }

    public double getMacdLine() {
        return macdLine;
    }

    public double getSignalLine() {
        return signalLine;
    }

}