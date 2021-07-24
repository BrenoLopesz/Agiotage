package com.brenolopes.agiotage;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.transition.Scene;
import android.transition.Transition;
import android.transition.TransitionInflater;
import android.transition.TransitionManager;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import com.google.gson.Gson;

public class AddLoanActivity extends AppCompatActivity {
    float money;
    private String description, name;
    private Debtor[] debtors;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_loan);
        debtors = new Gson().fromJson(getIntent().getStringExtra("debtors"), Debtor[].class);

        transitionScenes(0);
        MenuBar.setUp(this, debtors);
    }

    // Cria um array de buttons que transiciona pro item do array de scenes correspondente
    private void transitionScenes(int i) {
        Button[] continue_buttons =
                { (Button) findViewById(R.id.continue_button),
                  (Button) findViewById(R.id.continue_button_2),
                  (Button) findViewById(R.id.continue_button_3) };

        // Previne crashs
        if(i > continue_buttons.length)
            return;

        ViewGroup scene_root = (ViewGroup) findViewById(R.id.scene_root);
        Scene[] other_scenes =
                { Scene.getSceneForLayout(scene_root, R.layout.description_set, this),
                  Scene.getSceneForLayout(scene_root, R.layout.name_set, this) };

        Transition fadeTransition =
                TransitionInflater.from(this).
                        inflateTransition(R.transition.fade_transition);

        setReturnButton(i, fadeTransition, scene_root);

        continue_buttons[i].setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveData(i);

                if(other_scenes.length <= i) {
                    MyJSON.addDebt(money, name, description, getApplicationContext());
                    debtors = MyJSON.getDebtors(getApplicationContext());
                    Intent intent = new Intent(AddLoanActivity.this, LoanListActivity.class);
                    intent.putExtra("debtors", new Gson().toJson(debtors));
                    startActivity(intent);
                    overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                    return;
                }

                TransitionManager.go(other_scenes[i], fadeTransition);
                transitionScenes(i + 1);
            }
        });
    }

    private void saveData(int dataType) {
        if(dataType == 0)
            money = getMoney();

        if(dataType == 1)
            description = getDescription();

        if(dataType == 2)
            name = getName();
    }

    private float getMoney() {
        MoneyInput moneyInput = (MoneyInput) findViewById(R.id.editTextNumber);
        Log.i("money input", Integer.toString(moneyInput.money));
        return ((float) moneyInput.money) / 100;
    }

    private String getDescription() {
        EditText descInput = (EditText) findViewById(R.id.editText_2);
        return descInput.getText().toString();
    }

    private String getName() {
        EditText descInput = (EditText) findViewById(R.id.editText_3);
        return descInput.getText().toString();
    }

    private void setReturnButton(int i, Transition fadeTransition, ViewGroup scene_root) {
        ImageView[] return_buttons =
                { (ImageView) findViewById(R.id.return_button_1),
                  (ImageView) findViewById(R.id.return_button_2),
                  (ImageView) findViewById(R.id.return_button_3) };

        return_buttons[i].setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(i == 0) {
                    Intent intent = new Intent(AddLoanActivity.this, MainActivity.class);
                    startActivity(intent);
                    overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                    return;
                }

                Scene value_scene = Scene.getSceneForLayout(scene_root, R.layout.add_loan, AddLoanActivity.this);
                Scene description_scene = Scene.getSceneForLayout(scene_root, R.layout.description_set, AddLoanActivity.this);
                TransitionManager.go(i == 1? value_scene : description_scene, fadeTransition);
                transitionScenes(i - 1);
            }
        });
    }
}