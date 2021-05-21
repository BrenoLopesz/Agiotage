package com.brenolopes.agiotage;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.google.gson.Gson;

public class MainActivity extends AppCompatActivity {
    private Debtor[] debtors;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        debtors = MyJSON.getDebtors(getApplicationContext());

        Button add_loan = findViewById(R.id.add_loan_button);
        Button view_loans = findViewById(R.id.view_loans_button);
        Button view_report = findViewById(R.id.view_report_button);

        add_loan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, AddLoanActivity.class);
                intent.putExtra("debtors", new Gson().toJson(debtors));
                startActivity(intent);
                overridePendingTransition(0, 0);
            }
        });

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
                Intent intent = new Intent(MainActivity.this, ReportActivity.class);
                startActivity(intent);
                overridePendingTransition(0, 0);
            }
        });
    }
}