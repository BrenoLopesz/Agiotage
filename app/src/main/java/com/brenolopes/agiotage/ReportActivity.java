package com.brenolopes.agiotage;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.os.Bundle;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;
import com.github.mikephil.charting.formatter.ValueFormatter;

import java.util.ArrayList;
import java.util.List;

public class ReportActivity extends AppCompatActivity {
    String[] meses = new String[] { "Janeiro", "Abril", "Julho", "Dezembro" };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_report);
        LineChart chart = findViewById(R.id.chart);
        int[] dataSet = new int[] { 5, 4, 9, 7 };

        List<Entry> entries = new ArrayList<>();
        int i = 0;
        for (int data : dataSet) {
            entries.add(new Entry(i, data));
            i++;
        }

        LineDataSet lineDataSet = new LineDataSet(entries, "Label"); // add entries to dataset
        styleLineDataSet(lineDataSet);
        //lineDataSet.setValueFormatter(new MYvalueformatter());
        LineData lineData = new LineData(lineDataSet);
        chart.setData(lineData);
        chart.getXAxis().setValueFormatter(new IndexAxisValueFormatter(meses));
        styleChart(chart);
    }

    private void styleLineDataSet(LineDataSet lineDataSet) {
        int color = ContextCompat.getColor(getApplicationContext(), R.color.green_line);
        lineDataSet.setLineWidth(6f);
        lineDataSet.setCircleRadius(7f);
        lineDataSet.setColor(color, 255);
        lineDataSet.setCircleColor(color);
        lineDataSet.setCircleHoleColor(color);
        lineDataSet.setValueTextSize(18f);
    }

    private void styleChart(LineChart chart) {
        chart.getXAxis().setDrawGridLines(false);
        chart.getXAxis().setPosition(XAxis.XAxisPosition.BOTTOM);
        chart.getXAxis().setTextSize(10);
        chart.getAxisLeft().setDrawGridLines(false);
        chart.getAxisRight().setEnabled(false);
        chart.getAxisLeft().setTextSize(14);
        chart.getDescription().setEnabled(false);
        chart.getLegend().setEnabled(false);
        chart.setExtraOffsets(15, 30, 30, 15);
        chart.invalidate();
    }
}