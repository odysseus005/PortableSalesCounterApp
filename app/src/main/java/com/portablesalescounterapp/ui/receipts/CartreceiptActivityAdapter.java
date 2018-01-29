package com.portablesalescounterapp.ui.receipts;

import android.content.Context;
import android.databinding.DataBindingUtil;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import com.portablesalescounterapp.R;
import com.portablesalescounterapp.databinding.ItemReceiptCartBinding;


import java.util.ArrayList;


public class CartreceiptActivityAdapter extends RecyclerView.Adapter<CartreceiptActivityAdapter.ViewHolder> {
    private final Context context;
    private final ReceiptListView view;
    private ArrayList<String> prodIdcart;
    private ArrayList<String> prodNamecart;
    private ArrayList<String> prodQuantitycart;
    private ArrayList<String> prodPricecart;

    public CartreceiptActivityAdapter(Context context, ReceiptListView view) {
        this.context = context;
        this.view = view;

    }

    public void setProductList(ArrayList<String> prodIdcart,ArrayList<String> prodNamecart,ArrayList<String> prodQuantitycart,ArrayList<String> prodPricecart) {

        this.prodIdcart = prodIdcart;
        this.prodNamecart = prodNamecart;
        this.prodQuantitycart = prodQuantitycart;
        this.prodPricecart = prodPricecart;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        ItemReceiptCartBinding itemEmployeeBinding = DataBindingUtil.inflate(
                LayoutInflater.from(parent.getContext()),
                R.layout.item_receipt_cart,
                parent,
                false);
        return new ViewHolder(itemEmployeeBinding);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {

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
        private final ItemReceiptCartBinding  itemEmergencyBinding;

        public ViewHolder(ItemReceiptCartBinding  itemEmergencyBinding) {
            super(itemEmergencyBinding.getRoot());
            this.itemEmergencyBinding = itemEmergencyBinding;
        }
    }
}
