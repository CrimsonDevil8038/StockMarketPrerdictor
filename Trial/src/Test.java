import java.util.Scanner;

public class Test {
    public static void main(String[] args) {

        CSV_Data csvData= new CSV_Data();
        Scanner scanner = new Scanner(System.in);

       System.out.println("Enter Stock Name");
        String name = scanner.nextLine();

        System.out.println("Enter File Absolute Path");
        String path = scanner.nextLine();

        System.out.println(name+"\t\t"+path);


        csvData.data_feeder(path,name);
        csvData.data_viewer(name);
    }
}
