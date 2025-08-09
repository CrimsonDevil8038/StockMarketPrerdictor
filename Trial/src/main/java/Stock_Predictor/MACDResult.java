package Stock_Predictor;

public class MACDResult {
    private static final long serialVersionUID = 5;
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

}