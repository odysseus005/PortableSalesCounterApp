package com.portablesalescounterapp.ui.reports;

import com.hannesdorfmann.mosby.mvp.MvpView;


public interface ReportsView extends MvpView {


    void onSaleReport();

    void onTopSalesReport();

    void onTopProductReport();

    void onDssReport();

}
