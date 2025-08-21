package Stock_Predictor.Account;

import Stock_Predictor.JDBC.JDBC_Manager;
import java.time.Instant;
import java.util.Scanner;

import static Stock_Predictor.Color.*;

public class AccountManager {
    Scanner scanner = new Scanner(System.in);
    JDBC_Manager jdbcManager = new JDBC_Manager();
    private String username;

    private boolean check_UserName(String name) {

        for (int i = 0; i < name.length(); i++) {
            if ((name.charAt(i) >= 'a' && name.charAt(i) <= 'z') || (name.charAt(i) >= 'A' && name.charAt(i) <= 'Z') || (name.charAt(i) == ' ')) {
                continue;
            } else {
                return false;
            }
        }
        return true;
    }

    private boolean check_Password(String password) {

        if (password.length()<8) {
            System.out.println("Please Enter Password of Minimum of 8 Length");
        }
        return (password.length() >= 8);
    }

    private boolean check_PanCard(String pancard) {
        boolean check = true;

        if (pancard.length() == 10) {
            int i = 0;
            for (i = 0; i < 5; i++) {
                if ((pancard.charAt(i) >= 'a' && pancard.charAt(i) <= 'z') || (pancard.charAt(i) >= 'A' && pancard.charAt(i) <= 'Z')) {
                    continue;
                } else {
                    return false;
                }
            }

            for (i = 5; i < 9; i++) {
                if ((pancard.charAt(i) >= '0' && pancard.charAt(i) <= '9')) {
                    continue;
                } else {
                    return false;
                }
            }
            return (pancard.charAt(i) >= 'a' && pancard.charAt(i) <= 'z') || (pancard.charAt(i) >= 'A' && pancard.charAt(i) <= 'Z');
        } else {
            return false;
        }

    }

    private boolean check_AadharCard(String aadharcard) {
        if (aadharcard.length() == 12) {
            for (int i = 0; i < aadharcard.length(); i++) {
                if ((aadharcard.charAt(i) >= '0' && aadharcard.charAt(i) <= '9')) {
                    continue;
                } else {
                    return false;
                }
            }

            return true;
        } else {
            return false;
        }
    }

    private boolean check_Mobile(String mobile) {
        if (mobile.length() == 10) {
            for (int i = 0; i < mobile.length(); i++) {
                if ((mobile.charAt(i) >= '0' && mobile.charAt(i) <= '9')) {
                    continue;
                } else {
                    return false;
                }
            }
            return true;
        } else {
            return false;
        }
    }

    private Instant set_LastLogin() {
        return Instant.now();
    }

    public void data() {

        try {
            String usename = "";
            do {
                System.out.print("Enter First Name: ");
                String fname = scanner.next();

                System.out.print("Enter Surname: ");
                String sname = scanner.next();

                usename = fname + " " + sname;

            } while (!check_UserName(usename));

            String aadharcad = "";
            do {
                System.out.print("Enter Aadhar Card: ");
                aadharcad = scanner.next();

            } while (!check_AadharCard(aadharcad));

            String pancard = "";
            do {

                System.out.print("Enter PanCard: ");
                pancard = scanner.next();
            } while (!check_PanCard(pancard));

            String mobile = "";
            do {

                System.out.print("Enter Mobile: ");
                mobile = scanner.next();
            } while (!check_Mobile(mobile));

            String password = "";
            do {

                System.out.print("Enter Password: ");
                password = scanner.next();


            } while (!check_Password(password));

            if (jdbcManager.insert_user(usename, password, pancard, aadharcad, mobile, set_LastLogin())) {
                System.out.println(GREEN + "Signup Successful" + RESET);
            } else {
                throw new RuntimeException();
            }


        } catch (Exception e) {
            System.out.println(RED + "Signup Unsuccessful" + RESET);
            System.out.println();
        }
    }

    public boolean check_credentials() {

        for (int attempt = 1; attempt <= 3; attempt++) {
            System.out.print("Enter First Name: ");
            String fname = scanner.next();

            System.out.print("Enter Surname: ");
            String sname = scanner.next();

            String username = fname + " " + sname;

            System.out.print("Enter Password: ");
            String password = scanner.next();


            if (jdbcManager.check_data(username, password)) {
                System.out.println(GREEN + "Login Successful" + RESET);
                this.username = username;
                return true;
            } else {
                System.out.println(RED + "Invalid credentials. Please try again." + RESET);
            }
        }

        System.out.println(RED + "Too many failed login attempts." + RESET);
        return false;
    }

    public String getUsername() {
        return username;
    }


}