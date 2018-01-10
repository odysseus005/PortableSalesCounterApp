package com.portablesalescounterapp.ui.reports.salesReport;

import android.app.ProgressDialog;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.MenuItem;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;
import com.github.mikephil.charting.formatter.LargeValueFormatter;
import com.hannesdorfmann.mosby.mvp.MvpActivity;
import com.portablesalescounterapp.R;
import com.portablesalescounterapp.databinding.ActivitySalechartBinding;

import java.util.ArrayList;


public class SaleChartActivity extends MvpActivity<SaleChartView, SaleChartPresenter> implements SaleChartView {

    private ActivitySalechartBinding binding;
    private ProgressDialog progressDialog;
    float barWidth;
    float barSpace;
    float groupSpace;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_salechart);


        binding = DataBindingUtil.setContentView(this, R.layout.activity_salechart);
        binding.setView(getMvpView());
        setSupportActionBar(binding.toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("SaleChart");

        barWidth = 0.3f;
        barSpace = 0f;
        groupSpace = 0.6f;

         groupSpace = 0.06f;
         barSpace = 0.02f; // x2 dataset
         barWidth = 0.45f; // x2 dataset
        // (0.02 + 0.45) * 2 + 0.06 = 1.00 -> interval per "group"

        //Description desc = new Description();
        //desc.setText("Sample Description and Lorem ipsum dolom");
        binding.barChart.setDescription(null);
        binding.barChart.setPinchZoom(true);
        binding.barChart.setScaleEnabled(true);
        binding.barChart.setDrawBarShadow(false);
        binding.barChart.setDrawGridBackground(false);


        int groupCount = 6;

        ArrayList xVals = new ArrayList();

        xVals.add("Jan");
        xVals.add("Feb");
        xVals.add("Mar");
        xVals.add("Apr");
        xVals.add("May");
        xVals.add("Jun");

        ArrayList yVals1 = new ArrayList();
        ArrayList yVals2 = new ArrayList();
        ArrayList yVals3 = new ArrayList();
        ArrayList yVals4 = new ArrayList();
        ArrayList yVals5 = new ArrayList();


        yVals1.add(new BarEntry(1, (float) 1));
        yVals2.add(new BarEntry(1, (float) 2));
        yVals3.add(new BarEntry(1, (float) 3));
        yVals4.add(new BarEntry(1, (float) 4));
        yVals5.add(new BarEntry(1, (float) 5));
        yVals1.add(new BarEntry(2, (float) 6));
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
        yVals5.add(new BarEntry(6, (float) 20));

        BarDataSet set1, set2,set3,set4,set5;
        set1 = new BarDataSet(yVals1, "Sample Product 1");
        set1.setColor(Color.RED);
        set2 = new BarDataSet(yVals2, "Sample Product 2");
        set2.setColor(Color.BLUE);
        set3 = new BarDataSet(yVals3, "Sample Product 3");
        set3.setColor(Color.YELLOW);
        set4 = new BarDataSet(yVals4, "Sample Product 4");
        set4.setColor(Color.CYAN);
        set5 = new BarDataSet(yVals5, "Sample Product 5");
        set5.setColor(Color.GREEN);


        BarData data = new BarData(set1, set2,set3,set4,set5);
        data.setValueFormatter(new LargeValueFormatter());
        binding.barChart.setData(data);
        binding.barChart.getBarData().setBarWidth(barWidth);
        binding.barChart.getXAxis().setAxisMinimum(0);
        binding.barChart.getXAxis().setAxisMaximum(0 + binding.barChart.getBarData().getGroupWidth(groupSpace, barSpace) * groupCount);
        binding.barChart.groupBars(0, groupSpace, barSpace);
        binding.barChart.getData().setHighlightEnabled(false);
        //binding.barChart.invalidate();

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
      /*  XAxis xAxis = binding.barChart.getXAxis();
        xAxis.setGranularity(1f);
        xAxis.setGranularityEnabled(true);
        xAxis.setCenterAxisLabels(true);
        xAxis.setDrawGridLines(false);
        xAxis.setAxisMaximum(6);
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setValueFormatter(new IndexAxisValueFormatter(xVals));
            //Y-axis
        binding.barChart.getAxisRight().setEnabled(false);
        YAxis leftAxis = binding.barChart.getAxisLeft();
        leftAxis.setValueFormatter(new LargeValueFormatter());
        leftAxis.setDrawGridLines(true);
        leftAxis.setSpaceTop(35f);
        leftAxis.setAxisMinimum(0f);*/

        binding.barChart.invalidate();

    }



    @NonNull
    @Override
    public SaleChartPresenter createPresenter() {
        return new SaleChartPresenter();
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
