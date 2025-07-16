package Stock_Predictor;

import java.util.*;

class DateFormatter {

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

    String toFormat2(String format1) {
        String format2 = "";
        String[] format;
        format = format1.split("[/-]");
        int[] check = new int[3];
        for (int i = 0; i < 3; i++) {
            check[i] = Integer.parseInt(format[i]);
        }
        boolean checkFormat = checkFormat1(check);
        if (checkFormat) {
            String[] months = {"0", "Jan", "Feb", "Mar", "Apr", "May", "Jun",
                    "Jul", "Aug", "Sep", "Oct", "Nov", "Dec"};
            String monthName = months[check[1]];
            format2 = check[0] + "-" + monthName + "-" + check[2];
            return format2;
        } else {
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

        String[] dates = check(start, end);

        String[] startdays;
        startdays = dates[0].split("[/-]");

        String[] enddays;
        enddays = dates[1].split("[/-]");

        int[] sdays = new int[3];
        int[] edays = new int[3];

        for (int i = 0; i < 3; i++) {
            sdays[i] = Integer.parseInt(startdays[i]);
            edays[i] = Integer.parseInt(enddays[i]);
        }

        numberOfDays = dateday(edays) - dateday(sdays);

        return numberOfDays;
    }

    String[] check(String start, String end) {
        String startFormat = formatChecker(start);
        String endFormat = formatChecker(end);

        if (startFormat.equalsIgnoreCase("Format1") && endFormat.equalsIgnoreCase("Format1")) {
            start = toFormat2(start);
            end = toFormat2(end);
        } else if (startFormat.equalsIgnoreCase("Format1")) {
            start = toFormat2(start);
        } else if (endFormat.equalsIgnoreCase("Format1")) {
            end = toFormat2(end);
        }

        return new String[]{start, end};
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

            days = toNumberofDays(start, end);

        } while (days > 0);

        start = start.substring(0, start.length() - 4) + start.substring(start.length() - 3);
        System.out.println(start);


        return new Object[]{days, start};
    }


}

