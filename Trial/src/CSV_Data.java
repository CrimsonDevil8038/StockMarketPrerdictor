import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.HashMap;
import java.util.Map;

class CSV_Data{
    // A CSV file is a  commoa seperated file.

    static private final HashMap<String,Stock> stockHashMap = new HashMap<String,Stock>();

    public void data_feeder(String path,String name){
        File stockfile  = new File(path);
        Stock stock = new Stock();
        if(stockfile.exists() && stockfile.isFile()){
            try(BufferedReader bufferedReader = new BufferedReader(new FileReader(stockfile))){
                String data = bufferedReader.readLine();

                while ((data = bufferedReader.readLine())!= null){
                    String [] data_form = data.split(",");
                    double[] double_values = new double[5];
                    long long_value;

                   double_values[0] = Double.parseDouble(data_form[1]);
                   double_values[1] = Double.parseDouble(data_form[2]);
                   double_values[2] = Double.parseDouble(data_form[3]);
                   double_values[3] = Double.parseDouble(data_form[4]);
                   long_value = Long.parseLong(data_form[5]);
                   double_values[4] = Double.parseDouble(data_form[6]);

                   stock.getStockDataLinkedList().add(new Stock_Data(data_form[0],double_values[0],double_values[1],double_values[2],double_values[3],double_values[4],long_value));

                }

                Stock stock_check = stockHashMap.put(name,stock);
                if(stock_check == null){
                    System.out.println("Stock Data Entered Successfully");
                }
                else{
                    System.err.println("Stock Data Entered Unsuccessfully");
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
        }else{
            System.out.println("Stock File of Path: "+ path+" does not exist");
        }
    }


    public  void data_viewer(String name){
        if(stockHashMap.containsKey(name)){
            Stock stock = stockHashMap.get(name);

            System.out.printf("%-12s %15s %15s %15s %15s %15s %15s\n",
                    "Date", "Open", "High", "Low", "Close", "Shares Traded", "Turnover");

            for (int i = 0;i<stock.getStockDataLinkedList().size();i++){
                System.out.println(stock.getStockDataLinkedList().get(i).toString());
            }
        }
        else{
            System.out.println("No Data Found");
            System.out.println("Please Check Stock Name FRom the Following:");
            int i = 1;
            for (Map.Entry<String,Stock> e: stockHashMap.entrySet()){
                System.out.println(i+"."+e.getKey());
            }

        }
    }

}
