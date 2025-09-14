package Stock_Predictor;

import Stock_Predictor.Account.AccountManager;
import Stock_Predictor.JDBC.JDBC_Manager;
import Stock_Predictor.PortFolioManagment.Portfolio;
import Stock_Predictor.Predict_And_Analysis.CSV_Manager;
import Stock_Predictor.Predict_And_Analysis.DataPreparation;

import java.sql.Date;
import java.util.Scanner;

import static Stock_Predictor.Color.*;

public class Main {
    static Scanner scanner = new Scanner(System.in);
    static AccountManager accountManager = new AccountManager();
    static CSV_Manager csvManager = new CSV_Manager();
    static DataPreparation dataPreparation = new DataPreparation();
    static JDBC_Manager jdbcManager = new JDBC_Manager();
    static Portfolio portfolio;


    static void basic() {
        jdbcManager.create_Rest();

    }


    public static void main(String[] args) {
        System.out.println(YELLOW + "Welcome to Stock Prediction and Management" + RESET);

        try {
            basic();
        } catch (Exception e) {
            System.out.println(RED+"Unable to Generate the Table"+RESET);
        }

        int n = 0;
        do {
            System.out.println("Press 1 to Signup");
            System.out.println("Press 2 to Signin");
            System.out.println(RED + "Press 3 to Exit System" + RESET);


            try {
                System.out.print(BLUE + "Enter Choice: " + RESET);
                n = scanner.nextInt();
            } catch (Exception e) {
                System.out.println(RED + "Invalid Choice: Please Enter a  number" + RESET);
                scanner.next();
                n = -1;
            }

            switch (n) {
                case 1: {
                    System.out.println();
                    accountManager.data();
                    System.out.println();
                    break;
                }
                case 2: {
                    System.out.println();
                    if (accountManager.check_credentials()) {
                        String loggedInUsername = accountManager.getUsername();
                        int userId = jdbcManager.get_userid(loggedInUsername);

                        if (userId != -1) {
                            portfolio = new Portfolio(jdbcManager, dataPreparation);
                            portfolio.setUserid(userId);
                            stock_main(loggedInUsername);
                        } else {
                            System.out.println(RED + "Could not retrieve user details from database." + RESET);
                        }

                    } else {
                        System.out.println(RED + "Forceful Exit" + RESET);
                    }
                    System.out.println();
                    break;
                }
                case 3: {
                    System.out.println();
                    System.out.println(ORANGE + "Exiting" + RESET);
                    System.out.println();
                    break;
                }

                default: {
                    System.out.println(RED + "Enter Valid Value" + RESET);
              }

            }

        } while (n != 3);
    }

    static void stock_main(String username) {
        int n;
        do {

            System.out.println("Press 1 for Predict and Analysis");
            System.out.println("Press 2 for Monitor Shares From Portfolio");
            System.out.println("Press 3 for Add/Remove Shares From Portfolio ");
            System.out.println(RED + "Press 4  to Exit Submenu" + RESET);
            try {
                System.out.print(BLUE + "Enter Choice: " + RESET);
                n = scanner.nextInt();
            } catch (Exception e) {
                System.out.println(RED + "Invalid Choice: Please Enter a  number" + RESET);
                scanner.next();
                n = -1;
            }

            switch (n) {
                case 1: {
                    System.out.println();
                    int m;
                    do {
                        System.out.println("Press 1 to Import New Stock Value");
                        System.out.println("Press 2 to Prediction");
                        System.out.println(RED + "Press 3 to Exit Submenu" + RESET);
                        try {
                            System.out.print(BLUE + "Enter Choice: " + RESET);
                            m = scanner.nextInt();
                        } catch (Exception e) {
                            System.err.println("Invalid Choice: Please Enter a  number");
                            scanner.next();
                            m = -1;
                        }

                        switch (m) {
                            case 1: {
                                System.out.println();
                                case_21();
                                System.out.println();
                                break;
                            }
                            case 2: {
                                System.out.println();
                                case_22();
                                System.out.println();
                                break;
                            }
                            case 3: {
                                System.out.println(ORANGE + "Exiting Submenu" + RESET);
                                break;
                            }
                            default: {
                                System.out.println(RED + "Enter Valid Value" + RESET);
                            }
                        }

                    } while (m != 3);                    System.out.println();
                    break;

                }

                case 2: {
                    System.out.println();
                    portfolio.displayPortfolio();
                    System.out.println();
                    break;
                }

                case 3: {
                    System.out.println();
                   portfolio_submenu(portfolio);
                    System.out.println();
                    break;
                }
                case 4: {
                    System.out.println();
                    System.out.println(RED+ "Exiting Submenu" + RESET);
                    System.out.println();
                    break;
                }
                default: {
                    System.out.println(RED+"Enter Valid Value"+RESET);
                    break;
                }

            }
        } while (n != 4);


    }

