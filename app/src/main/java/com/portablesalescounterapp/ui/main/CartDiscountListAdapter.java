package com.portablesalescounterapp.ui.main;

import android.content.Context;
import android.databinding.DataBindingUtil;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import com.portablesalescounterapp.R;
import com.portablesalescounterapp.databinding.ItemCartDiscountBinding;
import com.portablesalescounterapp.model.data.Discount;

import java.util.ArrayList;
import java.util.List;


public class CartDiscountListAdapter extends RecyclerView.Adapter<CartDiscountListAdapter.ViewHolder> {
    private final Context context;
    private final MainActivityView view;
    private List<Discount> categoryList;

    public CartDiscountListAdapter(Context context, MainActivityView view) {
        this.context = context;
        this.view = view;
        categoryList = new ArrayList<>();
    }

    public void setDiscountList(List<Discount> categoryList) {
        this.categoryList = categoryList;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        ItemCartDiscountBinding itemEmployeeBinding = DataBindingUtil.inflate(
                LayoutInflater.from(parent.getContext()),
                R.layout.item_cart_discount,
                parent,
                false);
        return new ViewHolder(itemEmployeeBinding);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.itemEmergencyBinding.setDiscount(categoryList.get(position));
        holder.itemEmergencyBinding.setView(view);


        if(categoryList.get(position).getDiscountCode().equalsIgnoreCase("P"))
            holder.itemEmergencyBinding.disCode.setText("%");
        else
            holder.itemEmergencyBinding.disCode.setText("php");

    }

    @Override
    public int getItemCount() {
        return categoryList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private final ItemCartDiscountBinding itemEmergencyBinding;

        public ViewHolder(ItemCartDiscountBinding itemEmergencyBinding) {
            super(itemEmergencyBinding.getRoot());
            this.itemEmergencyBinding = itemEmergencyBinding;
        }
    }
}
