package com.portablesalescounterapp.ui.item.discount;

import android.content.Context;
import android.databinding.DataBindingUtil;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import com.portablesalescounterapp.R;
import com.portablesalescounterapp.app.Endpoints;
import com.portablesalescounterapp.databinding.ItemDiscountBinding;
import com.portablesalescounterapp.model.data.Discount;


import java.util.ArrayList;
import java.util.List;


public class DiscountListAdapter extends RecyclerView.Adapter<DiscountListAdapter.ViewHolder> {
    private final Context context;
    private final DiscountListView view;
    private List<Discount> categoryList;
    private String userEmail;

    public DiscountListAdapter(Context context, DiscountListView view, String userEmail) {
        this.context = context;
        this.view = view;
        this.userEmail = userEmail;
        categoryList = new ArrayList<>();
    }

    public void setDiscountList(List<Discount> categoryList) {
        this.categoryList = categoryList;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        ItemDiscountBinding itemEmployeeBinding = DataBindingUtil.inflate(
                LayoutInflater.from(parent.getContext()),
                R.layout.item_discount,
                parent,
                false);
        return new ViewHolder(itemEmployeeBinding);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.itemEmergencyBinding.setDiscount(categoryList.get(position));
        holder.itemEmergencyBinding.setView(view);

    }

    @Override
    public int getItemCount() {
        return categoryList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private final ItemDiscountBinding itemEmergencyBinding;

        public ViewHolder(ItemDiscountBinding itemEmergencyBinding) {
            super(itemEmergencyBinding.getRoot());
            this.itemEmergencyBinding = itemEmergencyBinding;
        }
    }
}
