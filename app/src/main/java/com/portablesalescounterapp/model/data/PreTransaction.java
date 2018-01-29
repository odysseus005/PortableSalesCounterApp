package com.portablesalescounterapp.model.data;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;


public class PreTransaction extends RealmObject {

    @PrimaryKey
    @SerializedName("pretransaction_id")
    @Expose
    private int pretransactionId;
    @SerializedName("pretransactionPrice")
    @Expose
    private String pretransactionPrice;

    @SerializedName("pretransactionCode")
    @Expose
    private String pretransactionCode;

    @SerializedName("pretransactionDiscount")
    @Expose
    private String pretransactionDiscount;


    @SerializedName("predate")
    @Expose
    private String predate;

    @SerializedName("prediscount_id")
    @Expose
    private int prediscountId;
    @SerializedName("prediscount_name")
    @Expose
    private String prediscountName;

    @SerializedName("preuser_id")
    @Expose
    private int preuserId;
    @SerializedName("preuserName")
    @Expose
    private String preuserName;

    @SerializedName("prebusiness_id")
    @Expose
    private int prebusinessId;


    @SerializedName("pretransactionIdList")
    @Expose
    private String pretransactionIdList;

    @SerializedName("pretransactionNameList")
    @Expose
    private String pretransactionNameList;

    @SerializedName("pretransactionQuantityList")
    @Expose
    private String pretransactionQuantityList;

    @SerializedName("pretransactionPriceList")
    @Expose
    private String pretransactionPriceList;

    public int getPretransactionId() {
        return pretransactionId;
    }

    public void setPretransactionId(int pretransactionId) {
        this.pretransactionId = pretransactionId;
    }

    public String getPretransactionPrice() {
        return pretransactionPrice;
    }

    public void setPretransactionPrice(String pretransactionPrice) {
        this.pretransactionPrice = pretransactionPrice;
    }

    public String getPretransactionCode() {
        return pretransactionCode;
    }

    public void setPretransactionCode(String pretransactionCode) {
        this.pretransactionCode = pretransactionCode;
    }

    public String getPretransactionDiscount() {
        return pretransactionDiscount;
    }

    public void setPretransactionDiscount(String pretransactionDiscount) {
        this.pretransactionDiscount = pretransactionDiscount;
    }

    public String getPredate() {
        return predate;
    }

    public void setPredate(String predate) {
        this.predate = predate;
    }

    public int getPrediscountId() {
        return prediscountId;
    }

    public void setPrediscountId(int prediscountId) {
        this.prediscountId = prediscountId;
    }

    public String getPrediscountName() {
        return prediscountName;
    }

    public void setPrediscountName(String prediscountName) {
        this.prediscountName = prediscountName;
    }

    public int getPreuserId() {
        return preuserId;
    }

    public void setPreuserId(int preuserId) {
        this.preuserId = preuserId;
    }

    public String getPreuserName() {
        return preuserName;
    }

    public void setPreuserName(String preuserName) {
        this.preuserName = preuserName;
    }

    public int getPrebusinessId() {
        return prebusinessId;
    }

    public void setPrebusinessId(int prebusinessId) {
        this.prebusinessId = prebusinessId;
    }

    public String getPretransactionIdList() {
        return pretransactionIdList;
    }

    public void setPretransactionIdList(String pretransactionIdList) {
        this.pretransactionIdList = pretransactionIdList;
    }

    public String getPretransactionNameList() {
        return pretransactionNameList;
    }

    public void setPretransactionNameList(String pretransactionNameList) {
        this.pretransactionNameList = pretransactionNameList;
    }

    public String getPretransactionQuantityList() {
        return pretransactionQuantityList;
    }

    public void setPretransactionQuantityList(String pretransactionQuantityList) {
        this.pretransactionQuantityList = pretransactionQuantityList;
    }

    public String getPretransactionPriceList() {
        return pretransactionPriceList;
    }

    public void setPretransactionPriceList(String pretransactionPriceList) {
        this.pretransactionPriceList = pretransactionPriceList;
    }

    public String getPretransactionStatus() {
        return pretransactionStatus;
    }

    public void setPretransactionStatus(String pretransactionStatus) {
        this.pretransactionStatus = pretransactionStatus;
    }

    @SerializedName("pretransactionStatus")
    @Expose
    private String pretransactionStatus;



}

