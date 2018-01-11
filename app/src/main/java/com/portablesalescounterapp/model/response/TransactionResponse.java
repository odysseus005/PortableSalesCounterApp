package com.portablesalescounterapp.model.response;


import com.google.gson.annotations.SerializedName;
import com.portablesalescounterapp.model.data.Transaction;

public class TransactionResponse {

    private String result;

    public String getResult() {
        return result;
    }


    public void setResult(String result) {
        this.result = result;
    }


    public Transaction getTransaction() {
        return transaction;
    }

    public void setTransaction(Transaction transaction) {
        this.transaction = transaction;
    }

    public Transaction transaction;

    @SerializedName("categoryTotal")
    private String categoryTotal;

    public String getCategoryTotal() {
        return categoryTotal;
    }

    public void setCategoryTotal(String categoryTotal) {
        this.categoryTotal = categoryTotal;
    }


    @SerializedName("business_id")
    private String business_id;

    public String getBusiness_id() {
        return business_id;
    }

    public void setBusiness_id(String business_id) {
        this.business_id = business_id;
    }


    @SerializedName("data")
    private String image;

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }
}
