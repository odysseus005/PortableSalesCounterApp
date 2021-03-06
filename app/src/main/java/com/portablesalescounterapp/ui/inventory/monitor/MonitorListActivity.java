package com.portablesalescounterapp.ui.inventory.monitor;

import android.Manifest;
import android.annotation.SuppressLint;
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
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;

import com.hannesdorfmann.mosby.mvp.viewstate.MvpViewStateActivity;
import com.hannesdorfmann.mosby.mvp.viewstate.ViewState;
import com.portablesalescounterapp.R;

import com.portablesalescounterapp.databinding.ActivityMonitorListBinding;
import com.portablesalescounterapp.databinding.DialogEditMonitor2Binding;
import com.portablesalescounterapp.databinding.DialogEditMonitorBinding;
import com.portablesalescounterapp.model.data.Category;
import com.portablesalescounterapp.model.data.Products;
import com.portablesalescounterapp.model.data.User;
import com.portablesalescounterapp.ui.guest.GuestActivity;
import com.portablesalescounterapp.util.DateTimeUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;

import io.realm.Case;
import io.realm.Realm;
import io.realm.RealmChangeListener;
import io.realm.RealmResults;
import io.realm.Sort;
import pl.aprilapps.easyphotopicker.EasyImage;


public class MonitorListActivity
        extends MvpViewStateActivity<MonitorListView, MonitorListPresenter>
        implements SwipeRefreshLayout.OnRefreshListener, MonitorListView {


    private static final int PERMISSION_READ_EXTERNAL_STORAGE = 124;
    private static final int PERMISSION_WRITE_EXTERNAL_STORAGE = 125;
    private static final int PERMISSION_CAMERA = 126;
    private ActivityMonitorListBinding binding;
    private Realm realm;
    private User user;
    private MonitorListAdapter adapterPromo;
    private RealmResults<Products> employeeRealmResults;
    private RealmResults<Category> categoryRealmResults;
    private ArrayList<Integer> categoryIdList;
    private Dialog dialog;
    private ProgressDialog progressDialog;
    private String searchText;
    private String sorter="Name";

    @SuppressWarnings("ConstantConditions")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
        searchText="";

        EasyImage.configuration(this)
                .setImagesFolderName("PSCApp")
                .saveInRootPicturesDirectory();
        realm = Realm.getDefaultInstance();
        user = realm.where(User.class).findFirst();
        binding = DataBindingUtil.setContentView(this, R.layout.activity_monitor_list);
        setSupportActionBar(binding.toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Product Inventory");

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
        adapterPromo = new MonitorListAdapter(this, getMvpView(),user.getEmail());
        binding.recyclerView.setAdapter(adapterPromo);
        employeeRealmResults = realm.where(Products.class).findAllAsync();
        employeeRealmResults.addChangeListener(new RealmChangeListener<RealmResults<Products>>() {
            @Override
            public void onChange(RealmResults<Products> element) {
             prepareList();

            }
        });
        binding.swipeRefreshLayout.post(new Runnable() {
            @Override
            public void run() {
                binding.swipeRefreshLayout.setRefreshing(true);
                onRefresh();
            }
        });



        // Spinner Drop down elements
        List<String> categories = new ArrayList<String>();
        categories.add("Name");
        categories.add("Product ID");
        categories.add("Quantity Ascending");
        categories.add("Quantity Descending");
        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, categories);
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        binding.spCategory.setAdapter(dataAdapter);


        binding.spCategory.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                String item = parent.getItemAtPosition(position).toString();

                // Showing selected spinner item

               // Toast.makeText(parent.getContext(), "Selected: " + item, Toast.LENGTH_LONG).show();
                sorter= item;
               prepareList();

            }

            @Override
            public void onNothingSelected(AdapterView<?> arg0) {


            }
        });

    }



    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);




        SearchView search = (SearchView) menu.findItem(R.id.action_search).getActionView();
        search.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                searchText = newText;
                prepareList();
                return true;
            }
        });


        return super.onCreateOptionsMenu(menu);

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
        presenter.onStop();
        employeeRealmResults.removeChangeListeners();
        realm.close();
        super.onDestroy();
    }

    @NonNull
    @Override
    public MonitorListPresenter createPresenter() {
        return new MonitorListPresenter();
    }

    @NonNull
    @Override
    public ViewState<MonitorListView> createViewState() {
        setRetainInstance(true);
        return new MonitorViewState();
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
    public void OnRestockEdit(final Products products) {



        dialog = new Dialog(MonitorListActivity.this);
        final DialogEditMonitorBinding dialogBinding = DataBindingUtil.inflate(
                getLayoutInflater(),
                R.layout.dialog_edit_monitor,
                null,
                false);


        if(products.getProductCode().equalsIgnoreCase("E"))
        {    dialogBinding.prodCode.setText(" pcs.");
            dialogBinding.prodCode2.setText(" pcs.");
        }
        else
        {
            dialogBinding.prodCode.setText(" kg");
            dialogBinding.prodCode2.setText(" kg");
        }


        dialogBinding.setProduct(products);

        dialogBinding.cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                dialog.dismiss();
            }
        });
        dialogBinding.send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                presenter.restockContact(Integer.toString(products.getProductId()),
                        products.getProductName(),
                        (Integer.parseInt(dialogBinding.etValue.getText().toString())+Integer.parseInt(products.getProductSKU()))+"",
                        dialogBinding.etValue.getText().toString(),
                        DateTimeUtils.getCurrentTimeStamp(),
                        user.getUserId()+"",
                        user.getFullName(),
                        user.getBusiness_id());
                //dialog.dismiss();
            }
        });

        dialog.setContentView(dialogBinding.getRoot());
        dialog.setCancelable(false);
        dialog.show();

    }



    @Override
    public void OnRemoveEdit(final Products products) {



        dialog = new Dialog(MonitorListActivity.this);
        final DialogEditMonitor2Binding dialogBinding = DataBindingUtil.inflate(
                getLayoutInflater(),
                R.layout.dialog_edit_monitor2,
                null,
                false);


        if(products.getProductCode().equalsIgnoreCase("E"))
        {    dialogBinding.prodCode.setText(" pcs.");
            dialogBinding.prodCode2.setText(" pcs.");
        }
        else
        {
            dialogBinding.prodCode.setText(" kg");
            dialogBinding.prodCode2.setText(" kg");
        }


        dialogBinding.setProduct(products);

        dialogBinding.cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                dialog.dismiss();
            }
        });
        dialogBinding.send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                presenter.restockContact(Integer.toString(products.getProductId()),
                        products.getProductName(),
                        (Integer.parseInt(products.getProductSKU())-Integer.parseInt(dialogBinding.etValue.getText().toString()))+"",
                        "-"+(dialogBinding.etValue.getText().toString()),
                        DateTimeUtils.getCurrentTimeStamp(),
                        user.getUserId()+"",
                        user.getFullName(),
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




    private void prepareList() {

        if (employeeRealmResults.isLoaded() && employeeRealmResults.isValid()) {
            List<Products> productsList;
            if (searchText.isEmpty()) {
                if(sorter.equalsIgnoreCase("name")) {
                    productsList = realm.copyFromRealm(employeeRealmResults.where().notEqualTo("productStatus","D").findAll().sort("productName", Sort.ASCENDING));
                }
                else if(sorter.equalsIgnoreCase("Product ID"))
                {
                    productsList = realm.copyFromRealm(employeeRealmResults.where().notEqualTo("productStatus","D").findAll().sort("productId", Sort.ASCENDING));

                }else if(sorter.equalsIgnoreCase("Quantity Ascending"))
                {
                    productsList = realm.copyFromRealm(employeeRealmResults.where().notEqualTo("productStatus","D").findAll().sort("productSKU", Sort.ASCENDING));

                }else if(sorter.equalsIgnoreCase("Quantity Descending"))
                {
                    productsList = realm.copyFromRealm(employeeRealmResults.where().notEqualTo("productStatus","D").findAll().sort("productSKU", Sort.DESCENDING));

                }else
                {
                    productsList = realm.copyFromRealm(employeeRealmResults.where().notEqualTo("productStatus","D").findAll().sort("productName", Sort.ASCENDING));

                }
            } else {

                if(sorter.equalsIgnoreCase("name")) {
                    productsList = realm.copyFromRealm(employeeRealmResults.where()
                            .contains("productName", searchText, Case.INSENSITIVE)
                            .or()
                            .contains("productSKU", searchText, Case.INSENSITIVE)
                            .notEqualTo("productStatus","D")
                            .findAll().sort("productName", Sort.ASCENDING));
                }
                else if(sorter.equalsIgnoreCase("Product ID"))
                {
                    productsList = realm.copyFromRealm(employeeRealmResults.where()
                            .contains("productName", searchText, Case.INSENSITIVE)
                            .or()
                            .contains("productSKU", searchText, Case.INSENSITIVE)
                            .notEqualTo("productStatus","D")
                            .findAll().sort("productId", Sort.ASCENDING));

                }else if(sorter.equalsIgnoreCase("Quantity Ascending"))
                {
                    productsList = realm.copyFromRealm(employeeRealmResults.where()
                            .contains("productName", searchText, Case.INSENSITIVE)
                            .or()
                            .contains("productSKU", searchText, Case.INSENSITIVE)
                            .notEqualTo("productStatus","D")
                            .findAll().sort("productSKU", Sort.ASCENDING));

                }else if(sorter.equalsIgnoreCase("Quantity Descending"))
                {
                    productsList = realm.copyFromRealm(employeeRealmResults.where()
                            .contains("productName", searchText, Case.INSENSITIVE)
                            .or()
                            .contains("productSKU", searchText, Case.INSENSITIVE)
                            .notEqualTo("productStatus","D")
                            .findAll().sort("productSKU", Sort.DESCENDING));

                }else
                {
                    productsList = realm.copyFromRealm(employeeRealmResults.where()
                            .contains("productName", searchText, Case.INSENSITIVE)
                            .or()
                            .contains("productSKU", searchText, Case.INSENSITIVE)
                            .notEqualTo("productStatus","D")
                            .findAll().sort("productName", Sort.ASCENDING));

                }


            }
            adapterPromo.setProductList(productsList);
            adapterPromo.notifyDataSetChanged();
        }
    }



}


