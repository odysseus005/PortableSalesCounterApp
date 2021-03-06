package com.portablesalescounterapp.ui.guest;

import com.hannesdorfmann.mosby.mvp.MvpBasePresenter;
import com.portablesalescounterapp.R;
import com.portablesalescounterapp.app.App;
import com.portablesalescounterapp.app.Constants;
import com.portablesalescounterapp.app.Endpoints;
import com.portablesalescounterapp.model.data.Category;
import com.portablesalescounterapp.model.data.Discount;
import com.portablesalescounterapp.model.data.PreTransaction;
import com.portablesalescounterapp.model.data.Products;
import com.portablesalescounterapp.model.data.Transaction;
import com.portablesalescounterapp.model.data.User;
import com.portablesalescounterapp.model.response.PreTransactionResponse;
import com.portablesalescounterapp.model.response.ResultResponse;
import com.portablesalescounterapp.model.response.TransactionResponse;

import java.util.ArrayList;
import java.util.List;

import io.realm.Realm;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


@SuppressWarnings("ConstantConditions")
public class GuestActivityPresenter extends MvpBasePresenter<GuestActivityView> {


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
                                        getView().showAlert("Unable to Connect to Server");
                                }
                            });
                        } else {
                            if (isViewAttached())
                                getView().showAlert("Unable to Connect to Server");
                        }
                    }

                    @Override
                    public void onFailure(Call<List<Products>> call, Throwable t) {
                        t.printStackTrace();
                        if (isViewAttached()) {
                            getView().stopRefresh();
                            getView().showAlert("Unable to Connect to Server");
                        }
                    }
                });
    }

    Products getProductQr(String id){
        return realm.where(Products.class)
                .equalTo(Constants.PRODUCT_BAR, id)
                .or()
                .equalTo(Constants.PRODUCT_QR, id)
                .findFirst();
    }


    public void addTransaction(String transPrice,
                              String transCode,
                              String transDiscount,
                              String idList,
                              String nameList,
                              String quanList,
                              String priceList,
                              String did,
                              String dname,
                              String user_id,
                              String username,
                              String date,
                              String bid) {
        if ( transPrice.equals("") || transCode.equals("") ||
                idList.equals("")||date.equals("")) {
            getView().showAlert("Error Transaction!");
        }
        else {
            getView().startLoading();
            App.getInstance().getApiInterface().preaddTransaction(Endpoints.PREADD_TRANSACTION,transPrice,
                    transCode,
                    transDiscount,
                    idList,
                    nameList,
                   quanList,
                    priceList,
                    did,
                   dname,
                   user_id,
                    username,
                    date,
                    bid)
                    .enqueue(new Callback<PreTransactionResponse>() {
                        @Override
                        public void onResponse(Call<PreTransactionResponse> call, final Response<PreTransactionResponse> response) {
                            getView().stopLoading();
                            if (response.isSuccessful()) {
                                switch (response.body().getResult()) {
                                    case Constants.SUCCESS:
                                        PreTransaction trans = response.body().getPreTransaction();
                                        getView().onTransactionSuccess(trans);
                                        break;

                                    default:
                                        getView().showAlert(String.valueOf(R.string.cantConnect));
                                        break;
                                }
                            } else {
                                getView().showAlert("Oops something went wrong");
                            }
                        }

                        @Override
                        public void onFailure(Call<PreTransactionResponse> call, Throwable t) {

                            getView().stopLoading();
                            getView().showAlert("Error Connecting to Server");
                        }
                    });
        }
    }



    public void getDiscount(String businessId) {
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
                                        getView().showAlert("Unable to Connect to Server");
                                }
                            });
                        } else {
                            if (isViewAttached())
                                getView().showAlert("Unable to Connect to Server");
                        }
                    }

                    @Override
                    public void onFailure(Call<List<Discount>> call, Throwable t) {
                        t.printStackTrace();
                        if (isViewAttached()) {
                            getView().stopRefresh();
                            getView().showAlert("Unable to Connect to Server");
                        }
                    }
                });
    }



    public void getCategory(String businessId) {
        App.getInstance().getApiInterface().getCategory(Endpoints.ALL_CATEGORY,businessId)
                .enqueue(new Callback<List<Category>>() {
                    @Override
                    public void onResponse(Call<List<Category>> call, final Response<List<Category>> response) {
                        if (isViewAttached()) {
                            getView().stopRefresh();
                        }
                        if (response.isSuccessful()) {
                            final Realm realm = Realm.getDefaultInstance();
                            realm.executeTransactionAsync(new Realm.Transaction() {
                                @Override
                                public void execute(Realm realm) {
                                    realm.delete(Category.class);
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
                                        getView().showAlert("Unable to Connect to Server");
                                }
                            });
                        } else {
                            if (isViewAttached())
                                getView().showAlert("Unable to Connect to Server");
                        }
                    }

                    @Override
                    public void onFailure(Call<List<Category>> call, Throwable t) {
                        t.printStackTrace();
                        if (isViewAttached()) {
                            getView().stopRefresh();
                            getView().showAlert("Unable to Connect to Server");
                        }
                    }
                });
    }



    public String listToString(ArrayList<String> listcrt)
    {
        String finalOutput="";

        for(int a=0;a<listcrt.size();a++)
        {
            if((a+1)==listcrt.size())
                finalOutput += (listcrt.get(a));
            else
                finalOutput += (listcrt.get(a)+":");

        }


        return  finalOutput;

    }





    public void onStop() {
        realm.close();
    }


}
