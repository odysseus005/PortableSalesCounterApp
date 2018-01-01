package com.portablesalescounterapp.ui.item.discount;

import android.os.Bundle;
import android.support.annotation.NonNull;

import com.hannesdorfmann.mosby.mvp.viewstate.RestorableViewState;

class DiscountViewState implements RestorableViewState<DiscountListView> {
    @Override
    public void saveInstanceState(@NonNull Bundle out) {

    }

    @Override
    public RestorableViewState<DiscountListView> restoreInstanceState(Bundle in) {
        return this;
    }

    @Override
    public void apply(DiscountListView view, boolean retained) {

    }
}
