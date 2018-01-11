package com.portablesalescounterapp.app;




import com.portablesalescounterapp.model.data.Business;
import com.portablesalescounterapp.model.data.Category;
import com.portablesalescounterapp.model.data.Discount;
import com.portablesalescounterapp.model.data.Employee;
import com.portablesalescounterapp.model.data.Products;
import com.portablesalescounterapp.model.data.Restock;
import com.portablesalescounterapp.model.data.Transaction;
import com.portablesalescounterapp.model.data.User;
import com.portablesalescounterapp.model.response.LoginResponse;
import com.portablesalescounterapp.model.response.ResultResponse;
import com.portablesalescounterapp.model.response.TransactionResponse;

import java.util.List;
import java.util.Map;


import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FieldMap;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;


public interface ApiInterface {

    @FormUrlEncoded
    @POST(Endpoints.CLIENT)
    Call<LoginResponse> login(@Field(Constants.TAG) String tag,
                              @Field(Constants.EMAIL) String username,
                              @Field(Constants.PASSWORD) String password);


    @FormUrlEncoded
    @POST(Endpoints.CLIENT)
    Call<ResultResponse> register(@Field(Constants.TAG) String tag,
                                  @Field(Constants.EMAIL) String username,
                                  @Field(Constants.PASSWORD) String password,
                                  @Field(Constants.FIRST_NAME) String firstName,
                                  @Field(Constants.LAST_NAME) String lastName,
                                  @Field(Constants.CONTACT) String contact,
                                  @Field(Constants.BIRTHDAY) String birthday,
                                  @Field(Constants.ADDRESS) String address,
                                  @Field(Constants.POSITION) String position,
                                  @Field(Constants.BUSINESS_ID) String businessID
    );

    @FormUrlEncoded
    @POST(Endpoints.CLIENT)
    Call<ResultResponse> registerBusiness(@Field(Constants.TAG) String tag,
                                          @Field(Constants.BUSINESS_NAME) String bName,
                                          @Field(Constants.BUSINESS_ADDRESS) String bAddress,
                                          @Field(Constants.BUSINESS_CONTACT) String bContact,
                                          @Field(Constants.BUSINESS_DESCRIPTION) String bDescription
    );



    @FormUrlEncoded
    @POST(Endpoints.CLIENT)
    Call<ResultResponse> checkEmail(@Field(Constants.TAG) String tag, @Field(Constants.EMAIL) String email);




    @Multipart
    @POST("updateUserWithImage")
    Call<User> updateUserWithImage(@Part MultipartBody.Part image,
                                   @Part(Constants.USER_ID) RequestBody user_id,
                                   @Part(Constants.FIRST_NAME) RequestBody first_name,
                                   @Part(Constants.LAST_NAME) RequestBody last_name,
                                   @Part(Constants.CONTACT) RequestBody contact,
                                   @Part(Constants.BIRTHDAY) RequestBody birthday,
                                   @Part(Constants.ADDRESS) RequestBody address);


    @FormUrlEncoded
    @POST(Endpoints.CLIENT)
    Call<User> updateUser(@Field(Constants.TAG) String tag,
                          @Field(Constants.USER_ID) String user_id,
                          @Field(Constants.FIRST_NAME) String first_name,
                          @Field(Constants.LAST_NAME) String last_name,
                          @Field(Constants.CONTACT) String contact,
                          @Field(Constants.BIRTHDAY) String birthday,
                          @Field(Constants.ADDRESS) String address,
                          @Field(Constants.POSITION) String position);


    @FormUrlEncoded
    @POST(Endpoints.CLIENT)
    Call<ResultResponse> changePassword(@Field(Constants.TAG) String tag,
                                        @Field(Constants.USER_ID) String user_id,
                                        @Field(Constants.PASSWORD) String password);


    @FormUrlEncoded
    @POST(Endpoints.EMPLOYEE)
    Call<List<Employee>> getEmployee(@Field(Constants.TAG) String tag, @Field(Constants.BUSINESS_ID) String user_id );


    @FormUrlEncoded
    @POST(Endpoints.EMPLOYEE)
    Call<List<Employee>> deleteEmployee(@Field(Constants.TAG) String tag, @Field(Constants.EMPLOYEE_ID) String user_id,@Field(Constants.BUSINESS_ID) String business_id);


    @FormUrlEncoded
    @POST(Endpoints.EMPLOYEE)
    Call<List<Employee>> updateEmployee(@Field(Constants.TAG) String tag,
                                        @Field(Constants.EMPLOYEE_ID) String employee_id,
                                        @Field(Constants.FIRST_NAME) String first_name,
                                        @Field(Constants.LAST_NAME) String last_name,
                                        @Field(Constants.CONTACT) String contact,
                                        @Field(Constants.BIRTHDAY) String birthday,
                                        @Field(Constants.ADDRESS) String address,
                                        @Field(Constants.POSITION) String position,
                                        @Field(Constants.BUSINESS_ID) String business_id);

