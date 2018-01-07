package com.portablesalescounterapp.ui.manageqr;

import android.os.Bundle;
import android.support.annotation.NonNull;

import com.hannesdorfmann.mosby.mvp.viewstate.RestorableViewState;

class ProductQrViewState implements RestorableViewState<ProductQrListView> {
    @Override
    public void saveInstanceState(@NonNull Bundle out) {

    }

    @Override
    public RestorableViewState<ProductQrListView> restoreInstanceState(Bundle in) {
        return this;
    }

    @Override
    public void apply(ProductQrListView view, boolean retained) {

    }
}
