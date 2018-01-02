package com.portablesalescounterapp.ui.item.product;

import android.content.Context;
import android.databinding.DataBindingUtil;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import com.bumptech.glide.Glide;
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

       // if(!(employeeList.get(position).getPosition().equalsIgnoreCase("owner"))) {
            holder.itemEmergencyBinding.setProduct(employeeList.get(position));
            holder.itemEmergencyBinding.setView(view);
            //String imageURL = Endpoints.IMAGE_URL.replace(Endpoints.IMG_HOLDER, employeeList.get(position).getImage());
            String imageURL = Endpoints.URL_IMAGE + userEmail;
            Glide.with(context)
                    .load(imageURL)
                    .skipMemoryCache(true)
                    .centerCrop()
                    .error(R.drawable.default_user)
                    .into(holder.itemEmergencyBinding.imageProfile);
            Log.d("TAG", imageURL);
      //  }
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
