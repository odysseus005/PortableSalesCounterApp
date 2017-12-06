package com.portablesalescounterapp.ui.login;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.NonNull;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.hannesdorfmann.mosby.mvp.viewstate.MvpViewStateActivity;
import com.hannesdorfmann.mosby.mvp.viewstate.ViewState;
import com.portablesalescounterapp.R;
import com.portablesalescounterapp.databinding.ActivityLoginBinding;
import com.portablesalescounterapp.model.data.User;

import java.util.Arrays;
import java.util.List;

import io.realm.Realm;


/**
 * A login screen that offers login via email/password.
 */
public class LoginActivity extends MvpViewStateActivity<LoginView, LoginPresenter>
        implements LoginView, TextWatcher {


    // UI references.
    private ActivityLoginBinding binding;
    private ProgressDialog progressDialog;
    private Realm realm;
    User user;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        // Set up the login form.
        setRetainInstance(true);
        realm = Realm.getDefaultInstance();


    //KEYHASH
       /* try {
            PackageInfo info = getPackageManager().getPackageInfo(
                    "jazevangelio.newvawepp",
                    PackageManager.GET_SIGNATURES);
            for (Signature signature : info.signatures) {
                MessageDigest md = MessageDigest.getInstance("SHA");
                md.update(signature.toByteArray());
                Log.d("KeyHash:", Base64.encodeToString(md.digest(), Base64.DEFAULT));
            }
        } catch (PackageManager.NameNotFoundException e) {
            Log.d("LOG","KEYHASG");

        } catch (NoSuchAlgorithmException e) {
            Log.d("LOG","KEY Algo");
        }*/


         user = realm.where(User.class).findFirst();
        if (user != null) {
            onLoginSuccess();
        }
        binding = DataBindingUtil.setContentView(this, R.layout.activity_login);
        binding.setView(getMvpView());
        binding.etEmail.addTextChangedListener(this);
        binding.etPassword.addTextChangedListener(this);



      //FaceBook
        FacebookSdk.sdkInitialize(getApplicationContext());
        callbackManager = CallbackManager.Factory.create();
        LoginManager.getInstance().registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                Log.d("TAG","SUCESS?>> ");

                AccessToken accessToken = loginResult.getAccessToken();
                Profile profile = Profile.getCurrentProfile();
                profile.getProfilePictureUri(200,200).toString();
                Log.d("TAG",">>>"+profile.getFirstName());
               // saveFacebookLoginData("facebook", loginResult.getAccessToken());
            }

            @Override
            public void onCancel() {
                Log.d("TAG","ERROR?>> ");
            }

            @Override
            public void onError(FacebookException error) {
                Toast.makeText(getApplicationContext(), "" + error.getMessage(), Toast.LENGTH_LONG).show();
                Log.d("TAG",error.getMessage());
            }
        });
        //




        myFirebaseRef = new Firebase("https://vawapp-49d2f.firebaseio.com/");


    }
    @Override
    protected void onActivityResult(int requestCode, int responseCode, Intent intent) {
        super.onActivityResult(requestCode, responseCode, intent);
        //Facebook login
        callbackManager.onActivityResult(requestCode, responseCode, intent);

    }

    @Override
    public void onFBClicked(){
        LoginManager
                .getInstance()
                .logInWithReadPermissions(
                        this,
                        Arrays.asList("public_profile", "user_friends", "email")
                );
    }


    private void saveFacebookLoginData(String provider, AccessToken accessToken){
        String token=accessToken.getToken();

        if( token != null ){

            myFirebaseRef.authWithOAuthToken(
                    provider,
                    token,
                    new Firebase.AuthResultHandler() {
                        @Override
                        public void onAuthenticated(AuthData authData) {
                            String uid=authData.getUid();
                            String name=authData.getProviderData().get("displayName").toString();
                            String email=authData.getProviderData().get("email").toString();
                            String image=authData.getProviderData().get("profileImageURL").toString();

                            Toast.makeText(getApplicationContext(), "" + name, Toast.LENGTH_LONG).show();

                          /*
                            Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                            intent.putExtra("user_id",uid);
                            intent.putExtra("profile_picture",image);
                            startActivity(intent);
                            finish();*/
                        }

                        @Override
                        public void onAuthenticationError(FirebaseError firebaseError) {
                            Toast.makeText(getApplicationContext(), "" + firebaseError.getMessage(), Toast.LENGTH_LONG).show();
                        }
                    });
        }
        else{
            myFirebaseRef.unauth();
        }
    }

    @Override
    protected  void onResume()
    {
        super.onResume();

    }
    @Override
    protected void onStop() {
        super.onStop();
        //Facebook login

    }
    /***
     * Start of LoginView
     ***/

    @Override
    protected void onDestroy() {
        realm.close();
        super.onDestroy();
    }

    /***
     * Start of MvpViewStateActivity
     ***/

    @NonNull
    @Override
    public LoginPresenter createPresenter() {
        return new LoginPresenter();
    }

    @NonNull
    @Override
    public ViewState<LoginView> createViewState() {
        setRetainInstance(true);
        return new LoginViewState();
    }

    /***
     * End of MvpViewStateActivity
     ***/

    @Override
    public void onNewViewStateInstance() {
        saveValues();
    }

    @Override
    public void onLoginButtonClicked() {
        presenter.login(
                binding.etEmail.getText().toString(),
                binding.etPassword.getText().toString()
        );
    }

    @Override
    public void onRegisterButtonClicked() {
        startActivity(new Intent(this, RegisterActivity.class));
    }

    @Override
    public void showAlert(String message) {
        Toast.makeText(LoginActivity.this, message, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void setEditTextValue(String username, String password) {
        binding.etEmail.setText(username);
        binding.etPassword.setText(password);
    }

    @Override
    public void startLoading() {
        if (progressDialog == null) {
            progressDialog = new ProgressDialog(this);
            progressDialog.setMessage("Loading...");
            progressDialog.setCancelable(false);
        }
        progressDialog.show();
    }

    @Override
    public void stopLoading() {
        if (progressDialog != null) progressDialog.dismiss();
    }


    @Override
    public void onLoginSuccess() {


        Handler h = new Handler(Looper.getMainLooper());
        h.post(new Runnable() {
            public void run() {

                realm = Realm.getDefaultInstance();

                final User user2 = realm.where(User.class).findFirst();
                if(user2.getFirstlogin().equalsIgnoreCase("true"))
                {

                 Dialog dialog = new Dialog(LoginActivity.this);
                    final DialogConfirmCodeBinding dialogBinding = DataBindingUtil.inflate(
                            getLayoutInflater(),
                            R.layout.dialog_confirm_code,
                            null,
                            false);
                    dialogBinding.send.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if(dialogBinding.etKeyword.getText().toString().equalsIgnoreCase(user2.getCodex()))
                                presenter.firstLogin(user2.getUserId()+"");
                            else
                                showAlert("Invalid Code");

                        }
                    });
                    dialog.setTitle("Verify Account");
                    dialog.setContentView(dialogBinding.getRoot());
                    dialog.setCancelable(false);
                    dialog.show();
                }else {

                    realm = Realm.getDefaultInstance();
                    final List<Emergency> emer = realm.where(Emergency.class).findAll();
                    if(!emer.isEmpty()) {
                        startActivity(new Intent(LoginActivity.this, MainActivity.class));
                        finish();
                    }else if(emer.size()==1 || emer.size()>=3){
                        startActivity(new Intent(LoginActivity.this, MainActivity.class));
                        finish();
                    }
                    else if(emer.isEmpty()){
                        startActivity(new Intent(LoginActivity.this, EmergencyListActivity.class));
                        finish();
                    }else{
                        startActivity(new Intent(LoginActivity.this, EmergencyListActivity.class));
                        finish();
                    }
                }

            }
        });


    }

    @Override
    public void onLoad(User user) {
        presenter.load(user.getUserId()+"");
    }


    @Override
    public void onCodeSuccess() {
        startActivity(new Intent(this, MainActivity.class));
        finish();
        showAlert("Verification Success!");

    }


    @Override
    public void onForgotPasswordButtonClicked() {
        startActivity(new Intent(this, ForgotPasswordActivity.class));
    }

    @Override
    public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

    }

    @Override
    public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
        saveValues();
    }

    @Override
    public void afterTextChanged(Editable editable) {

    }

    private void saveValues() {
        LoginViewState loginViewState = (LoginViewState) getViewState();
        loginViewState.setUsername(binding.etEmail.getText().toString());
        loginViewState.setPassword(binding.etPassword.getText().toString());
    }

    @Override
    public  void onBackPressed()
    {
        finish();

    }
}

