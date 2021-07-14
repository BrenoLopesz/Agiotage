package com.brenolopes.agiotage;

import java.util.Calendar;
import java.util.Date;

public class ChartData {
    private static ChartMode mode = ChartMode.YEAR;
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
                if(!DateUtils.isFromCurrentYear(debt.getDate()))
                    continue;

                int month = DateUtils.getMonth(debt.getDate());
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
                if(!DateUtils.isFromCurrentMonth(debt.getDate()))
                    continue;

                int dayOfMonth = DateUtils.getDayOfMonth(debt.getDate());
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
                if(!DateUtils.isFromCurrentWeek(debt.getDate()))
                    continue;

                int dayOfWeek = DateUtils.getDayOfWeek(debt.getDate());
                float totalValue = totalByDays[dayOfWeek - 1] + debt.getValue();
                totalByDays[dayOfWeek - 1] = totalValue;
            }
        }
        return totalByDays;
    }

    /** Retorna as labels (o "nome" de cada valor no gráfico)
     * de acordo com o Mode informado.
     */
    public static String[] getLabels(ChartMode mode) {
        switch (mode) {
            case YEAR:
                return meses;
            case MONTH:
                return DateUtils.getNumberOfDaysInMonth();
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

    public static ChartMode getMode() { return mode; }

    public static void setMode(ChartMode _mode) { mode = _mode; }
}
