package com.portablesalescounterapp.ui.profile.edit;

import com.hannesdorfmann.mosby.mvp.MvpView;


public interface EditProfileView extends MvpView{


    void showAlert(String message);

    void onEdit();

    void startLoading();

    void stopLoading();

    void onChangePassword();

    void finishAct();

    void onBirthdayClicked();

    void finish();

    void onPasswordChanged();
}
