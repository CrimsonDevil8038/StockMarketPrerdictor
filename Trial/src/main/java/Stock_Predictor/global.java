package Stock_Predictor;

import java.io.*;
import java.util.ArrayList;
import java.util.Scanner;

class global {
    public static void main(String[] args) throws IOException {
        Scanner sc = new Scanner(System.in);
        Admin admin = new Admin();
        admin.start();

        while (true) {
            System.out.println("Add a Stock_Predictor.User Question ?(yes/no)");
            String s = sc.nextLine();
            if (s.equalsIgnoreCase("no")) {
                break;
            } else {
                System.out.println("Enter user Name:");
                String name = sc.nextLine();
                System.out.println("Enter Question :");
                String question = sc.nextLine();

                User u = new User(name, question);
                u.start();
            }
            System.out.println("Stock_Predictor.User has asked Question.");
        }
    }
}

class User extends Thread {
    String name;
    String question;

    User(String name, String question) {
        this.name = name;
        this.question = question;
    }

    public void run() {
        synchronized (global.class) {
            try {
                FileWriter fw = new FileWriter("faq.txt");
                fw.write("Stock_Predictor.User : " + name + "\n");
                fw.write("Question : " + question + "\n");
                fw.close();
                System.out.println(name + "Submitted question");
            } catch (IOException ioe) {
                System.out.println("Error opening file");
            }
        }
    }
}

class Admin extends Thread {
    public void run() {
        while (true) {
            try {
                Thread.sleep(3000);
                synchronized (global.class) {
                    Scanner sc = new Scanner(System.in);
                    File f1 = new File("faq.txt");
                    if (!f1.exists()) {
                        f1.createNewFile();
                    }
                    BufferedReader br = new BufferedReader(new FileReader(f1));
                    ArrayList<String> l = new ArrayList<>();
                    String line;
                    while ((line = br.readLine()) != null) {
                        l.add(line);
                    }
                    br.close();

                    ArrayList<String> updatel = new ArrayList<>(l);
                    boolean answered = false;

                    for (int i = 0; i < l.size(); i++) {
                        String question = l.get(i);
                        if (question.startsWith("Question :")) {
                            if (i + 1 >= l.size() || !l.get(i + 1).startsWith("A:")) {
                                System.out.println("Question : " + question);
                                String answer = sc.nextLine();
                                System.out.println("Answer : " + answer);
                                updatel.add(i + 1, answer);
                                answered = true;
                            }
                        }
                    }
                    if (answered) {
                        FileWriter fw = new FileWriter("faq.txt");
                        for (String s : updatel) {
                            fw.write(s + "\n");
                        }
                        fw.close();
                    } else {
                        System.out.println("No new Question");
                    }
                }
            } catch (Exception e) {
                System.out.println("Error sleeping");
            }
        }
    }
}