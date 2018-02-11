package com.portablesalescounterapp.ui.inventory.monitor;

import com.hannesdorfmann.mosby.mvp.MvpView;
import com.portablesalescounterapp.model.data.Products;


public interface MonitorListView extends MvpView {

    void stopRefresh();

    void showAlert(String message);

    void OnRestockEdit(Products employee);

    void OnRemoveEdit(Products employee);

    void startLoading();

    void stopLoading();

    void onRefreshDelete();

    void dismiss();


}
