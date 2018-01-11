package com.portablesalescounterapp.ui.business;

import com.hannesdorfmann.mosby.mvp.MvpView;
import com.portablesalescounterapp.model.data.Business;
import com.portablesalescounterapp.model.data.Discount;


public interface BusinessListView extends MvpView {

    void stopRefresh();

    void showAlert(String message);

   void OnButtonClick(Business buss);


    void startLoading();

    void stopLoading();


    void dismiss();


}
