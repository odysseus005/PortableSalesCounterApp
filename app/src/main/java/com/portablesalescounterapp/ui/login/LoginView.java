package com.portablesalescounterapp.ui.login;


import com.hannesdorfmann.mosby.mvp.MvpView;
import com.portablesalescounterapp.model.data.User;


public interface LoginView extends MvpView {

    void onLoginButtonClicked();

    void onRegisterButtonClicked();

    void showAlert(String message);

    void setEditTextValue(String username, String password);

    void startLoading();

    void stopLoading();

    void onLoginSuccess();

    void onForgotPasswordButtonClicked();


}
