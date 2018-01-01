package com.portablesalescounterapp.ui.item;

import android.app.ProgressDialog;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.MenuItem;
import android.widget.Toast;

import com.hannesdorfmann.mosby.mvp.MvpActivity;
import com.portablesalescounterapp.R;
import com.portablesalescounterapp.databinding.ActivityItemBinding;
import com.portablesalescounterapp.ui.item.category.CategoryListActivity;
import com.portablesalescounterapp.ui.item.discount.DiscountListActivity;


public class ItemActivity extends MvpActivity<ItemView, ItemPresenter> implements ItemView {

    private ActivityItemBinding binding;
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_item);


        binding = DataBindingUtil.setContentView(this, R.layout.activity_item);
        binding.setView(getMvpView());
        setSupportActionBar(binding.toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("List of Items");


    }

    @Override
    public void onDiscountClicked() {
        startActivity(new Intent(this, DiscountListActivity.class));

    }

    @Override
    public void onProductClicked() {
       // startActivity(new Intent(this, DiscountListActivity.class));

    }

    @Override
    public void onCategoryClicked() {
        startActivity(new Intent(this, CategoryListActivity.class));

    }


    @NonNull
    @Override
    public ItemPresenter createPresenter() {
        return new ItemPresenter();
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
