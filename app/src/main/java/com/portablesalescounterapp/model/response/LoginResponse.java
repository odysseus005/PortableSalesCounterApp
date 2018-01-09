package com.portablesalescounterapp.model.response;


import com.portablesalescounterapp.model.data.Business;
import com.portablesalescounterapp.model.data.User;

public class LoginResponse extends ResultResponse {
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

    private Business business;


}
