package com.portablesalescounterapp.ui.item.category;

import android.util.Log;
import android.util.Patterns;

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
public class CategoryListPresenter extends MvpBasePresenter<CategoryListView> {


    private Realm realm;
    private User user;

    public void onStart() {
        realm = Realm.getDefaultInstance();
        user = App.getUser();
    }

    public void load(String businessId) {
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
                                getView().showAlert(response.errorBody().toString());
                        }
                    }

                    @Override
                    public void onFailure(Call<List<Category>> call, Throwable t) {
                        t.printStackTrace();
                        if (isViewAttached()) {
                            getView().stopRefresh();
                            getView().showAlert(t.getLocalizedMessage());
                        }
                    }
                });
    }



    public void productCategory(String bussinessId,String categoryId) {
        App.getInstance().getApiInterface().getProductCategory(Endpoints.ALL_PRODUCT_CATEGORY,bussinessId,categoryId)
                .enqueue(new Callback<ResultResponse>() {
                    @Override
                    public void onResponse(Call<ResultResponse> call, Response<ResultResponse> response) {
                        getView().stopRefresh();
                        if (response.body().getResult().equals("success")) {
                            getView().onEditCategCount(response.body().getCategoryTotal());
                        } else {
                            getView().showAlert("Erron on Connecting to Server");
                        }
                    }

                    @Override
                    public void onFailure(Call<ResultResponse> call, Throwable t) {
                        getView().stopRefresh();
                        Log.e("", "onFailure: Error calling register api", t);
                        getView().stopLoading();
                        getView().showAlert("Error Connecting to Server");
                    }
                });
    }


    public void updateContact(String category_id,
                              String name,
                              String description,
                                String bid) {
        if ( name.equals("") || description.equals("")) {
            getView().showAlert("Fill-up all fields");
        }
         else {
            getView().startLoading();
            App.getInstance().getApiInterface().updateCategory(Endpoints.UPDATE_CATEGORY,category_id, name, description,bid)
                    .enqueue(new Callback<List<Category>>() {
                        @Override
                        public void onResponse(Call<List<Category>> call, final Response<List<Category>> response) {
                            getView().stopLoading();

                            if(response.message().equalsIgnoreCase("existing"))
                            {
                                getView().showAlert("Category Already Exist!");
                            }
                            else if (response.isSuccessful()) {
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
                        public void onFailure(Call<List<Category>> call, Throwable t) {

                            getView().stopLoading();
                            if(t.getMessage().contains("Expected"))
                                getView().showAlert("Category Already Exist!");
                            else
                                getView().showAlert("Error Connecting to Server");
                        }
                    });
        }
    }


    public void addContact(String business_id,
                           String name,
                           String description) {
        if ( name.equals("") || description.equals("")) {
            getView().showAlert("Fill-up all fields");
        }else {
            getView().startLoading();
            App.getInstance().getApiInterface().addCategory(Endpoints.ADD_CATEGORY,name, description, business_id)
                    .enqueue(new Callback<List<Category>>() {
                        @Override
                        public void onResponse(Call<List<Category>> call, final Response<List<Category>> response) {
                            getView().stopLoading();

                            if(response.message().equalsIgnoreCase("existing"))
                            {
                                getView().showAlert("Category Already Exist!");
                            }
                            else if (response.isSuccessful()) {
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
                        public void onFailure(Call<List<Category>> call, Throwable t) {

                            getView().stopLoading();
                            if(t.getMessage().contains("Expected"))
                                getView().showAlert("Category Already Exist!");
                            else
                                getView().showAlert("Error Connecting to Server");
                        }
                    });
        }
    }


    public void deleteContact(String categoryId,String businessId) {

            getView().startLoading();
            App.getInstance().getApiInterface().deleteCategory(Endpoints.DELETE_CATEGORY,categoryId,businessId)
                    .enqueue(new Callback<List<Category>>() {
                        @Override
                        public void onResponse(Call<List<Category>> call, final Response<List<Category>> response) {
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
                        public void onFailure(Call<List<Category>> call, Throwable t) {

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
