package com.portablesalescounterapp.ui.manageqr;

import com.hannesdorfmann.mosby.mvp.MvpBasePresenter;
import com.portablesalescounterapp.app.App;
import com.portablesalescounterapp.app.Endpoints;
import com.portablesalescounterapp.model.data.Category;
import com.portablesalescounterapp.model.data.Products;
import com.portablesalescounterapp.model.data.User;
import com.portablesalescounterapp.model.response.ResultResponse;

import java.io.File;
import java.util.List;

import io.realm.Realm;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


@SuppressWarnings("ConstantConditions")
public class ProductQrListPresenter extends MvpBasePresenter<ProductQrListView> {


    private Realm realm;
    private User user;

    public void onStart() {
        realm = Realm.getDefaultInstance();
        user = App.getUser();
    }

    public void load(String bussinessId) {
        App.getInstance().getApiInterface().getProduct(Endpoints.ALL_PRODUCT,bussinessId)
                .enqueue(new Callback<List<Products>>() {
                    @Override
                    public void onResponse(Call<List<Products>> call, final Response<List<Products>> response) {
                        if (isViewAttached()) {
                            getView().stopRefresh();
                        }
                        if (response.isSuccessful()) {
                            final Realm realm = Realm.getDefaultInstance();
                            realm.executeTransactionAsync(new Realm.Transaction() {
                                @Override
                                public void execute(Realm realm) {
                                    realm.delete(Products.class);
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
                                        getView().showAlert(error.getLocalizedMessage());
                                }
                            });
                        } else {
                            if (isViewAttached())
                                getView().showAlert(response.errorBody().toString());
                        }
                    }

                    @Override
                    public void onFailure(Call<List<Products>> call, Throwable t) {
                        t.printStackTrace();
                        if (isViewAttached()) {
                            getView().stopRefresh();
                            getView().showAlert(t.getLocalizedMessage());
                        }
                    }
                });
    }


    public void updateContact(final String product_id,
                              String product_qr,
                              final String tag,
                              final String product_name,
                              String bid) {
        if (false) {
            getView().showAlert("Fill-up all fields");
        }
         else {
            getView().startLoading();
            App.getInstance().getApiInterface().updateQr(Endpoints.QR_PRODUCT,product_id,"",product_qr,bid)
                    .enqueue(new Callback<List<Products>>() {
                        @Override
                        public void onResponse(Call<List<Products>> call, final Response<List<Products>> response) {
                            getView().stopLoading();
                            if (response.isSuccessful()) {
                                realm.executeTransaction(new Realm.Transaction() {
                                    @Override
                                    public void execute(Realm realm) {
                                        realm.copyToRealmOrUpdate(response.body());

                                        if(tag.equalsIgnoreCase("C")) {
                                            getView().showToast("Qr Code Successfully Created for " + product_name);
                                            getView().dismiss();
                                        }
                                        else if (tag.equalsIgnoreCase("D"))
                                            getView().showToast("Qr Code Successfully Removed for "+product_name);

                                    }
                                });
                            } else {
                                getView().showAlert("Oops something went wrong");
                            }
                        }

                        @Override
                        public void onFailure(Call<List<Products>> call, Throwable t) {

                            getView().stopLoading();
                            getView().showAlert("Error Connecting to Server");
                        }
                    });
        }
    }







    public void onStop() {
        realm.close();
    }

}
