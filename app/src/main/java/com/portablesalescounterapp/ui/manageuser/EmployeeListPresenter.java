package com.portablesalescounterapp.ui.manageuser;

import android.util.Log;
import android.util.Patterns;

import com.hannesdorfmann.mosby.mvp.MvpBasePresenter;
import com.portablesalescounterapp.R;
import com.portablesalescounterapp.app.App;
import com.portablesalescounterapp.app.Constants;
import com.portablesalescounterapp.app.Endpoints;
import com.portablesalescounterapp.model.data.Employee;
import com.portablesalescounterapp.model.data.User;
import com.portablesalescounterapp.model.response.ResultResponse;

import java.io.File;
import java.io.IOException;
import java.util.List;

import io.realm.Realm;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


@SuppressWarnings("ConstantConditions")
public class EmployeeListPresenter extends MvpBasePresenter<EmployeeListView> {


    private Realm realm;
    private User user;

    public void onStart() {
        realm = Realm.getDefaultInstance();
        user = App.getUser();
    }

    public void load(String userId) {
        App.getInstance().getApiInterface().getEmployee(Endpoints.ALL_EMPLOYEE,userId)
                .enqueue(new Callback<List<Employee>>() {
                    @Override
                    public void onResponse(Call<List<Employee>> call, final Response<List<Employee>> response) {
                        if (isViewAttached()) {
                            getView().stopRefresh();
                        }
                        if (response.isSuccessful()) {
                            final Realm realm = Realm.getDefaultInstance();
                            realm.executeTransactionAsync(new Realm.Transaction() {
                                @Override
                                public void execute(Realm realm) {
                                    realm.delete(Employee.class);
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
                    public void onFailure(Call<List<Employee>> call, Throwable t) {
                        t.printStackTrace();
                        if (isViewAttached()) {
                            getView().stopRefresh();
                            getView().showAlert(t.getLocalizedMessage());
                        }
                    }
                });
    }


    public void updateContact(String employee_id,
                              String firstName,
                              String lastName,
                              String birthday,
                              String contact,
                              String address,
                              String position,
                              String bid) {
        if ( firstName.equals("") || lastName.equals("") || birthday.equals("") ||
                contact.equals("") || address.equals("")) {
            getView().showAlert("Fill-up all fields");
        }else if (!Patterns.PHONE.matcher(contact).matches()) { // check if mobile number is valid
            getView().showAlert("Invalid mobile number.");
        }
         else {
            getView().startLoading();
            App.getInstance().getApiInterface().updateEmployee(Endpoints.UPDATE_EMPLOYEE,employee_id, firstName, lastName, contact, birthday, address,position,bid)
                    .enqueue(new Callback<List<Employee>>() {
                        @Override
                        public void onResponse(Call<List<Employee>> call, final Response<List<Employee>> response) {
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
                        public void onFailure(Call<List<Employee>> call, Throwable t) {

                            getView().stopLoading();
                            getView().showAlert("Error Connecting to Server");
                        }
                    });
        }
    }


    public void addContact(String email,
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
            App.getInstance().getApiInterface().addEmployee(Endpoints.ADD_EMPLOYEE,email, password, firstName, lastName, contact, birthday, address,position,bussinessID)
                    .enqueue(new Callback<List<Employee>>() {
                        @Override
                        public void onResponse(Call<List<Employee>> call, final Response<List<Employee>> response) {
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
                        public void onFailure(Call<List<Employee>> call, Throwable t) {

                            getView().stopLoading();
                            getView().showAlert("Error Connecting to Server");
                        }
                    });
        }
    }


    public void deleteContact(String userId,String businessId) {

            getView().startLoading();
            App.getInstance().getApiInterface().deleteEmployee(Endpoints.DELETE_EMPLOYEE,userId,businessId)
                    .enqueue(new Callback<List<Employee>>() {
                        @Override
                        public void onResponse(Call<List<Employee>> call, final Response<List<Employee>> response) {
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
                        public void onFailure(Call<List<Employee>> call, Throwable t) {

                            getView().stopLoading();
                            getView().showAlert("Error Connecting to Server");
                        }
                    });

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
