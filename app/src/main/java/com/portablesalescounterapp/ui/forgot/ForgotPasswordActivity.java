package com.portablesalescounterapp.ui.forgot;

import android.app.ProgressDialog;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.MenuItem;
import android.widget.Toast;

import com.hannesdorfmann.mosby.mvp.MvpActivity;
import com.portablesalescounterapp.R;
import com.portablesalescounterapp.databinding.ActivityForgotPassBinding;


public class ForgotPasswordActivity extends MvpActivity<ForgotView, ForgotPresenter> implements ForgotView {

    private ActivityForgotPassBinding binding;
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_pass);

        binding = DataBindingUtil.setContentView(this, R.layout.activity_forgot_pass);
        binding.setView(getMvpView());


        setSupportActionBar(binding.toolbar);
        if (getSupportActionBar() != null)
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        //add text listener && disable submit button
        binding.email.addTextChangedListener(codeWatcher);
        binding.submit.setEnabled(false);
        binding.submit.setAlpha(.5f);


    }

    private final TextWatcher codeWatcher = new TextWatcher() {
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        public void onTextChanged(CharSequence s, int start, int before, int count) {
            if (s.length() > 0) {
                //enable submit button
                binding.submit.setEnabled(true);
                binding.submit.setAlpha(1f);
            } else {
                //disable submit button
                binding.submit.setEnabled(false);
                binding.submit.setAlpha(.5f);
            }
        }

        public void afterTextChanged(Editable s) {
            if (s.length() > 0) {
                //enable submit button
                binding.submit.setEnabled(true);
                binding.submit.setAlpha(1f);
            } else {
                //disable submit button
                binding.submit.setEnabled(false);
                binding.submit.setAlpha(.5f);
            }
        }
    };


    @Override
    public void startLoading(String s) {
        if (progressDialog == null) {
            progressDialog = new ProgressDialog(ForgotPasswordActivity.this);
            progressDialog.setCancelable(false);
            progressDialog.setMessage(s);
        }
        progressDialog.show();
    }

    @Override
    public void stopLoading() {
        if (progressDialog != null) progressDialog.dismiss();
    }

    //start of view
    @Override
    public void OnButtonSubmit() {
        presenter.submitEmail(binding.email.getText().toString());
    }

    @Override
    public void showAlert(String s) {
        Toast.makeText(this, s, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onEmailExist() {
        Toast.makeText(this, "Your Password has been reset and your new password will be send to your email!", Toast.LENGTH_LONG).show();
        //binding.panel1.setVisibility(View.GONE);
        finish();
    }



    //end of view

    @NonNull
    @Override
    public ForgotPresenter createPresenter() {
        return new ForgotPresenter();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