    @FormUrlEncoded
    @POST(Endpoints.EMPLOYEE)
    Call<List<Employee>> addEmployee(@Field(Constants.TAG) String tag,
                                     @Field(Constants.EMAIL) String username,
                                     @Field(Constants.PASSWORD) String password,
                                     @Field(Constants.FIRST_NAME) String firstName,
                                     @Field(Constants.LAST_NAME) String lastName,
                                     @Field(Constants.CONTACT) String contact,
                                     @Field(Constants.BIRTHDAY) String birthday,
                                     @Field(Constants.ADDRESS) String address,
                                     @Field(Constants.POSITION) String position,
                                     @Field(Constants.BUSINESS_ID) String businessID
    );


    @FormUrlEncoded
    @POST(Endpoints.CATEGORY)
    Call<List<Category>> getCategory(@Field(Constants.TAG) String tag, @Field(Constants.BUSINESS_ID) String user_id );


    @FormUrlEncoded
    @POST(Endpoints.CATEGORY)
    Call<List<Category>> deleteCategory(@Field(Constants.TAG) String tag, @Field(Constants.CATEGORY_ID) String user_id,@Field(Constants.BUSINESS_ID) String business_id);


    @FormUrlEncoded
    @POST(Endpoints.CATEGORY)
    Call<List<Category>> updateCategory(@Field(Constants.TAG) String tag,
                                        @Field(Constants.CATEGORY_ID) String category_id,
                                        @Field(Constants.CATEGORY_NAME) String name,
                                        @Field(Constants.CATEGORY_DESCRIPTION) String description,
                                        @Field(Constants.BUSINESS_ID) String business_id);

    @FormUrlEncoded
    @POST(Endpoints.CATEGORY)
    Call<List<Category>> addCategory(@Field(Constants.TAG) String tag,
                                     @Field(Constants.CATEGORY_NAME) String name,
                                     @Field(Constants.CATEGORY_DESCRIPTION) String description,
                                     @Field(Constants.BUSINESS_ID) String businessID
    );



    @FormUrlEncoded
    @POST(Endpoints.DISCOUNT)
    Call<List<Discount>> getDiscount(@Field(Constants.TAG) String tag, @Field(Constants.BUSINESS_ID) String business_id );


    @FormUrlEncoded
    @POST(Endpoints.DISCOUNT)
    Call<List<Discount>> deleteDiscount(@Field(Constants.TAG) String tag, @Field(Constants.DISCOUNT_ID) String disc_id,@Field(Constants.BUSINESS_ID) String business_id);


    @FormUrlEncoded
    @POST(Endpoints.DISCOUNT)
    Call<List<Discount>> updateDiscount(@Field(Constants.TAG) String tag,
                                        @Field(Constants.DISCOUNT_ID) String category_id,
                                        @Field(Constants.DISCOUNT__NAME) String name,
                                        @Field(Constants.DISCOUNT_CODE) String code,
                                        @Field(Constants.DISCOUNT_VALUE) String value,
                                        @Field(Constants.BUSINESS_ID) String business_id);


    @FormUrlEncoded
    @POST(Endpoints.DISCOUNT)
    Call<List<Discount>> addDiscount(@Field(Constants.TAG) String tag,
                                     @Field(Constants.DISCOUNT__NAME) String name,
                                     @Field(Constants.DISCOUNT_CODE) String code,
                                     @Field(Constants.DISCOUNT_VALUE) String value,
                                     @Field(Constants.BUSINESS_ID) String business_id);




    @FormUrlEncoded
    @POST(Endpoints.PRODUCT)
    Call<List<Restock>> getRestock(@Field(Constants.TAG) String tag, @Field(Constants.BUSINESS_ID) String business_id );



    @FormUrlEncoded
    @POST(Endpoints.PRODUCT)
    Call<ResultResponse> getProductCategory(@Field(Constants.TAG) String tag, @Field(Constants.BUSINESS_ID) String business_id,@Field(Constants.CATEGORY_ID) String category_id );


    @FormUrlEncoded
    @POST(Endpoints.PRODUCT)
    Call<List<Products>> getProduct(@Field(Constants.TAG) String tag, @Field(Constants.BUSINESS_ID) String business_id );


    @FormUrlEncoded
    @POST(Endpoints.PRODUCT)
    Call<List<Products>> deleteProduct(@Field(Constants.TAG) String tag, @Field(Constants.PRODUCT_ID) String prod_id,@Field(Constants.BUSINESS_ID) String business_id);


