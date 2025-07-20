package Stock_Predictor;

import java.util.Stack;

public class DerivedIndicators {

    double averagePrice(double high, double low, double close) {
        return ((high + low + close) / 3);
    }

    double medianPrice(double high, double low) {
        return ((high + low) / 2);
    }

    double priceRange(double high, double low) {
        return (high - low);
    }

    double bodySize(double open, double closs) {
        return (closs - open);
    }

    double simpleMovingAverage(double[] close) {

        double sum = 0;
        for (int i = 0; i < close.length; i++) {
            sum += close[i];
        }
        return (sum / close.length);
    }

    double exponentialMovingAverage(double[] close) {

        double sum = 0;
        for (int i = close.length - 1; i > 1; i++) {
            sum += close[i];
        }
        double sma = (sum / (close.length - 1));

        double a = 2 / (close.length);

        return ((a * close[0]) + ((1 - a) * sma));
    }

    double movingAverageConvergenceDivergence(double[] close_1, double[] close_2) {
        return Math.abs(exponentialMovingAverage(close_1) - exponentialMovingAverage(close_2));
    }

    double vwap(double[] high, double[] low, double[] close, double[] volume) {

        double sum = 0;
        double vsum = 0;
        for (int i = 0; i < high.length; i++) {
            double sma = (averagePrice(high[i], low[i], close[i]) * volume[i]);
            sum += sma;
            vsum += volume[i];
        }

        return (sum / vsum);
    }

    double rsi(double[]  close,double[] open){
        Stack<Double> gain = new Stack<Double>();
        Stack<Double> loss = new Stack<Double>();
        for (int i = 0; i < 14; i++) {
            double difference = close[i] - open[i];
            if(difference < 0) {
                loss.push(difference);
            }
            else{
                gain.push(difference);
            }
        }
        double avggain=0;
        double avgloss=0;
        for(double d:loss){
            double sumloss=0;
            sumloss=+d;
            avgloss=sumloss/14;
        }
        for(double d:gain){
            double sumgain=0;
            sumgain+=d;
            avggain=sumgain/14;
        }
        double rs=avggain/avgloss;
        return 100-(100/(1+rs));
    }

    double roc(double close1,double[] close,int period){
        double roc=0;
        double avgsum=0;
        int count = 0;
        for (int i = close.length - 1; count <period ; i--, count++) {
            double sum = 0;
            sum += close[i];
            avgsum += sum/period;
        }
        roc=(((close1-avgsum)*100)/avgsum);
        return roc;
    }

    double[] soscillator(double close,double[] open,double[] high,double[] low){
        double[] k=new double[14];
        for(int i=high.length-1;i>high.length-15;i--){
            int j=0;
            k[j]=((close-low[i])/(high[i]-low[i]))*100;
        }
        return k;
    }
}