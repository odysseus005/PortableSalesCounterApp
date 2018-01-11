package com.portablesalescounterapp.ui.manageqr;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.databinding.DataBindingUtil;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
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
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.hannesdorfmann.mosby.mvp.viewstate.MvpViewStateActivity;
import com.hannesdorfmann.mosby.mvp.viewstate.ViewState;
import com.journeyapps.barcodescanner.BarcodeEncoder;
import com.portablesalescounterapp.R;

import com.portablesalescounterapp.databinding.ActivityProductQrListBinding;
import com.portablesalescounterapp.databinding.DialogEditProductQrBinding;
import com.portablesalescounterapp.model.data.Category;
import com.portablesalescounterapp.model.data.Products;
import com.portablesalescounterapp.model.data.User;
import com.portablesalescounterapp.util.BarCodeUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import io.realm.Case;
import io.realm.Realm;
import io.realm.RealmChangeListener;
import io.realm.RealmResults;
import pl.aprilapps.easyphotopicker.EasyImage;


public class ProductQrListActivity
        extends MvpViewStateActivity<ProductQrListView, ProductQrListPresenter>
        implements SwipeRefreshLayout.OnRefreshListener, ProductQrListView {


    private static final int PERMISSION_READ_EXTERNAL_STORAGE = 124;
    private static final int PERMISSION_WRITE_EXTERNAL_STORAGE = 125;
    private static final int PERMISSION_CAMERA = 126;
    private ActivityProductQrListBinding binding;
    private Realm realm;
    private User user;
    private ProductQrListAdapter adapterPromo;
    private RealmResults<Products> employeeRealmResults;
    private RealmResults<Category> categoryRealmResults;
    private ArrayList<Integer> categoryIdList;
    private Dialog dialog;
    private ProgressDialog progressDialog;
    private Products prodAdd;
    private int emerID=0;
    private  String productCode = "E", categoryId;
    private String searchText;

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
        binding = DataBindingUtil.setContentView(this, R.layout.activity_product_qr_list);
        setSupportActionBar(binding.toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Manage Product Code");

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
        adapterPromo = new ProductQrListAdapter(this, getMvpView(),user.getEmail());
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
    public ProductQrListPresenter createPresenter() {
        return new ProductQrListPresenter();
    }

    @NonNull
    @Override
    public ViewState<ProductQrListView> createViewState() {
        setRetainInstance(true);
        return new ProductQrViewState();
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
    public void showToast(String message) {
        Toast.makeText(this, message,
                Toast.LENGTH_LONG).show();
    }

    @Override
    public void dismiss() {
       //dialog.dismiss();

        if(prodAdd!=null)
        viewQr(prodAdd);

    }


    @Override
    public void OnButtonDelete(final Products products) {


        viewQr(products);

    }


        @Override
    public void OnButtonAdd(final Products products) {

            presenter.updateContact(Integer.toString(products.getProductId()),
                    "psc"+(products.getProductId()+1000)+"",
                    "C",
                    products.getProductName(),
                    user.getBusiness_id());
            //dialog.dismiss();

            prodAdd = products;


    }


    public void viewQr(final Products product)
    {

        prodAdd = null;

        dialog = new Dialog(ProductQrListActivity.this);
        final DialogEditProductQrBinding dialogBinding = DataBindingUtil.inflate(
                getLayoutInflater(),
                R.layout.dialog_edit_product_qr,
                null,
                false);




        dialogBinding.setProduct(product);

        try {


            Bitmap bitmap =  TextToImageEncode("psc"+(product.getProductId()+1000)+"",ProductQrListActivity.this);

            dialogBinding.productQR.setImageBitmap(bitmap);
            dialogBinding.productQRcodeView.setText((product.getProductId()+1000)+"");
        } catch (Exception e) {
            showAlert("Error Displaying Qr Code");
            e.printStackTrace();
        }

        dialogBinding.cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                presenter.updateContact(Integer.toString(product.getProductId()),
                        "",
                        "D",
                        product.getProductName(),
                        user.getBusiness_id());
                dialog.dismiss();
            }
        });
        dialogBinding.send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                takeScreenshot(dialogBinding.qrScreenshot,product.getProductName());

            }
        });

        dialogBinding.close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                dialog.dismiss();

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


    public  Bitmap TextToImageEncode(String value, Context context) throws WriterException {

        Bitmap bitmap = null;

        MultiFormatWriter multiFormatWriter = new MultiFormatWriter();
        try {
            BitMatrix bitMatrix = multiFormatWriter.encode(value, BarcodeFormat.QR_CODE,200,200);
            BarcodeEncoder barcodeEncoder = new BarcodeEncoder();
             bitmap = barcodeEncoder.createBitmap(bitMatrix);

        } catch (WriterException e) {
            e.printStackTrace();
        }

        return bitmap;

    }


    private void takeScreenshot(View v1,String productName) {
        Date now = new Date();
        android.text.format.DateFormat.format("yyyy-MM-dd_hh:mm:ss", now);

        try {
            // image naming and path  to include sd card  appending name you choose for file
            String mPath = Environment.getExternalStorageDirectory().toString() + "/Download/" +productName+now + ".jpg";

            // create bitmap screen capture
            // v1 = getWindow().getDecorView();
            v1.setDrawingCacheEnabled(true);
            Bitmap bitmap = Bitmap.createBitmap(v1.getDrawingCache());
            v1.setDrawingCacheEnabled(false);

            File imageFile = new File(mPath);

            FileOutputStream outputStream = new FileOutputStream(imageFile);
            int quality = 100;
            bitmap.compress(Bitmap.CompressFormat.JPEG, quality, outputStream);
            outputStream.flush();
            outputStream.close();

            openScreenshot(imageFile);
        } catch (Throwable e) {
            // Several error may come out with file handling or DOM
            e.printStackTrace();
        }
    }



    private void openScreenshot(File imageFile) {
        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_VIEW);
        Uri uri = Uri.fromFile(imageFile);
        intent.setDataAndType(uri, "image/*");
        startActivity(intent);
    }

    private void prepareList() {

        if (employeeRealmResults.isLoaded() && employeeRealmResults.isValid()) {
            List<Products> productsList;
            if (searchText.isEmpty()) {
                productsList = realm.copyFromRealm(employeeRealmResults);
            } else {
                productsList = realm.copyFromRealm(employeeRealmResults.where()
                        .contains("firstname", searchText, Case.INSENSITIVE)
                        .or()
                        .contains("lastname", searchText, Case.INSENSITIVE)
                        .or()
                        .contains("birthday", searchText, Case.INSENSITIVE)
                        .or()
                        .contains("address", searchText, Case.INSENSITIVE)
                        .or()
                        .contains("contact", searchText, Case.INSENSITIVE)
                        .findAll());
            }
            adapterPromo.setProductList(productsList);
            adapterPromo.notifyDataSetChanged();
        }
    }


}


