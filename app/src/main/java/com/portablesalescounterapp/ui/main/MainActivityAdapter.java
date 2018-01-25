package com.portablesalescounterapp.ui.main;

import android.content.Context;
import android.databinding.DataBindingUtil;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.portablesalescounterapp.R;
import com.portablesalescounterapp.app.Endpoints;
import com.portablesalescounterapp.databinding.ItemProductsMainBinding;
import com.portablesalescounterapp.model.data.Products;
import com.portablesalescounterapp.util.DateTimeUtils;

import java.util.ArrayList;
import java.util.List;


public class MainActivityAdapter extends RecyclerView.Adapter<MainActivityAdapter.ViewHolder> {
    private final Context context;
    private final MainActivityView view;
    private List<Products> employeeList;

    public MainActivityAdapter(Context context, MainActivityView view) {
        this.context = context;
        this.view = view;
        employeeList = new ArrayList<>();
    }

    public void setProductList(List<Products> employeeList) {
        this.employeeList = employeeList;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        ItemProductsMainBinding itemEmployeeBinding = DataBindingUtil.inflate(
                LayoutInflater.from(parent.getContext()),
                R.layout.item_products_main,
                parent,
                false);
        return new ViewHolder(itemEmployeeBinding);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {



        if(employeeList.get(position).getProductStatus().equalsIgnoreCase("D")) {
            holder.itemEmergencyBinding.setView(null);
        }
        else
        {


            holder.itemEmergencyBinding.setProduct(employeeList.get(position));
            holder.itemEmergencyBinding.setView(view);
            holder.itemEmergencyBinding.mainPrice.setText("Php: " +DateTimeUtils.parseDoubleTL(Double.parseDouble(employeeList.get(position).getProductPrice())));
            String imageURL = Endpoints.URL_IMAGE + employeeList.get(position).getProductId() + "prod";
            Glide.with(context)
                    .load(imageURL)
                    .diskCacheStrategy(DiskCacheStrategy.NONE)
                    .centerCrop()
                    .error(R.drawable.placeholder)
                    .into(holder.itemEmergencyBinding.productPicture);
            Log.d("TAG", imageURL);
        }

    }

    @Override
    public int getItemCount() {
        return employeeList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private final ItemProductsMainBinding  itemEmergencyBinding;

        public ViewHolder(ItemProductsMainBinding  itemEmergencyBinding) {
            super(itemEmergencyBinding.getRoot());
            this.itemEmergencyBinding = itemEmergencyBinding;
        }
    }
}
