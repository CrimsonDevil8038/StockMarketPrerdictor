package Stock_Predictor;

import Stock_Predictor.Account.AccountManager;
import Stock_Predictor.JDBC.JDBC_Manager;
//import Stock_Predictor.PortFolioManagment.Portfolio;
import Stock_Predictor.Predict_And_Analysis.CSV_Manager;
import Stock_Predictor.Predict_And_Analysis.DataPreparation;
//import Stock_Predictor.PortFolioManagment.Portfolio;

import java.sql.Date;
import java.util.Scanner;

public class Main {
    static Scanner scanner = new Scanner(System.in);
    static AccountManager accountManager = new AccountManager();
    static CSV_Manager csvManager = new CSV_Manager();
    static DataPreparation dataPreparation = new DataPreparation();
    static JDBC_Manager jdbcManager = new JDBC_Manager();
//    static Portfolio portfolio = new Portfolio(jdbcManager, scanner);

    static void basic() {
        jdbcManager.create_Rest();
    }


    public static void main(String[] args) {
        System.out.println("Welcome to Stock Prediction and Management");
        basic();

        int n = 0;
        do {
            System.out.println("Press 1 to Signup");
            System.out.println("Press 2 to Signin");
            System.out.println("Press 3 to Exit");
            System.out.print("Enter Choice: ");
            n = scanner.nextInt();

            switch (n) {
                case 1: {
                    accountManager.data();
                    break;
                }
                case 2: {
                    if (accountManager.check_credentials() == true) {
                        stock_main(accountManager.getUsername());
                    }
                    break;
                }
                case 3: {
                    System.out.println("Exiting");
                    break;
                }

                default: {
                    System.out.println("Enter Valid Value");
                }

            }

        } while (n != 3);
    }

    static void stock_main(String username) {
        int n;
        do {
//            System.out.println("Press 1 for Global Insight Box");
            System.out.println("Press 1 for Predict and Analysis");
            System.out.println("Press 2 for View Portfolio");
            System.out.println("Press 3 for Stock Exchange");
            System.out.println("Press 4  to Exit");
            System.out.print("Enter Choice: ");
            n = scanner.nextInt();

            switch (n) {
//                case 1: {
//                    int m;
//                    do {
//                        System.out.println("Press 1 for FAQs");
//                        System.out.println("Press 2 to Ask Question");
//                        System.out.println("Press 3 to view Answer to Previous Questions");
//                        System.out.println("Press 4 to Exit");
//                        System.out.print("Enter Choice: ");
//                        m = scanner.nextInt();
//
//                        switch (m) {
//                            case 1: {
//                                break;
//                            }
//                            case 2: {
//                                break;
//                            }
//                            case 3: {
//                                break;
//                            }
//                            case 4: {
//                                System.out.println("Exitting Submenu");
//                                break;
//                            }
//                            default: {
//                                System.out.println("Enter Valid Value");
//                            }
//                        }
//
//                    } while (m != 4);
//
//                    break;
//                }

                case 1: {
                    int m;
                    do {
                        System.out.println("Press 1 to Import New Stock Value");
                        System.out.println("Press 2 to Prediction");
                        System.out.println("Press 3 to Exit");
                        System.out.print("Enter Choice: ");
                        m = scanner.nextInt();

                        switch (m) {
                            case 1: {
                                case_21();
                                break;
                            }
                            case 2: {
                                case_22();

                                break;
                            }
                            case 3: {
                                System.out.println("Exiting Submenu");
                                break;
                            }
                            default: {
                                System.out.println("Enter Valid Value");
                            }
                        }

                    } while (m != 3);
                    break;

                }

                case 2: {
//                    portfolio.displayPortfolios(username);
                    break;
                }

                case 3: {
                    stockExchangeMenu(username);
                    break;
                }
                case 4: {
                    System.out.println("Exiting Submenu");
                }
                default: {
                    System.out.println("Enter Valid Value");
                }

            }
        } while (n != 4);


    }

