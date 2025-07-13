import java.util.LinkedList;

public class Stock {
    private LinkedList<Stock_Data> stockDataLinkedList= new LinkedList<Stock_Data>();
    private  String firstLine;

    public LinkedList<Stock_Data> getStockDataLinkedList() {
        return stockDataLinkedList;
    }

    public void setFirstLine(String firstLine) {
        this.firstLine = firstLine;
    }

    public String getFirstLine() {
        return firstLine;
    }
}
