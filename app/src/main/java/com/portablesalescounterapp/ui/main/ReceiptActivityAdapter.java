package com.portablesalescounterapp.ui.main;

import android.content.Context;
import android.databinding.DataBindingUtil;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import com.portablesalescounterapp.R;
import com.portablesalescounterapp.databinding.ItemReceiptCartBinding;
import com.portablesalescounterapp.databinding.ItemReceiptSendBinding;
import com.portablesalescounterapp.ui.main.MainActivityView;

import java.util.ArrayList;


public class ReceiptActivityAdapter extends RecyclerView.Adapter<ReceiptActivityAdapter.ViewHolder> {
    private final Context context;
    private final MainActivityView view;
    private ArrayList<String> prodIdcart;
    private ArrayList<String> prodNamecart;
    private ArrayList<String> prodQuantitycart;
    private ArrayList<String> prodPricecart;

    public ReceiptActivityAdapter(Context context, MainActivityView view) {
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
        ItemReceiptSendBinding itemEmployeeBinding = DataBindingUtil.inflate(
                LayoutInflater.from(parent.getContext()),
                R.layout.item_receipt_send,
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
        private final  ItemReceiptSendBinding  itemEmergencyBinding;

        public ViewHolder( ItemReceiptSendBinding  itemEmergencyBinding) {
            super(itemEmergencyBinding.getRoot());
            this.itemEmergencyBinding = itemEmergencyBinding;
        }
    }
}
