package com.portablesalescounterapp.ui.item.product;

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
import com.portablesalescounterapp.databinding.ItemProductsBinding;
import com.portablesalescounterapp.model.data.Products;


import java.util.ArrayList;
import java.util.List;


public class ProductListAdapter extends RecyclerView.Adapter<ProductListAdapter.ViewHolder> {
    private final Context context;
    private final ProductListView view;
    private List<Products> employeeList;
    private String userEmail;

    public ProductListAdapter(Context context, ProductListView view, String userEmail) {
        this.context = context;
        this.view = view;
        this.userEmail = userEmail;
        employeeList = new ArrayList<>();
    }

    public void setProductList(List<Products> employeeList) {
        this.employeeList = employeeList;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        ItemProductsBinding itemEmployeeBinding = DataBindingUtil.inflate(
                LayoutInflater.from(parent.getContext()),
                R.layout.item_products,
                parent,
                false);
        return new ViewHolder(itemEmployeeBinding);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {

        if(!(employeeList.get(position).getProductStatus().equalsIgnoreCase("A"))) {
           holder.itemEmergencyBinding.send.setVisibility(View.GONE);
            holder.itemEmergencyBinding.cancel.setVisibility(View.GONE);
        }
        else
            holder.itemEmergencyBinding.arch.setVisibility(View.GONE);

        holder.itemEmergencyBinding.setProduct(employeeList.get(position));
        holder.itemEmergencyBinding.setView(view);
        //String imageURL = Endpoints.URL_IMAGE + employeeList.get(position).getProductName();
        String imageURL = Endpoints.URL_IMAGE + employeeList.get(position).getProductId() + "prod";
        Glide.with(context)
                .load(imageURL)
                .diskCacheStrategy(DiskCacheStrategy.NONE)
                .centerCrop()
                .error(R.drawable.placeholder)
                .into(holder.itemEmergencyBinding.imageProfile);
        Log.d("TAG", imageURL);

    }

    @Override
    public int getItemCount() {
        return employeeList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private final ItemProductsBinding  itemEmergencyBinding;

        public ViewHolder(ItemProductsBinding  itemEmergencyBinding) {
            super(itemEmergencyBinding.getRoot());
            this.itemEmergencyBinding = itemEmergencyBinding;
        }
    }
}
