package com.portablesalescounterapp.ui.register;

import android.util.Log;
import android.util.Patterns;

import com.hannesdorfmann.mosby.mvp.MvpNullObjectBasePresenter;
import com.portablesalescounterapp.R;
import com.portablesalescounterapp.app.App;
import com.portablesalescounterapp.app.Constants;
import com.portablesalescounterapp.app.Endpoints;
import com.portablesalescounterapp.model.response.ResultResponse;

import java.io.IOException;


import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class RegisterPresenter extends MvpNullObjectBasePresenter<RegisterView> {

    private static final String TAG = RegisterPresenter.class.getSimpleName();

    public void registerUser(String email,
                         String password,
                         String confirmPassword,
                         String firstName,
                         String lastName,
                         String birthday,
                         String contact,
                         String address,
                         String position,
                         String bussinessID) {

        if (email.equals("") || password.equals("") || confirmPassword.equals("") || firstName.equals("") || lastName.equals("") || birthday.equals("") ||
                contact.equals("") || address.equals("")) {
            getView().showAlert("Fill-up all fields");
        }else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) { //check if email is valid
            getView().showAlert("Invalid email address.");
        } else if (!Patterns.PHONE.matcher(contact).matches()) { // check if mobile number is valid
            getView().showAlert("Invalid mobile number.");
        }
         else if (password.length() < 8) {
            getView().showAlert("Password must be atleast 8 characters");
        } /*else if (password.matches("[A-Za-z0-9 ]*")) {
            getView().showAlert("Password must have at least 1 numeric and special character");
        } */else if (!password.contentEquals(confirmPassword)) {
            getView().showAlert("Password does not match");
        } else {
            getView().startLoading();
            App.getInstance().getApiInterface().register(Endpoints.REGISTER,email, password, firstName, lastName, contact, birthday, address,position,bussinessID)
                    .enqueue(new Callback<ResultResponse>() {
                        @Override
                        public void onResponse(Call<ResultResponse> call, Response<ResultResponse> response) {
                            getView().stopLoading();
                            if (response.isSuccessful()) {
                                switch (response.body().getResult()) {
                                    case Constants.SUCCESS:
                                        getView().onRegistrationSuccess();
                                        break;
                                    case Constants.EMAIL_EXIST:
                                        getView().showAlert("Email already exists");
                                        break;
                                    default:
                                        getView().showAlert(String.valueOf(R.string.cantConnect));
                                        break;
                                }
                            } else {
                                try {
                                    String errorBody = response.errorBody().string();
                                    getView().showAlert(errorBody);
                                } catch (IOException e) {
                                    Log.e(TAG, "onResponse: Error parsing error body as string", e);
                                    getView().showAlert(response.message() != null ?
                                            response.message() : "Unknown Exception");
                                }
                            }
                        }

                        @Override
                        public void onFailure(Call<ResultResponse> call, Throwable t) {
                            Log.e(TAG, "onFailure: Error calling register api", t);
                            getView().stopLoading();
                            getView().showAlert("Error Connecting to Server");
                        }
                    });
        }

    }


    public void registerBusiness(String businessName,String businessAddress,String businessContact, String businessDescription) {

        if (businessName.equals("") || businessAddress.equals("") || businessContact.equals("") || businessDescription.equals("")) {
            getView().showAlert("Fill-up all fields");
        }else {
            getView().startLoading();
            App.getInstance().getApiInterface().registerBusiness(Endpoints.BUSINESS_REGISTER,businessName,businessAddress,businessContact,businessDescription)
                    .enqueue(new Callback<ResultResponse>() {
                        @Override
                        public void onResponse(Call<ResultResponse> call, Response<ResultResponse> response) {
                            getView().stopLoading();
                            if (response.isSuccessful()) {
                                switch (response.body().getResult()) {
                                    case Constants.SUCCESS:

                                        getView().onBusinessRegistrationSuccess(response.body().getBusiness_id());
                                        break;
                                    case Constants.EMAIL_EXIST:
                                        getView().showAlert("Email already exists");
                                        break;
                                    default:
                                        getView().showAlert(String.valueOf(R.string.cantConnect));
                                        break;
                                }
                            } else {
                                try {
                                    String errorBody = response.errorBody().string();
                                    getView().showAlert(errorBody);
                                } catch (IOException e) {
                                    Log.e(TAG, "onResponse: Error parsing error body as string", e);
                                    getView().showAlert(response.message() != null ?
                                            response.message() : "Unknown Exception");
                                }
                            }
                        }

                        @Override
                        public void onFailure(Call<ResultResponse> call, Throwable t) {
                            Log.e(TAG, "onFailure: Error calling register api", t);
                            getView().stopLoading();
                            getView().showAlert("Error Connecting to Server");
                        }
                    });
        }

    }


}
