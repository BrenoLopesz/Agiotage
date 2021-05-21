package com.brenolopes.agiotage;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Debtor implements Serializable {
    private String name;
    private Debt[] debts;

    public Debtor(String _name, Debt[] _debts) {
        this.name = _name;
        this.debts = _debts;
    }

    public String getName() {
        return name;
    }

    public Debt[] getDebts() {
        return debts;
    }

    public void addDebt(Debt debt) {
        List<Debt> new_debts = new ArrayList<Debt>(Arrays.asList(debts));
        new_debts.add(debt);
        Debt[] new_debts_array = new Debt[new_debts.size()];
        debts = new_debts.toArray(new_debts_array);
    }

    public Debt getDebt(int index) {
        return debts[index];
    }

    public int getDebtIndex(Debt debt) {
        int i = 0;
        for(Debt d : debts) {
            if(debt == d)
                return i;

            i++;
        }
        return -1;
    }

    public void removeDebt(Debt debt) {
        Debt[] new_debts = new Debt[debts.length - 1];
        int i = 0;
        for(Debt d : debts) {
            if(d == debt)
                continue;

            new_debts[i] = d;
            i++;
        }

        this.debts = new_debts;
    }
}
