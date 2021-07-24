package com.brenolopes.agiotage;

import android.content.Intent;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

import com.google.gson.Gson;

public class MenuBar {

    public static void setUp(AppCompatActivity activity, Debtor[] debtors) {
        activity.findViewById(R.id.add_loan_icon).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(android.view.View v) {
                Intent intent = new Intent(activity, AddLoanActivity.class);
                intent.putExtra("debtors", new Gson().toJson(debtors));
                activity.startActivity(intent);
                activity.overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
            }
        });

        activity.findViewById(R.id.report_icon).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(android.view.View v) {
                if(debtors.length == 0) {
                    Intent intent = new Intent(activity, EmptyReport.class);
                    activity.startActivity(intent);
                    activity.overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                    return;
                }

                Intent intent = new Intent(activity, ReportActivity.class);
                intent.putExtra("debtors", new Gson().toJson(debtors));
                activity.startActivity(intent);
                activity.overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
            }
        });

        activity.findViewById(R.id.loan_list_icon).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(android.view.View v) {
                if(debtors.length == 0) {
                    Intent intent = new Intent(activity, EmptyList.class);
                    activity.startActivity(intent);
                    activity.overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                    return;
                }

                Intent intent = new Intent(activity, LoanListActivity.class);
                intent.putExtra("debtors", new Gson().toJson(debtors));
                activity.startActivity(intent);
                activity.overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
            }
        });
    }
}
