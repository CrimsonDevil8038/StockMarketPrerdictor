package Stock_Predictor;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;

import java.io.*;
import java.util.*;

public class CSV_Manager implements Serializable {

    private static final long SerialID = 0;

    private final HashMap<String, Stock> stockHashMap = new HashMap<>();
    private final String commonPath = "S:\\Shlok\\LEARNING\\Java\\Sem_2_Project\\StockMarketPrerdictor\\Trial\\src\\main\\resources";
    DerivedIndicators derivedIndicators = new DerivedIndicators();

    void readCSV(String path, String name) {


        try (BufferedReader bufferedReader = new BufferedReader(new FileReader(path))) {

            Map<String, Integer> selectedHeader = headerRefiner(getHeaders(new File(path)));
            Stock stock = new Stock();
            Iterable<CSVRecord> records = null;
            records = CSVFormat.EXCEL
                    .withFirstRecordAsHeader()  // 👈 Add this line
                    .parse(bufferedReader);

            for (CSVRecord record : records) {
                List<Map.Entry<String, Integer>> entries = new ArrayList<>(selectedHeader.entrySet());
                Collections.sort(entries, Comparator.comparing(Map.Entry::getValue));
                int i = 0;
                String[] key_values = new String[entries.size()];
                for (Map.Entry<String, Integer> e : entries) {
                    String key = record.get(e.getKey());
                    key_values[i] = key;
                    i++;
                }
                if (i == entries.size()) {
                    Stock_Data stockData = dataEntries(key_values);
                    stock.getStock_data().add(stockData);
                }
            }

            stockHashMap.put(name, stock);
            writeCSV(name);

        } catch (FileNotFoundException e) {

            throw new RuntimeException(e);

        } catch (IOException e) {

            throw new RuntimeException(e);
        }

    }

    Map<String, Integer> getHeaders(File path) {
        Map<String, Integer> headerMap = new HashMap<>();
        try (BufferedReader bufferedReader = new BufferedReader(new FileReader(path))) {

            CSVParser csvParser = CSVFormat.DEFAULT.withFirstRecordAsHeader().parse(bufferedReader);
            headerMap = csvParser.getHeaderMap();

        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        return headerMap;
    }

    Map<String, Integer> headerRefiner(Map<String, Integer> headerMap) {
        Map<String, Integer> selectedHeaders = new HashMap<>();

        String[][] multiHeaders = new String[][]{
                {"Date", "Date ", "Timestamp", "Datetime", "Trade Date"},
                {"Open", "Open ", "Opening Price", "O", "Open Price"},
                {"High", "High ", "Day High", "High Price"},
                {"Low", "Low ", "Day Low", "Low Price"},
                {"Close", "Close ", "Closing Price", "C", "Close Price"},
                {"Volume", "Shares Traded", "Volume ", "Shares Traded ", "No.of Shares", "Total Traded Quantity", "Traded Qty", "Qty", "Shares Traded", "Total Volume", "Volume Traded", "Traded Volume"}
        };
        int z = 0;
        for (int i = 0; i < multiHeaders.length; i++) {
            for (int j = 0; j < multiHeaders[i].length; j++) {
                Integer n = headerMap.get(multiHeaders[i][j]);
                if (n == null) {
                    continue;
                } else {
                    selectedHeaders.put(multiHeaders[i][j], n);
                    z++;
                    break;
                }
            }
        }

        return selectedHeaders;

    }

    Stock_Data dataEntries(String[] key_values) {
        return new Stock_Data(key_values[0], Double.parseDouble(key_values[1]), Double.parseDouble(key_values[2]),
                Double.parseDouble(key_values[3]), Double.parseDouble(key_values[4]),
                Double.parseDouble(key_values[5]));
    }

    void viewStock(String name) {
        if (stockHashMap.containsKey(name)) {
            stockHashMap.get(name).showStockData();
        } else {
            System.out.println("No Data Found");
        }
    }

    void viewStockTimePeriod(String name) {
        if (stockHashMap.containsKey(name)) {
            stockHashMap.get(name).showData_timeperiod();
        } else {
            System.out.println("No Data Found");
        }
    }

    void writeCSV(String name) {

        if (stockHashMap.containsKey(name)) {

            Stock stock = stockHashMap.get(name);
            File filegeneration = new File(commonPath, name + ".csv");
            try (BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(filegeneration))) {
                bufferedWriter.write("Date,Open,High,Low,CLose,Volume,VWAP");
                bufferedWriter.newLine();
                for (int i = 0; i < stock.getStock_data().size(); i++) {
                    bufferedWriter.write(stock.getStock_data().get(i).toCSV());
                    bufferedWriter.newLine();
                }

            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        } else {
            System.out.println("No Stock Data: " + name);
        }
    }

    void CSV(String path, String name) {
        if (path.contains("S:\\Shlok\\LEARNING\\Java\\Sem_2_Project\\StockMarketPrerdictor\\Trial\\src\\main\\resources")) {
            System.out.println("File Already Entered");
        } else {
            readCSV(path, name);
        }

    }
}

