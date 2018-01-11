package com.portablesalescounterapp.ui.item.product;

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
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.ImageView;

import com.hannesdorfmann.mosby.mvp.viewstate.MvpViewStateActivity;
import com.hannesdorfmann.mosby.mvp.viewstate.ViewState;
import com.portablesalescounterapp.R;

import com.portablesalescounterapp.databinding.ActivityProductListBinding;
import com.portablesalescounterapp.databinding.DialogAddProductBinding;
import com.portablesalescounterapp.databinding.DialogEditProductBinding;
import com.portablesalescounterapp.model.data.Category;
import com.portablesalescounterapp.model.data.Products;
import com.portablesalescounterapp.model.data.User;
import com.portablesalescounterapp.ui.item.discount.DiscountListActivity;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

import io.realm.Case;
import io.realm.Realm;
import io.realm.RealmChangeListener;
import io.realm.RealmResults;
import pl.aprilapps.easyphotopicker.EasyImage;


public class ProductListActivity
        extends MvpViewStateActivity<ProductListView, ProductListPresenter>
        implements SwipeRefreshLayout.OnRefreshListener, ProductListView {


    private static final int PERMISSION_READ_EXTERNAL_STORAGE = 124;
    private static final int PERMISSION_WRITE_EXTERNAL_STORAGE = 125;
    private static final int PERMISSION_CAMERA = 126;
    private ActivityProductListBinding binding;
    private Realm realm;
    private User user;
    private ProductListAdapter adapterPromo;
    private RealmResults<Products> employeeRealmResults;
    private RealmResults<Category> categoryRealmResults;
    private ArrayList<Integer> categoryIdList;
    private Dialog dialog;
    private ProgressDialog progressDialog;
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
        binding = DataBindingUtil.setContentView(this, R.layout.activity_product_list);
        setSupportActionBar(binding.toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Product");

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
        adapterPromo = new ProductListAdapter(this, getMvpView(),user.getEmail());
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
        getMenuInflater().inflate(R.menu.menu_add, menu);



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
    public ProductListPresenter createPresenter() {
        return new ProductListPresenter();
    }

    @NonNull
    @Override
    public ViewState<ProductListView> createViewState() {
        setRetainInstance(true);
        return new ProductViewState();
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
    public void OnButtonDelete(Products products) {
        presenter.deleteContact(Integer.toString(products.getProductId()),user.getBusiness_id());
    }

    @Override
    public void OnButtonEdit(final Products products) {

        emerID = products.getProductId();

        dialog = new Dialog(ProductListActivity.this);
        final DialogEditProductBinding dialogBinding = DataBindingUtil.inflate(
                getLayoutInflater(),
                R.layout.dialog_edit_product,
                null,
                false);
        dialogBinding.btnChangeImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PopupMenu popupMenu = new PopupMenu(ProductListActivity.this, dialogBinding.btnChangeImage);
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
        });



        if(products.getProductCode().equalsIgnoreCase("W"))
        {
            dialogBinding.weight.setBackgroundColor(ContextCompat.getColor(ProductListActivity.this, R.color.lightGray));
            dialogBinding.each.setBackgroundColor(ContextCompat.getColor(ProductListActivity.this, R.color.colorPrimary));

        }


        dialogBinding.each.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                productCode = "E";
                dialogBinding.each.setBackgroundColor(ContextCompat.getColor(ProductListActivity.this, R.color.lightGray));
                dialogBinding.weight.setBackgroundColor(ContextCompat.getColor(ProductListActivity.this, R.color.colorPrimary));

            }
        });

        dialogBinding.weight.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                productCode = "W";
                dialogBinding.weight.setBackgroundColor(ContextCompat.getColor(ProductListActivity.this, R.color.lightGray));
                dialogBinding.each.setBackgroundColor(ContextCompat.getColor(ProductListActivity.this, R.color.colorPrimary));

            }
        });

        categoryIdList = new ArrayList<>();
        final List<String> promoList = new ArrayList<>();

        presenter.getCategory(user.getBusiness_id());
        categoryRealmResults = realm.where(Category.class).findAll();


        categoryRealmResults.addChangeListener(new RealmChangeListener<RealmResults<Category>>() {
            @Override
            public void onChange(RealmResults<Category> element) {
                categoryIdList.add(0);
                categoryId = 0+"";
                int currCateg=1;
                int ctr=1;
                promoList.add("Select Category");
                for (Category category : categoryRealmResults) {
                    if(category.getCategoryId()==(products.getCategoryId())) {
                        currCateg = ctr;
                        categoryId = ""+ctr;
                    }
                    promoList.add(category.getCategoryName());
                    categoryIdList.add(category.getCategoryId());
                    ctr++;
                }


                ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(ProductListActivity.this, R.layout.spinner_custom_item, promoList);
                dialogBinding.spCategory.setAdapter(arrayAdapter);
                dialogBinding.spCategory.setSelection(currCateg);
            }
        });


        /**
         * Triggers on click of the spinner
         */
        dialogBinding.spCategory.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                categoryId = ""+(categoryIdList.get(position));
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });




        dialogBinding.setProduct(products);

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


                presenter.updateContact(Integer.toString(products.getProductId()),
                        dialogBinding.etName.getText().toString(),
                        dialogBinding.etDescription.getText().toString(),
                        dialogBinding.etPrice.getText().toString(),
                        productCode,
                        dialogBinding.etBarcode.getText().toString(),
                        categoryId,
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
        dialog = new Dialog(ProductListActivity.this);

        dialog.getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.MATCH_PARENT);

        final DialogAddProductBinding dialogBinding = DataBindingUtil.inflate(
                getLayoutInflater(),
                R.layout.dialog_add_product,
                null,
                false);




        dialogBinding.each.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                productCode = "E";
                dialogBinding.each.setBackgroundColor(ContextCompat.getColor(ProductListActivity.this, R.color.lightGray));
                dialogBinding.weight.setBackgroundColor(ContextCompat.getColor(ProductListActivity.this, R.color.colorPrimary));

            }
        });

        dialogBinding.weight.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                productCode = "W";
                dialogBinding.weight.setBackgroundColor(ContextCompat.getColor(ProductListActivity.this, R.color.lightGray));
                dialogBinding.each.setBackgroundColor(ContextCompat.getColor(ProductListActivity.this, R.color.colorPrimary));

            }
        });

        categoryIdList = new ArrayList<>();
        final List<String> promoList = new ArrayList<>();

        presenter.getCategory(user.getBusiness_id());
        categoryRealmResults = realm.where(Category.class).findAll();

        categoryRealmResults.addChangeListener(new RealmChangeListener<RealmResults<Category>>() {
            @Override
            public void onChange(RealmResults<Category> element) {
                categoryIdList.add(0);
                categoryId = 0+"";
                promoList.add("Select Category");
                for (Category category : categoryRealmResults) {
                    promoList.add(category.getCategoryName());
                    categoryIdList.add(category.getCategoryId());
                }


                ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(ProductListActivity.this, R.layout.spinner_custom_item, promoList);
                dialogBinding.spCategory.setAdapter(arrayAdapter);
            }
        });

        /**
         * Triggers on click of the spinner
         */
        dialogBinding.spCategory.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                categoryId = ""+(categoryIdList.get(position));
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });



        dialogBinding.cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        dialogBinding.send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                presenter.addContact(dialogBinding.etName.getText().toString(),
                        dialogBinding.etDescription.getText().toString(),
                        dialogBinding.etPrice.getText().toString(),
                        productCode,
                        dialogBinding.etBarcode.getText().toString(),
                        categoryId,
                        user.getBusiness_id());
                //dialog.dismiss();
            }
        });
        dialog.setContentView(dialogBinding.getRoot());
        dialog.setCancelable(false);
        dialog.show();

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
               // Log.d(TAG, imageFile.getAbsolutePath());
                uploadImage(imageFile);
            }

            @Override
            public void onCanceled(EasyImage.ImageSource source, int type) {
                //Cancel handling, you might wanna remove taken photo if it was canceled
                if (source == EasyImage.ImageSource.CAMERA) {
                    File photoFile = EasyImage.lastlyTakenButCanceledPhoto(ProductListActivity.this);
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
                        if(emerID!=0)
                        presenter.upload(user.getEmail()+emerID+emerID,imageFile);
                        else
                            showAlert("Error on Uploading Image");
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



    private void prepareList() {

        if (employeeRealmResults.isLoaded() && employeeRealmResults.isValid()) {
            List<Products> productsList;
            if (searchText.isEmpty()) {
                productsList = realm.copyFromRealm(employeeRealmResults);
            } else {
                productsList = realm.copyFromRealm(employeeRealmResults.where()
                        .contains("productName", searchText, Case.INSENSITIVE)
                        .or()
                        .contains("productDescription", searchText, Case.INSENSITIVE)
                        .or()
                        .contains("productPrice", searchText, Case.INSENSITIVE)
                        .findAll());
            }
            adapterPromo.setProductList(productsList);
            adapterPromo.notifyDataSetChanged();
        }
    }











}


