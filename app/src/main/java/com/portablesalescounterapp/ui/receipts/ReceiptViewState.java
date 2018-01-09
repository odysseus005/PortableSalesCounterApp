package com.portablesalescounterapp.ui.receipts;

import android.os.Bundle;
import android.support.annotation.NonNull;

import com.hannesdorfmann.mosby.mvp.viewstate.RestorableViewState;

class ReceiptViewState implements RestorableViewState<ReceiptListView> {
    @Override
    public void saveInstanceState(@NonNull Bundle out) {

    }

    @Override
    public RestorableViewState<ReceiptListView> restoreInstanceState(Bundle in) {
        return this;
    }

    @Override
    public void apply(ReceiptListView view, boolean retained) {

    }
}
