package Stock_Predictor;

public class MACDResult {
    private final double macdLine;
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

    @Override
    public String toString() {
        return "MACD Line: " + String.format("%.2f", macdLine) +
                ", Signal Line: " + String.format("%.2f", signalLine);
    }
}