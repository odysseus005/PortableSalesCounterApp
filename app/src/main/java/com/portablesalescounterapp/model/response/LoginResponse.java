package com.portablesalescounterapp.model.response;


import com.google.gson.annotations.SerializedName;
import com.portablesalescounterapp.model.data.Business;
import com.portablesalescounterapp.model.data.User;

public class LoginResponse extends ResultResponse {

    @SerializedName("user")
    private User user;

    public User getUser() {
        return user;
    }



    public void setUser(User user) {
        this.user = user;
    }


    public Business getBusiness() {
        return business;
    }

    public void setBusiness(Business business) {
        this.business = business;
    }

    @SerializedName("business")
    private Business business;


}
