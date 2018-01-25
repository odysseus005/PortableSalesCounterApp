package com.portablesalescounterapp.ui.inventory.monitor;

import android.content.Context;
import android.databinding.DataBindingUtil;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bumptech.glide.Glide;
import com.portablesalescounterapp.R;
import com.portablesalescounterapp.app.Endpoints;
import com.portablesalescounterapp.databinding.ItemMonitorBinding;
import com.portablesalescounterapp.model.data.Products;

import java.util.ArrayList;
import java.util.List;


public class MonitorListAdapter extends RecyclerView.Adapter<MonitorListAdapter.ViewHolder> {
    private final Context context;
    private final MonitorListView view;
    private List<Products> employeeList;
    private String userEmail;

    public MonitorListAdapter(Context context, MonitorListView view, String userEmail) {
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
        ItemMonitorBinding itemEmployeeBinding = DataBindingUtil.inflate(
                LayoutInflater.from(parent.getContext()),
                R.layout.item_monitor,
                parent,
                false);
        return new ViewHolder(itemEmployeeBinding);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {

        if(employeeList.get(position).getProductStatus().equalsIgnoreCase("D")) {

            holder.itemEmergencyBinding.setView(null);
            }else
        {

            holder.itemEmergencyBinding.setProduct(employeeList.get(position));
            holder.itemEmergencyBinding.setView(view);
            if (employeeList.get(position).getProductCode().equalsIgnoreCase("E"))
                holder.itemEmergencyBinding.prodCode.setText(" pcs.");
            else
                holder.itemEmergencyBinding.prodCode.setText(" kg");
            //String imageURL = Endpoints.IMAGE_URL.replace(Endpoints.IMG_HOLDER, employeeList.get(position).getImage());

        }

    }

    @Override
    public int getItemCount() {
        return employeeList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private final ItemMonitorBinding  itemEmergencyBinding;

        public ViewHolder(ItemMonitorBinding  itemEmergencyBinding) {
            super(itemEmergencyBinding.getRoot());
            this.itemEmergencyBinding = itemEmergencyBinding;
        }
    }
}
