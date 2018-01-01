package com.portablesalescounterapp.ui.manageuser;

import com.hannesdorfmann.mosby.mvp.MvpView;
import com.portablesalescounterapp.model.data.Employee;


public interface EmployeeListView extends MvpView {

    void stopRefresh();

    void showAlert(String message);

   void OnButtonDelete(Employee employee);

    void OnButtonEdit(Employee employee);

    void startLoading();

    void stopLoading();

    void onRefreshDelete();

    void dismiss();


}
