package com.portablesalescounterapp.model.response;


import com.google.gson.annotations.SerializedName;
import com.portablesalescounterapp.model.data.PreTransaction;

public class PreTransactionResponse {

    private String result;

    public String getResult() {
        return result;
    }


    public void setResult(String result) {
        this.result = result;
    }


    public PreTransaction getPreTransaction() {
        return pretransaction;
    }

    public void setPreTransaction(PreTransaction pretransaction) {
        this.pretransaction = pretransaction;
    }

    public PreTransaction pretransaction;

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