    static void case_21() {
        int n;
        do {
            System.out.println("Press 1 to Add Data");
            System.out.println(RED + "Press 2 to Exit Submenu" + RESET);
            try {
                System.out.print("Enter Choice: ");
                n = scanner.nextInt();
            } catch (Exception e) {
                System.err.println("Invalid Choice: Please Enter a  number");
                scanner.next();
                n = -1;
            }

            switch (n) {
                case 1: {
                    System.out.print("Enter Path: ");
                    String path = scanner.next();

                    System.out.print("Enter Stock Name: ");
                    String name = scanner.next();

                    csvManager.readCSV(path, name);
                    name += "Stock_" + name;

                    if (!jdbcManager.check(name)) {
                        System.out.println(GREEN + "Data Inserted Successful" + RESET);
                    } else {
                        System.out.println(RED + "Data Insertion Unsuccessful" + RESET);
                    }
                    break;
                }
                case 2: {
                    System.out.println(ORANGE + "Exiting Submenu" + RESET);
                    return;
                }
                default: {
                    System.out.println(RED + "Enter Valid Value" + RESET);
                }
            }

        } while (n != 2);

    }

    static void case_22() {
        System.out.println(YELLOW + "\n\t Prediction Model " + RESET);
        int modelChoice = 0;
        do {
            System.out.println("Select Prediction Model:");
            System.out.println("Press 1 Short-Term (Predict next day's price)");
            System.out.println("Press 2 Long-Term (Predict price in the future)");
            System.out.println(RED + "Press 3 to Exit Submenu" + RESET);
            try {
                System.out.print(BLUE + "Enter choice: " + RESET);
                modelChoice = scanner.nextInt();
            } catch (Exception e) {
                System.err.println("Invalid Choice: Please Enter a  number");
                scanner.next();
                modelChoice = -1;
            }

            switch (modelChoice) {
                case 1:
                    try {
                        try {
                            jdbcManager.Stock_tables();
                        } catch (Exception e) {
                            return;
                        }
                        System.out.print("Enter the name of the stock to predict : ");
                        String stockName = scanner.next();
                        String tableName = "stock_" + stockName;

                        if (!jdbcManager.check(tableName)) {
                            System.out.println(RED + "Error: No data found for stock '" + stockName + "'. Please import data first using option 2-1." + RESET);
                            return;
                        }
                        System.out.println("\nTraining Short-Term Model for " + stockName + "...");
                        dataPreparation.data_short_term(tableName);
                        dataPreparation.gradientDescentWithRegularization(1000);

                        Date predictDate = null;
                        System.out.println(BLUE + "Min Date: " + jdbcManager.get_minDate(tableName) + "\nMax Date: " + jdbcManager.get_maxDate(tableName) + RESET);
                        Date maxDate =jdbcManager.get_maxDate(tableName);


                        predictDate = jdbcManager.get_maxDate(tableName);

                        double predictedPrice = dataPreparation.predict(tableName, predictDate);
                        System.out.printf(PINK+"Predicted Close Price for %s on the next trading day %s: %.2f\n", stockName, jdbcManager.calculateNextBusinessDay(jdbcManager.get_maxDate(tableName)), predictedPrice);
                        System.out.print(RESET);
                        break;

                    } catch (Exception e) {
                        System.out.println(RED + "Unable to Process" +e.getCause()+ RESET);
                       return;
                    }


                case 2:
                    try {
                        try {
                            jdbcManager.Stock_tables();
                        } catch (Exception e) {
                            return;
                        }
                        System.out.print("Enter the name of the stock to predict : ");
                        String stockName = scanner.next();
                        String tableName = "stock_" + stockName;

                        if (!jdbcManager.check(tableName)) {
                            System.out.println(RED + "Error: No data found for stock '" + stockName + "'. Please import data first using option 2-1." + RESET);
                            return;
                        }
                        System.out.print("Enter prediction horizon in business days : ");
                        int horizon = scanner.nextInt();

                        System.out.println("\nTraining Long-Term Model for " + stockName + "...");
                        dataPreparation.data_long_term(stockName, horizon);
                        dataPreparation.gradientDescent_long_term(2000);

                        System.out.println(BLUE + "Min Date: " + jdbcManager.get_minDate(tableName) + "\nMax Date: " + jdbcManager.get_maxDate(tableName) + RESET);
                        Date longPredictDate = jdbcManager.get_maxDate(tableName);


                        double longPredictedPrice = dataPreparation.predict_long_term(stockName, longPredictDate);

                        for (int i = 0; i < horizon; i++) {
                            longPredictDate = jdbcManager.calculateNextBusinessDay(longPredictDate);
                        }

                        System.out.printf(PINK+"Predicted Close Price for %s in %d business days on %s: %.2f\n", stockName.toUpperCase(), horizon,longPredictDate,longPredictedPrice);
                        System.out.print(RESET);
                        break;
                    } catch (Exception e) {
                        System.out.println(RED + "Unable to Process" +e.getCause()+ RESET);
                       return;

                    }
                case 3:
                    System.out.println(ORANGE + "Exiting Submenu" + RESET);
                    break;

                default:
                    System.out.println(RED + "Invalid model choice." + RESET);
                    break;
            }

        } while (modelChoice != 3);
    }

