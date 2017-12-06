package com.portablesalescounterapp.ui.login;

import android.os.Bundle;
import android.support.annotation.NonNull;

import com.hannesdorfmann.mosby.mvp.viewstate.RestorableViewState;
import com.portablesalescounterapp.app.Constants;


public class LoginViewState implements RestorableViewState<LoginView> {
    private String email;
    private String password;

    @Override
    public void saveInstanceState(@NonNull Bundle out) {
        out.putString(Constants.EMAIL, email);
        out.putString(Constants.PASSWORD, password);
    }

    @Override
    public RestorableViewState<LoginView> restoreInstanceState(Bundle in) {
        email = in.getString(Constants.EMAIL, "");
        password = in.getString(Constants.PASSWORD, "");
        return this;
    }

    @Override
    public void apply(LoginView view, boolean retained) {
        view.setEditTextValue(email, password);
    }

    public void setUsername(String email) {
        this.email = email;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
