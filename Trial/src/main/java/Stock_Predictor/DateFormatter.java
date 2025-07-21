package Stock_Predictor;

import java.io.Serializable;
import java.util.Scanner;

class DateFormatter implements Serializable {

    private  static  final  long SerialID = 3;

    String toFormat1(String format2) {
        String format1 = "";
        String[] format;
        format = format2.split("[/-]");
        boolean check = checkFormat2(format);
        if (check) {
            int month = 0;

            switch (format[1]) {
                case "Jan": {
                    month = 1;
                    break;
                }
                case "Feb": {
                    month = 2;
                    break;
                }
                case "Mar": {
                    month = 3;
                    break;
                }
                case "Apr": {
                    month = 4;
                    break;
                }
                case "May": {
                    month = 5;
                    break;
                }
                case "Jun": {
                    month = 6;
                    break;
                }
                case "Jul": {
                    month = 7;
                    break;
                }
                case "Aug": {
                    month = 8;
                    break;
                }
                case "Sep": {
                    month = 9;
                    break;
                }
                case "Oct": {
                    month = 10;
                    break;
                }
                case "Nov": {
                    month = 11;
                    break;
                }
                case "Dec": {
                    month = 12;
                    break;
                }
            }
            format1 = format[0] + "-" + month + " -" + format[2];
            return format1;
        } else {
            return "0-0-0000";
        }
    }

    String toFormat2(String inputDate) {
        inputDate = inputDate.replace("/", "-");  // Normalize delimiters
        String formatType = formatChecker(inputDate);

        if (formatType.equalsIgnoreCase("Format1")) {
            return inputDate;
        }

        try {
            String[] parts = inputDate.split("-");
            int day = Integer.parseInt(parts[0]);
            int month = Integer.parseInt(parts[1]);
            int year = Integer.parseInt(parts[2]);

            int[] check = {day, month, year};
            if (!checkFormat1(check)) {
                return "0-0-0000";
            }

            String[] months = {"0", "Jan", "Feb", "Mar", "Apr", "May", "Jun",
                    "Jul", "Aug", "Sep", "Oct", "Nov", "Dec"};

            return day + "-" + months[month] + "-" + year;
        } catch (Exception e) {
            return "0-0-0000";
        }
    }


    boolean checkFormat1(int[] date) {
        boolean b = true;
        try {
            if (date[2] >= 1924 && date[2] <= 2050 && date[1] >= 1 && date[1] <= 12) {
                switch (date[1]) {
                    case 1:
                    case 3:
                    case 5:
                    case 7:
                    case 8:
                    case 10:
                    case 12: {
                        if (date[0] >= 1 && date[0] <= 31) {
                        } else {
                            b = false;
                        }
                        break;
                    }

                    case 4:
                    case 6:
                    case 9:
                    case 11: {
                        if (date[0] >= 1 && date[0] <= 30) {
                        } else {
                            b = false;
                        }
                        break;
                    }

                    case 2: {
                        if ((date[2] % 400 == 0) || (date[2] % 100 != 0 && date[2] % 4 == 0)) {
                            if (date[0] >= 1 && date[0] <= 29) {
                            } else {
                                b = false;
                            }
                        } else {
                            if (date[0] >= 1 && date[0] <= 28) {
                            } else {
                                b = false;
                            }
                        }
                        break;
                    }

                    default: {
                        b = false;
                    }

                }
                return b;
            } else {
                return false;
            }
        } catch (Exception e) {
            return false;
        }
    }

    boolean checkFormat2(String[] date) {
        boolean b = true;
        try {
            int day = Integer.parseInt(date[0]);
            String monthStr = date[1];
            int year = Integer.parseInt(date[2]);

            String[] months = {"Jan", "Feb", "Mar", "Apr", "May", "Jun",
                    "Jul", "Aug", "Sep", "Oct", "Nov", "Dec"};
            int month = -1;
            for (int i = 0; i < months.length; i++) {
                if (months[i].equalsIgnoreCase(monthStr)) {
                    month = i + 1;
                    break;
                }
            }

            if (month == -1 || year < 1924 || year > 2050) return false;

            switch (month) {
                case 1:
                case 3:
                case 5:
                case 7:
                case 8:
                case 10:
                case 12:
                    b = (day >= 1 && day <= 31);
                    break;

                case 4:
                case 6:
                case 9:
                case 11:
                    b = (day >= 1 && day <= 30);
                    break;

                case 2:
                    boolean isLeap = (year % 400 == 0) ||
                            (year % 100 != 0 && year % 4 == 0);
                    b = isLeap ? (day >= 1 && day <= 29)
                            : (day >= 1 && day <= 28);
                    break;

                default:
                    b = false;
            }

            return b;
        } catch (Exception e) {
            return false;
        }
    }

    int toNumberofDays(String start, String end) {
        int numberOfDays = 0;

        String startFormat = formatChecker(start);
        String endFormat = formatChecker(end);


        if (startFormat.equalsIgnoreCase("Format1")) {
            start = toFormat1(start);
        }
        if (endFormat.equalsIgnoreCase("Format1")) {
            end = toFormat1(end);
        }


        String[] startdays = start.split("[/-]");
        String[] enddays = end.split("[/-]");

        int[] sdays = new int[3];
        int[] edays = new int[3];

        try {
            for (int i = 0; i < 3; i++) {
                sdays[i] = Integer.parseInt(startdays[i].trim());
                edays[i] = Integer.parseInt(enddays[i].trim());
            }

            numberOfDays = dateday(edays) - dateday(sdays);
            return numberOfDays;

        } catch (NumberFormatException e) {
            System.err.println("Error parsing date after conversion. Please check date inputs.");
            e.printStackTrace();
            return 0;
        }
    }


    String formatChecker(String formatcheck) {
        try {
            String[] dates;
            dates = formatcheck.split("[/-]");
            int[] check = new int[3];
            for (int i = 0; i < 3; i++) {
                check[i] = Integer.parseInt(dates[i]);
            }
            return "Format2";
        } catch (Exception e) {
            return "Format1";
        }

    }

    int dateday(int[] date) {

        int countdays = 0;

        for (int i = 1924; i < date[2]; i++) {
            if ((i % 400 == 0) || (i % 100 != 0 && i % 4 == 0)) {
                countdays += 366;
            } else {
                countdays += 365;
            }
        }
        for (int i = 1; i < date[1]; i++) {
            switch (i) {
                case 1:
                case 3:
                case 5:
                case 7:
                case 8:
                case 10:
                case 12: {
                    countdays += 31;
                    break;
                }

                case 4:
                case 6:
                case 9:
                case 11: {
                    countdays += 30;
                    break;
                }

                case 2: {
                    if ((date[2] % 400 == 0) || (date[2] % 100 != 0 && date[2] % 4 == 0)) {
                        countdays += 29;
                    } else {
                        countdays += 28;
                    }
                    break;
                }
            }
        }

        countdays += date[0];
        return countdays;
    }

    Object[] input() {
        Scanner scanner = new Scanner(System.in);
        int days = 0;
        String start, end;
        do {
            System.out.println("Enter Start and End Dates\n\t\t(Either in Format:1/1/2001 or 1/Jan/2001 or 1-1-2001 or 1-Jan-2001)");

            System.out.println("Enter Start Date");
            start = scanner.next();

            System.out.println("Enter End Date");
            end = scanner.next();

            days = toNumberofDays(end, start);

        } while (days > 0);

        start = start.substring(0, start.length() - 4) + start.substring(start.length() - 2);
        end = end.substring(0, end.length() - 4) + end.substring(end.length() - 2);

        return new Object[]{start, end};
    }


}

