package com.portablesalescounterapp.ui.item.category;

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
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.ImageView;

import com.hannesdorfmann.mosby.mvp.viewstate.MvpViewStateActivity;
import com.hannesdorfmann.mosby.mvp.viewstate.ViewState;
import com.portablesalescounterapp.R;
import com.portablesalescounterapp.databinding.ActivityCategoryListBinding;
import com.portablesalescounterapp.databinding.DialogAddCategoryBinding;
import com.portablesalescounterapp.databinding.DialogEditCategoryBinding;
import com.portablesalescounterapp.model.data.Category;
import com.portablesalescounterapp.model.data.User;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

import io.realm.Realm;
import io.realm.RealmChangeListener;
import io.realm.RealmResults;
import pl.aprilapps.easyphotopicker.EasyImage;


public class CategoryListActivity
        extends MvpViewStateActivity<CategoryListView, CategoryListPresenter>
        implements SwipeRefreshLayout.OnRefreshListener, CategoryListView {



    private ActivityCategoryListBinding binding;
    private Realm realm;
    private User user;
    private CategoryListAdapter adapterPromo;
    private RealmResults<Category> employeeRealmResults;
    private Dialog dialog;
    private ProgressDialog progressDialog;
    private int emerID=0;

    @SuppressWarnings("ConstantConditions")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);

        EasyImage.configuration(this)
                .setImagesFolderName("PSCApp")
                .saveInRootPicturesDirectory();
        realm = Realm.getDefaultInstance();
        user = realm.where(User.class).findFirst();
        binding = DataBindingUtil.setContentView(this, R.layout.activity_category_list);
        setSupportActionBar(binding.toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Contact Person");

        presenter.onStart();
        // binding.swipeRefreshLayout.setColorSchemeColors(getResources().getIntArray(R.array.swipe_refresh_layout_color_scheme));
        binding.swipeRefreshLayout.setOnRefreshListener(this);
        binding.recyclerView.setItemAnimator(new DefaultItemAnimator());
        binding.recyclerView.setLayoutManager(new LinearLayoutManager(this));
        binding.recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                int topRowVerticalPosition = (recyclerView == null || recyclerView.getChildCount() == 0)
                        ? 0 : recyclerView.getChildAt(0).getTop();
                binding.swipeRefreshLayout.setEnabled(topRowVerticalPosition >= 0);
            }
        });
        adapterPromo = new CategoryListAdapter(this, getMvpView(),user.getEmail());
        binding.recyclerView.setAdapter(adapterPromo);
        employeeRealmResults = realm.where(Category.class).findAllAsync();
        employeeRealmResults.addChangeListener(new RealmChangeListener<RealmResults<Category>>() {
            @Override
            public void onChange(RealmResults<Category> element) {
               List<Category> promoList = realm.copyFromRealm(employeeRealmResults);
                adapterPromo.setCategoryList(promoList);
                adapterPromo.notifyDataSetChanged();

            }
        });
        binding.swipeRefreshLayout.post(new Runnable() {
            @Override
            public void run() {
                binding.swipeRefreshLayout.setRefreshing(true);
                onRefresh();
            }
        });
    }



    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_add, menu);
        return super.onCreateOptionsMenu(menu);

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
               onBackPressed();
                return true;

            case R.id.action_add:
                   add();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    protected void onDestroy() {
        presenter.onStop();
        employeeRealmResults.removeChangeListeners();
        realm.close();
        super.onDestroy();
    }

    @NonNull
    @Override
    public CategoryListPresenter createPresenter() {
        return new CategoryListPresenter();
    }

    @NonNull
    @Override
    public ViewState<CategoryListView> createViewState() {
        setRetainInstance(true);
        return new CategoryViewState();
    }

    @Override
    public void onNewViewStateInstance() {

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
    public void stopRefresh() {
        binding.swipeRefreshLayout.setRefreshing(false);
    }

    @Override
    public void showAlert(String message) {
        new AlertDialog.Builder(this)
                .setTitle(message)
                .setPositiveButton("Close", null)
                .show();
    }

    @Override
    public void dismiss() {
       dialog.dismiss();
    }

    @Override
    public void OnButtonDelete(Category category) {
        presenter.deleteContact(Integer.toString(category.getCategoryId()),user.getBusiness_id());
    }

    @Override
    public void OnButtonEdit(final Category emergency) {

        emerID = emergency.getCategoryId();

        dialog = new Dialog(CategoryListActivity.this);
        final DialogEditCategoryBinding dialogBinding = DataBindingUtil.inflate(
                getLayoutInflater(),
                R.layout.dialog_edit_category,
                null,
                false);




        dialogBinding.setCategory(emergency);

        dialogBinding.cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                emerID=0;
                dialog.dismiss();
            }
        });
        dialogBinding.send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                presenter.updateContact(Integer.toString(emergency.getCategoryId()),
                        dialogBinding.etName.getText().toString(),
                        dialogBinding.etDesc.getText().toString(),
                        user.getBusiness_id());
                //dialog.dismiss();
            }
        });

        dialog.setContentView(dialogBinding.getRoot());
        dialog.setCancelable(false);
        dialog.show();

    }

    @Override
    public void onRefresh() {
        presenter.load(""+user.getBusiness_id());
    }

    @Override
    public void onRefreshDelete() {
        presenter.load(""+user.getBusiness_id());
    }

    public void add()
    {

        realm = Realm.getDefaultInstance();
        user = realm.where(User.class).findFirst();
        dialog = new Dialog(CategoryListActivity.this);
        final DialogAddCategoryBinding dialogBinding = DataBindingUtil.inflate(
                getLayoutInflater(),
                R.layout.dialog_add_category,
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
                presenter.addContact(
                        user.getBusiness_id(),
                        dialogBinding.etName.getText().toString(),
                        dialogBinding.etDesc.getText().toString()
                        );
                //dialog.dismiss();
            }
        });
        dialog.setContentView(dialogBinding.getRoot());
        dialog.setCancelable(false);
        dialog.show();

    }


}


