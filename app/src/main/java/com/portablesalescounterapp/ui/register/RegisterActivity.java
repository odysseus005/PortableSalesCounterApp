package com.portablesalescounterapp.ui.register;

import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.MenuItem;
import android.view.View;
import android.widget.DatePicker;
import android.widget.RadioButton;
import android.widget.Toast;

import com.hannesdorfmann.mosby.mvp.viewstate.MvpViewStateActivity;
import com.hannesdorfmann.mosby.mvp.viewstate.ViewState;
import com.portablesalescounterapp.R;
import com.portablesalescounterapp.databinding.ActivityRegisterBinding;
import com.portablesalescounterapp.ui.login.LoginActivity;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;



public class RegisterActivity extends MvpViewStateActivity<RegisterView, RegisterPresenter> implements RegisterView, TextWatcher {
    private ActivityRegisterBinding binding;
    private ProgressDialog progressDialog;
    private String etAddress;
    private String business_id;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        setRetainInstance(true);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_register);
        binding.setView(getMvpView());

      /*  setSupportActionBar(binding.toolbar);
        if (getSupportActionBar() != null)
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowTitleEnabled(true);
            getSupportActionBar().setTitle("Registration");
     */
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    /***
     * Start of MvpViewStateActivity
     ***/

    @NonNull
    @Override
    public RegisterPresenter createPresenter() {
        return new RegisterPresenter();
    }

    @NonNull
    @Override
    public ViewState<RegisterView> createViewState() {
        setRetainInstance(true);
        return new RegisterViewState();
    }

    @Override
    public void onNewViewStateInstance() {
        initializeViewStateValues();

    }


    /***
     * End of MvpViewStateActivity
     ***/

    @Override
    public void onNext()
    {
        binding.regBusinessgroup.setVisibility(View.VISIBLE);
        binding.regUsergroup.setVisibility(View.GONE);
        presenter.registerUser(
                binding.etEmail.getText().toString(),
                binding.etPassword.getText().toString(),
                binding.etRepeatPassword.getText().toString(),
                binding.etFirstName.getText().toString(),
                binding.etLastName.getText().toString(),
                binding.etBirthday.getText().toString(),
                binding.etMobileNumber.getText().toString(),
                binding.etAddress.getText().toString(),
                "Owner",
                business_id);
    }


    @Override
    public void onSubmit() {


        presenter.registerBusiness(binding.etBusinessname.getText().toString(),
        binding.etBusinessaddress.getText().toString(),
        binding.etBusinesscontact.getText().toString(),
        binding.etBusinessdescription.getText().toString());




    }

    @Override
    public void showAlert(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void setEditTextValue(String email, String password, String confirmPassword, String firstName, String lastName, String birthday, String contact, String address) {
        binding.etEmail.setText(email);
        binding.etPassword.setText(password);
        binding.etRepeatPassword.setText(confirmPassword);
        binding.etFirstName.setText(firstName);
        binding.etLastName.setText(lastName);
        binding.etBirthday.setText(birthday);
        binding.etMobileNumber.setText(contact);
        binding.etAddress.setText(address);

    }

    @Override
    public void startLoading() {
        if (progressDialog == null) {
            progressDialog = new ProgressDialog(RegisterActivity.this);
            progressDialog.setCancelable(false);
            progressDialog.setMessage("Signing up...");
        }
        progressDialog.show();
    }

    @Override
    public void stopLoading() {
        if (progressDialog != null) progressDialog.dismiss();
    }


    @Override
    public void onRegistrationSuccess() {
        new AlertDialog.Builder(this)
                .setTitle("Register Successful")
                .setMessage("Go Back to Login Page Thank you!")
                .setCancelable(false)
                .setPositiveButton("Close", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        RegisterActivity.this.finish();
                       // Toast.makeText(RegisterActivity.this, "An email has been sent to your email for verification!", Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(RegisterActivity.this, LoginActivity.class));
                        finish();
                    }
                })
                .show();
    }

    @Override
    public void onBusinessRegistrationSuccess(String business_id) {

        binding.regBusinessgroup.setVisibility(View.GONE);
        binding.regUsergroup.setVisibility(View.VISIBLE);

        business_id = business_id;


    }




    @Override
    public void onBirthdayClicked() {
        Calendar newCalendar = Calendar.getInstance();
        DatePickerDialog datePickerDialog = new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {
            SimpleDateFormat dateFormatter = new SimpleDateFormat("yyyy-MM-dd", Locale.US);

            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                Calendar newDate = Calendar.getInstance();
                newDate.set(year, monthOfYear, dayOfMonth);
                binding.etBirthday.setText(dateFormatter.format(newDate.getTime()));
            }

        }, newCalendar.get(Calendar.YEAR), newCalendar.get(Calendar.MONTH), newCalendar.get(Calendar.DAY_OF_MONTH));
        datePickerDialog.show();

    }



    @Override
    public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

    }

    @Override
    public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
        initializeViewStateValues();
    }

    @Override
    public void afterTextChanged(Editable editable) {

    }

    @Override
    public void onBackPressed()
    {
        startActivity(new Intent(this, LoginActivity.class));
        finish();
    }



    private void initializeViewStateValues() {
        RegisterViewState registerViewState = (RegisterViewState) getViewState();

    }

}
