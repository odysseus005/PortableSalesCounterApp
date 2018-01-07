package com.portablesalescounterapp.ui.manageqr;

import com.hannesdorfmann.mosby.mvp.MvpView;
import com.portablesalescounterapp.model.data.Products;


public interface ProductQrListView extends MvpView {

    void stopRefresh();

    void showAlert(String message);

    void showToast(String message);

   void OnButtonAdd(Products employee);

    void OnButtonDelete(Products employee);

    void startLoading();

    void stopLoading();

    void onRefreshDelete();

    void dismiss();


}
