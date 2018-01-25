package com.portablesalescounterapp.ui.manageqr;

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

import com.portablesalescounterapp.databinding.ItemProductsQrBinding;
import com.portablesalescounterapp.model.data.Products;

import java.util.ArrayList;
import java.util.List;


public class ProductQrListAdapter extends RecyclerView.Adapter<ProductQrListAdapter.ViewHolder> {
    private final Context context;
    private final ProductQrListView view;
    private List<Products> employeeList;
    private String userEmail;

    public ProductQrListAdapter(Context context, ProductQrListView view, String userEmail) {
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
        ItemProductsQrBinding itemEmployeeBinding = DataBindingUtil.inflate(
                LayoutInflater.from(parent.getContext()),
                R.layout.item_products_qr,
                parent,
                false);
        return new ViewHolder(itemEmployeeBinding);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {


        if(employeeList.get(position).getProductStatus().equalsIgnoreCase("A")) {
            holder.itemEmergencyBinding.setProduct(employeeList.get(position));
            holder.itemEmergencyBinding.setView(view);


            if ((employeeList.get(position).getProductBar().equalsIgnoreCase("") && employeeList.get(position).getProductQr().equalsIgnoreCase(""))) {
                holder.itemEmergencyBinding.add.setVisibility(View.VISIBLE);
                holder.itemEmergencyBinding.delete.setVisibility(View.GONE);
                holder.itemEmergencyBinding.productQRcode.setVisibility(View.GONE);
                holder.itemEmergencyBinding.productBarcode.setVisibility(View.GONE);
            } else if (employeeList.get(position).getProductBar().equalsIgnoreCase("0") && employeeList.get(position).getProductQr().equalsIgnoreCase("")) {
                holder.itemEmergencyBinding.add.setVisibility(View.VISIBLE);
                holder.itemEmergencyBinding.delete.setVisibility(View.GONE);
                holder.itemEmergencyBinding.productQRcode.setVisibility(View.GONE);
                holder.itemEmergencyBinding.productBarcode.setVisibility(View.GONE);
            } else if (!(employeeList.get(position).getProductBar().isEmpty())) {
                holder.itemEmergencyBinding.productBarcode.setText("Product Bar Code : " + employeeList.get(position).getProductBar());
                holder.itemEmergencyBinding.add.setVisibility(View.GONE);
                holder.itemEmergencyBinding.delete.setVisibility(View.GONE);
                holder.itemEmergencyBinding.productQRcode.setVisibility(View.GONE);
            } else {
                holder.itemEmergencyBinding.add.setVisibility(View.GONE);
                holder.itemEmergencyBinding.delete.setVisibility(View.VISIBLE);
            }


            String imageURL = Endpoints.URL_IMAGE + employeeList.get(position).getProductName();
            Glide.with(context)
                    .load(imageURL)
                    .diskCacheStrategy(DiskCacheStrategy.NONE)
                    .centerCrop()
                    .error(R.drawable.placeholder)
                    .into(holder.itemEmergencyBinding.imageProfile);
            Log.d("TAG", imageURL);
        }else
        {
            holder.itemEmergencyBinding.setView(null);
        }

    }

    @Override
    public int getItemCount() {
        return employeeList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private final ItemProductsQrBinding  itemEmergencyBinding;

        public ViewHolder(ItemProductsQrBinding  itemEmergencyBinding) {
            super(itemEmergencyBinding.getRoot());
            this.itemEmergencyBinding = itemEmergencyBinding;
        }
    }
}
