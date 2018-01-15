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
import android.view.WindowManager;
import android.widget.Toast;

import com.hannesdorfmann.mosby.mvp.viewstate.MvpViewStateActivity;
import com.hannesdorfmann.mosby.mvp.viewstate.ViewState;
import com.portablesalescounterapp.R;
import com.portablesalescounterapp.databinding.ActivityLoginBinding;
import com.portablesalescounterapp.databinding.DialogVerificationBinding;
import com.portablesalescounterapp.model.data.User;
import com.portablesalescounterapp.ui.business.BusinessListActivity;
import com.portablesalescounterapp.ui.business.BusinessListView;
import com.portablesalescounterapp.ui.forgot.ForgotPasswordActivity;
import com.portablesalescounterapp.ui.main.MainActivity;
import com.portablesalescounterapp.ui.register.RegisterActivity;

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
            onLoginSuccess(user);
        }
        binding = DataBindingUtil.setContentView(this, R.layout.activity_login);
        binding.setView(getMvpView());
        binding.etEmail.addTextChangedListener(this);
        binding.etPassword.addTextChangedListener(this);





    }



    @Override
    protected  void onResume()
    {
        super.onResume();
        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);

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


       /* startActivity(new Intent(this, GuestActivity.class));
        finish();*/

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
    public void onLoginSuccess(final User user) {


        if(!(user.getFirstlogin().equalsIgnoreCase("true")))
        {

            Dialog dialog = new Dialog(LoginActivity.this);
            final DialogVerificationBinding dialogBinding = DataBindingUtil.inflate(
                    getLayoutInflater(),
                    R.layout.dialog_verification,
                    null,
                    false);
            dialogBinding.send.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(dialogBinding.etCode.getText().toString().equalsIgnoreCase(user.getFirstlogin()))
                        presenter.firstLogin(String.valueOf(user.getUserId()));
                    else
                        showAlert("Invalid Code");

                }
            });
            dialog.setContentView(dialogBinding.getRoot());
            dialog.setCancelable(false);
            dialog.show();



        }else {

            showAlert("Verification Successful!");
            startActivity(new Intent(this, MainActivity.class));
            finish();
        }

    }

    @Override
    public void onLoginGuestClicked() {

        startActivity(new Intent(this, BusinessListActivity.class));

    }


   /* @Override
    public void onCodeSuccess() {
        startActivity(new Intent(this, GuestActivity.class));
        finish();
        showAlert("Verification Success!");

    }*/


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
//        loginViewState.setUsername(binding.etEmail.getText().toString());
  //      loginViewState.setPassword(binding.etPassword.getText().toString());
    }

    @Override
    public  void onBackPressed()
    {
        finish();

    }
}

