package com.portablesalescounterapp.ui.forgot;

import com.hannesdorfmann.mosby.mvp.MvpView;


public interface ForgotView extends MvpView {
    //start of view
    void OnButtonSubmit();

    void startLoading(String s);

    void stopLoading();

    void showAlert(String s);

    void onEmailExist();


}
