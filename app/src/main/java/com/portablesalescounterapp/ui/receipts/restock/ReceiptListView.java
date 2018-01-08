package com.portablesalescounterapp.ui.receipts.restock;

import com.hannesdorfmann.mosby.mvp.MvpView;
import com.portablesalescounterapp.model.data.Products;


public interface ReceiptListView extends MvpView {

    void stopRefresh();

    void showAlert(String message);


    void startLoading();

    void stopLoading();


    void dismiss();


}
