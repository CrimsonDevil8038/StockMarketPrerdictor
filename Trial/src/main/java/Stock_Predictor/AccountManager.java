package Stock_Predictor;

public class AccountManager {

    boolean check_UserName(String name){

        boolean check = true;
        for (int i = 0; i < name.length(); i++) {
            if (!((name.charAt(i)>= 'a' && name.charAt(i)<= 'z') || (name.charAt(i)>= 'A' && name.charAt(i)<= 'Z')  || (name.charAt(i) == ' '))){
                check = false;
            }
        }
        return  check;
    }
}
