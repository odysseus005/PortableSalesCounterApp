package com.portablesalescounterapp.ui.guest;

import com.hannesdorfmann.mosby.mvp.MvpView;
import com.portablesalescounterapp.model.data.Discount;
import com.portablesalescounterapp.model.data.Products;


public interface GuestActivityView extends MvpView {



    void stopRefresh();

    void showError(String message);

    void showAlert(String message);

    void startLoading();

    void stopLoading();

    void OnButtonAddtoCart();

    void onItemClick(Products product);

    void onItemRemove(Products product);

    void onAddDiscount(Discount discount);

    void onTransactionSuccess();






}
