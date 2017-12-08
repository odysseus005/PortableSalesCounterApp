package com.portablesalescounterapp.ui.main;

import com.hannesdorfmann.mosby.mvp.MvpView;



public interface MainActivityView extends MvpView {



    void stopRefresh();

    void showError(String message);




}
