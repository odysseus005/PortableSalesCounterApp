package com.portablesalescounterapp.ui.item.category;

import com.hannesdorfmann.mosby.mvp.MvpView;
import com.portablesalescounterapp.model.data.Category;


public interface CategoryListView extends MvpView {

    void stopRefresh();

    void showAlert(String message);

   void OnButtonDelete(Category category);

    void OnButtonEdit(Category category);

    void startLoading();

    void stopLoading();

    void onRefreshDelete();

    void dismiss();

    void onEditCategCount(String cnt);


}
