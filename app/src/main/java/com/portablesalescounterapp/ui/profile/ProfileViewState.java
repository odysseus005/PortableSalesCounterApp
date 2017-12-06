package com.portablesalescounterapp.ui.profile;

import android.os.Bundle;
import android.support.annotation.NonNull;

import com.hannesdorfmann.mosby.mvp.viewstate.RestorableViewState;


public class ProfileViewState implements RestorableViewState<ProfileView> {
    @Override
    public void saveInstanceState(@NonNull Bundle out) {

    }

    @Override
    public RestorableViewState<ProfileView> restoreInstanceState(Bundle in) {
        return this;
    }

    @Override
    public void apply(ProfileView view, boolean retained) {

    }
}
