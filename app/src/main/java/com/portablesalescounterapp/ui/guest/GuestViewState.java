package com.portablesalescounterapp.ui.guest;

import android.os.Bundle;
import android.support.annotation.NonNull;

import com.hannesdorfmann.mosby.mvp.viewstate.RestorableViewState;

/**
 * @author pocholomia
 * @since 05/10/2016
 */

class GuestViewState implements RestorableViewState<GuestActivityView> {
    @Override
    public void saveInstanceState(@NonNull Bundle out) {

    }

    @Override
    public RestorableViewState<GuestActivityView> restoreInstanceState(Bundle in) {
        return this;
    }

    @Override
    public void apply(GuestActivityView view, boolean retained) {

    }
}
