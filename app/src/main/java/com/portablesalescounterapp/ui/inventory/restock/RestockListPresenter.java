package com.portablesalescounterapp.ui.inventory.restock;

import com.hannesdorfmann.mosby.mvp.MvpBasePresenter;
import com.portablesalescounterapp.app.App;
import com.portablesalescounterapp.app.Endpoints;
import com.portablesalescounterapp.model.data.Products;
import com.portablesalescounterapp.model.data.Restock;
import com.portablesalescounterapp.model.data.User;

import java.util.List;

import io.realm.Realm;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


@SuppressWarnings("ConstantConditions")
public class RestockListPresenter extends MvpBasePresenter<RestockListView> {


    private Realm realm;
    private User user;

    public void onStart() {
        realm = Realm.getDefaultInstance();
        user = App.getUser();
    }

    public void load(String bussinessId) {
        App.getInstance().getApiInterface().getRestock(Endpoints.ALL_RESTOCK_PRODUCT,bussinessId)
                .enqueue(new Callback<List<Restock>>() {
                    @Override
                    public void onResponse(Call<List<Restock>> call, final Response<List<Restock>> response) {
                        if (isViewAttached()) {
                            getView().stopRefresh();
                        }
                        if (response.isSuccessful()) {
                            final Realm realm = Realm.getDefaultInstance();
                            realm.executeTransactionAsync(new Realm.Transaction() {
                                @Override
                                public void execute(Realm realm) {
                                    realm.delete(Restock.class);
                                    realm.copyToRealmOrUpdate(response.body());
                                }
                            }, new Realm.Transaction.OnSuccess() {
                                @Override
                                public void onSuccess() {
                                    realm.close();
                                }
                            }, new Realm.Transaction.OnError() {
                                @Override
                                public void onError(Throwable error) {
                                    realm.close();
                                    error.printStackTrace();
                                    if (isViewAttached())
                                        getView().showAlert("Unable to Process Transaction!");
                                }
                            });
                        } else {
                            if (isViewAttached())
                                getView().showAlert("Unable to Process Transaction!");
                        }
                    }

                    @Override
                    public void onFailure(Call<List<Restock>> call, Throwable t) {
                        t.printStackTrace();
                        if (isViewAttached()) {
                            getView().stopRefresh();
                            getView().showAlert("Can't Connect to the Server");
                        }
                    }
                });
    }












    public void onStop() {
        realm.close();
    }

}
