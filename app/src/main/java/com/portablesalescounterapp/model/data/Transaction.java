package com.portablesalescounterapp.model.data;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;


public class Transaction extends RealmObject {

    @PrimaryKey
    @SerializedName("transaction_id")
    @Expose
    private int transactionId;
    @SerializedName("transactionPrice")
    @Expose
    private String transactionPrice;

    @SerializedName("transactionCode")
    @Expose
    private String transactionCode;

    @SerializedName("transactionDiscount")
    @Expose
    private String transactionDiscount;


    @SerializedName("date")
    @Expose
    private String date;

    @SerializedName("discount_id")
    @Expose
    private int discountId;
    @SerializedName("discount_name")
    @Expose
    private String discountName;

    @SerializedName("user_id")
    @Expose
    private int userId;
    @SerializedName("userName")
    @Expose
    private String userName;

    @SerializedName("business_id")
    @Expose
    private int businessId;



    @SerializedName("transactionIdList")
    @Expose
    private String transactionIdList;

    @SerializedName("transactionNameList")
    @Expose
    private String transactionNameList;

    @SerializedName("transactionQuantityList")
    @Expose
    private String transactionQuantityList;

    @SerializedName("transactionPriceList")
    @Expose
    private String transactionPriceList;

    @SerializedName("transactionStatus")
    @Expose
    private String transactionStatus;


    public int getTransactionId() {
        return transactionId;
    }

    public void setTransactionId(int transactionId) {
        this.transactionId = transactionId;
    }

    public String getTransactionPrice() {
        return transactionPrice;
    }

    public void setTransactionPrice(String transactionPrice) {
        this.transactionPrice = transactionPrice;
    }

    public String getTransactionCode() {
        return transactionCode;
    }

    public void setTransactionCode(String transactionCode) {
        this.transactionCode = transactionCode;
    }

    public String getTransactionDiscount() {
        return transactionDiscount;
    }

    public void setTransactionDiscount(String transactionDiscount) {
        this.transactionDiscount = transactionDiscount;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public int getDiscountId() {
        return discountId;
    }

    public void setDiscountId(int discountId) {
        this.discountId = discountId;
    }

    public String getDiscountName() {
        return discountName;
    }

    public void setDiscountName(String discountName) {
        this.discountName = discountName;
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

    public int getBusinessId() {
        return businessId;
    }

    public void setBusinessId(int businessId) {
        this.businessId = businessId;
    }

    public String getTransactionIdList() {
        return transactionIdList;
    }

    public void setTransactionIdList(String transactionIdList) {
        this.transactionIdList = transactionIdList;
    }

    public String getTransactionNameList() {
        return transactionNameList;
    }

    public void setTransactionNameList(String transactionNameList) {
        this.transactionNameList = transactionNameList;
    }

    public String getTransactionQuantityList() {
        return transactionQuantityList;
    }

    public void setTransactionQuantityList(String transactionQuantityList) {
        this.transactionQuantityList = transactionQuantityList;
    }

    public String getTransactionPriceList() {
        return transactionPriceList;
    }

    public void setTransactionPriceList(String transactionPriceList) {
        this.transactionPriceList = transactionPriceList;
    }


    public String getTransactionStatus() {
        return transactionStatus;
    }

    public void setTransactionStatus(String transactionStatus) {
        this.transactionStatus = transactionStatus;
    }

}
