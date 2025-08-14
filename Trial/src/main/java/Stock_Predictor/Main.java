package Stock_Predictor;

import java.util.Scanner;

public class Main {
    static Scanner scanner = new Scanner(System.in);
    static  AccountManager accountManager = new AccountManager();
    static CSV_Manager csvManager = new CSV_Manager();


    public static void main(String[] args) {
        System.out.println("Welcome to Stock Prediction and Management");

        int n = 0;
        do{
            System.out.println("Press 1 to Signup");
            System.out.println("Press 2 to Signin");
            System.out.println("Press 3 to Exit");
            System.out.print("Enter Choice: ");
            n =  scanner.nextInt();

            switch (n){
                case 1:{
                    accountManager.data();
                    break;
                }
                case 2:{
                    if(accountManager.check_credentials() == true){
                        stock_main(accountManager.getUsername());
                    }
                    break;
                }
                case 3:{
                    System.out.println("Exiting");
                    break;
                }

                default:{
                    System.out.println("Enter Valid Value");
                }

            }

        }while (n!=3);
    }

    static void stock_main(String username){
        int n;
        do{
            System.out.println("Press 1 for Global Insight Box");
            System.out.println("Press 2 for Predict and Analysis");
            System.out.println("Press 3 for View Portfolio");
            System.out.println("Press 4 for Stock Exchange");
            System.out.println("Press 5  to Exit");
            n = scanner.nextInt();

            switch (n){
                case 1:{
                    int m;
                    do {
                        System.out.println("Press 1 for FAQs");
                        System.out.println("Press 2 to Ask Question");
                        System.out.println("Press 3 to view Answer to Previous Questions");
                        System.out.print("Enter Choice: ");
                        m = scanner.nextInt();

                        switch (m){
                            case 1:{
                                break;
                            }
                            case 2:{
                                break;
                            }
                            case 3:{
                                break;
                            }
                            case 4:{
                                System.out.println("Exitting Submenu");
                                break;
                            }
                            default:{
                                System.out.println("Enter Valid Value");
                            }
                        }

                    }while (m!=3);

                    break;
                }

                case 2:{
                    int m;
                    do {
                        System.out.println("Press 1 to Import New Stock Value");
                        System.out.println("Press 2 to Prediction");
                        System.out.println("Press 3 to ");
                        System.out.print("Enter Choice: ");
                        m = scanner.nextInt();

                        switch (m){
                            case 1:{
                                break;
                            }
                            case 2:{
                                break;
                            }
                            case 3:{
                                System.out.println("Exitting Submenu");
                            }
                            default:{
                                System.out.println("Enter Valid Value");
                            }
                        }

                    }while (m!=3);
                    break;

                }
            }

        }while (n!=5);


    }

}
