package com.brenolopes.agiotage;

import java.util.Calendar;
import java.util.Date;

public class DateUtils {
    public static boolean isFromCurrentYear(Date date) {
        int year = Calendar.getInstance().get(Calendar.YEAR);
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        return calendar.get(Calendar.YEAR) == year;
    }

    public static boolean isFromCurrentMonth(Date date) {
        Calendar currentCalendar = Calendar.getInstance();
        int year = currentCalendar.get(Calendar.YEAR);
        int month = currentCalendar.get(Calendar.MONTH);
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        return  calendar.get(Calendar.YEAR) == year &&
                calendar.get(Calendar.MONTH) == month;
    }

    public static boolean isFromCurrentWeek(Date date) {
        Calendar currentCalendar = Calendar.getInstance();
        int year = currentCalendar.get(Calendar.YEAR);
        int month = currentCalendar.get(Calendar.MONTH);
        int week_of_month = currentCalendar.get(Calendar.WEEK_OF_MONTH);
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        return  calendar.get(Calendar.YEAR) == year &&
                calendar.get(Calendar.MONTH) == month &&
                calendar.get(Calendar.WEEK_OF_MONTH) == week_of_month;
    }

    public static int getMonth(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        return calendar.get(Calendar.MONTH);
    }

    public static int getDayOfMonth(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        return calendar.get(Calendar.DAY_OF_MONTH);
    }

    public static int getDayOfWeek(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        return calendar.get(Calendar.DAY_OF_WEEK);
    }

    /** Pega a quantidade de dias no mês e converte para um
     * array de string com cada dia.<br>
     * Exemplo: { "0", "1", "2", ..., "30", "31" };
     */
    public static String[] getNumberOfDaysInMonth() {
        int daysOfMonth = Calendar.getInstance().getActualMaximum(Calendar.DAY_OF_MONTH);
        String[] days = new String[daysOfMonth];
        for(int i = 0; i < daysOfMonth; i++) {
            days[i] = Integer.toString(i + 1);
        }

        return days;
    }
}
