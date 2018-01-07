package com.portablesalescounterapp.ui.main;

import android.content.Context;
import android.databinding.DataBindingUtil;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import com.bumptech.glide.Glide;
import com.portablesalescounterapp.R;
import com.portablesalescounterapp.app.Endpoints;
import com.portablesalescounterapp.databinding.ItemCartBinding;
import com.portablesalescounterapp.model.data.Products;

import java.util.ArrayList;
import java.util.List;


public class CartMainActivityAdapter extends RecyclerView.Adapter<CartMainActivityAdapter.ViewHolder> {
    private final Context context;
    private final MainActivityView view;
    private List<Products> employeeList;
    private ArrayList<String> prodIdcart;
    private ArrayList<String> prodNamecart;
    private ArrayList<String> prodQuantitycart;
    private ArrayList<String> prodPricecart;

    public CartMainActivityAdapter(Context context, MainActivityView view) {
        this.context = context;
        this.view = view;
        employeeList = new ArrayList<>();
    }

    public void setProductList(List<Products> employeeList,ArrayList<String> prodIdcart,ArrayList<String> prodNamecart,ArrayList<String> prodQuantitycart,ArrayList<String> prodPricecart) {
        this.employeeList = employeeList;
        this.prodIdcart = prodIdcart;
        this.prodNamecart = prodNamecart;
        this.prodQuantitycart = prodQuantitycart;
        this.prodPricecart = prodPricecart;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        ItemCartBinding itemEmployeeBinding = DataBindingUtil.inflate(
                LayoutInflater.from(parent.getContext()),
                R.layout.item_cart,
                parent,
                false);
        return new ViewHolder(itemEmployeeBinding);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {

            holder.itemEmergencyBinding.setProduct(employeeList.get(position));
            holder.itemEmergencyBinding.setView(view);

        holder.itemEmergencyBinding.cartPrice.setText(prodPricecart.get(position));
        holder.itemEmergencyBinding.cartProdName.setText(prodNamecart.get(position));
        holder.itemEmergencyBinding.cartQuantity.setText(prodQuantitycart.get(position));

    }

    @Override
    public int getItemCount() {
        return prodIdcart.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private final ItemCartBinding  itemEmergencyBinding;

        public ViewHolder(ItemCartBinding  itemEmergencyBinding) {
            super(itemEmergencyBinding.getRoot());
            this.itemEmergencyBinding = itemEmergencyBinding;
        }
    }
}
