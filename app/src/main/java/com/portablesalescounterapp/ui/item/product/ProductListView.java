package com.portablesalescounterapp.ui.item.product;

import com.hannesdorfmann.mosby.mvp.MvpView;
import com.portablesalescounterapp.model.data.Products;


public interface ProductListView extends MvpView {

    void stopRefresh();

    void showAlert(String message);

   void OnButtonDelete(Products employee);

    void OnButtonEdit(Products employee);

    void startLoading();

    void stopLoading();

    void onRefreshDelete();

    void dismiss();


}
