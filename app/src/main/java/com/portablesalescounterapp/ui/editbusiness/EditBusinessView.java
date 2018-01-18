package com.portablesalescounterapp.ui.editbusiness;

import com.hannesdorfmann.mosby.mvp.MvpView;


public interface EditBusinessView extends MvpView{


    void showAlert(String message);

    void onEdit();

    void startLoading();

    void stopLoading();

    void finishAct();


    void finish();

}
