package com.portablesalescounterapp.ui.inventory.restock;

import android.os.Bundle;
import android.support.annotation.NonNull;

import com.hannesdorfmann.mosby.mvp.viewstate.RestorableViewState;

class RestockViewState implements RestorableViewState<RestockListView> {
    @Override
    public void saveInstanceState(@NonNull Bundle out) {

    }

    @Override
    public RestorableViewState<RestockListView> restoreInstanceState(Bundle in) {
        return this;
    }

    @Override
    public void apply(RestockListView view, boolean retained) {

    }
}
