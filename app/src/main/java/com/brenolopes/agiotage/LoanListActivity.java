package com.brenolopes.agiotage;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import com.google.gson.Gson;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

public class LoanListActivity extends AppCompatActivity implements LoanListAdapter.ItemClickListener {
    LoanListAdapter adapter;
    Debtor[] debtors;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.loan_list);
        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true);
        debtors = new Gson().fromJson(getIntent().getStringExtra("debtors"), Debtor[].class);
        HashMap<String, List<Debt>> debts_by_day = separateDebtsByDay(debtors);
        List<String> sortedDays = sortDays(debts_by_day);

        for(String day : sortedDays) {
            Log.i("day", day);
            for(Debt debt : debts_by_day.get(day)) {
                Debtor debtor = getDebtOwner(debt, debtors);
                Log.i("debt", debtor.getName() + " -> " + debt.getDescription() + ": " + Float.toString(debt.getValue()));
            }
        }

        // set up the RecyclerView
        RecyclerView recyclerView = findViewById(R.id.rvAnimals);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new LoanListAdapter(this, debts_by_day, sortedDays, debtors);
        adapter.setClickListener(this);
        recyclerView.setAdapter(adapter);

        setReturnButton();
    }

    @Override
    public void onItemClick(View view, int position) {
        Intent intent = new Intent(LoanListActivity.this, EditActivity.class);
        Debt debt = adapter.getDebtByPosition(position);
        intent.putExtra("description", debt.getDescription());
        intent.putExtra("value", debt.getValue());
        intent.putExtra("isPayed", debt.isPayed());
        intent.putExtra("debt_index", getDebtOwner(debt, debtors).getDebtIndex(debt));
        intent.putExtra("debt_owner_index", getDebtOwnerIndex(getDebtOwner(debt, debtors)));
        intent.putExtra("debtors", debtors);
        startActivity(intent);
        overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
    }

    private HashMap<String, List<Debt>> separateDebtsByDay(Debtor[] _debtors) {
        List<Debtor> debtor_list = Arrays.asList(_debtors);
        HashMap<String, List<Debt>> debts_by_day = new HashMap<String, List<Debt>>();
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");

        for(Debtor debtor : debtor_list) {
            for(Debt debt : debtor.getDebts()) {
                String date_formatted = sdf.format(debt.getDate());

                if(!debts_by_day.containsKey(sdf.format(debt.getDate()))) {
                    List<Debt> debt_list = new ArrayList<Debt>();
                    debt_list.add(debt);
                    debts_by_day.put(date_formatted, debt_list);
                    continue;
                }

                List<Debt> debt_list = debts_by_day.get(date_formatted);
                debt_list.add(debt);
                debts_by_day.put(date_formatted, debt_list);
            }
        }

        return debts_by_day;
    }

    public static Debtor getDebtOwner(Debt debt, Debtor[] _debtors) {
        for(Debtor debtor : _debtors)
            for(Debt d : debtor.getDebts())
                if(d == debt)
                    return debtor;

        return null;
    }

    private List<String> sortDays(HashMap<String, List<Debt>> hashMap) {
        List<String> list = new ArrayList<String>();
        list.addAll(hashMap.keySet());
        Collections.sort(list);
        Collections.reverse(list);

        return list;
    }

    private void setReturnButton() {
        ImageView returnButton = findViewById(R.id.returnButton);
        returnButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LoanListActivity.this, MainActivity.class);
                startActivity(intent);
                overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
            }
        });
    }

    public int getDebtOwnerIndex(Debtor debtor) {
        int i = 0;
        for(Debtor d : debtors) {
            if(debtor == d)
                return i;

            i++;
        }
        return -1;
    }
}