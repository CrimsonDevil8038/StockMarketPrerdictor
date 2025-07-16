package Stock_Predictor;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;

import java.io.*;
import java.util.*;

public class CSV_Manager {
    HashMap<String, Stock> stockHashMap = new HashMap<>();

    void readCSV(String path, String name) {



        try(BufferedReader bufferedReader = new BufferedReader(new FileReader(path))){

            Map<String,Integer> selectedHeader = headerRefiner(getHeaders(new File(path)));

            Iterable<CSVRecord> records = null;
            records = CSVFormat.EXCEL
                    .withFirstRecordAsHeader()  // 👈 Add this line
                    .parse(bufferedReader);

            for (CSVRecord record : records) {
                List<Map.Entry<String, Integer>> entries = new ArrayList<>(selectedHeader.entrySet());
                Collections.sort(entries, Comparator.comparing(Map.Entry::getValue));
                int i = 0;
                for (Map.Entry<String, Integer> e : entries) {
                    String key = record.get(e.getKey());
                    System.out.print(e.getKey() + "\t" + key+"\t");
                    i++;
                }
                if(i == entries.size()){

                }


                System.out.println();

            }

        } catch (FileNotFoundException e) {

            throw new RuntimeException(e);

        } catch (IOException e) {

            throw new RuntimeException(e);
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

        String [][] multiHeaders = new String[][]{
                {"Date","Date ","Timestamp","Datetime", "Trade Date"},
                {"Open","Open ","Opening Price","O","Open Price"},
                
                {"High","High ","Day High","High Price"},
                {"Low","Low ","Day Low","Low Price"},
                {"Close","Close ","Closing Price","C","Close Price"},
                {"Volume","Shares Traded","Volume ","Shares Traded ","No.of Shares","Total Traded Quantity","Traded Qty","Qty","Shares Traded","Total Volume","Volume Traded","Traded Volume"}
                };
        int z = 0;
        for (int i = 0; i < multiHeaders.length; i++) {
            for (int j = 0; j < multiHeaders[i].length; j++) {
                Integer n = headerMap.get(multiHeaders[i][j]);
                if(n == null){
                    continue;
                }else {
                    selectedHeaders.put(multiHeaders[i][j],n);
                    z++;
                    break;
                }
            }
        }
        System.out.println(selectedHeaders);

        return  selectedHeaders;

    }




}

