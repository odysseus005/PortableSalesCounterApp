package com.portablesalescounterapp.ui.profile;

import android.util.Log;

import com.hannesdorfmann.mosby.mvp.MvpNullObjectBasePresenter;
import com.portablesalescounterapp.R;
import com.portablesalescounterapp.app.App;
import com.portablesalescounterapp.app.Constants;
import com.portablesalescounterapp.app.Endpoints;
import com.portablesalescounterapp.model.data.Business;
import com.portablesalescounterapp.model.data.User;
import com.portablesalescounterapp.model.response.ResultResponse;


import io.realm.Realm;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class ProfilePresenter extends MvpNullObjectBasePresenter<ProfileView> {
    private static final String TAG = ProfilePresenter.class.getSimpleName();
    User user2;
    void changePassword(String currPass, String newPass, String confirmNewPass) {
        final User user = App.getUser();
        if (currPass.equals(user.getPassword())) {

             if (newPass.equals(confirmNewPass)) {
                getView().showProgress();
                App.getInstance().getApiInterface().changePassword(Endpoints.UPDATEPASS,user.getUserId() + "", newPass).enqueue(new Callback<ResultResponse>() {
                    @Override
                    public void onResponse(Call<ResultResponse> call, final Response<ResultResponse> response) {
                        getView().stopProgress();
                        if (response.isSuccessful()) {
                            if (response.body().getResult().equals(Constants.SUCCESS)) {


                                final Realm realm = Realm.getDefaultInstance();
                                realm.executeTransactionAsync(new Realm.Transaction() {
                                    @Override
                                    public void execute(Realm realm) {
                                        user2 = response.body().getUser();

                                        realm.copyToRealmOrUpdate(user2);


                                    }
                                }, new Realm.Transaction.OnSuccess() {
                                    @Override
                                    public void onSuccess() {
                                        realm.close();
                                        getView().onPasswordChanged();
                                    }
                                }, new Realm.Transaction.OnError() {
                                    @Override
                                    public void onError(Throwable error) {
                                        realm.close();
                                        Log.e(TAG, "onError: Unable to save USER", error);
                                        getView().showAlert("Error Saving API Response");
                                    }
                                });

                            } else {
                                getView().showAlert(String.valueOf(R.string.cantConnect));
                            }
                        } else {
                            getView().showAlert(response.message() != null ? response.message()
                                    : "Unknown Error");
                        }
                    }

                    @Override
                    public void onFailure(Call<ResultResponse> call, Throwable t) {
                        getView().stopProgress();
                        Log.e(TAG, "onFailure: Error calling login api", t);
                        getView().stopProgress();
                        if(t.getMessage().contains("Expected"))
                            getView().showAlert("Email Already Exist!");
                        else
                            getView().showAlert("Error Connecting to Server");
                    }
                });
            } else {
                getView().showAlert("New Password Mismatch");
            }
        } else {
            getView().showAlert("Wrong Current Password!");
        }
    }
}
