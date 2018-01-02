package com.portablesalescounterapp.ui.item.product;

import android.os.Bundle;
import android.support.annotation.NonNull;

import com.hannesdorfmann.mosby.mvp.viewstate.RestorableViewState;

class ProductViewState implements RestorableViewState<ProductListView> {
    @Override
    public void saveInstanceState(@NonNull Bundle out) {

    }

    @Override
    public RestorableViewState<ProductListView> restoreInstanceState(Bundle in) {
        return this;
    }

    @Override
    public void apply(ProductListView view, boolean retained) {

    }
}
