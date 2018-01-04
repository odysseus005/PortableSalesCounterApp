package com.portablesalescounterapp.ui.inventory;

import android.app.ProgressDialog;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.MenuItem;

import com.hannesdorfmann.mosby.mvp.MvpActivity;
import com.portablesalescounterapp.R;
import com.portablesalescounterapp.databinding.ActivityInventoryBinding;
import com.portablesalescounterapp.ui.inventory.monitor.MonitorListActivity;
import com.portablesalescounterapp.ui.inventory.restock.RestockListActivity;


public class InventoryActivity extends MvpActivity<InventoryView, InventoryPresenter> implements InventoryView {

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
    public InventoryPresenter createPresenter() {
        return new InventoryPresenter();
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
