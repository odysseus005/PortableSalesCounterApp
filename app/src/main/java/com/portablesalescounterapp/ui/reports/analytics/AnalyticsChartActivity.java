package com.portablesalescounterapp.ui.reports.analytics;

import android.app.ProgressDialog;
import android.databinding.DataBindingUtil;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.MenuItem;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;
import com.github.mikephil.charting.formatter.LargeValueFormatter;
import com.github.mikephil.charting.utils.ColorTemplate;

import com.hannesdorfmann.mosby.mvp.MvpActivity;
import com.portablesalescounterapp.R;
import com.portablesalescounterapp.databinding.ActivityAnalyticschartBinding;


import java.util.ArrayList;


public class AnalyticsChartActivity extends MvpActivity<AnalyticsChartView, AnalyticsChartPresenter> implements AnalyticsChartView {

    private ActivityAnalyticschartBinding binding;
    private ProgressDialog progressDialog;
    float barWidth;
    float barSpace;
    float groupSpace;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_analyticschart);


        binding = DataBindingUtil.setContentView(this, R.layout.activity_analyticschart);
        binding.setView(getMvpView());
        setSupportActionBar(binding.toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Analytics Chart Top Revenue(Monthly-June)");
        //  barWidth = 0.3f;
        // barSpace = 0f;
        // groupSpace = 0.6f;

        // groupSpace = 0.06f;
        barSpace = 0.1f; // x2 dataset
        barWidth = 1f; // x2 dataset
        // (0.02 + 0.45) * 2 + 0.06 = 1.00 -> interval per "group"

        //Description desc = new Description();
        //desc.setText("Sample Description and Lorem ipsum dolom");
        binding.barChart.setDescription(null);
        binding.barChart.setPinchZoom(true);
        binding.barChart.setScaleEnabled(true);
        binding.barChart.setDrawBarShadow(false);
        binding.barChart.setDrawGridBackground(false);
        binding.barChart.setMaxVisibleValueCount(30);


        int groupCount = 6;

        ArrayList xVals = new ArrayList();

        xVals.add("Jun");
        /*xVals.add("Jul");
        xVals.add("Aug");
        xVals.add("Sept");
        xVals.add("Oct");*/
        //CHECK THE DOCU NEED INSIDE DAPAT HINDI ISA ISA
        ArrayList yVals1 = new ArrayList();
        ArrayList yVals2 = new ArrayList();
        ArrayList yVals3 = new ArrayList();
        ArrayList yVals4 = new ArrayList();
        ArrayList yVals5 = new ArrayList();
        yVals1.add(new BarEntry(.5f, (float) 4));
        yVals2.add(new BarEntry(.5f, (float) 3.7));
        yVals3.add(new BarEntry(.5f, (float) 3));
        yVals4.add(new BarEntry(.5f, (float) 2.8));
        yVals5.add(new BarEntry(.5f, (float) 1.5));


      /*  yVals1.add(new BarEntry(1.5f, (float) 1));
        yVals2.add(new BarEntry(1.5f, (float) 2.7));
        yVals3.add(new BarEntry(1.5f, (float) 3.5));
        yVals4.add(new BarEntry(1.5f, (float) 4));
        yVals5.add(new BarEntry(1.5f, (float) 5));


        yVals1.add(new BarEntry(2.5f, (float) 3));
        yVals2.add(new BarEntry(2.5f, (float) 2));
        yVals3.add(new BarEntry(2.5f, (float) 1.5));
        yVals4.add(new BarEntry(2.5f, (float) 1));
        yVals5.add(new BarEntry(2.5f, (float) .8));


        yVals1.add(new BarEntry(3.5f, (float) 4.8));
        yVals2.add(new BarEntry(3.5f, (float) 3));
        yVals3.add(new BarEntry(3.5f, (float) 2.5));
        yVals4.add(new BarEntry(3.5f, (float) 1.9));
        yVals5.add(new BarEntry(3.5f, (float) .9));


        yVals1.add(new BarEntry(4.5f, (float) 5));
        yVals2.add(new BarEntry(4.5f, (float) 4));
        yVals3.add(new BarEntry(4.5f, (float) 3));
        yVals4.add(new BarEntry(4.5f, (float) 2));
        yVals5.add(new BarEntry(4.5f, (float) 1));*/


       /* yVals1.add(new BarEntry(2, (float) 6));
        yVals2.add(new BarEntry(2, (float) 7));
        yVals3.add(new BarEntry(2, (float) 8));
        yVals4.add(new BarEntry(2, (float) 9));
        yVals5.add(new BarEntry(2, (float) 10));
        yVals1.add(new BarEntry(3, (float) 11));
        yVals2.add(new BarEntry(3, (float) 12));
        yVals3.add(new BarEntry(3, (float) 13));
        yVals4.add(new BarEntry(3, (float) 14));
        yVals5.add(new BarEntry(3, (float) 15));
        yVals1.add(new BarEntry(4, (float) 16));
        yVals2.add(new BarEntry(4, (float) 17));
        yVals3.add(new BarEntry(4, (float) 18));
        yVals4.add(new BarEntry(4, (float) 19));
        yVals5.add(new BarEntry(4, (float) 20));
        yVals1.add(new BarEntry(5, (float) 16));
        yVals2.add(new BarEntry(5, (float) 17));
        yVals3.add(new BarEntry(5, (float) 18));
        yVals4.add(new BarEntry(5, (float) 19));
        yVals5.add(new BarEntry(5, (float) 20));
        yVals1.add(new BarEntry(6, (float) 16));
        yVals2.add(new BarEntry(6, (float) 17));
        yVals3.add(new BarEntry(6, (float) 18));
        yVals4.add(new BarEntry(6, (float) 19));
        yVals5.add(new BarEntry(6, (float) 1000));*/

        BarDataSet set1, set2,set3,set4,set5;
        set1 = new BarDataSet(yVals1, "Sample Product 1");
        set1.setColor(Color.BLUE);
        set2 = new BarDataSet(yVals2, "Sample Product 2");
        set2.setColor(Color.RED);
        set3 = new BarDataSet(yVals3, "Sample Product 3");
        set3.setColor(Color.YELLOW);
        set4 = new BarDataSet(yVals4, "Sample Product 4");
        set4.setColor(Color.CYAN);
        set5 = new BarDataSet(yVals5, "Sample Product 5");
        set5.setColor(Color.GREEN);


        BarData data = new BarData(set1,set2,set3,set4,set5);
        // data.setValueFormatter(new LargeValueFormatter());
        binding.barChart.setData(data);
        // binding.barChart.getBarData().setBarWidth(1f);
        binding.barChart.getXAxis().setAxisMinimum(0);
        //binding.barChart.getXAxis().setAxisMaximum(0 + binding.barChart.getBarData().getGroupWidth(groupSpace, barSpace) * groupCount);
        //binding.barChart.groupBars(0, groupSpace, barSpace);
        binding.barChart.getData().setHighlightEnabled(false);
        binding.barChart.invalidate();

        Legend l = binding.barChart.getLegend();
        l.setVerticalAlignment(Legend.LegendVerticalAlignment.TOP);
        l.setHorizontalAlignment(Legend.LegendHorizontalAlignment.RIGHT);
        l.setOrientation(Legend.LegendOrientation.HORIZONTAL);
        l.setDrawInside(true);
        l.setYOffset(20f);
        l.setXOffset(0f);
        l.setYEntrySpace(0f);
        l.setTextSize(8f);


        //X-axis
        XAxis xAxis = binding.barChart.getXAxis();
        xAxis.setGranularity(1f);
        xAxis.setGranularityEnabled(true);
        xAxis.setCenterAxisLabels(true);
        xAxis.setDrawGridLines(false);
        xAxis.setAxisMaximum(5);
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setValueFormatter(new IndexAxisValueFormatter(xVals));
        //Y-axis
        binding.barChart.getAxisRight().setEnabled(false);
        YAxis leftAxis = binding.barChart.getAxisLeft();
        leftAxis.setValueFormatter(new LargeValueFormatter());
        leftAxis.setDrawGridLines(true);
        leftAxis.setSpaceTop(35f);
        leftAxis.setAxisMinimum(0f);

        binding.barChart.invalidate();
    }



    @NonNull
    @Override
    public AnalyticsChartPresenter createPresenter() {
        return new AnalyticsChartPresenter();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
