package Stock_Predictor;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
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

    Map<String,Integer> getHeaders(File path){
        Map<String,Integer> headerMap = new HashMap<>();
        try(BufferedReader bufferedReader = new BufferedReader(new FileReader(path))){

            CSVParser  csvParser = CSVFormat.DEFAULT.withFirstRecordAsHeader().parse(bufferedReader);
            headerMap = csvParser.getHeaderMap();

        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        return headerMap;
    }

    Map<String,Integer> headerRefiner( Map<String,Integer> headerMap){
        Map<String,Integer> selectedHeaders = new HashMap<>();

        String [][] multiHeaders = new String[][]{{"Date","Timestamp","Datetime", "Trade Date"},
                {"Open","Opening Price","O","Open Price"},
                {"High","Day High","High Price"},
                {"Low","Day Low","Low Price"},
                {"Close","Closing Price","C","Close Price"},
                {"Volume","No.of Shares","Total Traded Quantity","Traded Qty","Qty","Shares Traded","Total Volume","Volume Traded","Traded Volume"}
                };
        int z = 0;
        for (int i = 0; i < multiHeaders.length; i++) {
            for (int j = 0; j < multiHeaders[i].length; j++) {
                Integer n = headerMap.get(multiHeaders[i][j]);
                if(n == null){
                    continue;
                }else {
                    selectedHeaders.put(multiHeaders[i][0],n);
                    z++;
                    break;
                }
            }
        }

        return  selectedHeaders;

    }

}

