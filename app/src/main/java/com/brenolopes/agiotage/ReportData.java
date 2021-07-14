package com.brenolopes.agiotage;

public class ReportData {
    private static ReportMode mode = ReportMode.GENERAL;
    private static int selectedDebtorIndex = 0;
    private static Debtor[] totalDebtors = {};

    /** Retorna os Debtors para serem exibidos no Report. <br>
     * Caso o modo seja individual, exibe apenas as informações do Debtor selecionado. */
    public static Debtor[] getDebtors() {
        return mode == ReportMode.GENERAL? totalDebtors : new Debtor[] { getSelectedDebtor() };
    }

    public static void setTotalDebtors(Debtor[] _debtors) {
        totalDebtors = _debtors;
    }

    public static Debtor[] getTotalDebtors() { return totalDebtors; }

    public static Debtor getSelectedDebtor() {
        return selectedDebtorIndex == -1? null : totalDebtors[selectedDebtorIndex];
    }

    public static void setSelectedDebtor(int _index) {
        selectedDebtorIndex = _index;
    }

    public static int getSelectedDebtorIndex() { return selectedDebtorIndex; }

    public static ReportMode getMode() {
        return mode;
    }

    public static void setMode(ReportMode _mode) {
        mode = _mode;
    }

}
