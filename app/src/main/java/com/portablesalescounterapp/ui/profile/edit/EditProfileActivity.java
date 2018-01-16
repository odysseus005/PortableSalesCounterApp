package com.portablesalescounterapp.ui.profile.edit;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.databinding.DataBindingUtil;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.PopupMenu;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.hannesdorfmann.mosby.mvp.MvpActivity;
import com.portablesalescounterapp.R;
import com.portablesalescounterapp.app.Constants;
import com.portablesalescounterapp.app.Endpoints;
import com.portablesalescounterapp.databinding.ActivityEditProfileBinding;
import com.portablesalescounterapp.model.data.User;
import com.portablesalescounterapp.util.CircleTransform;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

import io.realm.Realm;

import pl.aprilapps.easyphotopicker.EasyImage;

public class EditProfileActivity extends MvpActivity<EditProfileView, EditProfilePresenter> implements EditProfileView {

    private static final int PERMISSION_READ_EXTERNAL_STORAGE = 124;
    private static final int PERMISSION_WRITE_EXTERNAL_STORAGE = 125;
    private static final int PERMISSION_CAMERA = 126;
    private ActivityEditProfileBinding binding;
    private Realm realm;
    private String TAG = EditProfileActivity.class.getSimpleName();
    private ProgressDialog progressDialog;
    private User user;
    private Dialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        EasyImage.configuration(this)
                .setImagesFolderName("VawApp")
                .saveInRootPicturesDirectory();
        setContentView(R.layout.activity_edit_profile);
        //setRetainInstance(true);
        realm = Realm.getDefaultInstance();
        binding = DataBindingUtil.setContentView(this, R.layout.activity_edit_profile);
        binding.setView(getMvpView());
        setSupportActionBar(binding.toolbar);
        if (getSupportActionBar() != null)
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        binding.setActivity(this);
        user = realm.where(User.class).findFirst();
        binding.setUser(user);

