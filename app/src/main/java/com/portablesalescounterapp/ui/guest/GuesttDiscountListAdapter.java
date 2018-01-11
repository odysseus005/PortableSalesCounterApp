package com.portablesalescounterapp.ui.guest;

import android.content.Context;
import android.databinding.DataBindingUtil;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import com.portablesalescounterapp.R;
import com.portablesalescounterapp.databinding.ItemCartDiscountBinding;
import com.portablesalescounterapp.databinding.ItemCartGuestDiscountBinding;
import com.portablesalescounterapp.model.data.Discount;


import java.util.ArrayList;
import java.util.List;


public class GuesttDiscountListAdapter extends RecyclerView.Adapter<GuesttDiscountListAdapter.ViewHolder> {
    private final Context context;
    private final GuestActivityView view;
    private List<Discount> categoryList;

    public GuesttDiscountListAdapter(Context context, GuestActivityView view) {
        this.context = context;
        this.view = view;
        categoryList = new ArrayList<>();
    }

    public void setDiscountList(List<Discount> categoryList) {
        this.categoryList = categoryList;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        ItemCartGuestDiscountBinding itemEmployeeBinding = DataBindingUtil.inflate(
                LayoutInflater.from(parent.getContext()),
                R.layout.item_cart_guest_discount,
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
        private final ItemCartGuestDiscountBinding itemEmergencyBinding;

        public ViewHolder(ItemCartGuestDiscountBinding itemEmergencyBinding) {
            super(itemEmergencyBinding.getRoot());
            this.itemEmergencyBinding = itemEmergencyBinding;
        }
    }
}