    @FormUrlEncoded
    @POST(Endpoints.PRODUCT)
    Call<List<Products>> updateProduct(@Field(Constants.TAG) String tag,
                                        @Field(Constants.PRODUCT_ID) String product_id,
                                        @Field(Constants.PRODUCT_NAME) String name,
                                        @Field(Constants.PRODUCT_DESCRIPTION) String desc,
                                        @Field(Constants.PRODUCT_PRICE) String price,
                                        @Field(Constants.PRODUCT_CODE) String code,
                                       @Field(Constants.PRODUCT_BAR) String bar,
                                       @Field(Constants.CATEGORY_ID) String category_id,
                                       @Field(Constants.BUSINESS_ID) String business_id
                                       );

    @FormUrlEncoded
    @POST(Endpoints.PRODUCT)
    Call<List<Products>> updateQr(@Field(Constants.TAG) String tag,
                                       @Field(Constants.PRODUCT_ID) String product_id,
                                       @Field(Constants.QR_STATUS) String name,
                                       @Field(Constants.PRODUCT_QR) String desc,
                                       @Field(Constants.BUSINESS_ID) String business_id
    );



    @FormUrlEncoded
    @POST(Endpoints.PRODUCT)
    Call<List<Products>> restockProduct(@Field(Constants.TAG) String tag,
                                       @Field(Constants.PRODUCT_ID) String product_id,
                                        @Field(Constants.PRODUCT_NAME) String product_name,
                                        @Field(Constants.PRODUCT_TOTAL) String product_total,
                                        @Field(Constants.PRODUCT_RESTOCK) String product_restock,
                                        @Field(Constants.USER_ID) String user_id,
                                        @Field(Constants.FIRST_NAME) String name,
                                        @Field(Constants.DATE_TIME) String date,
                                       @Field(Constants.BUSINESS_ID) String business_id
    );

    @FormUrlEncoded
    @POST(Endpoints.PRODUCT)
    Call<List<Products>> addProduct(@Field(Constants.TAG) String tag,
                                    @Field(Constants.PRODUCT_NAME) String name,
                                    @Field(Constants.PRODUCT_DESCRIPTION) String desc,
                                    @Field(Constants.PRODUCT_PRICE) String price,
                                    @Field(Constants.PRODUCT_CODE) String code,
                                    @Field(Constants.PRODUCT_BAR) String bar,
                                    @Field(Constants.CATEGORY_ID) String category_id,
                                    @Field(Constants.BUSINESS_ID) String business_id
    );


    @FormUrlEncoded
    @POST(Endpoints.EMPLOYEE)
    Call<List<Business>> getBusiness(@Field(Constants.TAG) String tag);




    @FormUrlEncoded
    @POST(Endpoints.TRANSACTION)
    Call<List<Transaction>> getTransaction(@Field(Constants.TAG) String tag, @Field(Constants.BUSINESS_ID) String business_id );


    @FormUrlEncoded
    @POST(Endpoints.TRANSACTION)
    Call<TransactionResponse> addTransaction(@Field(Constants.TAG) String tag,
                                             @Field(Constants.TRANSACTION_PRICE) String transPrice,
                                             @Field(Constants.TRANSACTION_CODE) String transCode,
                                             @Field(Constants.TRANSACTION_DISCOUNT) String transDiscount,
                                             @Field(Constants.TRANSACTION_IDLIST) String transIdList,
                                             @Field(Constants.TRANSACTION_NAMELIST) String transNameList,
                                             @Field(Constants.TRANSACTION_QUANTITYLIST) String transQuanList,
                                             @Field(Constants.TRANSACTION_PRICELIST) String transPriceList,
                                             @Field(Constants.DISCOUNT_ID) String disId,
                                             @Field(Constants.DISCOUNT__NAME) String disName,
                                             @Field(Constants.USER_ID) String user_id,
                                             @Field(Constants.FIRST_NAME) String name,
                                             @Field(Constants.DATE_TIME) String date,
                                             @Field(Constants.BUSINESS_ID) String business_id
    );



    @Multipart
    @POST("upload.php?")
    Call<ResultResponse> uploadImage(@Part MultipartBody.Part image);

    @Multipart
    @POST("upload.php?")
    Call<ResultResponse> uploadFile(@Part MultipartBody.Part file, @Part("name") RequestBody name);



    @POST("setAppointment")
    @FormUrlEncoded
    Call<ResultResponse> setAppointment(@FieldMap Map<String, String> params);


   /* @POST("getAppointments")
    @FormUrlEncoded
    Call<List<Appointment>> getAppointments(@Field("user_id") String user_id);*/
}
