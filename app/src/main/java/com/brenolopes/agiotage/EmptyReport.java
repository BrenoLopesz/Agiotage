package com.brenolopes.agiotage;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.gson.Gson;

public class EmptyReport extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.empty_report);
        Debtor[] debtors = MyJSON.getDebtors(getApplicationContext());
        setReturnButton();

        findViewById(R.id.add_loan_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(EmptyReport.this, AddLoanActivity.class);
                intent.putExtra("debtors", new Gson().toJson(debtors));
                startActivity(intent);
                overridePendingTransition(0, 0);
            }
        });

        MenuBar.setUp(this, debtors);
    }

    private void setReturnButton() {
        ImageView returnButton = findViewById(R.id.returnButton);
        returnButton.setOnClickListener(v -> {
            Intent intent = new Intent(EmptyReport.this, MainActivity.class);
            startActivity(intent);
            overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
        });
    }
}
