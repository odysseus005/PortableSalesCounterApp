package com.portablesalescounterapp.ui.receipts;

import android.content.Context;
import android.databinding.DataBindingUtil;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import com.portablesalescounterapp.R;
import com.portablesalescounterapp.databinding.ItemReceiptBinding;
import com.portablesalescounterapp.model.data.Transaction;


import java.util.ArrayList;
import java.util.List;


public class ReceiptListAdapter extends RecyclerView.Adapter<ReceiptListAdapter.ViewHolder> {
    private final Context context;
    private final ReceiptListView view;
    private List<Transaction> employeeList;
    private String userEmail;

    public ReceiptListAdapter(Context context, ReceiptListView view, String userEmail) {
        this.context = context;
        this.view = view;
        this.userEmail = userEmail;
        employeeList = new ArrayList<>();
    }

    public void setProductList(List<Transaction> employeeList) {
        this.employeeList = employeeList;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        ItemReceiptBinding itemEmployeeBinding = DataBindingUtil.inflate(
                LayoutInflater.from(parent.getContext()),
                R.layout.item_restock,
                parent,
                false);
        return new ViewHolder(itemEmployeeBinding);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {

            holder.itemEmergencyBinding.setTransaction(employeeList.get(position));
            holder.itemEmergencyBinding.setView(view);

            //
        // holder.itemEmergencyBinding.restockRemaining.setText((Integer.parseInt(employeeList.get(position).getProductTotal()) - Integer.parseInt(employeeList.get(position).getProductRestock()))+"");

    }

    @Override
    public int getItemCount() {
        return employeeList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private final ItemReceiptBinding  itemEmergencyBinding;

        public ViewHolder(ItemReceiptBinding  itemEmergencyBinding) {
            super(itemEmergencyBinding.getRoot());
            this.itemEmergencyBinding = itemEmergencyBinding;
        }
    }
}
