package com.portablesalescounterapp.ui.main;

import android.Manifest;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.databinding.DataBindingUtil;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.design.widget.NavigationView;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.zxing.integration.android.IntentIntegrator;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.hannesdorfmann.mosby.mvp.viewstate.MvpViewStateActivity;
import com.hannesdorfmann.mosby.mvp.viewstate.ViewState;
import com.portablesalescounterapp.BuildConfig;
import com.portablesalescounterapp.R;
import com.portablesalescounterapp.app.Endpoints;
import com.portablesalescounterapp.databinding.ActivityMainBinding;
import com.portablesalescounterapp.databinding.DialogAddCartBinding;
import com.portablesalescounterapp.databinding.DialogCartBinding;
import com.portablesalescounterapp.databinding.DialogDiscountBinding;
import com.portablesalescounterapp.databinding.DialogReceiptBinding;
import com.portablesalescounterapp.model.data.Business;
import com.portablesalescounterapp.model.data.Category;
import com.portablesalescounterapp.model.data.Discount;
import com.portablesalescounterapp.model.data.Products;
import com.portablesalescounterapp.model.data.Transaction;
import com.portablesalescounterapp.model.data.User;
import com.portablesalescounterapp.ui.inventory.InventoryActivity;
import com.portablesalescounterapp.ui.item.ItemActivity;
import com.portablesalescounterapp.ui.item.product.ProductListActivity;
import com.portablesalescounterapp.ui.login.LoginActivity;
import com.portablesalescounterapp.ui.manageqr.ProductQrListActivity;
import com.portablesalescounterapp.ui.manageuser.EmployeeListActivity;
import com.portablesalescounterapp.ui.profile.ProfileActivity;
import com.portablesalescounterapp.ui.receipts.CartreceiptActivityAdapter;
import com.portablesalescounterapp.ui.receipts.ReceiptListActivity;
import com.portablesalescounterapp.ui.reports.salesReport.SaleChartActivity;
import com.portablesalescounterapp.util.AnyOrientationCaptureActivity;
import com.portablesalescounterapp.util.CircleTransform;
import com.portablesalescounterapp.util.DateTimeUtils;
import com.portablesalescounterapp.util.GridSpacingItemDecoration;

import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import io.realm.Case;
import io.realm.Realm;
import io.realm.RealmChangeListener;
import io.realm.RealmModel;
import io.realm.RealmResults;


