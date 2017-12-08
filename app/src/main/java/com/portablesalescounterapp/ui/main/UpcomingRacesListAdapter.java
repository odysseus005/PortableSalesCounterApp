package com.portablesalescounterapp.ui.main;

import android.content.Context;
import android.databinding.DataBindingUtil;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import com.bumptech.glide.Glide;

import java.util.ArrayList;
import java.util.List;

import tip.edu.ph.runrio.R;
import tip.edu.ph.runrio.app.Endpoints;
import tip.edu.ph.runrio.databinding.ItemRaceHomeBinding;
import tip.edu.ph.runrio.model.data.UpcomingRaces;


public class UpcomingRacesListAdapter extends RecyclerView.Adapter<UpcomingRacesListAdapter.ViewHolder> {
    private List<UpcomingRaces> event;
    private final Context context;
    private final MainActivityView view;
    private static final int VIEW_TYPE_DEFAULT = 0;


    public UpcomingRacesListAdapter(Context context, MainActivityView view) {
        this.context = context;
        this.view = view;
        event = new ArrayList<>();
    }


    @Override
    public int getItemViewType(int position) {
        return VIEW_TYPE_DEFAULT;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        ItemRaceHomeBinding itemEventBinding = DataBindingUtil.inflate(
                LayoutInflater.from(parent.getContext()),
                R.layout.item_race_home,
                parent,
                false);
        return new ViewHolder(itemEventBinding);
    }

    @Override
    public void onBindViewHolder(UpcomingRacesListAdapter.ViewHolder holder, int position) {
        holder.itemEventBinding.setRace(event.get(position));
        holder.itemEventBinding.setView(view);
        Glide.with(holder.itemView.getContext())
                .load(Endpoints.EVENT_URL_IMAGE+event.get(position).getRaceImage())
                .centerCrop()
               // .error(R.drawable.sample_event)
                .into(holder.itemEventBinding.upcomingRacesHomeImage);
    }


    public void clear() {
        event.clear();
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return event.size();
    }

    public void setUpcomingRaces(List<UpcomingRaces> event) {
        this.event = event;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private final ItemRaceHomeBinding itemEventBinding;

        public ViewHolder(ItemRaceHomeBinding itemEventBinding) {
            super(itemEventBinding.getRoot());
            this.itemEventBinding = itemEventBinding;
        }
    }
}
