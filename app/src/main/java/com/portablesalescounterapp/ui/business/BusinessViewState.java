package com.portablesalescounterapp.ui.business;

import android.os.Bundle;
import android.support.annotation.NonNull;

import com.hannesdorfmann.mosby.mvp.viewstate.RestorableViewState;

class BusinessViewState implements RestorableViewState<BusinessListView> {
    @Override
    public void saveInstanceState(@NonNull Bundle out) {

    }

    @Override
    public RestorableViewState<BusinessListView> restoreInstanceState(Bundle in) {
        return this;
    }

    @Override
    public void apply(BusinessListView view, boolean retained) {

    }
}
