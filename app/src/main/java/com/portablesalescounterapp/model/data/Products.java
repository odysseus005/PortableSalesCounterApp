package com.portablesalescounterapp.model.data;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;


public class Products extends RealmObject {

    @PrimaryKey
    @SerializedName("product_id")
    @Expose
    private int productId;
    @SerializedName("productName")
    @Expose
    private String productName;
    @SerializedName("productDescription")
    @Expose
    private String productDescription;
    @SerializedName("productPrice")
    @Expose
    private String productPrice;
    @SerializedName("image")
    @Expose
    private String productImage;
    @SerializedName("productBar")
    @Expose
    private String productBar;
    @SerializedName("productQr")
    @Expose
    private String productQr;

    @SerializedName("productSKU")
    @Expose
    private String productSKU;

    @SerializedName("productCode")
    @Expose
    private String productCode;

    @SerializedName("productStatus")
    @Expose
    private String productStatus;

    @SerializedName("category_id")
    @Expose
    private String categoryId;


    @SerializedName("business_id")
    @Expose
    private int businessId;


    public String getProductSKU() {
        return productSKU;
    }

    public void setProductSKU(String productSKU) {
        this.productSKU = productSKU;
    }


    public int getBusinessId() {
        return businessId;
    }

    public void setBusinessId(int businessId) {
        this.businessId = businessId;
    }

    public String getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(String categoryId) {
        this.categoryId = categoryId;
    }


    public int getProductId() {
        return productId;
    }

    public void setProductId(int productId) {
        this.productId = productId;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public String getProductDescription() {
        return productDescription;
    }

    public void setProductDescription(String productDescription) {
        this.productDescription = productDescription;
    }

    public String getProductPrice() {
        return productPrice;
    }

    public void setProductPrice(String productPrice) {
        this.productPrice = productPrice;
    }

    public String getProductImage() {
        return productImage;
    }

    public void setProductImage(String productImage) {
        this.productImage = productImage;
    }

    public String getProductBar() {
        return productBar;
    }

    public void setProductBar(String productBar) {
        this.productBar = productBar;
    }

    public String getProductQr() {
        return productQr;
    }

    public void setProductQr(String productQr) {
        this.productQr = productQr;
    }

    public String getProductCode() {
        return productCode;
    }

    public void setProductCode(String productCode) {
        this.productCode = productCode;
    }

    public String getProductStatus() {
        return productStatus;
    }

    public void setProductStatus(String productStatus) {
        this.productStatus = productStatus;
    }




}
