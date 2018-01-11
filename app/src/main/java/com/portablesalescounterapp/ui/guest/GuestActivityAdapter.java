package com.portablesalescounterapp.ui.guest;

import android.content.Context;
import android.databinding.DataBindingUtil;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import com.bumptech.glide.Glide;
import com.portablesalescounterapp.R;
import com.portablesalescounterapp.app.Endpoints;
import com.portablesalescounterapp.databinding.ItemProductsGuestBinding;

import com.portablesalescounterapp.model.data.Products;

import java.util.ArrayList;
import java.util.List;


public class GuestActivityAdapter extends RecyclerView.Adapter<GuestActivityAdapter.ViewHolder> {
    private final Context context;
    private final GuestActivityView view;
    private List<Products> employeeList;

    public GuestActivityAdapter(Context context, GuestActivityView view) {
        this.context = context;
        this.view = view;
        employeeList = new ArrayList<>();
    }

    public void setProductList(List<Products> employeeList) {
        this.employeeList = employeeList;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        ItemProductsGuestBinding itemEmployeeBinding = DataBindingUtil.inflate(
                LayoutInflater.from(parent.getContext()),
                R.layout.item_products_guest,
                parent,
                false);
        return new ViewHolder(itemEmployeeBinding);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {

            holder.itemEmergencyBinding.setProduct(employeeList.get(position));
            holder.itemEmergencyBinding.setView(view);
             String imageURL = Endpoints.URL_IMAGE + employeeList.get(position).getProductName();
            Glide.with(context)
                    .load(imageURL)
                    .skipMemoryCache(true)
                    .centerCrop()
                    .error(R.drawable.placeholder)
                    .into(holder.itemEmergencyBinding.productPicture);
            Log.d("TAG", imageURL);

    }

    @Override
    public int getItemCount() {
        return employeeList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private final ItemProductsGuestBinding  itemEmergencyBinding;

        public ViewHolder(ItemProductsGuestBinding  itemEmergencyBinding) {
            super(itemEmergencyBinding.getRoot());
            this.itemEmergencyBinding = itemEmergencyBinding;
        }
    }
}
