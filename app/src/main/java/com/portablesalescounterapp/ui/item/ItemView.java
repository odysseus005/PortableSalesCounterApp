package com.portablesalescounterapp.ui.item;

import com.hannesdorfmann.mosby.mvp.MvpView;


public interface ItemView extends MvpView {


    void onProductClicked();

    void onDiscountClicked();

    void onCategoryClicked();



}
