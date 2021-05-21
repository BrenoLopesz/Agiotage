package com.brenolopes.agiotage;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

public class LoanListAdapter extends RecyclerView.Adapter<LoanListAdapter.ViewHolder> {

    private HashMap<String, List<Debt>> debts_by_day;
    private List<String> sortedDays;
    private LayoutInflater mInflater;
    private ItemClickListener mClickListener;
    private Debtor[] debtors;
    private List<Debt> firstDebtsOfDays = new ArrayList<Debt>();

    // data is passed into the constructor
    LoanListAdapter(Context context, HashMap<String, List<Debt>> data, List<String> _sortedDays, Debtor[] _debtors) {
        this.mInflater = LayoutInflater.from(context);
        this.debts_by_day = data;
        this.sortedDays = _sortedDays;
        this.debtors = _debtors;

        for(String day : sortedDays) {
            List<Debt> debts = debts_by_day.get(day);
            firstDebtsOfDays.add(debts.get(debts.size() - 1));
        }
    }

    // inflates the row layout from xml when needed
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = mInflater.inflate(R.layout.row_layout, parent, false);
        return new ViewHolder(view);
    }

    // binds the data to the TextView in each row
    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Debt debt = getDebtByPosition(position);
        Debtor debtor = LoanListActivity.getDebtOwner(debt, debtors);
        holder.loan_description.setText(debt.getDescription());
        holder.debtor_name.setText(debtor.getName());
        holder.loan_value.setText("R$ " + new DecimalFormat("#0.00").format(debt.getValue()).replace(".", ","));

        holder.is_payed.setText(debt.isPayed()? "Pago" : "Não pago");

        holder.loan_value.setTextColor(debt.isPayed()?
                Color.parseColor("#4F4F4F") :
                Color.parseColor("#6E0740"));

        holder.icon.setImageResource(debt.isPayed()? R.drawable.ic_check_mark : R.drawable.ic_wrong);

        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yy");

        if(!isStartOfDay(debt))
            holder.dateDiv.setVisibility(View.GONE);
        else {
            holder.dateTitle.setText(sdf.format(debt.getDate()));
            holder.dateDiv.setVisibility(View.VISIBLE);
        }
    }

    // total number of rows
    @Override
    public int getItemCount() {
        int total = 0;
        for(List<Debt> list : debts_by_day.values()) {
            total += list.size();
        }
        return total;
    }


    // stores and recycles views as they are scrolled off screen
    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView loan_description, debtor_name, dateTitle, loan_value, is_payed;
        ImageView icon;
        ConstraintLayout dateDiv;

        ViewHolder(View itemView) {
            super(itemView);
            loan_description = itemView.findViewById(R.id.loan_description);
            debtor_name = itemView.findViewById(R.id.debtor_name);
            dateTitle = itemView.findViewById(R.id.dateTitle);
            loan_value = itemView.findViewById(R.id.loan_value);
            is_payed = itemView.findViewById(R.id.is_payed);
            dateDiv = itemView.findViewById(R.id.dateDiv);
            icon = itemView.findViewById(R.id.icon);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            if (mClickListener != null) mClickListener.onItemClick(view, getAdapterPosition());
        }
    }

    // allows clicks events to be caught
    void setClickListener(ItemClickListener itemClickListener) {
        this.mClickListener = itemClickListener;
    }

    // parent activity will implement this method to respond to click events
    public interface ItemClickListener {
        void onItemClick(View view, int position);
    }

    public Debt getDebtByPosition(int position) {
        int i = 0;
        for(String day : sortedDays) {
            for(Debt debt : reverseList(debts_by_day.get(day))) {
                if(i == position)
                    return debt;

                i++;
            }
        }
        return null;
    }

    private boolean isStartOfDay(Debt debt) {
        return firstDebtsOfDays.contains(debt);
    }

    private List<Debt> reverseList(List<Debt> debtList) {
        List<Debt> clone = new ArrayList<Debt>();
        clone.addAll(debtList);
        Collections.reverse(clone);
        return clone;
    }
}