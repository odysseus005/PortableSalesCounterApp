package com.portablesalescounterapp.ui.reports;

import android.app.ProgressDialog;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.MenuItem;

import com.hannesdorfmann.mosby.mvp.MvpActivity;
import com.portablesalescounterapp.R;



public class ReportsActivity extends MvpActivity<ReportsView, ReportsPresenter> implements ReportsView {

    private ActivityInventoryBinding binding;
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_item);


        binding = DataBindingUtil.setContentView(this, R.layout.activity_inventory);
        binding.setView(getMvpView());
        setSupportActionBar(binding.toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Items");


    }

    @Override
    public void onMonitorClicked() {
        startActivity(new Intent(this, MonitorListActivity.class));

    }

    @Override
    public void onRestockClicked() {
       startActivity(new Intent(this, RestockListActivity.class));

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
