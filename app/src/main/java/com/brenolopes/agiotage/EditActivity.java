package com.brenolopes.agiotage;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ArgbEvaluator;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.Intent;
import android.graphics.drawable.TransitionDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.animation.LinearInterpolator;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import com.google.gson.Gson;

import java.text.DecimalFormat;

public class EditActivity extends AppCompatActivity {
    private boolean isPayed;
    private int animDuration = 350;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.edit_activity);

        String description = getIntent().getStringExtra("description");
        float value = getIntent().getFloatExtra("value", 0.0f);
        DecimalFormat formatter = new DecimalFormat("#0.00");
        String formattedValue = formatter.format((double) value).replace(".", ",");

        ((EditText) findViewById(R.id.desc_text)).setText(description);
        MoneyInput moneyInput = (MoneyInput) findViewById(R.id.money_input);
        moneyInput.setText("R$ " + formattedValue);
        moneyInput.setValue(Math.round(value * 100));
        isPayed = getIntent().getBooleanExtra("isPayed", false);
        boolean shouldReverse = isPayed;

        Button yes_button = (Button) findViewById(R.id.button);
        Button no_button = (Button) findViewById(R.id.button2);

        ObjectAnimator anim1 = getAnimation(yes_button);
        ObjectAnimator anim2 = getAnimation(no_button);

        if(shouldReverse) {
            anim1.setDuration(0);
            anim2.setDuration(0);
            anim1.reverse();
            anim2.start();

            TransitionDrawable transition1 = (TransitionDrawable) yes_button.getBackground();
            TransitionDrawable transition2 = (TransitionDrawable) no_button.getBackground();

            transition1.startTransition(0);
            transition2.startTransition(0);

            anim1.setDuration(animDuration);
            anim2.setDuration(animDuration);
        }

        yes_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(isPayed)
                    return;

                runAnimation(anim1, yes_button, true, true);
                runAnimation(anim2, no_button, false, false);

                isPayed = !isPayed;
            }
        });

        no_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!isPayed)
                    return;

                runAnimation(anim1, yes_button, true, false);
                runAnimation(anim2, no_button, true, true);

                isPayed = !isPayed;
            }
        });

        setReturnButton();
        setSaveButton();
        setDeleteButton();
    }

    private ObjectAnimator getAnimation(Button button) {
        Integer colorFrom = getResources().getColor(R.color.white);
        Integer colorTo = getResources().getColor(R.color.unselected);
        ObjectAnimator animator = ObjectAnimator.ofObject(button, "textColor", new ArgbEvaluator(), colorFrom, colorTo);
        animator.setDuration(animDuration);

        return animator;
    }

    private void runAnimation(ObjectAnimator animation, Button button, boolean reverse, boolean reverseText) {
        TransitionDrawable transition = (TransitionDrawable) button.getBackground();

        if(reverse)
            transition.reverseTransition(animDuration);
        else
            transition.startTransition(animDuration);


        if(reverseText)
            animation.reverse();
        else
            animation.start();
    }

    private void setReturnButton() {
        ImageView returnButton = findViewById(R.id.returnButton2);
        returnButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(EditActivity.this, LoanListActivity.class);
                intent.putExtra("debtors", new Gson().toJson(getIntent().getSerializableExtra("debtors")));
                startActivity(intent);
                overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
            }
        });
    }

    private void setSaveButton() {
        Button saveButton = findViewById(R.id.save_button);
        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Debtor[] debtors = (Debtor[]) getIntent().getSerializableExtra("debtors");
                Debtor debtor = debtors[getIntent().getIntExtra("debt_owner_index", -1)];
                int index = getIntent().getIntExtra("debt_index", -1);
                Debt debt = debtor.getDebt(index);
                EditText descText = findViewById(R.id.desc_text);
                MoneyInput moneyInput = findViewById(R.id.money_input);
                debt.setDescription(descText.getText().toString());
                debt.setValue(((float) moneyInput.getMoney()) / 100f);
                debt.setPayed(isPayed);

                String json = new Gson().toJson(debtors);
                FileHandler.setUp(getApplicationContext(), "loans.json");
                FileHandler.saveData(json);

                Intent intent = new Intent(EditActivity.this, LoanListActivity.class);
                intent.putExtra("debtors", new Gson().toJson(debtors));
                startActivity(intent);
                overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
            }
        });
    }

    private void setDeleteButton() {
        Button saveButton = findViewById(R.id.delete_button);
        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Debtor[] debtors = (Debtor[]) getIntent().getSerializableExtra("debtors");
                Debtor debtor = debtors[getIntent().getIntExtra("debt_owner_index", -1)];
                int index = getIntent().getIntExtra("debt_index", -1);
                Debt debt = debtor.getDebt(index);
                debtor.removeDebt(debt);

                String json = new Gson().toJson(debtors);
                FileHandler.setUp(getApplicationContext(), "loans.json");
                FileHandler.saveData(json);

                Intent intent = new Intent(EditActivity.this, LoanListActivity.class);
                intent.putExtra("debtors", new Gson().toJson(debtors));
                startActivity(intent);
                overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
            }
        });
    }
}
