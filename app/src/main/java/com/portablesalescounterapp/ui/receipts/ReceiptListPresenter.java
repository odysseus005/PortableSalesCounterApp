package com.portablesalescounterapp.ui.receipts;

import android.util.Log;

import com.hannesdorfmann.mosby.mvp.MvpBasePresenter;
import com.portablesalescounterapp.R;
import com.portablesalescounterapp.app.App;
import com.portablesalescounterapp.app.Constants;
import com.portablesalescounterapp.app.Endpoints;
import com.portablesalescounterapp.model.data.Restock;
import com.portablesalescounterapp.model.data.Transaction;
import com.portablesalescounterapp.model.data.User;
import com.portablesalescounterapp.model.response.ResultResponse;

import java.util.ArrayList;
import java.util.List;

import io.realm.Realm;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


@SuppressWarnings("ConstantConditions")
public class ReceiptListPresenter extends MvpBasePresenter<ReceiptListView> {


    private Realm realm;
    private User user;

    public void onStart() {
        realm = Realm.getDefaultInstance();
        user = App.getUser();
    }

    public void load(String bussinessId) {
        App.getInstance().getApiInterface().getTransaction(Endpoints.ALL_TRANSACTION,bussinessId)
                .enqueue(new Callback<List<Transaction>>() {
                    @Override
                    public void onResponse(Call<List<Transaction>> call, final Response<List<Transaction>> response) {
                        if (isViewAttached()) {
                            getView().stopRefresh();
                        }
                        if (response.isSuccessful()) {
                            final Realm realm = Realm.getDefaultInstance();
                            realm.executeTransactionAsync(new Realm.Transaction() {
                                @Override
                                public void execute(Realm realm) {
                                    realm.delete(Transaction.class);
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
                    public void onFailure(Call<List<Transaction>> call, Throwable t) {
                        t.printStackTrace();
                        if (isViewAttached()) {
                            getView().stopRefresh();
                            getView().showAlert(t.getLocalizedMessage());
                        }
                    }
                });
    }





    public void refundTransaction(String bussinessId,String transactid,String transIdlist,String transQuantitylist) {
        App.getInstance().getApiInterface().refundTransaction(Endpoints.REFUND_TRANSACTION,transactid,transIdlist,transQuantitylist,bussinessId)
                .enqueue(new Callback<ResultResponse>() {
                    @Override
                    public void onResponse(Call<ResultResponse> call, final Response<ResultResponse> response) {
                        if (isViewAttached()) {
                            getView().stopRefresh();
                        }
                        if (response.isSuccessful()) {
                            switch (response.body().getResult()) {
                                case Constants.SUCCESS:
                                    getView().showAlert("Refund Successful!");
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
                            getView().showAlert(t.getLocalizedMessage());
                        }
                    }
                });
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





    public void onStop() {
        realm.close();
    }

}