        presenter.onStart();
        loadImage();

    }

    /***
     * Start of MvpViewStateActivity
     ***/

    @NonNull
    @Override
    public EditProfilePresenter createPresenter() {
        return new EditProfilePresenter();
    }

    private void loadImage() {
        String imageURL = "https://payapp.tip.edu.ph/api/storage/app/image/default_buyer.png";
       // if (user.getImage() != null && !user.getImage().isEmpty()) {
            imageURL = Endpoints.URL_IMAGE+user.getEmail();
            Log.d("TAG",imageURL);
       // }
        Glide.with(this)
                .load(imageURL)
                .transform(new CircleTransform(this))
                .diskCacheStrategy(DiskCacheStrategy.NONE)
                .error(R.drawable.default_buyer)
                .into(binding.imageView);
    }
    public void onEdit() {

        presenter.updateUser(user.getUserId() + "",
                binding.firstName.getText().toString(),
                binding.lastName.getText().toString(),
                binding.contact.getText().toString(),
                binding.birthday.getText().toString(),
                binding.address.getText().toString(),"");
    }

    @Override
    public void showAlert(String s) {
        Toast.makeText(this, s, Toast.LENGTH_SHORT).show();
    }


    @Override
    public void startLoading() {
        if (progressDialog == null) {
            progressDialog = new ProgressDialog(this);
            progressDialog.setMessage("Updating...");
            progressDialog.setCancelable(false);
        }
        progressDialog.show();
    }

    @Override
    public void stopLoading() {
        if (progressDialog.isShowing()) {
            progressDialog.dismiss();
        }
    }

    @Override
    public void onChangePassword(){
        presenter.updateUser(user.getUserId() + "",
                binding.firstName.getText().toString(),
                binding.lastName.getText().toString(),
                binding.contact.getText().toString(),
                binding.birthday.getText().toString(),
                binding.address.getText().toString(),"");
    }


    @Override
    public void finishAct() {
        finish();
        showAlert("Profile Updated");
    }

    @Override
    public void onBirthdayClicked() {
        Calendar newCalendar = Calendar.getInstance();
        DatePickerDialog datePickerDialog = new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {
            SimpleDateFormat dateFormatter = new SimpleDateFormat("yyyy-MM-dd", Locale.US);

            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                Calendar newDate = Calendar.getInstance();
                newDate.set(year, monthOfYear, dayOfMonth);
                binding.birthday.setText(dateFormatter.format(newDate.getTime()));
            }

        }, newCalendar.get(Calendar.YEAR), newCalendar.get(Calendar.MONTH), newCalendar.get(Calendar.DAY_OF_MONTH));
        datePickerDialog.show();

    }

    @Override
    public void onPasswordChanged() {
        if(dialog.isShowing()){
            dialog.dismiss();
            showAlert("Password Successfully Changed!");
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.profile, menu);
        return true;
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

    @Override
    protected void onDestroy() {
        super.onDestroy();
        realm.close();
    }

    @Override
    protected void onStop() {
        super.onStop();
        presenter.onStop();
    }

    public void onChangeUserImage() {
        /*if (user.getFbID() != null && !user.getFbID().isEmpty()) {
            return;
        }*/

        Log.d("TAG",">>>");
        PopupMenu popupMenu = new PopupMenu(this, binding.btnChangeImage);
        popupMenu.inflate(R.menu.edit_user_image);
        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.action_select_picture:
                        selectPicture();
                        break;
                    case R.id.action_take_picture:
                        takePicture();
                        break;
                }
                return false;
            }
        });
        popupMenu.show();
    }


    private void takePicture() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
                requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, PERMISSION_WRITE_EXTERNAL_STORAGE);
            return;
        }
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
                requestPermissions(new String[]{Manifest.permission.CAMERA}, PERMISSION_CAMERA);
            return;
        }
        EasyImage.openCamera(this, 0);
    }

    private void selectPicture() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
                requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, PERMISSION_READ_EXTERNAL_STORAGE);
            return;
        }
        EasyImage.openGallery(this, 0);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        EasyImage.handleActivityResult(requestCode, resultCode, data, this, new EasyImage.Callbacks() {
            @Override
            public void onImagePickerError(Exception e, EasyImage.ImageSource source, int type) {
                e.printStackTrace();
                showAlert(e.getLocalizedMessage());
            }

            @Override
            public void onImagePicked(File imageFile, EasyImage.ImageSource source, int type) {
                Log.d(TAG, imageFile.getAbsolutePath());
                uploadImage(imageFile);
            }

            @Override
            public void onCanceled(EasyImage.ImageSource source, int type) {
                //Cancel handling, you might wanna remove taken photo if it was canceled
                if (source == EasyImage.ImageSource.CAMERA) {
                    File photoFile = EasyImage.lastlyTakenButCanceledPhoto(EditProfileActivity.this);
                    if (photoFile != null) //noinspection ResultOfMethodCallIgnored
                        photoFile.delete();
                }
            }
        });
    }

    private void uploadImage(final File imageFile) {
        @SuppressLint("InflateParams") View view = LayoutInflater.from(this).inflate(R.layout.design_image, null);
        ImageView imageView = (ImageView) view.findViewById(R.id.image_user);

        Bitmap bitmap = null;
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inPreferredConfig = Bitmap.Config.ARGB_8888;
        try {
            bitmap = BitmapFactory.decodeStream(new FileInputStream(imageFile), null, options);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        imageView.setImageBitmap(bitmap);

        new AlertDialog.Builder(this)
                .setTitle("Upload Profile Picture")
                .setView(view)
                .setPositiveButton("UPLOAD", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        presenter.upload(user.getEmail(),imageFile);
                    }
                })
                .setNegativeButton("CANCEL", null)
                .setCancelable(false)
                .show();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case PERMISSION_READ_EXTERNAL_STORAGE:
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) { // Permission Granted
                    EasyImage.openGallery(this, 0);
                } else { // Permission Denied
                    showAlert("Storage Read/Write Permission Denied");
                }
                break;
            case PERMISSION_WRITE_EXTERNAL_STORAGE:
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) { // Permission Granted
                    takePicture();
                } else { // Permission Denied
                    showAlert("Storage Read/Write Permission Denied");
                }
                break;
            case PERMISSION_CAMERA:
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) { // Permission Granted
                    takePicture();
                } else { // Permission Denied
                    showAlert("Camera Permission Denied");
                }
                break;
            default:
                super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }


}
