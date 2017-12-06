package com.portablesalescounterapp.app;



public class Endpoints {

    public static final String _ID = "{id}/";
    //public static final String BASE_URL = "http://115.85.25.206/";
    //public static final String BASE_URL = "http://findingodysseus.000webhostapp.com";
    public static final String BASE_URL = "https://vawfreephapp.000webhostapp.com";
    public static final String URL_IMAGE = "https://vawfreephapp.000webhostapp.com/vaw/images/";
    public static final String API_URL = BASE_URL+ "/vaw/";
    public static final String IMAGE_UPLOAD = BASE_URL + "/src/v1/";


    //User
    public static final String CLIENT = "client.php?";
    public static final String LOGIN = "loginClient";
    public static final String REGISTER = "registerClient";
    public static final String UPDATEUSER = "editProfile";
    public static final String UPDATEPASS = "editProfile2";
    public static final String FORGOTPASS = "forgotPass";
    public static final String FIRSTLOGIN = "firstLogin";

    //Company
    public static final String COMPANY = "company.php?";
    public static final String ALL_COMPANY = "allCompany";


    //EmergencyContact
    public static final String EMERGENCY = "emergency.php?";
    public static final String ALL_EMERGENCY = "allEmergencyContact";
    public static final String EDIT_EMERGENCY = "editEmergency";
    public static final String ADD_EMERGENCY = "addEmergency";
    public static final String DELETE_EMERGENCY = "deleteEmergency";


    public static final String VERIFY = "verify";
    public static final String VERIFY_RESEND_EMAIL ="resendEmail";

    public static final String SAVE_USER_TOKEN = "saveUserToken";
    public static final String DELETE_USER_TOKEN = "deleteUserToken";
}
