package com.brenolopes.agiotage;

import java.io.Serializable;
import java.util.Date;

public class Debt implements Serializable {
    private float value;
    private String description;
    private final Date date;
    private boolean payed;

    public Debt(float _value, String _description) {
        this.value = _value;
        this.description = _description;
        this.date = new Date();
        this.payed = false;
    }

    public Date getDate() {
        return date;
    }

    public float getValue() {
        return value;
    }

    public String getDescription() {
        return description;
    }

    public boolean isPayed() {
        return payed;
    }

    public void setValue(float _value) {
        this.value = _value;
    }

    public void setDescription(String _description) {
        this.description = _description;
    }

    public void setPayed(boolean _payed) {
        this.payed = _payed;
    }
}
