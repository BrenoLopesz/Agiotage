package com.brenolopes.agiotage;

import java.text.DecimalFormat;

public class DebtorsInfo {
    private final Debtor[] debtors;

    public DebtorsInfo(Debtor[] _debtors) {
        this.debtors = _debtors;
    }

    public String getInfo(InfoType infoType) {
        switch(infoType) {
            case LOAN_TOTAL:
                return formatMoney(getLoansTotal());
            case LOANS_THIS_MONTH:
                return "+ " + formatMoney(getLoansThisMonth());
            case LOAN_TOTAL_RECOVERED_THIS_MONTH:
                return formatMoney(getRecoveredLoansThisMonth());
            case DEBTORS_AMOUNT:
                return String.valueOf(getDebtorsAmount());
            case NEW_DEBTORS_AMOUNT:
                return String.valueOf(getNewDebtorsAmount());
            case EX_DEBTORS_AMOUNT:
                return String.valueOf(getExDebtorsAmount());
        }

        return null;
    }

    /** O total de empréstimos que não foram pagos */
    private float getLoansTotal() {
        float total = 0;
        for(Debtor debtor : debtors)
            for(Debt debt : debtor.getDebts())
                if(!debt.isPayed()) total += debt.getValue();

        return total;
    }

    private float getLoansThisMonth() {
        float total = 0;
        for(Debtor debtor : debtors)
            for(Debt debt : debtor.getDebts())
                if(DateUtils.isFromCurrentMonth(debt.getDate())) total += debt.getValue();

        return total;
    }

    private float getRecoveredLoansThisMonth() {
        float total = 0;
        for(Debtor debtor : debtors)
            for(Debt debt : debtor.getDebts())
                if(DateUtils.isFromCurrentMonth(debt.getDate()) && debt.isPayed())
                    total += debt.getValue();

        return total;
    }

    private int getDebtorsAmount() {
        return debtors.length;
    }

    /** Número de novos devedores este mês */
    private int getNewDebtorsAmount() {
        int total = 0;

        // Verifica se o debtor possui uma debt anterior ao mês atual
        for(Debtor debtor : debtors) {
            boolean hasOldDebt = false;
            for (Debt debt : debtor.getDebts())
                if(!DateUtils.isFromCurrentMonth(debt.getDate()))
                    hasOldDebt = true;

            if(!hasOldDebt) total++;
        }
        return total;
    }

    /** Número de debtors que quitaram suas dívidas este mês */
    private int getExDebtorsAmount() {
        int total = 0;

        // Precisa achar um Debtor que não possui dívidas e que quitou pelo menos uma este mês
        for(Debtor debtor : debtors) {
            // Se ele ainda possui dívidas, continua procurando
            if(hasUnpayedDebt(debtor)) continue;

            boolean hasDebtThisMonth = false;

            for (Debt debt : debtor.getDebts())
                if (DateUtils.isFromCurrentMonth(debt.getDate())) {
                    hasDebtThisMonth = true;
                    break;
                }

            if(hasDebtThisMonth) total++;
        }
        return total;
    }

    private boolean hasUnpayedDebt(Debtor debtor) {
        for (Debt debt : debtor.getDebts())
            if(!debt.isPayed()) return true;

        return false;
    }

    private String formatMoney(float value) {
        DecimalFormat formatter = new DecimalFormat("#0.00");
        return "R$ " + formatter.format((double) value).replace(".", ",");
    }
}
