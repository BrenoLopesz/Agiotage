package com.brenolopes.agiotage;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.content.res.ResourcesCompat;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

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

    Debtor[] debtors;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_report);
        debtors = new Gson().fromJson(getIntent().getStringExtra("debtors"), Debtor[].class);

        LineChart chart = findViewById(R.id.chart);
        ChartMode mode = ChartMode.MONTH;
        String[] labels = ChartData.getLabels(mode);
        float[] dataSet = ChartData.getData(mode, debtors);

        // Adiciona as informações no chart
        List<Entry> entries = new ArrayList<>();
        int i = 0;
        for (float data : dataSet) {
            entries.add(new Entry(i, data));
            i++;
        }

        LineDataSet lineDataSet = new LineDataSet(entries, "Label"); // add entries to dataset
        styleLineDataSet(lineDataSet);
        LineData lineData = new LineData(lineDataSet);
        chart.setData(lineData);
        chart.getXAxis().setValueFormatter(new IndexAxisValueFormatter(labels));
        styleChart(chart);

        setReturnButton();
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

    private void styleChart(LineChart chart) {
        int color = ContextCompat.getColor(getApplicationContext(), R.color.white);
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
        chart.setExtraOffsets(30, 30, 30, 15);
        chart.invalidate();
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
        //Long.MIN_VALUE == -Long.MIN_VALUE so we need an adjustment here
        if (value == Long.MIN_VALUE) return format(Long.MIN_VALUE + 1);
        if (value < 0) return "-" + format(-value);
        if (value < 1000) return Long.toString(value); //deal with easy case

        Map.Entry<Long, String> e = suffixes.floorEntry(value);
        Long divideBy = e.getKey();
        String suffix = e.getValue();

        long truncated = value / (divideBy / 10); //the number part of the output times 10
        boolean hasDecimal = truncated < 100 && (truncated / 10d) != (truncated / 10f);
        return hasDecimal ? (truncated / 10d) + suffix : (truncated / 10) + suffix;
    }
}