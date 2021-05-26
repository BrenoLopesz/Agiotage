package com.brenolopes.agiotage;

import android.animation.ArgbEvaluator;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.graphics.drawable.TransitionDrawable;
import android.util.Log;
import android.widget.Button;

import androidx.appcompat.content.res.AppCompatResources;

import java.util.Arrays;

public class SelectableButtons {
    private final Context context;
    private Button[] buttons;
    private int selectedIndex = -1;
    SelectListener listener;

    public SelectableButtons(Context _context, Button... _buttons) {
        this.buttons = _buttons;
        this.context = _context;
        setClickable();
    }

    public void setSelected(Button button, boolean animate) {
        // Se o button não pertence ao SelectableButtons
        if(!hasButton(button)) {
            Log.e("Button not find", "O Button fornecido não pertence aos SelectableButtons");
            return;
        }

        Log.i("isSelected", String.valueOf(isAnyButtonSelected()));
        if(isAnyButtonSelected()) {
            // Torna o botão selecionado, instantaneamente, em não selecionado
            TransitionDrawable selectedButtonTransition = (TransitionDrawable) getSelectedButton().getBackground();
            selectedButtonTransition.reverseTransition(animate? 200 : 0);
            getTextColorAnimation(getSelectedButton(), animate).reverse();
        }

        TransitionDrawable transition = (TransitionDrawable) button.getBackground();
        transition.startTransition(animate? 200 : 0);
        getTextColorAnimation(button, animate).start();
        this.selectedIndex = getButtonIndex(button);
    }

    private void setClickable() {
        for(Button button : buttons) {
            button.setOnClickListener(v -> {
                setSelected(button, true);
                Log.i("isnull", String.valueOf(listener == null));
                if(listener != null) listener.run(getButtonIndex(button));
            });
        }
    }

    public void onChange(SelectListener _listener) {
        this.listener = _listener;
    }

    private ObjectAnimator getTextColorAnimation(Button button, boolean animate) {
        Integer colorFrom = this.context.getResources().getColor(R.color.unselected);
        Integer colorTo = this.context.getResources().getColor(R.color.white);
        ObjectAnimator animator = ObjectAnimator.ofObject(button, "textColor", new ArgbEvaluator(), colorFrom, colorTo);
        animator.setDuration(animate? 200 : 0);

        return animator;
    }

    private boolean hasButton(Button button) {
        return Arrays.asList(buttons).contains(button);
    }

    private int getButtonIndex(Button button) {
        for(int i = 0; i < buttons.length; i++)
            if(buttons[i] == button)
                return i;

        return -1;
    }

    private boolean isAnyButtonSelected() {
        return selectedIndex != -1;
    }

    private Button getSelectedButton() {
        return buttons[selectedIndex];
    }
}
