package com.brenolopes.agiotage;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.widget.NestedScrollView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.os.Debug;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ScrollView;
import android.widget.TextView;

import com.google.gson.Gson;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

public class MainActivity extends AppCompatActivity implements LoanListAdapter.ItemClickListener {
    private Debtor[] debtors;
    LoanListAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        debtors = MyJSON.getDebtors(getApplicationContext());

        configName();
        Log.i("name", String.valueOf(NameHandler.getName() == null));

        ((NestedScrollView) findViewById(R.id.nestedScrollView)).scrollTo(0, 0);
        ((TextView) findViewById(R.id.name_placeholder)).setText(NameHandler.getName());

        // TODO: Atualizar o "debtors" em toda transição de cena
        // (Ex.: Adicionou uma debt e depois transicionou para o report)

        DebtorsInfo infos = new DebtorsInfo(debtors);
        ((TextView) findViewById(R.id.main_debtors_amount)).setText(infos.getInfo(InfoType.ACTIVE_DEBTORS_AMOUNT));
        ((TextView) findViewById(R.id.main_total_debt)).setText(infos.getInfo(InfoType.LOAN_TOTAL));

        Button view_loans = findViewById(R.id.view_loans_button);
        Button view_report = findViewById(R.id.view_report_button);

        view_loans.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, LoanListActivity.class);
                intent.putExtra("debtors", new Gson().toJson(debtors));
                startActivity(intent);
                overridePendingTransition(0, 0);
            }
        });

        view_report.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(debtors.length == 0) {
                    Intent intent = new Intent(MainActivity.this, EmptyReport.class);
                    startActivity(intent);
                    overridePendingTransition(0, 0);
                    return;
                }

                Intent intent = new Intent(MainActivity.this, ReportActivity.class);
                intent.putExtra("debtors", new Gson().toJson(debtors));
                startActivity(intent);
                overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
            }
        });

        setUpRecyclerView();
        MenuBar.setUp(this, debtors);
    }

    private void setUpRecyclerView() {
        HashMap<String, List<Debt>> debts_by_day = separateDebtsByDay(debtors);
        List<String> sortedDays = sortDays(debts_by_day);

        for(String day : sortedDays) {
            Log.i("day", day);
            for(Debt debt : debts_by_day.get(day)) {
                Debtor debtor = getDebtOwner(debt, debtors);
                Log.i("debt", debtor.getName() + " -> " + debt.getDescription() + ": " + Float.toString(debt.getValue()));
            }
        }

        RecyclerView recyclerView = findViewById(R.id.rvMiniList);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new LoanListAdapter(this, debts_by_day, sortedDays, debtors, true);
        adapter.setClickListener(this);
        recyclerView.setAdapter(adapter);
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

    public int getDebtOwnerIndex(Debtor debtor) {
        int i = 0;
        for(Debtor d : debtors) {
            if(debtor == d)
                return i;

            i++;
        }
        return -1;
    }

    public void configName() {
        NameHandler.initialize(getApplicationContext());
        if(!NameHandler.hasName()) {
            Intent intent = new Intent(MainActivity.this, NameSetActivity.class);
            intent.putExtra("debtors", new Gson().toJson(debtors));
            startActivity(intent);
            overridePendingTransition(0, 0);
        }
    }

    @Override
    public void onItemClick(View view, int position) {
        Intent intent = new Intent(MainActivity.this, EditActivity.class);
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

    public void preventClicks(View view) {}
}