package com.brenolopes.agiotage;

import java.util.Calendar;
import java.util.Date;

public class ChartData {
    private static String[] meses = new String[] { "Janeiro", "Fevereiro", "Março",
            "Abril", "Maio", "Junho",
            "Julho", "Agosto", "Setembro",
            "Outubro", "Novembro", "Dezembro" };

    private static String[] dias = new String[] { "Domingo", "Segunda", "Terça", "Quarta",
            "Quinta", "Sexta", "Sábado" };

    /** Retorna a soma dos Debts de cada mês deste ano. */
    private static float[] getDebtsTotalByMonth(Debtor[] debtors) {
        float[] totalByMonths = new float[12];

        for(Debtor debtor : debtors) {
            for(Debt debt : debtor.getDebts()) {
                if(!isFromCurrentYear(debt.getDate()))
                    continue;

                int month = getMonth(debt.getDate());
                float totalValue = totalByMonths[month] + debt.getValue();
                totalByMonths[month] = totalValue;
            }
        }
        return totalByMonths;
    }

    /** Retorna a soma dos Debts de cada dia deste mês. */
    private static float[] getDebtsTotalByDayOfMonth(Debtor[] debtors) {
        Calendar calendar = Calendar.getInstance();
        float[] totalByDays = new float[calendar.getActualMaximum(Calendar.DAY_OF_MONTH)];

        for(Debtor debtor : debtors) {
            for(Debt debt : debtor.getDebts()) {
                if(!isFromCurrentMonth(debt.getDate()))
                    continue;

                int dayOfMonth = getDayOfMonth(debt.getDate());
                float totalValue = totalByDays[dayOfMonth - 1] + debt.getValue();
                totalByDays[dayOfMonth - 1] = totalValue;
            }
        }
        return totalByDays;
    }

    /** Retorna a soma dos Debts de cada dia desta semana. */
    private static float[] getDebtsTotalByDayOfWeek(Debtor[] debtors) {
        float[] totalByDays = new float[7];

        for(Debtor debtor : debtors) {
            for(Debt debt : debtor.getDebts()) {
                if(!isFromCurrentWeek(debt.getDate()))
                    continue;

                int dayOfWeek = getDayOfWeek(debt.getDate());
                float totalValue = totalByDays[dayOfWeek - 1] + debt.getValue();
                totalByDays[dayOfWeek - 1] = totalValue;
            }
        }
        return totalByDays;
    }

    private static boolean isFromCurrentYear(Date date) {
        int year = Calendar.getInstance().get(Calendar.YEAR);
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        return calendar.get(Calendar.YEAR) == year;
    }

    private static boolean isFromCurrentMonth(Date date) {
        Calendar currentCalendar = Calendar.getInstance();
        int year = currentCalendar.get(Calendar.YEAR);
        int month = currentCalendar.get(Calendar.MONTH);
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        return  calendar.get(Calendar.YEAR) == year &&
                calendar.get(Calendar.MONTH) == month;
    }

    private static boolean isFromCurrentWeek(Date date) {
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

    private static int getMonth(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        return calendar.get(Calendar.MONTH);
    }

    private static int getDayOfMonth(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        return calendar.get(Calendar.DAY_OF_MONTH);
    }

    private static int getDayOfWeek(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        return calendar.get(Calendar.DAY_OF_WEEK);
    }

    /** Pega a quantidade de dias no mês e converte para um
     * array de string com cada dia.<br>
     * Exemplo: { "0", "1", "2", ..., "30", "31" };
     */
    private static String[] getNumberOfDaysInMonth() {
        int daysOfMonth = Calendar.getInstance().getActualMaximum(Calendar.DAY_OF_MONTH);
        String[] days = new String[daysOfMonth];
        for(int i = 0; i < daysOfMonth; i++) {
            days[i] = Integer.toString(i + 1);
        }

        return days;
    }

    /** Retorna as labels (o "nome" de cada valor no gráfico)
     * de acordo com o Mode informado.
     */
    public static String[] getLabels(ChartMode mode) {
        switch (mode) {
            case YEAR:
                return meses;
            case MONTH:
                return getNumberOfDaysInMonth();
            case WEEK:
                return dias;
        }

        return null;
    }

    /** Retorna os valores para preencher o gráfico */
    public static float[] getData(ChartMode mode, Debtor[] debtors) {
        switch (mode) {
            case YEAR:
                return getDebtsTotalByMonth(debtors);
            case MONTH:
                return getDebtsTotalByDayOfMonth(debtors);
            case WEEK:
                return getDebtsTotalByDayOfWeek(debtors);
        }

        return null;
    }
}
