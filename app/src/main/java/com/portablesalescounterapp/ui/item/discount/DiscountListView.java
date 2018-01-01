package com.portablesalescounterapp.ui.item.discount;

import com.hannesdorfmann.mosby.mvp.MvpView;
import com.portablesalescounterapp.model.data.Discount;


public interface DiscountListView extends MvpView {

    void stopRefresh();

    void showAlert(String message);

   void OnButtonDelete(Discount disc);

    void OnButtonEdit(Discount disc);

    void startLoading();

    void stopLoading();

    void onRefreshDelete();

    void dismiss();


}
