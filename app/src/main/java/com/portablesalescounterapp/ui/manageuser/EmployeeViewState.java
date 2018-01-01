package com.portablesalescounterapp.ui.manageuser;

import android.os.Bundle;
import android.support.annotation.NonNull;

import com.hannesdorfmann.mosby.mvp.viewstate.RestorableViewState;

class EmployeeViewState implements RestorableViewState<EmployeeListView> {
    @Override
    public void saveInstanceState(@NonNull Bundle out) {

    }

    @Override
    public RestorableViewState<EmployeeListView> restoreInstanceState(Bundle in) {
        return this;
    }

    @Override
    public void apply(EmployeeListView view, boolean retained) {

    }
}
