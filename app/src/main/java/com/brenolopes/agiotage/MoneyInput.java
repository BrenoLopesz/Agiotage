package com.brenolopes.agiotage;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import java.text.DecimalFormat;

public class MoneyInput extends androidx.appcompat.widget.AppCompatEditText {
     int money = 0;

    public MoneyInput(Context context, AttributeSet attrs) {
        super(context, attrs);

        this.setOnKeyListener(new OnKeyListener() {
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                String key = Character.toString((char) event.getUnicodeChar(event.getMetaState()));
                if(event.getAction() == KeyEvent.ACTION_UP && isNumeric(key)) {
                    money *= 10;
                    money += Integer.parseInt(key);
                    DecimalFormat formatter = new DecimalFormat("#0.00");
                    setText("R$ " + formatter.format((double) money / 100).replace(".", ","));
                } else if(event.getAction() == KeyEvent.ACTION_UP && event.getKeyCode() == KeyEvent.KEYCODE_DEL) {
                    money -= getLastDigit(money);
                    money /= 10;
                    DecimalFormat formatter = new DecimalFormat("#0.00");
                    setText("R$ " + formatter.format((double) money / 100).replace(".", ","));
                }
                return false;
            }
        });
    }

    public void setValue(int value) {
        money = value;
    }

    @Override
    public void onSelectionChanged(int start, int end) {

        CharSequence text = getText();
        if (text != null) {
            if (start != text.length() || end != text.length()) {
                setSelection(text.length(), text.length());
                return;
            }
        }

        super.onSelectionChanged(start, end);
    }

    public static boolean isNumeric(String strNum) {
        if (strNum == null) {
            return false;
        }
        try {
            int d = Integer.parseInt(strNum);
        } catch (NumberFormatException nfe) {
            return false;
        }
        return true;
    }

    public int getLastDigit(int value) {
        DecimalFormat formatter = new DecimalFormat("#0.00");
        String toStr = formatter.format(value);
        return Integer.parseInt(toStr.substring(toStr.length() - 1));
    }

    public int getMoney() {
        return money;
    }
}
