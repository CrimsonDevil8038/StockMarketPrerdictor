package Stock_Predictor;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVRecord;

import java.io.*;
import java.util.*;

public class CSV_Manager {
    HashMap<String, Stock> stockHashMap = new HashMap<>();

    void readCSV(String path, String name) {

        Reader in = null;
        try {
            in = new FileReader("C:\\Users\\patel\\Downloads\\Stock_Files\\543940.CSV");
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }

        Iterable<CSVRecord> records = null;
            try {
                records = CSVFormat.EXCEL
                        .withFirstRecordAsHeader()  // 👈 Add this line
                        .parse(in);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }


        for (CSVRecord record : records) {
            String lastName = record.get("Date");
            String firstName = record.get("Open Price");
            System.out.println(lastName+"\t"+firstName);
        }
    }
}

