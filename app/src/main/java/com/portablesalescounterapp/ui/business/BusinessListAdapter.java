package com.portablesalescounterapp.ui.business;

import android.content.Context;
import android.databinding.DataBindingUtil;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.portablesalescounterapp.R;
import com.portablesalescounterapp.app.Endpoints;
import com.portablesalescounterapp.databinding.ItemBusinessBinding;
import com.portablesalescounterapp.model.data.Business;

import java.util.ArrayList;
import java.util.List;


public class BusinessListAdapter extends RecyclerView.Adapter<BusinessListAdapter.ViewHolder> {
    private final Context context;
    private final BusinessListView view;
    private List<Business> categoryList;


    public BusinessListAdapter(Context context, BusinessListView view) {
        this.context = context;
        this.view = view;

        categoryList = new ArrayList<>();
    }

    public void setDiscountList(List<Business> categoryList) {
        this.categoryList = categoryList;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        ItemBusinessBinding itemEmployeeBinding = DataBindingUtil.inflate(
                LayoutInflater.from(parent.getContext()),
                R.layout.item_business,
                parent,
                false);



        return new ViewHolder(itemEmployeeBinding);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.itemEmergencyBinding.setBusiness(categoryList.get(position));
        holder.itemEmergencyBinding.setView(view);

        String imageURL = Endpoints.URL_IMAGE + categoryList.get(position).getBusinessId();
        Glide.with(context)
                .load(imageURL)
                .diskCacheStrategy(DiskCacheStrategy.NONE)
                .centerCrop()
                .error(R.drawable.sample_event)
                .into(holder.itemEmergencyBinding.eventImage);
        Log.d("TAG", imageURL);

    }

    @Override
    public int getItemCount() {
        return categoryList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private final ItemBusinessBinding itemEmergencyBinding;

        public ViewHolder(ItemBusinessBinding itemEmergencyBinding) {
            super(itemEmergencyBinding.getRoot());
            this.itemEmergencyBinding = itemEmergencyBinding;
        }
    }
}
