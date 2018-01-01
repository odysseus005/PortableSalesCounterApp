package com.portablesalescounterapp.ui.item.discount;

import com.hannesdorfmann.mosby.mvp.MvpBasePresenter;
import com.portablesalescounterapp.app.App;
import com.portablesalescounterapp.app.Endpoints;
import com.portablesalescounterapp.model.data.Discount;
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
public class DiscountListPresenter extends MvpBasePresenter<DiscountListView> {


    private Realm realm;
    private User user;

    public void onStart() {
        realm = Realm.getDefaultInstance();
        user = App.getUser();
    }

    public void load(String businessId) {
        App.getInstance().getApiInterface().getDiscount(Endpoints.ALL_DISCOUNT,businessId)
                .enqueue(new Callback<List<Discount>>() {
                    @Override
                    public void onResponse(Call<List<Discount>> call, final Response<List<Discount>> response) {
                        if (isViewAttached()) {
                            getView().stopRefresh();
                        }
                        if (response.isSuccessful()) {
                            final Realm realm = Realm.getDefaultInstance();
                            realm.executeTransactionAsync(new Realm.Transaction() {
                                @Override
                                public void execute(Realm realm) {
                                    realm.delete(Discount.class);
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
                    public void onFailure(Call<List<Discount>> call, Throwable t) {
                        t.printStackTrace();
                        if (isViewAttached()) {
                            getView().stopRefresh();
                            getView().showAlert(t.getLocalizedMessage());
                        }
                    }
                });
    }


    public void updateContact(String discount_id,
                              String name,
                              String value,
                                String code,
                                String bid) {
        if ( name.equals("") || value.equals("")||code.equals("")) {
            getView().showAlert("Fill-up all fields");
        }
         else {
            getView().startLoading();
            App.getInstance().getApiInterface().updateDiscount(Endpoints.UPDATE_DISCOUNT,discount_id, name,code ,value,bid)
                    .enqueue(new Callback<List<Discount>>() {
                        @Override
                        public void onResponse(Call<List<Discount>> call, final Response<List<Discount>> response) {
                            getView().stopLoading();
                            if (response.isSuccessful()) {
                                realm.executeTransaction(new Realm.Transaction() {
                                    @Override
                                    public void execute(Realm realm) {
                                        realm.copyToRealmOrUpdate(response.body());
                                        getView().dismiss();
                                    }
                                });
                            } else {
                                getView().showAlert("Oops something went wrong");
                            }
                        }

                        @Override
                        public void onFailure(Call<List<Discount>> call, Throwable t) {

                            getView().stopLoading();
                            getView().showAlert("Error Connecting to Server");
                        }
                    });
        }
    }


    public void addContact(String business_id,
                           String name,
                           String value,
                           String code) {
        if ( name.equals("") || value.equals("")||code.equals("")) {
            getView().showAlert("Fill-up all fields");
        }else {
            getView().startLoading();
            App.getInstance().getApiInterface().addDiscount(Endpoints.ADD_DISCOUNT,name, code,value, business_id)
                    .enqueue(new Callback<List<Discount>>() {
                        @Override
                        public void onResponse(Call<List<Discount>> call, final Response<List<Discount>> response) {
                            getView().stopLoading();
                            if (response.isSuccessful()) {
                                realm.executeTransaction(new Realm.Transaction() {
                                    @Override
                                    public void execute(Realm realm) {
                                        realm.copyToRealmOrUpdate(response.body());
                                        getView().dismiss();

                                    }
                                });
                            } else {
                                getView().showAlert("Oops something went wrong");
                            }
                        }

                        @Override
                        public void onFailure(Call<List<Discount>> call, Throwable t) {

                            getView().stopLoading();
                            getView().showAlert("Error Connecting to Server");
                        }
                    });
        }
    }


    public void deleteContact(String discountId,String businessId) {

            getView().startLoading();
            App.getInstance().getApiInterface().deleteDiscount(Endpoints.DELETE_DISCOUNT,discountId,businessId)
                    .enqueue(new Callback<List<Discount>>() {
                        @Override
                        public void onResponse(Call<List<Discount>> call, final Response<List<Discount>> response) {
                            getView().stopLoading();
                            if (response.isSuccessful()) {
                                realm.executeTransaction(new Realm.Transaction() {
                                    @Override
                                    public void execute(Realm realm) {

                                        getView().onRefreshDelete();
                                    }
                                });
                            } else {
                                getView().showAlert("Oops something went wrong");
                            }
                        }

                        @Override
                        public void onFailure(Call<List<Discount>> call, Throwable t) {

                            getView().stopLoading();
                            getView().showAlert("Error Connecting to Server");
                        }
                    });

    }




    public void onStop() {
        realm.close();
    }

}
