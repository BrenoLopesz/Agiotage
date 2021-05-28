package com.brenolopes.agiotage;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.content.res.ResourcesCompat;

import android.animation.ArgbEvaluator;
import android.animation.ObjectAnimator;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.github.mikephil.charting.charts.Chart;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;
import com.github.mikephil.charting.formatter.ValueFormatter;
import com.google.gson.Gson;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.NavigableMap;
import java.util.TreeMap;

public class ReportActivity extends AppCompatActivity {
    private static final NavigableMap<Long, String> suffixes = new TreeMap<>();
    static {
        suffixes.put(1_000L, "k");
        suffixes.put(1_000_000L, "M");
    }

    ChartMode[] modes = { ChartMode.YEAR, ChartMode.MONTH, ChartMode.WEEK };
    Debtor[] debtors;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_report);
        debtors = new Gson().fromJson(getIntent().getStringExtra("debtors"), Debtor[].class);

        LineChart chart = findViewById(R.id.chart);
        ChartMode mode = ChartMode.YEAR;
        fillChartData(chart, mode);

        Button[] chartModeButtons = {
                (Button) findViewById(R.id.button_yearly),
                (Button) findViewById(R.id.button_monthly),
                (Button) findViewById(R.id.button_weekly),
        };

        SelectableButtons selectables = new SelectableButtons(getApplicationContext(), chartModeButtons);
        selectables.setSelected(findViewById(R.id.button_yearly), false);
        selectables.onChange((i) -> fillChartData(chart, modes[i]));
        setReturnButton();
        setDebtorsInfo();
    }

    private void fillChartData(Chart chart, ChartMode mode) {
        float[] dataSet = ChartData.getData(mode, debtors);

        // Adiciona as informações no chart
        List<Entry> entries = new ArrayList<>();
        int i = 0;
        for (float data : dataSet) {
            entries.add(new Entry(i, data));
            i++;
        }

        LineDataSet lineDataSet = new LineDataSet(entries, "Label"); // add entries to dataset
        LineData lineData = new LineData(lineDataSet);
        chart.setData(lineData);
        styleLineDataSet(lineDataSet);
        styleChart((LineChart) chart, mode);
    }

    private void styleLineDataSet(LineDataSet lineDataSet) {
        int color = ContextCompat.getColor(getApplicationContext(), R.color.green_line);
        lineDataSet.setLineWidth(6f);
        lineDataSet.setCircleRadius(7f);
        lineDataSet.setColor(color, 255);
        lineDataSet.setCircleColor(color);
        lineDataSet.setCircleHoleColor(color);
        lineDataSet.setValueTextSize(18f);
        lineDataSet.setValueTextColor(color);
        lineDataSet.setValueTypeface(ResourcesCompat.getFont(getApplicationContext(), R.font.roboto_bold));
        lineDataSet.setValueFormatter(new ValueFormatter() {
            @Override
            public String getPointLabel(Entry entry) {
                if (entry.getY() == 0) return "";
                DecimalFormat formatter = new DecimalFormat("#0");
                return format(Long.parseLong(formatter.format(entry.getY())));
            }
        });
    }

    private void styleChart(LineChart chart, ChartMode mode) {
        int color = ContextCompat.getColor(getApplicationContext(), R.color.white);
        String[] labels = ChartData.getLabels(mode);
        chart.getXAxis().setDrawGridLines(false);
        chart.getXAxis().setPosition(XAxis.XAxisPosition.BOTTOM);
        chart.getXAxis().setTextSize(10);
        chart.getXAxis().setLabelCount(11);
        chart.getXAxis().setLabelRotationAngle(50);
        chart.getAxisLeft().setAxisLineColor(color);
        chart.getAxisLeft().setDrawLabels(false);
        chart.getAxisLeft().setDrawGridLines(false);
        chart.getAxisRight().setEnabled(false);
        chart.getAxisLeft().setTextSize(14);
        chart.getDescription().setEnabled(false);
        chart.getLegend().setEnabled(false);
        chart.setExtraOffsets(30, 30, 30, 30);
        chart.getXAxis().setValueFormatter(new IndexAxisValueFormatter(labels));
        chart.invalidate();
    }

    private void setDebtorsInfo() {
        DebtorsInfo infos = new DebtorsInfo(debtors);
        ((TextView) findViewById(R.id.loan_total)).setText(infos.getInfo(InfoType.LOAN_TOTAL));
        ((TextView) findViewById(R.id.loans_this_month)).setText(infos.getInfo(InfoType.LOANS_THIS_MONTH));
        ((TextView) findViewById(R.id.loan_recovered_this_month)).setText(infos.getInfo(InfoType.LOAN_TOTAL_RECOVERED_THIS_MONTH));
        ((TextView) findViewById(R.id.debtors_amount)).setText(infos.getInfo(InfoType.DEBTORS_AMOUNT));
        ((TextView) findViewById(R.id.new_debtors_amount)).setText(infos.getInfo(InfoType.NEW_DEBTORS_AMOUNT));
        ((TextView) findViewById(R.id.ex_debtors_amount)).setText(infos.getInfo(InfoType.EX_DEBTORS_AMOUNT));
    }

    private void setReturnButton() {
        ImageView returnButton = findViewById(R.id.returnButton);
        returnButton.setOnClickListener(v -> {
            Intent intent = new Intent(ReportActivity.this, MainActivity.class);
            startActivity(intent);
            overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
        });
    }

    /** Formata os valores no gráfico. <br> (Exemplo: 2200 => 2.2k, 3550500 => 3.5M) */
    public static String format(long value) {
        //Long.MIN_VALUE == -Long.MIN_VALUE
        if (value == Long.MIN_VALUE) return format(Long.MIN_VALUE + 1);
        if (value < 0) return "-" + format(-value);
        if (value < 1000) return Long.toString(value); //deal with easy case

        Map.Entry<Long, String> e = suffixes.floorEntry(value);
        Long divideBy = e.getKey();
        String suffix = e.getValue();

        long truncated = value / (divideBy / 10);
        boolean hasDecimal = truncated < 100 && (truncated / 10d) != (truncated / 10f);
        return hasDecimal ? (truncated / 10d) + suffix : (truncated / 10) + suffix;
    }
}