package com.portablesalescounterapp.model.data;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;


public class Business extends RealmObject {

    @PrimaryKey
    @SerializedName("business_id")
    @Expose
    private int businessId;

    @SerializedName("businessName")
    @Expose
    private String businessName;
    @SerializedName("businessContact")
    @Expose
    private String businessContact;
    @SerializedName("businessAddress")
    @Expose
    private String businessAddress;
    @SerializedName("businessDescription")
    @Expose
    private String businessDescription;


    public String getBusinessDescription() {
        return businessDescription;
    }

    public void setBusinessDescription(String businessDescription) {
        this.businessDescription = businessDescription;
    }
    public int getBusinessId() {
        return businessId;
    }

    public void setBusinessId(int businessId) {
        this.businessId = businessId;
    }

    public String getBusinessName() {
        return businessName;
    }

    public void setBusinessName(String businessName) {
        this.businessName = businessName;
    }

    public String getBusinessContact() {
        return businessContact;
    }

    public void setBusinessContact(String businessContact) {
        this.businessContact = businessContact;
    }

    public String getBusinessAddress() {
        return businessAddress;
    }

    public void setBusinessAddress(String businessAddress) {
        this.businessAddress = businessAddress;
    }



}
