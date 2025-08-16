package Stock_Predictor;

import java.time.Instant;
import java.util.Scanner;

public class AccountManager {
    private String username;
    Scanner scanner = new Scanner(System.in);

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
        if (password.length() >= 8) {
            return true;
        } else {
            return false;
        }
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
            if ((pancard.charAt(i) >= 'a' && pancard.charAt(i) <= 'z') || (pancard.charAt(i) >= 'A' && pancard.charAt(i) <= 'Z')) {
                return true;
            } else {
                return false;
            }
        } else {
            return false;
        }

    }

    private boolean check_AadharCard(String aadharcard) {
        if (aadharcard.length() == 11) {
            for (int i = 0; i < aadharcard.length(); i++) {
                if ((aadharcard.charAt(i) >= '0' && aadharcard.charAt(i) <= '9')) {
                    continue;
                } else {
                    return false;
                }
            }

            return  true;
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
            return  true;
        } else {
            return false;
        }
    }

    private Instant set_LastLogin(){
        return Instant.now();
    }

    void data() {

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

            new JDBC_Manager().insert_user(usename, password, pancard, aadharcad, mobile, set_LastLogin());

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    boolean check_credentials(){
        int count =0;
        try{
            String password ="";
            boolean check = true;

            do {
                if(count>0){
                    System.err.println("Enter Valid Details");
                }
                System.out.print("Enter First Name: ");
                String fname = scanner.next();

                System.out.print("Enter Surname: ");
                String sname = scanner.next();

                username = fname + " " + sname;

                System.out.print("Enter Password: ");
                password = scanner.next();

                count++;

                if(new JDBC_Manager().check_data(username, password) && count<3){
                    System.out.println("Login Successful");
                    check =  false;
                }
                }while (check);

            if (check == false){
                return  true;
            }
            else {
                return  false;
            }



            } catch (Exception e) {
                throw new RuntimeException(e);
            }
    }

    public String getUsername() {
        return username;
    }
}