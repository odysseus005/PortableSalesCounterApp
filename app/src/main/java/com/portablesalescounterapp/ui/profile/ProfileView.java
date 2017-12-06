package com.portablesalescounterapp.ui.profile;

import com.hannesdorfmann.mosby.mvp.MvpView;


public interface ProfileView extends MvpView {

    void onChangePasswordClicked();

    void showProgress();

    void stopProgress();

    void showAlert(String message);

    void onPasswordChanged();
}