    // In Main.java
    static void portfolio_submenu(Portfolio portfolio) {
        int choice;
        do {
            System.out.println("\n--- Manage Portfolio ---");
            System.out.println("Press 1 to Buy Shares");
            System.out.println("Press 2 to Sell Shares");
            System.out.println(RED + "Press 3 to Return to Main Menu" + RESET);
            try {
                System.out.print(BLUE + "Enter Choice: " + RESET);
                choice = scanner.nextInt();
            } catch (Exception e) {
                System.out.println(RED + "Invalid Choice: Please Enter a number" + RESET);
                scanner.next();
                choice = -1;
            }

            switch (choice) {
                case 1: { // Buy Shares
                    try {
                        System.out.println("\nAvailable stocks in the system:");
                        jdbcManager.Stock_tables();
                    } catch (Exception e) {
                        System.out.println(RED + "Error: Could not retrieve stock list." + RESET);
                        break;
                    }

                    System.out.print("Enter Stock Name to Buy: ");
                    String stockName = scanner.next();
                    String tableName = "stock_" + stockName.toLowerCase();


                    if (!jdbcManager.check(tableName)) {
                        System.out.println(RED + "\nError: No data found for stock '" + stockName + "'." + RESET);
                        System.out.println(YELLOW + "Please import its data from the 'Predict and Analysis' menu first." + RESET);
                        break;
                    }


                    System.out.print("Enter Quantity: ");
                    long quantity = scanner.nextLong();
                    System.out.print("Enter Purchase Price per Share: ");
                    double price = scanner.nextDouble();
                    portfolio.addPortfolio(stockName, quantity, price);
                    break;
                }
                case 2: { // Sell Shares
                    System.out.print("Enter Stock Name to Sell: ");
                    String stockName = scanner.next();
                    System.out.print("Enter Quantity to Sell: ");
                    long quantity = scanner.nextLong();
                    portfolio.updatePortfolio(stockName, quantity);
                    break;
                }
                case 3:
                    System.out.println(ORANGE + "Returning to Main Menu..." + RESET);
                    break;
                default:
                    System.out.println(RED + "Enter a Valid Value" + RESET);
                    break;
            }
        } while (choice != 3);
    }
}