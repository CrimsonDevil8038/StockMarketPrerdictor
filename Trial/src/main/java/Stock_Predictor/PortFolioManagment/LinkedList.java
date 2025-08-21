package Stock_Predictor.PortFolioManagment;

import java.sql.Date;
import static Stock_Predictor.Color.*;

class Node {
    String stockname;
    double purchaseprice;
    double currentprice;
    double predictedprice;
    long quantity;
    Date predictionDate;
    Date curentDate;
    Node next;

    public Node(String stockname, double purchaseprice, double currentprice, double predictedprice, long quantity, Date predictionDate, Date curentDate) {
        this.stockname = stockname;
        this.purchaseprice = purchaseprice;
        this.currentprice = currentprice;
        this.predictedprice = predictedprice;
        this.quantity = quantity;
        this.predictionDate = predictionDate;
        this.curentDate = curentDate;
        this.next = null;
    }
}


class LinkedList {
    Node head;

    void addLast(String stockname, double purchaseprice, double currentprice, double predictedprice, long quantity, Date predictionDate, Date curentDate) {
        Node n = new Node(stockname, purchaseprice, currentprice, predictedprice, quantity, predictionDate, curentDate);
        if (head == null) {
            head = n;
        } else {
            Node temp = head;
            while (temp.next != null) {
                temp = temp.next;
            }
            temp.next = n;
        }
    }

    void display() {
        if (head == null) {
            System.out.println("\n--- Your Portfolio is empty ---");
            return;
        }
        Node temp = head;
        System.out.println(GREEN+"\n--- Your Portfolio ---"+GREEN);
        while (temp != null) {
            System.out.println("=========================================================================");

            String format1 = BLUE+"%-16s %-15s %-16s %-15d %-16s %-15.2f\n"+RESET;
            System.out.printf(format1,
                    "Stock Name:", temp.stockname,
                    "Quantity:", temp.quantity,
                    "Purchase Price:", temp.purchaseprice);

            String format2 = BLUE+"%-16s %-15s %-16s %-15.2f\n"+RESET;
            System.out.printf(format2,
                    "Current Date:", temp.curentDate,
                    "Current Price:", temp.currentprice);
            System.out.printf(format2,
                    "Prediction Date:", temp.predictionDate,
                    "Prediction Price:", temp.predictedprice);
            temp = temp.next;
        }
    }

    long sellShares(String stockname, long quantityToSell) {
        if (head == null) {
            System.out.println(RED + "Portfolio is empty." + RESET);
            return -1;
        }

        Node current = head;
        Node previous = null;

        while (current != null && !current.stockname.equalsIgnoreCase(stockname)) {
            previous = current;
            current = current.next;
        }

        if (current == null) {
            System.out.println(RED + "Stock '" + stockname + "' not found in your portfolio." + RESET);
            return -1;
        }

        if (current.quantity < quantityToSell) {
            System.out.println(RED + "Error: Not enough shares to sell. You have " + current.quantity + " but tried to sell " + quantityToSell + "." + RESET);
            return -2;
        }

        current.quantity -= quantityToSell;
        System.out.println(GREEN + "Sold " + quantityToSell + " shares of " + stockname + ". New quantity: " + current.quantity + RESET);

        if (current.quantity == 0) {
            System.out.println(YELLOW + stockname + " removed from portfolio as quantity is zero." + RESET);
            if (previous == null) {
                head = current.next;
            } else {
                previous.next = current.next;
            }
        }
        return current.quantity;
    }
}