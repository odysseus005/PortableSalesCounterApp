package com.portablesalescounterapp.model.response;


import com.google.gson.annotations.SerializedName;
import com.portablesalescounterapp.model.data.User;

public class ResultResponse {

    private String result;

    public String getResult() {
        return result;
    }


    public void setResult(String result) {
        this.result = result;
    }




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


    @SerializedName("user")
    private User user;

    public User getUser() {
        return user;
    }


    public void setUser(User user) {
        this.user = user;
    }


}
