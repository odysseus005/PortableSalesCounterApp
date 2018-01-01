package com.portablesalescounterapp.ui.profile.edit;

import android.util.Log;

import com.hannesdorfmann.mosby.mvp.MvpNullObjectBasePresenter;
import com.portablesalescounterapp.app.App;
import com.portablesalescounterapp.app.Endpoints;
import com.portablesalescounterapp.model.data.User;
import com.portablesalescounterapp.model.response.ResultResponse;

import java.io.File;

import io.realm.Realm;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class EditProfilePresenter extends MvpNullObjectBasePresenter<EditProfileView> {

    private static final String TAG = EditProfilePresenter.class.getSimpleName();
    private Realm realm;
    private User user;

    public void onStart() {
        realm = Realm.getDefaultInstance();
        user = App.getUser();
    }


    public void updateUser(String userId, String firstName, String lastName, String contact, String birthday, String address,String position) {
        if (firstName.equals("") || lastName.equals("") || birthday.equals("") || contact.equals("") || address.equals("")) {
            getView().showAlert("Fill-up all fields");
        } else {
            getView().startLoading();
            App.getInstance().getApiInterface().updateUser(Endpoints.UPDATEUSER,userId, firstName, lastName, contact, birthday, address, position)
                    .enqueue(new Callback<User>() {
                        @Override
                        public void onResponse(Call<User> call, final Response<User> response) {
                            getView().stopLoading();
                            if (response.isSuccessful() && response.body().getUserId() == user.getUserId()) {
                                realm.executeTransaction(new Realm.Transaction() {
                                    @Override
                                    public void execute(Realm realm) {
                                        realm.copyToRealmOrUpdate(response.body());
                                        getView().finishAct();
                                    }
                                });
                            } else {
                                getView().showAlert("Oops something went wrong");
                            }
                        }

                        @Override
                        public void onFailure(Call<User> call, Throwable t) {
                            Log.e(TAG, "onFailure: Error calling login api", t);
                            getView().stopLoading();
                            getView().showAlert("Error Connecting to Server");
                        }
                    });
        }
    }


   public void upload(String fname, final File imageFile) {
        // create RequestBody instance from file
        RequestBody requestFile = RequestBody.create(MediaType.parse("image/*"), imageFile);
        // MultipartBody.Part is used to send also the actual file name
        MultipartBody.Part body = MultipartBody.Part.createFormData("file", fname, requestFile);
       RequestBody filename = RequestBody.create(MediaType.parse("text/plain"), fname);
            getView().startLoading();
       App.getInstance().getApiInterface().uploadFile(body,filename)
                .enqueue(new Callback<ResultResponse>() {
                    @Override
                    public void onResponse(Call<ResultResponse> call, final Response<ResultResponse> response) {
                        getView().stopLoading();
                        if (response.isSuccessful()) {
                            if (response.body().getResult().equals("success")) {
                                final Realm realm = Realm.getDefaultInstance();
                                realm.executeTransactionAsync(new Realm.Transaction() {
                                    @Override
                                    public void execute(Realm realm) {
                                        User user = realm.where(User.class).findFirst();
                                       // user.setImage(response.body().getImage());
                                    }
                                }, new Realm.Transaction.OnSuccess() {
                                    @Override
                                    public void onSuccess() {
                                        getView().showAlert("Uploading Success");
                                        realm.close();
                                    }
                                }, new Realm.Transaction.OnError() {
                                    @Override
                                    public void onError(Throwable error) {
                                        realm.close();
                                        error.printStackTrace();
                                        getView().showAlert(error.getLocalizedMessage());
                                    }
                                });
                            } else {
                                getView().showAlert(response.body().getResult());
                            }
                        } else {
                            getView().showAlert("Error Server Connection");
                        }
                    }

                    @Override
                    public void onFailure(Call<ResultResponse> call, Throwable t) {
                        getView().stopLoading();
                        t.printStackTrace();
                        getView().showAlert(t.getLocalizedMessage());
                    }
                });
    }



    public void onStop() {
        realm.close();
    }


}