    static void case_21() {
        int n = 0;
        do {
            System.out.println("Press 1 to Add Data");
            System.out.println("Press 2 to Exit");
            System.out.print("Enter Choice: ");
            n = scanner.nextInt();

            switch (n) {
                case 1: {
                    System.out.print("Enter Path: ");
                    String path = scanner.next();

                    System.out.print("Enter Stock Name: ");
                    String name = scanner.next();

                    csvManager.readCSV(path, name);
                    name += "Stock_" + name;
                    if (!jdbcManager.check(name)) {
                        System.out.println("Data Inserted Successful");
                    } else {
                        System.out.println("Data Insertion Unsuccessful");
                    }
                    break;
                }
                case 2: {
                    System.out.println("Exiting Submenu");
                    return;
                }
                default: {
                    System.out.println("Enter Valid Value");
                }
            }

        } while (n != 2);

    }

    static void case_22() {
        jdbcManager.Stock_tables();
        System.out.println("\n--- Prediction Model ---");
        System.out.print("Enter the name of the stock to predict (e.g., TCS): ");
        String stockName = scanner.next().toUpperCase();
        String tableName = "stock_" + stockName.toLowerCase();

        if (!jdbcManager.check(tableName)) {
            System.out.println("Error: No data found for stock '" + stockName + "'. Please import data first using option 2-1.");
            return;
        }

        System.out.println("Select Prediction Model:");
        System.out.println("1. Short-Term (Predict next day's price)");
        System.out.println("2. Long-Term (Predict price in the future)");
        System.out.print("Enter choice: ");
        int modelChoice = scanner.nextInt();

        switch (modelChoice) {
            case 1:
                System.out.println("\nTraining Short-Term Model for " + stockName + "...");
                dataPreparation.data(tableName, 14); // 14 features for short-term model
                dataPreparation.gradientDescentWithRegularization(1000); // Train for 1000 epochs

                System.out.print("Enter date for prediction (format yyyy-MM-dd): ");
                String dateStr = scanner.next();
                Date predictDate = jdbcManager.getFormattedDate(dateStr);

                double predictedPrice = dataPreparation.predict(tableName, predictDate);
                System.out.printf("Predicted Close Price for %s on the next trading day: %.2f\n", stockName, predictedPrice);
                break;

            case 2:
                System.out.print("Enter prediction horizon in business days (e.g., 30 for one month): ");
                int horizon = scanner.nextInt();

                System.out.println("\nTraining Long-Term Model for " + stockName + "...");
                dataPreparation.data_long_term(stockName, horizon);
                dataPreparation.gradientDescent_long_term(2000); // Long-term might need more epochs

                System.out.print("Enter date for prediction (format yyyy-MM-dd): ");
                String longDateStr = scanner.next();
                Date longPredictDate = jdbcManager.getFormattedDate(longDateStr);

                double longPredictedPrice = dataPreparation.predict_long_term(stockName, longPredictDate);
                System.out.printf("Predicted Close Price for %s in %d business days: %.2f\n", stockName, horizon, longPredictedPrice);
                break;

            default:
                System.out.println("Invalid model choice.");
                break;
        }

    }

    static void stockExchangeMenu(String username) {
        int choice;
        do {
            System.out.println("\n--- Stock Exchange ---");
            System.out.println("1. View Your Portfolio");
            System.out.println("2. Buy Stock");
            System.out.println("3. Sell Stock");
            System.out.println("4. Back to Main Stock Menu");
            System.out.print("Enter Choice: ");
            choice = scanner.nextInt();

            switch (choice) {
//                case 1:
//                    portfolio.displayPortfolios(username);
//                    break;
//                case 2:
//                    jdbcManager.viewStock_Tables();
//                    portfolio.buy(username);
//                    break;
//                case 3:
//                    portfolio.sell(username);
//                    break;
                case 4:
                    System.out.println("Returning to main stock menu...");
                    break;
                default:
                    System.out.println("Invalid choice. Please try again.");
                    break;
            }
        } while (choice != 4);
    }
}