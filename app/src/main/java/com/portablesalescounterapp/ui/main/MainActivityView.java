package com.portablesalescounterapp.ui.main;

import com.hannesdorfmann.mosby.mvp.MvpView;
import com.portablesalescounterapp.model.data.Discount;
import com.portablesalescounterapp.model.data.Products;
import com.portablesalescounterapp.model.data.Transaction;


public interface MainActivityView extends MvpView {



    void stopRefresh();

    void showError(String message);

    void showAlert(String message);

    void startLoading();

    void stopLoading();

    void OnButtonAddtoCart();

    void onItemClick(Products product);

    void onItemRemove(Products product);

    void onAddDiscount(Discount discount);

    void onTransactionSuccess(Transaction transact);

    void onSelfSuccess();






}
