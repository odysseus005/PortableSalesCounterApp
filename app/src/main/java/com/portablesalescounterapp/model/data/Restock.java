package com.portablesalescounterapp.model.data;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;


public class Restock extends RealmObject {

    @PrimaryKey
    @SerializedName("restock_id")
    @Expose
    private int restockId;
    @SerializedName("productName")
    @Expose
    private String productName;
    @SerializedName("productTotal")
    @Expose
    private String productTotal;
    @SerializedName("productRestock")
    @Expose
    private String productRestock;
    @SerializedName("user_id")
    @Expose
    private int userId;
    @SerializedName("userName")
    @Expose
    private String userName;

    @SerializedName("business_id")
    @Expose
    private int businessId;



    public int getBusinessId() {
        return businessId;
    }

    public void setBusinessId(int businessId) {
        this.businessId = businessId;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    @SerializedName("date")
    @Expose
    private String date;

    public int getRestockId() {
        return restockId;
    }

    public void setRestockId(int restockId) {
        this.restockId = restockId;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public String getProductTotal() {
        return productTotal;
    }

    public void setProductTotal(String productTotal) {
        this.productTotal = productTotal;
    }

    public String getProductRestock() {
        return productRestock;
    }

    public void setProductRestock(String productRestock) {
        this.productRestock = productRestock;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }
}




