package com.portablesalescounterapp.ui.inventory.restock;

import com.hannesdorfmann.mosby.mvp.MvpView;
import com.portablesalescounterapp.model.data.Products;


public interface RestockListView extends MvpView {

    void stopRefresh();

    void showAlert(String message);

    void OnRestockEdit(Products employee);

    void startLoading();

    void stopLoading();

    void onRefreshDelete();

    void dismiss();


}
