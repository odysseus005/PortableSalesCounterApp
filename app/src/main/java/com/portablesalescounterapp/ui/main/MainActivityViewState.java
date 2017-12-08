package com.portablesalescounterapp.ui.main;

import android.os.Bundle;
import android.support.annotation.NonNull;

import com.hannesdorfmann.mosby.mvp.viewstate.RestorableViewState;

/**
 * @author pocholomia
 * @since 05/10/2016
 */

class MainActivityViewState implements RestorableViewState<MainActivityView> {
    @Override
    public void saveInstanceState(@NonNull Bundle out) {

    }

    @Override
    public RestorableViewState<MainActivityView> restoreInstanceState(Bundle in) {
        return this;
    }

    @Override
    public void apply(MainActivityView view, boolean retained) {

    }
}
