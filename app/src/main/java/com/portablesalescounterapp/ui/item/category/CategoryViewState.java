package com.portablesalescounterapp.ui.item.category;

import android.os.Bundle;
import android.support.annotation.NonNull;

import com.hannesdorfmann.mosby.mvp.viewstate.RestorableViewState;

class CategoryViewState implements RestorableViewState<CategoryListView> {
    @Override
    public void saveInstanceState(@NonNull Bundle out) {

    }

    @Override
    public RestorableViewState<CategoryListView> restoreInstanceState(Bundle in) {
        return this;
    }

    @Override
    public void apply(CategoryListView view, boolean retained) {

    }
}
