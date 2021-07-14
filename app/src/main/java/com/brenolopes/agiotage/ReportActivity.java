package com.brenolopes.agiotage;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.content.res.ResourcesCompat;

import android.animation.ArgbEvaluator;
import android.animation.ObjectAnimator;
import android.content.Intent;
import android.os.Bundle;
import android.text.Layout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewStub;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Spinner;
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

    // ReportData é responsável por qual tipo de report será exibido (geral ou individual)
    // ChartData é responsável por qual tipo de chart será exibido

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_report);
        debtors = new Gson().fromJson(getIntent().getStringExtra("debtors"), Debtor[].class);

        ReportData.setTotalDebtors(debtors);
        changeReportMode(ReportMode.GENERAL);
        LineChart chart = findViewById(R.id.chart);

        Button[] chartModeButtons = {
                (Button) findViewById(R.id.button_yearly),
                (Button) findViewById(R.id.button_monthly),
                (Button) findViewById(R.id.button_weekly),
        };

        Button[] reportModeButtons = {
                (Button) findViewById(R.id.button_general),  
                (Button) findViewById(R.id.button_individual),
        };

        // Cria os botões selecionáveis do ChartMode
        SelectableButtons chartModeSelectables = new SelectableButtons(getApplicationContext(), chartModeButtons);
        chartModeSelectables.setSelected(findViewById(R.id.button_yearly), false);
        chartModeSelectables.onChange((i) -> fillChartData(chart, modes[i]));

        // Cria os botões selecionáveis do ReportMode
        SelectableButtons reportModeSelectables = new SelectableButtons(getApplicationContext(), reportModeButtons);
        reportModeSelectables.setSelected(findViewById(R.id.button_general), false);
        reportModeSelectables.onChange((i) -> changeReportMode(i == 0? ReportMode.GENERAL : ReportMode.INDIVIDUAL));

        setReturnButton();
    }

    private void fillChartData(Chart chart, ChartMode mode) {
        ChartData.setMode(mode);
        float[] dataSet = ChartData.getData(mode,
                ReportData.getMode() == ReportMode.GENERAL ?
                        ReportData.getDebtors() :
                        new Debtor[] { ReportData.getSelectedDebtor() });

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

    private void changeReportMode(ReportMode mode) {
        ReportData.setMode(mode);
        LineChart chart = findViewById(R.id.chart);
        fillChartData(chart, ChartData.getMode());

        int layoutId = 0;
        if(mode == ReportMode.GENERAL)
            layoutId = R.layout.general_infos;
        if(mode == ReportMode.INDIVIDUAL)
            layoutId = R.layout.individual_infos;

        ViewGroup holder = (ViewGroup) findViewById(R.id.holder);
        LayoutInflater inflater = (LayoutInflater) this.getSystemService(LAYOUT_INFLATER_SERVICE);
        View childLayout = inflater.inflate(layoutId,
                holder, false);

        if(holder == null) {
            ViewStub stub = (ViewStub) findViewById(R.id.debt_infos);
            stub.setLayoutResource(layoutId);
            stub.inflate();
        } else {
            holder.removeAllViews();
            holder.addView(childLayout);
        }

        if(mode == ReportMode.INDIVIDUAL)
            fillDropdown();

        setDebtorsInfo();
        // Mudar o layout abaixo da chart
        // Mudar a data do chart
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

    // Define as infos abaixo do gráfico
    private void setDebtorsInfo() {
        ReportMode mode = ReportData.getMode();
        Spinner dropdown = findViewById(R.id.spinner);
        DebtorsInfo infos = new DebtorsInfo(mode == ReportMode.GENERAL?
                debtors :
                new Debtor[] { ReportData.getSelectedDebtor() });

        ((TextView) findViewById(R.id.loan_total)).setText(infos.getInfo(InfoType.LOAN_TOTAL));
        ((TextView) findViewById(R.id.loans_this_month)).setText(infos.getInfo(InfoType.LOANS_THIS_MONTH));

        Log.i("mode", String.valueOf(mode));
        // Não preenche o restante das infos caso o modo seja individual
        if(mode == ReportMode.INDIVIDUAL) return;
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

    private void fillDropdown() {
        Spinner dropdown = findViewById(R.id.spinner);
        String[] debtor_names = new String[debtors.length];


        for(int i = 0; i < debtors.length; i++) {
            debtor_names[i] = debtors[i].getName();
        }

        // Criar layout do spinner 
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, R.layout.spinner, debtor_names);
        dropdown.setAdapter(adapter);
        dropdown.setSelection(ReportData.getSelectedDebtorIndex());

        dropdown.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                ReportData.setSelectedDebtor(position);
                setDebtorsInfo();
                fillChartData(findViewById(R.id.chart), ChartData.getMode());
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
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