package com.portablesalescounterapp.app;



public class Endpoints {

    public static final String _ID = "{id}/";
    //public static final String BASE_URL = "http://115.85.25.206/";
    public static final String BASE_URL = "http://findingodysseus.000webhostapp.com";
    public static final String URL_IMAGE = BASE_URL+ "/portablesalescounter/images/";
    public static final String API_URL = BASE_URL+ "/portablesalescounter/";
    public static final String IMAGE_UPLOAD = BASE_URL + "/src/v1/";


    //User
    public static final String CLIENT = "client.php?";
    public static final String LOGIN = "loginClient";
    public static final String REGISTER = "registerClient";
    public static final String UPDATEUSER = "editProfile";
    public static final String UPDATEPASS = "editProfile2";
    public static final String FORGOTPASS = "forgotPass";
    public static final String FIRSTLOGIN = "firstLogin";


    //Business
    public static final String BUSINESS_REGISTER = "registerBusiness";
    public static final String BUSINESS_UPDATE = "updateBusiness";


    //Employee
    public static final String EMPLOYEE = "employee.php?";
    public static final String ALL_EMPLOYEE = "allEmployee";
    public static final String ADD_EMPLOYEE = "addEmployee";
    public static final String UPDATE_EMPLOYEE = "updateEmployee";
    public static final String DELETE_EMPLOYEE = "deleteEmployee";
    public static final String ALL_BUSINESS = "allBusiness";


    //Category
    public static final String CATEGORY = "category.php?";
    public static final String ALL_CATEGORY = "allCategory";
    public static final String ADD_CATEGORY = "addCategory";
    public static final String UPDATE_CATEGORY = "updateCategory";
    public static final String DELETE_CATEGORY = "deleteCategory";


    //Discount
    public static final String DISCOUNT = "discount.php?";
    public static final String ALL_DISCOUNT = "allDiscount";
    public static final String ADD_DISCOUNT = "addDiscount";
    public static final String UPDATE_DISCOUNT = "updateDiscount";
    public static final String DELETE_DISCOUNT = "deleteDiscount";


    //Product
    public static final String PRODUCT = "product.php?";
    public static final String ALL_PRODUCT = "allProduct";
    public static final String ALL_PRODUCT_CATEGORY = "allProductCategory";
    public static final String ADD_PRODUCT = "addProduct";
    public static final String UPDATE_PRODUCT = "updateProduct";
    public static final String DELETE_PRODUCT = "deleteProduct";
    public static final String RESTOCK_PRODUCT = "restockProduct";
    public static final String ALL_RESTOCK_PRODUCT = "allRestock";
    public static final String QR_PRODUCT = "qrProduct";


    //Transaction
    public static final String TRANSACTION  = "transaction.php?";
    public static final String ADD_TRANSACTION = "addTransaction";
    public static final String ALL_TRANSACTION = "allTransaction";
    public static final String UPDATE_TRANSACTION = "updateTransaction";
    public static final String REFUND_TRANSACTION = "refundTransaction";
    public static final String ADD_TRANSACTION_GUEST = "addTransactionGuest";


}