public class MainActivity
        extends MvpViewStateActivity<MainActivityView, MainActivityPresenter>
        implements SwipeRefreshLayout.OnRefreshListener , NavigationView.OnNavigationItemSelectedListener, MainActivityView {

    private final int PERMISSION_CODE = 9235;
    private static final String TAG = MainActivity.class.getSimpleName();
    private ActivityMainBinding binding;
    private CartDiscountListAdapter adapterDiscount;
    private MainActivityAdapter adapterPromo;
    private CartMainActivityAdapter adapterCart;
    private Business business;
    private RealmResults<Discount> discountRealmResults;
    private RealmResults<Products> employeeRealmResults;
    private RealmResults<Category> categoryRealmResults;
    private ArrayList<Integer> categoryIdList;
    private List<Products> productList;
    private ArrayList<String> prodIdcart;
    private ArrayList<String> prodNamecart;
    private ArrayList<String> prodQuantitycart;
    private ArrayList<String> prodPricecart;
    private TextView txtName;
    private TextView txtEmail;
    private ImageView imgProfile;
    private Realm realm;
    private User user;
    private Dialog dialog,dialog2;
    DialogCartBinding dialogBinding;
    private ProgressDialog progressDialog;
    private Products currProduct;
    private Transaction transQr;
    String cashCode="Cash",discountId="",discountName="",discountValue="0",discountCode="",oldTotal="";
    String vtsPrice="",vtsDiscount="";
    public double newPrice;
    private String searchText;
    private String filterCategory="";
    private ReceiptActivityAdapter adapterCart2;
    boolean qrSwitcher=true;

    @SuppressWarnings("ConstantConditions")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);

        searchText = "";
        realm = Realm.getDefaultInstance();
        user = realm.where(User.class).findFirst();
        business = realm.where(Business.class).findFirst();

        if (user == null) {
            Log.e(TAG, "No User found");
            finish();
        }

        productList = new ArrayList<>();
        prodIdcart = new ArrayList<>();
        prodNamecart = new ArrayList<>();
        prodQuantitycart = new ArrayList<>();
        prodPricecart = new ArrayList<>();


        binding = DataBindingUtil.setContentView(this, R.layout.activity_main);
        binding.appBarMain.setView(getMvpView());

        setSupportActionBar(binding.appBarMain.toolbar);
        getSupportActionBar().setTitle(BuildConfig.DEBUG ? "PSC Ap" : "PSC App");

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, binding.drawerLayout,
                binding.appBarMain.toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        binding.drawerLayout.addDrawerListener(toggle);
        toggle.syncState();
        binding.navView.setNavigationItemSelectedListener(this);


        txtName = (TextView) binding.navView.getHeaderView(0).findViewById(R.id.tvNumber1);
        txtEmail = (TextView) binding.navView.getHeaderView(0).findViewById(R.id.txt_email);
        imgProfile = (ImageView) binding.navView.getHeaderView(0).findViewById(R.id.imageView);

        user = realm.where(User.class).findFirstAsync();
        user.addChangeListener(new RealmChangeListener<RealmModel>() {
            @Override
            public void onChange(RealmModel element) {
                if (user.isLoaded() && user.isValid())
                    updateUI();
            }
        });

        presenter.onStart();

        binding.navView.setNavigationItemSelectedListener(this);
        //binding.swipeRefreshLayout.setColorSchemeColors(getResources().getIntArray(R.array.swipe_refresh_layout_color_scheme));
        binding.appBarMain.swipeRefreshLayout.setOnRefreshListener(this);
        binding.appBarMain.recyclerView.setLayoutManager(new GridLayoutManager(this, 2));
        binding.appBarMain.recyclerView.setItemAnimator(new DefaultItemAnimator());
        int spanCount = 2; // 3 columns
        int spacing = 20; // 50px
        boolean includeEdge = true;
        binding.appBarMain.recyclerView.addItemDecoration(new GridSpacingItemDecoration(spanCount, spacing, includeEdge));


        hideItem();

        binding.appBarMain.recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                int topRowVerticalPosition = (recyclerView == null || recyclerView.getChildCount() == 0)
                        ? 0 : recyclerView.getChildAt(0).getTop();
                binding.appBarMain.swipeRefreshLayout.setEnabled(topRowVerticalPosition >= 0);
            }
        });
        adapterPromo = new MainActivityAdapter(this, getMvpView());
        binding.appBarMain.recyclerView.setAdapter(adapterPromo);
        employeeRealmResults = realm.where(Products.class).findAllAsync();
        employeeRealmResults.addChangeListener(new RealmChangeListener<RealmResults<Products>>() {
            @Override
            public void onChange(RealmResults<Products> element) {

                prepareList();


            }
        });
        binding.appBarMain.swipeRefreshLayout.post(new Runnable() {
            @Override
            public void run() {
                binding.appBarMain.swipeRefreshLayout.setRefreshing(true);
                onRefresh();
            }
        });






        final List<String> promoList = new ArrayList<>();
        categoryIdList = new ArrayList<>();
        categoryRealmResults = realm.where(Category.class).findAll();

        categoryRealmResults.addChangeListener(new RealmChangeListener<RealmResults<Category>>() {
            @Override
            public void onChange(RealmResults<Category> element) {
                categoryIdList.clear();
                promoList.clear();
                categoryIdList.add(0);
                promoList.add("Select Category");
                for (Category category : categoryRealmResults) {
                    promoList.add(category.getCategoryName());
                    categoryIdList.add(category.getCategoryId());
                }


                ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(MainActivity.this, R.layout.spinner_custom_item, promoList);
               binding.appBarMain.spCategory.setAdapter(arrayAdapter);

            }


        });

        binding.appBarMain.spCategory.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> arg0, View arg1,
                                       int position, long id) {

                filterCategory = ""+(categoryIdList.get(position));
                Log.d("TAG>>>",filterCategory);
                prepareList();
            }

            @Override
            public void onNothingSelected(AdapterView<?> arg0) {

                filterCategory="";
                prepareList();
            }
        });



    }



    private void hideItem()
    {
        realm = Realm.getDefaultInstance();
        user = realm.where(User.class).findFirst();
        Menu nav_Menu =  binding.navView.getMenu();

        if((user.getPosition()).equalsIgnoreCase("cashier"))
        {
            nav_Menu.findItem(R.id.nav_items).setVisible(false);
            nav_Menu.findItem(R.id.nav_manageusers).setVisible(false);
            nav_Menu.findItem(R.id.nav_qrcode).setVisible(false);
            nav_Menu.findItem(R.id.nav_inventory).setVisible(false);
            nav_Menu.findItem(R.id.nav_qrcode).setVisible(false);

        }else if((user.getPosition()).equalsIgnoreCase("inventory custodian"))
        {

            nav_Menu.findItem(R.id.nav_manageusers).setVisible(false);
            nav_Menu.findItem(R.id.nav_receipts).setVisible(false);
            nav_Menu.findItem(R.id.nav_sales_png).setVisible(false);


        }
    }


    @RequiresApi(api = Build.VERSION_CODES.M)
    public void ScanBar (View view ) {
        requestScan();
    }

    // fucntion to scan barcode
    @RequiresApi(api = Build.VERSION_CODES.M)
    public void requestScan(){
        if (checkSelfPermission(Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.CAMERA}, PERMISSION_CODE);
        }else{
            startScan();
        }
    }


    public void startScan(){

        IntentIntegrator integrator = new IntentIntegrator(this);
        integrator.setCaptureActivity(AnyOrientationCaptureActivity.class);
        integrator.setDesiredBarcodeFormats(IntentIntegrator.ALL_CODE_TYPES);
        integrator.setOrientationLocked(false);
      //  integrator.setBeepEnabled(false);
        integrator.setPrompt("Scan Product Bar Code/Qr Code");
        integrator.setCameraId(0);  // Use a specific camera of the device
        integrator.setBeepEnabled(false);
        integrator.setBarcodeImageEnabled(true);
        integrator.initiateScan();

    }


    @Override
    protected void onActivityResult ( int requestCode, int resultCode, Intent in ) {
        // TODO Auto-generated method stub

        if( requestCode == IntentIntegrator.REQUEST_CODE ){


            if( resultCode == RESULT_OK ){
                String contents = in.getStringExtra( "SCAN_RESULT" );

                if(qrSwitcher) {
                    currProduct = presenter.getProductQr(contents);
                    if (currProduct.isLoaded() && currProduct.isValid())
                        OnButtonAddtoCart();

                }else
                {
                    transQr = presenter.getTransactionQr(contents);
                    if (transQr.isLoaded() && transQr.isValid())
                        onTransactionSuccess(transQr);

                        //OnButtonAddtoCart();
                }
            }
        }
    }



    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PERMISSION_CODE) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                startScan();
            }
            else {
                Toast.makeText(this, "Failed", Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);


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


        LinearLayout v2=(LinearLayout) menu.findItem(R.id.item_qrscan).getActionView();

        Button count2=(Button)v2.findViewById(R.id.scan);



        count2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                startScan();
                qrSwitcher=true;
            }
        });


        LinearLayout v3=(LinearLayout) menu.findItem(R.id.item_trscan).getActionView();

        Button count3=(Button)v3.findViewById(R.id.scan);



        count3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                startScan();
                qrSwitcher=false;
            }
        });



        LinearLayout v=(LinearLayout) menu.findItem(R.id.item_samplebadge).getActionView();

        Button count1=(Button)v.findViewById(R.id.counter);


        count1.setText(String.valueOf(prodIdcart.size()));

        count1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(prodIdcart.size()>0)
                checkout();
                else
                    showError("No Item on the Cart!");
            }
        });

        return true;
    }

    //GuestActivity Methods start

    private void updateUI() {
        txtName.setText(user.getFullName());
        txtEmail.setText(user.getEmail());
        String imageURL = "";

        if (user.getImage() != null && !user.getImage().isEmpty()) {
            imageURL = Endpoints.URL_IMAGE+user.getImage();//Endpoints.IMAGE_URL.replace(Endpoints.IMG_HOLDER, user.getImage());
        }

        Log.d("GuestActivity", "imageUrl: " + imageURL);
        Glide.with(this)
                .load(imageURL)
                .transform(new CircleTransform(this))
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .error(R.drawable.default_user)
                .into(imgProfile);



    }

    @Override
    public void onBackPressed() {
        if (binding.drawerLayout.isDrawerOpen(GravityCompat.START)) {
            binding.drawerLayout.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        // Handle navigation view item clicks here.
        final int id = item.getItemId();
       /* if(prodIdcart.size()>0) {

            new AlertDialog.Builder(this)
                    .setTitle("Are you sure you want to cancel your current transaction?")
                    .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialoginterface, int i) {

                            if (id == R.id.nav_sales_png) {
                                startActivity(new Intent(GuestActivity.this, GuestActivity.class));

                            } else if (id == R.id.nav_manageusers) {
                                startActivity(new Intent(GuestActivity.this, EmployeeListActivity.class));

                            } else if (id == R.id.nav_receipts) {

                            } else if (id == R.id.nav_items) {

                                startActivity(new Intent(GuestActivity.this, ItemActivity.class));
                            } else if (id == R.id.nav_inventory) {
                                startActivity(new Intent(GuestActivity.this, ReportsActivity.class));
                            } else if (id == R.id.nav_report) {

                            } else if (id == R.id.nav_qrcode) {
                                startActivity(new Intent(GuestActivity.this, ProductQrListActivity.class));
                            } else if (id == R.id.nav_profile) {
                                startActivity(new Intent(GuestActivity.this, ProfileActivity.class));
                            } else if (id == R.id.nav_logout) {
                                logOut(user);
                            }
                        }
                    })
                    .setNegativeButton("No", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialoginterface, int i) {

                        }
                    })
                    .show();

        }else {*/
            if (id == R.id.nav_sales_png) {
                startActivity(new Intent(this, MainActivity.class));

            } else if (id == R.id.nav_manageusers) {
                startActivity(new Intent(this, EmployeeListActivity.class));

            } else if (id == R.id.nav_receipts) {
                startActivity(new Intent(this, ReceiptListActivity.class));
            } else if (id == R.id.nav_items) {
                startActivity(new Intent(this, ItemActivity.class));
            } else if (id == R.id.nav_inventory) {
                startActivity(new Intent(this, InventoryActivity.class));
            } else if (id == R.id.nav_report) {

                startActivity(new Intent(this, SaleChartActivity.class));
            } else if (id == R.id.nav_qrcode) {
                startActivity(new Intent(this, ProductQrListActivity.class));
            } else if (id == R.id.nav_profile) {
                startActivity(new Intent(this, ProfileActivity.class));
            } else if (id == R.id.nav_logout) {
                logOut(user);
            }

            binding.drawerLayout.closeDrawer(GravityCompat.START);
       // }

        return true;
    }

    private void logOut(User user) {


        final Realm realm = Realm.getDefaultInstance();
        realm.executeTransactionAsync(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                realm.deleteAll();
            }
        }, new Realm.Transaction.OnSuccess() {
            @Override
            public void onSuccess() {
                realm.close();
                Intent intent = new Intent(MainActivity.this, LoginActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
            }
        }, new Realm.Transaction.OnError() {
            @Override
            public void onError(Throwable error) {
                error.printStackTrace();
                realm.close();
                Toast.makeText(MainActivity.this, "Realm Error", Toast.LENGTH_SHORT).show();
            }
        });

    }


    //MainAcitivity Methods End



    private void prepareList() {

        if (employeeRealmResults.isLoaded() && employeeRealmResults.isValid()) {
            List<Products> productsList;
            if (searchText.isEmpty()&&(filterCategory.equalsIgnoreCase("")||filterCategory.equalsIgnoreCase("0"))) {
                productsList = realm.copyFromRealm(employeeRealmResults);

            } else {
                if(filterCategory.equalsIgnoreCase("")||filterCategory.equalsIgnoreCase("0"))
                {
                    productsList = realm.copyFromRealm(employeeRealmResults.where()
                            .contains("productName", searchText, Case.INSENSITIVE)
                            .or()
                            .contains("productDescription", searchText, Case.INSENSITIVE)
                            .or()
                            .contains("productPrice", searchText, Case.INSENSITIVE)
                            .findAll());


                }else {

                    productsList = realm.copyFromRealm(employeeRealmResults.where()
                            .contains("categoryId", filterCategory, Case.INSENSITIVE)
                            .findAll());

            }
            }
            adapterPromo.setProductList(productsList);
            adapterPromo.notifyDataSetChanged();
        }
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
    public void onResume() {
        super.onResume();

       // loadData();
    }

    @Override
    protected void onDestroy() {
        presenter.onStop();
//        discountRealmResults.removeChangeListeners();
   //     employeeRealmResults.removeChangeListeners();
        realm.close();
        super.onDestroy();
    }

    @NonNull
    @Override
    public MainActivityPresenter createPresenter() {
        return new MainActivityPresenter();
    }

    @Override
    public void onRefresh() {
        presenter.load(user.getBusiness_id());
        presenter.getCategory(user.getBusiness_id());
        presenter.getDiscount(user.getBusiness_id());
        presenter.loadTransaction(user.getBusiness_id());
    }





    @Override
    public void stopRefresh() {
        binding.appBarMain.swipeRefreshLayout.setRefreshing(false);
    }

    @Override
    public void showError(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
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




    @NonNull
    @Override
    public ViewState<MainActivityView> createViewState() {
        setRetainInstance(true);
        return new MainActivityViewState();
    }

    @Override
    public void onNewViewStateInstance() {
        binding.appBarMain.swipeRefreshLayout.post(new Runnable() {
            @Override
            public void run() {

            }
        });
    }

    @Override
    public void showAlert(String message) {
        new AlertDialog.Builder(this)
                .setTitle(message)
                .setPositiveButton("Close", null)
                .show();
    }


    @Override
    public void onTransactionSuccess(Transaction transaction) {

        showError("Payment Successful!");


        final Dialog dialog = new Dialog(this);
        final DialogReceiptBinding dialogBinding = DataBindingUtil.inflate(
                getLayoutInflater(),
                R.layout.dialog_receipt,
                null,
                false);




        dialogBinding.recyclerView.setLayoutManager(new LinearLayoutManager(this));
        dialogBinding.recyclerView.setItemAnimator(new DefaultItemAnimator());

        adapterCart2 = new ReceiptActivityAdapter(this, getMvpView());
        dialogBinding.recyclerView.setAdapter(adapterCart2);
        adapterCart2.setProductList(presenter.StringtoList(transaction.getTransactionIdList()),presenter.StringtoList(transaction.getTransactionNameList()),presenter.StringtoList(transaction.getTransactionQuantityList()),presenter.StringtoList(transaction.getTransactionPriceList()));
        adapterCart2.notifyDataSetChanged();


        dialogBinding.orbName.setText(business.getBusinessName());
        dialogBinding.orbAddress.setText(business.getBusinessAddress());
        dialogBinding.orbContact.setText(business.getBusinessContact());
        dialogBinding.orDate.setText(DateTimeUtils.toReadable(transaction.getDate()));
        dialogBinding.orTime.setText(DateTimeUtils.getTimeOnly(transaction.getDate()));
        dialogBinding.orPayment.setText("Payment Method: "+transaction.getTransactionCode());
        dialogBinding.orPrice.setText("Php: "+transaction.getTransactionPrice());



        if(transaction.getTransactionDiscount().equalsIgnoreCase(""))
            dialogBinding.viewDiscount.setVisibility(View.GONE);
        else {
            dialogBinding.orDiscount.setText(" Php: -" + transaction.getTransactionDiscount());
            dialogBinding.orDiscountName.setText(transaction.getDiscountName());
            dialogBinding.viewDiscount.setVisibility(View.VISIBLE);
        }







        dialogBinding.cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                dialog.dismiss();
                MainActivity.this.recreate();

            }
        });

        dialogBinding.etReceiptEmail.setVisibility(View.VISIBLE);
        dialogBinding.send.setVisibility(View.VISIBLE);
        dialogBinding.send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(dialogBinding.etReceiptEmail.getText().toString().equalsIgnoreCase(""))
                {
                    showError("No Valid Email Address");
                }else {
                    dialogBinding.cancel.setVisibility(View.GONE);
                    dialogBinding.etReceiptEmail.setVisibility(View.GONE);
                    dialogBinding.send.setVisibility(View.GONE);
                    takeScreenshot(dialogBinding.sendLayout,dialogBinding.etReceiptEmail.getText().toString());
                    dialog.dismiss();
                    MainActivity.this.recreate();
                }

            }
        });

        dialog.setContentView(dialogBinding.getRoot());
        dialog.setCancelable(false);
        dialog.show();








    }


    @Override
    public void  onAddDiscount(Discount discount) {

            discountId = String.valueOf(discount.getDiscountId());
            discountCode = discount.getDiscountCode();
            discountName = discount.getDiscountName();
            discountValue = discount.getDiscountValue();
        if(!discountId.equalsIgnoreCase(""))
        {
            dialogBinding.viewDiscount.setVisibility(View.VISIBLE);
            dialogBinding.cartDiscountList.setText(discountName);
            double discounted = 0;
            if(discountCode.equalsIgnoreCase("P"))
            {
              discounted = Double.parseDouble(vtsPrice) * (Double.parseDouble(discountValue)/100);
            }
            else
            {
                discounted = Double.parseDouble(discountValue);
            }

            vtsDiscount = String.valueOf(discounted);
            dialogBinding.cartDiscountPrice.setText("Php: "+String.valueOf(discounted));


            oldTotal = dialogBinding.cartItemPrice.getText().toString();
            newPrice = Double.parseDouble(vtsPrice) - discounted;
            if(newPrice<0)
                newPrice = 0;

            vtsPrice = String.valueOf(newPrice);
            dialogBinding.cartItemPrice.setText("Php: "+String.valueOf(newPrice));

        }
            dialog2.dismiss();

    }

    @Override
    public void onItemRemove(Products products) {

        for(int a=0;a<prodIdcart.size();a++)
        {
            if(String.valueOf(products.getProductId()).equalsIgnoreCase(prodIdcart.get(a)))
            {
                productList.remove(a);
                prodIdcart.remove(a);
                prodNamecart.remove(a);
                prodQuantitycart.remove(a);
                prodPricecart.remove(a);

                adapterCart.setProductList(productList,prodIdcart,prodNamecart,prodQuantitycart,prodPricecart);
                adapterCart.notifyDataSetChanged();
            }
        }
    }


    @Override
    public void OnButtonAddtoCart() {


        dialog = new Dialog(MainActivity.this);
        final DialogAddCartBinding dialogBinding = DataBindingUtil.inflate(
                getLayoutInflater(),
                R.layout.dialog_add_cart,
                null,
                false);

        String prodCode;
        String prodCode2;
        if(currProduct.getProductCode().equalsIgnoreCase("E")) {
            prodCode = "pcs.";
            prodCode2 = "pc.";
        }
        else {
            prodCode = "kg";
            prodCode2 = "kilo";
        }


        dialogBinding.viewItemPrice.setText("Php: "+currProduct.getProductPrice()+" per "+prodCode2);
        dialogBinding.viewItemQuantity.setText("Remaining Quantity:  "+currProduct.getProductSKU()+prodCode);


        dialogBinding.setProduct(currProduct);
        dialogBinding.buyItemPrice.setText("Php: "+currProduct.getProductPrice());

        dialogBinding.buyItemQuantity.setText("1");
        newPrice = Integer.parseInt(currProduct.getProductPrice());

        dialogBinding.buyItemQuantity.addTextChangedListener(new TextWatcher() {

            public void afterTextChanged(Editable s) {
               // Log.d("TAG<>>>",currProduct.getProductPrice()+"< >"+dialogBinding.buyItemQuantity.getText().toString());
                if(!dialogBinding.buyItemQuantity.getText().toString().equalsIgnoreCase("")) {
                   if(Integer.parseInt(dialogBinding.buyItemQuantity.getText().toString())<=Integer.parseInt(currProduct.getProductSKU())) {
                       newPrice = (Integer.parseInt(currProduct.getProductPrice()) * Integer.parseInt(dialogBinding.buyItemQuantity.getText().toString()));
                       dialogBinding.buyItemPrice.setText("Php: " + newPrice);
                   }
                   else
                   {
                       showError("Not Enough Stocks!");
                       dialogBinding.buyItemQuantity.setText("1");
                   }

                }
            }

            public void beforeTextChanged(CharSequence s, int start,
                                          int count, int after) {
            }

            public void onTextChanged(CharSequence s, int start,
                                      int before, int count) {
            }
        });


        dialogBinding.buyItemProdcode.setText(prodCode);



        dialogBinding.send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                productList.add(currProduct);
                prodIdcart.add(currProduct.getProductId()+"");
                prodNamecart.add(currProduct.getProductName());
                prodQuantitycart.add(dialogBinding.buyItemQuantity.getText().toString());
                prodPricecart.add(String.valueOf(newPrice));

                MainActivity.this.invalidateOptionsMenu();

                dialog.dismiss();
            }
        });
        dialogBinding.cancel.setOnClickListener(new View.OnClickListener() {
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
    public void onItemClick(Products product) {

        currProduct = product;

        binding.appBarMain.itemView.setVisibility(View.VISIBLE);
        binding.appBarMain.remove.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                currProduct = null;
                binding.appBarMain.itemView.setVisibility(View.GONE);
            }
        });
        String imageURL = Endpoints.URL_IMAGE +currProduct.getProductName();
        Glide.with(this)
                .load(imageURL)
                .skipMemoryCache(true)
                .centerCrop()
                .error(R.drawable.placeholder)
                .into(binding.appBarMain.productImage);
        Log.d("TAG", imageURL);

        binding.appBarMain.viewItemDesc.setText(currProduct.getProductDescription());
        binding.appBarMain.viewItemPrice.setText("Php: "+currProduct.getProductPrice());
        binding.appBarMain.viewItemName.setText(currProduct.getProductName());

        String prodCode;
        if(currProduct.getProductCode().equalsIgnoreCase("E"))
            prodCode = "pcs.";
        else
            prodCode = "kg";
        binding.appBarMain.viewItemQuantity.setText("Quantity: "+currProduct.getProductSKU()+prodCode);


    }





    public void checkout()
    {


        dialog = new Dialog(MainActivity.this,android.R.style.Theme_Black_NoTitleBar_Fullscreen);
          dialogBinding = DataBindingUtil.inflate(
                getLayoutInflater(),
                R.layout.dialog_cart,
                null,
                false);


        dialogBinding.recyclerView.setLayoutManager(new LinearLayoutManager(this));
        dialogBinding.recyclerView.setItemAnimator(new DefaultItemAnimator());

        adapterCart = new CartMainActivityAdapter(this, getMvpView());
        dialogBinding.recyclerView.setAdapter(adapterCart);
        adapterCart.setProductList(productList,prodIdcart,prodNamecart,prodQuantitycart,prodPricecart);
        adapterCart.notifyDataSetChanged();

        int cartPrice = 0;

        for(int a=0;a<prodPricecart.size();a++)
        {
            cartPrice += Double.parseDouble(prodPricecart.get(a));
        }
        vtsPrice = String.valueOf(cartPrice);
        dialogBinding.cartItemPrice.setText("Php: "+cartPrice+"");






        dialogBinding.cash.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cashCode = "Cash";
                dialogBinding.cash.setBackgroundColor(ContextCompat.getColor(MainActivity.this, R.color.lightGray));
                dialogBinding.card.setBackgroundColor(ContextCompat.getColor(MainActivity.this, R.color.colorPrimary));

            }
        });

        dialogBinding.card.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cashCode = "Card";
                dialogBinding.card.setBackgroundColor(ContextCompat.getColor(MainActivity.this, R.color.lightGray));
                dialogBinding.cash.setBackgroundColor(ContextCompat.getColor(MainActivity.this, R.color.colorPrimary));

            }
        });



        dialogBinding.send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {



                presenter.addTransaction(
                        vtsPrice,
                        cashCode,vtsDiscount,
                       // dialogBinding.cartDiscountPrice.getText().toString(),
                        presenter.listToString(prodIdcart),
                        presenter.listToString(prodNamecart),
                        presenter.listToString(prodQuantitycart),
                        presenter.listToString(prodPricecart),
                        discountId,
                        discountName,
                        String.valueOf(user.getUserId()),
                        user.getFullName(),
                        DateTimeUtils.getCurrentTimeStamp(),
                        user.getBusiness_id());


                showError("here3");

                dialog.dismiss();
            }
        });

        dialogBinding.cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                dialog.dismiss();

            }
        });


        if(!discountId.equalsIgnoreCase(""))
        {
            dialogBinding.viewDiscount.setVisibility(View.VISIBLE);
            dialogBinding.cartDiscountList.setText(discountName);
            double discounted = 0;
            if(discountCode.equalsIgnoreCase("P"))
            {
                discounted = Double.parseDouble(vtsPrice) * (Double.parseDouble(discountValue)/100);
            }
            else
            {
                discounted = Double.parseDouble(discountValue);
            }

            vtsDiscount = String.valueOf(discounted);
            dialogBinding.cartDiscountPrice.setText("Php: "+String.valueOf(discounted));


            oldTotal = dialogBinding.cartItemPrice.getText().toString();
            newPrice = Double.parseDouble(vtsPrice) - discounted;
            if(newPrice<0)
                newPrice = 0;

            vtsPrice = String.valueOf(newPrice);
            dialogBinding.cartItemPrice.setText("Php: "+String.valueOf(newPrice));

        }


        dialogBinding.removeDiscountPrice.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                discountId = "";
                vtsPrice = String.valueOf(oldTotal);
                dialogBinding.cartItemPrice.setText(oldTotal);
                dialogBinding.viewDiscount.setVisibility(View.GONE);

            }
        });



        dialogBinding.cartDiscount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                dialog2 = new Dialog(MainActivity.this);
                final DialogDiscountBinding dialogBinding = DataBindingUtil.inflate(
                        getLayoutInflater(),
                        R.layout.dialog_discount,
                        null,
                        false);

                dialogBinding.recyclerView.setLayoutManager(new LinearLayoutManager(MainActivity.this));
                dialogBinding.recyclerView.setItemAnimator(new DefaultItemAnimator());

                adapterDiscount = new CartDiscountListAdapter(MainActivity.this, getMvpView());
                dialogBinding.recyclerView.setAdapter(adapterDiscount);
                discountRealmResults = realm.where(Discount.class).findAllAsync();
                discountRealmResults.addChangeListener(new RealmChangeListener<RealmResults<Discount>>() {
                    @Override
                    public void onChange(RealmResults<Discount> element) {
                        List<Discount> promoList = realm.copyFromRealm(discountRealmResults);
                        adapterDiscount.setDiscountList(promoList);
                        adapterDiscount.notifyDataSetChanged();

                    }
                });

                dialogBinding.cancel.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        dialog2.dismiss();

                    }
                });

                dialog2.setContentView(dialogBinding.getRoot());
                dialog2.setCancelable(false);
                dialog2.show();

            }
        });




        dialog.setContentView(dialogBinding.getRoot());
        dialog.setCancelable(false);
        dialog.show();








    }

    private void takeScreenshot(View v1,String email) {
        Date now = new Date();
        android.text.format.DateFormat.format("yyyy-MM-dd_hh:mm:ss", now);

        try {
            // image naming and path  to include sd card  appending name you choose for file
            String mPath = Environment.getExternalStorageDirectory().toString() + "/Download/" +email+now + ".jpg";

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

            sendMail(mPath,email);
            //openScreenshot(imageFile);
        } catch (Throwable e) {
            // Several error may come out with file handling or DOM
            e.printStackTrace();
        }
    }




    public void sendMail(String path,String email) {
        Intent emailIntent = new Intent(android.content.Intent.ACTION_SEND);
        emailIntent.putExtra(android.content.Intent.EXTRA_EMAIL,
                new String[] { email });
        emailIntent.putExtra(android.content.Intent.EXTRA_SUBJECT,
                "EReceipt");
        emailIntent.putExtra(android.content.Intent.EXTRA_TEXT,
                "");
        emailIntent.setType("image/png");
        Uri myUri = Uri.parse("file://" + path);
        emailIntent.putExtra(Intent.EXTRA_STREAM, myUri);
        startActivity(Intent.createChooser(emailIntent, "Send mail..."));
    }





}
