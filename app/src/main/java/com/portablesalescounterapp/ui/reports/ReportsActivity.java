package com.portablesalescounterapp.ui.reports;

import android.app.ProgressDialog;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.MenuItem;

import com.hannesdorfmann.mosby.mvp.MvpActivity;
import com.portablesalescounterapp.R;
import com.portablesalescounterapp.databinding.ActivityInventoryBinding;
import com.portablesalescounterapp.databinding.ActivityReportsBinding;
import com.portablesalescounterapp.ui.reports.analytics.AnalyticsChartActivity;
import com.portablesalescounterapp.ui.reports.salesReport.SaleChartActivity;


public class ReportsActivity extends MvpActivity<ReportsView, ReportsPresenter> implements ReportsView {

    private ActivityReportsBinding binding;
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reports);


        binding = DataBindingUtil.setContentView(this, R.layout.activity_reports);
        binding.setView(getMvpView());
        setSupportActionBar(binding.toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Items");


    }



    @Override
    public void onSaleReport() {
       startActivity(new Intent(this, SaleChartActivity.class));
    }

    @Override
    public void onTopSalesReport() {
        // startActivity(new Intent(this, RestockListActivity.class));
    }

    @Override
    public void onTopProductReport() {
        // startActivity(new Intent(this, RestockListActivity.class));
    }

    @Override
    public void onDssReport() {
        // startActivity(new Intent(this, RestockListActivity.class));
    }


    @Override
    public void onSRClicked() {
         startActivity(new Intent(this, SaleChartActivity.class));
    }

    @Override
    public void onARClicked() {
         startActivity(new Intent(this, AnalyticsChartActivity.class));
    }

    @Override
    public void onDSSClicked() {
        // startActivity(new Intent(this, RestockListActivity.class));
    }




    @NonNull
    @Override
    public ReportsPresenter createPresenter() {
        return new ReportsPresenter();
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
