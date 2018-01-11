package com.portablesalescounterapp.ui.guest;

import android.content.Context;
import android.databinding.DataBindingUtil;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import com.portablesalescounterapp.R;
import com.portablesalescounterapp.databinding.ItemCartBinding;
import com.portablesalescounterapp.databinding.ItemCartGuestBinding;
import com.portablesalescounterapp.model.data.Products;

import java.util.ArrayList;
import java.util.List;


public class CartGuestActivityAdapter extends RecyclerView.Adapter<CartGuestActivityAdapter.ViewHolder> {
    private final Context context;
    private final GuestActivityView view;
    private List<Products> employeeList;
    private ArrayList<String> prodIdcart;
    private ArrayList<String> prodNamecart;
    private ArrayList<String> prodQuantitycart;
    private ArrayList<String> prodPricecart;

    public CartGuestActivityAdapter(Context context, GuestActivityView view) {
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
        ItemCartGuestBinding itemEmployeeBinding = DataBindingUtil.inflate(
                LayoutInflater.from(parent.getContext()),
                R.layout.item_cart_guest,
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
        private final ItemCartGuestBinding  itemEmergencyBinding;

        public ViewHolder(ItemCartGuestBinding  itemEmergencyBinding) {
            super(itemEmergencyBinding.getRoot());
            this.itemEmergencyBinding = itemEmergencyBinding;
        }
    }
}
