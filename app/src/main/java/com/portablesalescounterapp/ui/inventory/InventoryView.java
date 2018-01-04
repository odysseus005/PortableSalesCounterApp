package com.portablesalescounterapp.ui.inventory;

import com.hannesdorfmann.mosby.mvp.MvpView;


public interface InventoryView extends MvpView {


    void onRestockClicked();

    void onMonitorClicked();



}
