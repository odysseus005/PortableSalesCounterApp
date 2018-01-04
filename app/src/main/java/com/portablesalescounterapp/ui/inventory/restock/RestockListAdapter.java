package com.portablesalescounterapp.ui.inventory.restock;

import android.content.Context;
import android.databinding.DataBindingUtil;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import com.portablesalescounterapp.R;
import com.portablesalescounterapp.databinding.ItemRestockBinding;
import com.portablesalescounterapp.model.data.Restock;


import java.util.ArrayList;
import java.util.List;


public class RestockListAdapter extends RecyclerView.Adapter<RestockListAdapter.ViewHolder> {
    private final Context context;
    private final RestockListView view;
    private List<Restock> employeeList;
    private String userEmail;

    public RestockListAdapter(Context context, RestockListView view, String userEmail) {
        this.context = context;
        this.view = view;
        this.userEmail = userEmail;
        employeeList = new ArrayList<>();
    }

    public void setProductList(List<Restock> employeeList) {
        this.employeeList = employeeList;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        ItemRestockBinding itemEmployeeBinding = DataBindingUtil.inflate(
                LayoutInflater.from(parent.getContext()),
                R.layout.item_restock,
                parent,
                false);
        return new ViewHolder(itemEmployeeBinding);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {

            holder.itemEmergencyBinding.setRestock(employeeList.get(position));
            holder.itemEmergencyBinding.setView(view);

    }

    @Override
    public int getItemCount() {
        return employeeList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private final ItemRestockBinding  itemEmergencyBinding;

        public ViewHolder(ItemRestockBinding  itemEmergencyBinding) {
            super(itemEmergencyBinding.getRoot());
            this.itemEmergencyBinding = itemEmergencyBinding;
        }
    }
}
