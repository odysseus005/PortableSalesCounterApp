package com.portablesalescounterapp.ui.register;

import android.os.Bundle;
import android.support.annotation.NonNull;

import com.hannesdorfmann.mosby.mvp.viewstate.RestorableViewState;


public class RegisterViewState implements RestorableViewState<RegisterView> {

    @Override
    public void saveInstanceState(@NonNull Bundle out) {

    }

    @Override
    public RestorableViewState<RegisterView> restoreInstanceState(Bundle in) {

        return this;
    }

    @Override
    public void apply(RegisterView view, boolean retained) {

    }


}
