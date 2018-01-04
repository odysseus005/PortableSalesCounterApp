package com.portablesalescounterapp.ui.inventory.monitor;

import android.os.Bundle;
import android.support.annotation.NonNull;

import com.hannesdorfmann.mosby.mvp.viewstate.RestorableViewState;

class MonitorViewState implements RestorableViewState<MonitorListView> {
    @Override
    public void saveInstanceState(@NonNull Bundle out) {

    }

    @Override
    public RestorableViewState<MonitorListView> restoreInstanceState(Bundle in) {
        return this;
    }

    @Override
    public void apply(MonitorListView view, boolean retained) {

    }
}
