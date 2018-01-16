package com.portablesalescounterapp.ui.receipts;

import com.hannesdorfmann.mosby.mvp.MvpView;
import com.portablesalescounterapp.model.data.Transaction;

public interface ReceiptListView extends MvpView {

    void stopRefresh();

    void showAlert(String message);

    void startLoading();

    void stopLoading();

    void dismiss();

    void onTransactionClicked(Transaction transact);

    void onButtonRefund(Transaction transact);


}
