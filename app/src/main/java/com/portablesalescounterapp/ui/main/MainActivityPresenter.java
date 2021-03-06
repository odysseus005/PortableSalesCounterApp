package com.portablesalescounterapp.ui.main;

import android.util.Log;

import com.hannesdorfmann.mosby.mvp.MvpBasePresenter;
import com.portablesalescounterapp.R;
import com.portablesalescounterapp.app.App;
import com.portablesalescounterapp.app.Constants;
import com.portablesalescounterapp.app.Endpoints;
import com.portablesalescounterapp.model.data.Business;
import com.portablesalescounterapp.model.data.Category;
import com.portablesalescounterapp.model.data.Discount;
import com.portablesalescounterapp.model.data.PreTransaction;
import com.portablesalescounterapp.model.data.Products;
import com.portablesalescounterapp.model.data.Restock;
import com.portablesalescounterapp.model.data.Transaction;
import com.portablesalescounterapp.model.data.User;
import com.portablesalescounterapp.model.response.ResultResponse;
import com.portablesalescounterapp.model.response.TransactionResponse;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import io.realm.Realm;
import io.realm.Sort;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;



@SuppressWarnings("ConstantConditions")
public class MainActivityPresenter extends MvpBasePresenter<MainActivityView> {


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
                                getView().showAlert("Error Connecting to Server");
                        }
                    }

                    @Override
                    public void onFailure(Call<List<Products>> call, Throwable t) {
                        t.printStackTrace();
                        if (isViewAttached()) {
                            getView().stopRefresh();
                            getView().showAlert("Unable to Connect!");
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

    PreTransaction getTransactionQr(int id){
        return realm.where(PreTransaction.class)
                .equalTo("pretransactionId", id)
                .findFirst();
    }


    public ArrayList<String> StringtoList(String strList)
    {
        ArrayList<String> finalOutput = new ArrayList<>();
        // Log.d(">>>",strList);
        String[] items = strList.split(":");
        //Log.d(">>>",items+"");
        for (String item : items)
        {
            //  Log.d(">>>",item);
            finalOutput.add(item);

        }

        return finalOutput;
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
            App.getInstance().getApiInterface().addTransaction(Endpoints.ADD_TRANSACTION,transPrice,
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
                    .enqueue(new Callback<TransactionResponse>() {
                        @Override
                        public void onResponse(Call<TransactionResponse> call, final Response<TransactionResponse> response) {
                            getView().stopLoading();
                            if (response.isSuccessful()) {
                                switch (response.body().getResult()) {
                                    case Constants.SUCCESS:

                                       /* final Realm realm = Realm.getDefaultInstance();
                                        realm.executeTransactionAsync(new Realm.Transaction() {
                                            @Override
                                            public void execute(Realm realm) {
                                                Transaction business = response.body().getBusiness();
                                                realm.copyToRealmOrUpdate(user);
                                                realm.copyToRealmOrUpdate(business);

                                            }
                                        }, new Realm.Transaction.OnSuccess() {
                                            @Override
                                            public void onSuccess() {
                                                realm.close();
                                                getView().onLoginSuccess();
                                            }
                                        }, new Realm.Transaction.OnError() {
                                            @Override
                                            public void onError(Throwable error) {
                                                realm.close();
                                                Log.e(TAG, "onError: Unable to save USER", error);
                                                getView().showAlert("Error Saving API Response");
                                            }
                                        });*/
                                        Transaction trans = response.body().getTransaction();
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
                        public void onFailure(Call<TransactionResponse> call, Throwable t) {

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
                                        getView().showAlert(error.getLocalizedMessage());
                                }
                            });
                        } else {
                            if (isViewAttached())
                                getView().showAlert("Error Connecting to Server");
                        }
                    }

                    @Override
                    public void onFailure(Call<List<Discount>> call, Throwable t) {
                        t.printStackTrace();
                        if (isViewAttached()) {
                            getView().stopRefresh();
                            getView().showAlert("Unable to Connect!");
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
                                        getView().showAlert(error.getLocalizedMessage());
                                }
                            });
                        } else {
                            if (isViewAttached())
                                getView().showAlert("Error Connecting to Server");
                        }
                    }

                    @Override
                    public void onFailure(Call<List<Category>> call, Throwable t) {
                        t.printStackTrace();
                        if (isViewAttached()) {
                            getView().stopRefresh();
                            getView().showAlert("Unable to Connect!");
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

    public void loadTransaction(String bussinessId) {
        App.getInstance().getApiInterface().pregetTransaction(Endpoints.PREALL_TRANSACTION,bussinessId)
                .enqueue(new Callback<List<PreTransaction>>() {
                    @Override
                    public void onResponse(Call<List<PreTransaction>> call, final Response<List<PreTransaction>> response) {
                        if (isViewAttached()) {
                            getView().stopRefresh();
                        }
                        if (response.isSuccessful()) {
                            final Realm realm = Realm.getDefaultInstance();
                            realm.executeTransactionAsync(new Realm.Transaction() {
                                @Override
                                public void execute(Realm realm) {
                                    realm.delete(PreTransaction.class);
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
                                getView().showAlert("Error Connecting to Server");
                        }
                    }

                    @Override
                    public void onFailure(Call<List<PreTransaction>> call, Throwable t) {
                        t.printStackTrace();
                        if (isViewAttached()) {
                            getView().stopRefresh();
                            getView().showAlert("Unable to Connect!");
                        }
                    }
                });
    }


  /* public void updateTransaction(String bussinessId,String productid,String userID,String name,String date,String idList,
                                  String nameList,
                                  String quanList,
                                  String priceList) {
        App.getInstance().getApiInterface().updateTransaction(Endpoints.UPDATE_TRANSACTION,productid,userID,name,date,idList,
                nameList,
                quanList,
                priceList,bussinessId)
                .enqueue(new Callback<ResultResponse>() {
                    @Override
                    public void onResponse(Call<ResultResponse> call, final Response<ResultResponse> response) {
                        if (isViewAttached()) {
                            getView().stopRefresh();
                        }
                        if (response.isSuccessful()) {
                            switch (response.body().getResult()) {
                                case Constants.SUCCESS:
                                    getView().showError("Payment Confirmation Successful!");
                                    getView().onSelfSuccess();
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
                    public void onFailure(Call<ResultResponse> call, Throwable t) {
                        t.printStackTrace();
                        if (isViewAttached()) {
                            getView().stopRefresh();
                            getView().showAlert("Unable to Connect!");
                        }
                    }
                });
    }
*/





    public void onStop() {
        realm.close();
    }


}
