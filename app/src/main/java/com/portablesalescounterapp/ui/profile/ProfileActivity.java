package com.portablesalescounterapp.ui.profile;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.bumptech.glide.Glide;
import com.hannesdorfmann.mosby.mvp.viewstate.MvpViewStateActivity;
import com.hannesdorfmann.mosby.mvp.viewstate.ViewState;
import com.portablesalescounterapp.R;
import com.portablesalescounterapp.app.Endpoints;
import com.portablesalescounterapp.databinding.ActivityProfileBinding;
import com.portablesalescounterapp.databinding.DialogChangePasswordBinding;
import com.portablesalescounterapp.model.data.User;
import com.portablesalescounterapp.ui.profile.edit.EditProfileActivity;
import com.portablesalescounterapp.util.CircleTransform;

import io.realm.Realm;
import io.realm.RealmChangeListener;
import io.realm.RealmModel;



public class ProfileActivity extends MvpViewStateActivity<ProfileView, ProfilePresenter>
        implements ProfileView, SwipeRefreshLayout.OnRefreshListener {

    private ActivityProfileBinding binding;
    private Realm realm;
    private User user;
    private ProgressDialog progressDialog;
    private Dialog dialog;

    @SuppressWarnings("ConstantConditions")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
        realm = Realm.getDefaultInstance();
        binding = DataBindingUtil.setContentView(this, R.layout.activity_profile);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("My Profile");
        //binding.swipeRefreshLayout.setColorSchemeColors(getResources().getIntArray(R.array.swipe_refresh_layout_color_scheme));
        binding.swipeRefreshLayout.setOnRefreshListener(this);

        Typeface typeface = Typeface.createFromAsset(getAssets(), "fonts/adam_reg.otf");
        Typeface face = Typeface.createFromAsset(getAssets(), "fonts/acid_reg.otf");

        binding.sex.setTypeface(face);
        binding.sex1.setTypeface(face);
        binding.mobile.setTypeface(face);
        binding.mobile1.setTypeface(face);
        binding.age.setTypeface(face);
        binding.age1.setTypeface(face);
        binding.changePass.setTypeface(typeface);

        binding.layoutHeader.tvNumber1.setTypeface(face);
        binding.layoutHeader.txtEmail.setTypeface(face);

        user = realm.where(User.class).findFirstAsync();
        user.addChangeListener(new RealmChangeListener<RealmModel>() {
            @Override
            public void onChange(RealmModel element) {
                if (user.isLoaded() && user.isValid()) {
                    binding.setUser(user);
                    String imageURL = "https://payapp.tip.edu.ph/api/storage/app/image/default_buyer.png";

                //    if (user.getImage() != null && !user.getImage().isEmpty()) {
                        imageURL = Endpoints.URL_IMAGE+user.getEmail();
                        Log.d("TAG",imageURL);
                    //}
                    Glide.with(ProfileActivity.this)
                            .load(imageURL)
                            .transform(new CircleTransform(ProfileActivity.this))
                           .skipMemoryCache(true)
                            .error(R.drawable.default_buyer)
                            .into(binding.layoutHeader.imageView);
                }
            }
        });
        binding.setView(getMvpView());
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_profile, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
            case R.id.action_edit_profile:
                startActivity(new Intent(this, EditProfileActivity.class));
                return true;


            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    protected void onDestroy() {
        user.removeChangeListeners();
        realm.close();
        super.onDestroy();
    }

    @Override
    public void onPasswordChanged() {
        if(dialog.isShowing()){
            dialog.dismiss();
            showAlert("Password Successfully Changed!");
        }
    }

    @NonNull
    @Override
    public ProfilePresenter createPresenter() {
        return new ProfilePresenter();
    }

    @NonNull
    @Override
    public ViewState<ProfileView> createViewState() {
        setRetainInstance(true);
        return new ProfileViewState();
    }

    @Override
    public void onNewViewStateInstance() {

    }

    @Override
    public void onRefresh() {
        // todo create refresh
        binding.swipeRefreshLayout.setRefreshing(false);
    }

    @Override
    public void onChangePasswordClicked() {

        dialog = new Dialog(ProfileActivity.this);
        final DialogChangePasswordBinding dialogBinding = DataBindingUtil.inflate(
                getLayoutInflater(),
                R.layout.dialog_change_password,
                null,
                false);
        dialogBinding.cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        dialogBinding.send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                presenter.changePassword(dialogBinding.etCurrPassword.getText().toString(),
                        dialogBinding.etNewPassword.getText().toString(),
                        dialogBinding.etConfirmPass.getText().toString());
            }
        });
        dialog.setContentView(dialogBinding.getRoot());
        dialog.setCancelable(false);
        dialog.show();
    }

    @Override
    public void showProgress() {
        if (progressDialog == null) {
            progressDialog = new ProgressDialog(this);
            progressDialog.setCancelable(false);
            progressDialog.setMessage("Connecting");
        }
        progressDialog.show();
    }

    @Override
    public void stopProgress() {
        if (progressDialog != null) progressDialog.dismiss();
    }

    @Override
    public void showAlert(String message) {
        new AlertDialog.Builder(this)
                .setTitle(message)
                .setPositiveButton("Close", null)
                .show();
    }
}
