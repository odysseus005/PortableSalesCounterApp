package com.portablesalescounterapp.ui.main;

import android.Manifest;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
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
import android.support.v4.app.ActivityCompat;
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
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
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
import com.portablesalescounterapp.model.data.PreTransaction;
import com.portablesalescounterapp.model.data.Products;
import com.portablesalescounterapp.model.data.Transaction;
import com.portablesalescounterapp.model.data.User;
import com.portablesalescounterapp.ui.editbusiness.EditBusinessActivity;
import com.portablesalescounterapp.ui.editbusiness.EditBusinessView;
import com.portablesalescounterapp.ui.guest.GuestActivity;
import com.portablesalescounterapp.ui.inventory.InventoryActivity;
import com.portablesalescounterapp.ui.item.ItemActivity;
import com.portablesalescounterapp.ui.item.product.ProductListActivity;
import com.portablesalescounterapp.ui.login.LoginActivity;
import com.portablesalescounterapp.ui.manageqr.ProductQrListActivity;
import com.portablesalescounterapp.ui.manageuser.EmployeeListActivity;
import com.portablesalescounterapp.ui.profile.ProfileActivity;
import com.portablesalescounterapp.ui.receipts.CartreceiptActivityAdapter;
import com.portablesalescounterapp.ui.receipts.ReceiptListActivity;
import com.portablesalescounterapp.ui.reports.ReportsActivity;
import com.portablesalescounterapp.ui.reports.analytics.AnalyticsChartActivity;
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
    private static final int MY_PERMISSIONS_REQUEST = 1;
    public static final int MY_PERMISSIONS_REQUEST_CAMERA = 100;
    public static final int MY_PERMISSIONS_REQUEST_LOCATION = 99;
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
    private Dialog dialog,dialog2,dialogTC;
    DialogCartBinding dialogBinding;
    private ProgressDialog progressDialog;
    private Products currProduct;
    private PreTransaction transQr;
    String cashCode="Cash",discountId="",discountName="",discountValue="0",discountCode="",oldTotal="";
    String vtsPrice="",vtsDiscount="";
    public double newPrice;
    private String searchText;
    private String filterCategory="";
    private ReceiptActivityAdapter adapterCart2;
    boolean qrSwitcher=true;
    boolean changeChecker =false;
    public int skuLimiter=0;
    public boolean scanChecker=false;
    double transactChange=0.0,transactCash=0.0;
    private String onScantransactid = "0";
     DialogReceiptBinding dialogBindingSuccess;

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

        int Permission_All = 1;
        String[] Permissions = { Manifest.permission.CAMERA, Manifest.permission.RECORD_AUDIO,Manifest.permission.READ_EXTERNAL_STORAGE,Manifest.permission.WRITE_EXTERNAL_STORAGE};
        if(!hasPermissions(this, Permissions)){
            ActivityCompat.requestPermissions(this, Permissions, Permission_All);
        }

        productList = new ArrayList<>();
        prodIdcart = new ArrayList<>();
        prodNamecart = new ArrayList<>();
        prodQuantitycart = new ArrayList<>();
        prodPricecart = new ArrayList<>();


        binding = DataBindingUtil.setContentView(this, R.layout.activity_main);
        binding.appBarMain.setView(getMvpView());

        setSupportActionBar(binding.appBarMain.toolbar);
        getSupportActionBar().setTitle(BuildConfig.DEBUG ? "Smart Sales Counter" : "Smart Sales Counter");

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

                ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(MainActivity.this, android.R.layout.simple_spinner_item, promoList);
                arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                binding.appBarMain.spCategory.setAdapter(arrayAdapter);
            }


        });

        binding.appBarMain.spCategory.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> arg0, View arg1,
                                       int position, long id) {

                filterCategory = ""+(categoryIdList.get(position));

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
            nav_Menu.findItem(R.id.nav_report).setVisible(false);
            nav_Menu.findItem(R.id.nav_businessprofile).setVisible(false);

        }else if((user.getPosition()).equalsIgnoreCase("inventory custodian"))
        {

            nav_Menu.findItem(R.id.nav_items).setVisible(false);
            nav_Menu.findItem(R.id.nav_manageusers).setVisible(false);
            nav_Menu.findItem(R.id.nav_report).setVisible(false);
            nav_Menu.findItem(R.id.nav_receipts).setVisible(false);
            nav_Menu.findItem(R.id.nav_sales).setVisible(false);
            nav_Menu.findItem(R.id.nav_businessprofile).setVisible(false);




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
        if(qrSwitcher)
        integrator.setPrompt("Scan Product Bar Code/Qr Code");
        else integrator.setPrompt("Scan Order Code");
        integrator.setCameraId(0);  // Use a specific camera of the device
        integrator.setBeepEnabled(true);
        integrator.setBarcodeImageEnabled(true);
        integrator.initiateScan();

    }

    public static boolean hasPermissions(Context context, String... permissions){

        if(Build.VERSION.SDK_INT>= Build.VERSION_CODES.M && context!=null && permissions!=null){
            for(String permission: permissions){
                if(ActivityCompat.checkSelfPermission(context, permission)!=PackageManager.PERMISSION_GRANTED){
                    return  false;
                }
            }
        }
        return true;
    }


    @Override
    protected void onActivityResult ( int requestCode, int resultCode, Intent in ) {
        // TODO Auto-generated method stub

        if( requestCode == IntentIntegrator.REQUEST_CODE ){


            if( resultCode == RESULT_OK ) {

                try {
                    String contents = in.getStringExtra("SCAN_RESULT");

                    if (qrSwitcher) {

                        currProduct = presenter.getProductQr(contents);
                        if (currProduct.isLoaded() && currProduct.isValid()) {
                            scanChecker=true;
                            OnButtonAddtoCart();
                        }
                    } else {
                        transQr = presenter.getTransactionQr(Integer.parseInt(contents));
                        if (transQr.getPreuserId() == 0) {
                            if (transQr.isLoaded() && transQr.isValid())

                                onScanTransact(transQr);
                        } else {
                            showAlert("Transaction Order is Already Paid!");
                        }


                    }
                } catch (Exception e) {
                    showError("Cannot Read Code!");
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

        switch (requestCode) {

            // other 'case' lines to check for other
            // permissions this app might request
            case MY_PERMISSIONS_REQUEST:{
                if(grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                }else{

                }
                return;
            }
            case MY_PERMISSIONS_REQUEST_CAMERA:{
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                } else {
                }
                return;
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


      /*  LinearLayout v2=(LinearLayout) menu.findItem(R.id.item_qrscan).getActionView();

        Button count2=(Button)v2.findViewById(R.id.scan);



       v2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


            }
        });


        LinearLayout v3=(LinearLayout) menu.findItem(R.id.item_trscan).getActionView();

        Button count3=(Button)v3.findViewById(R.id.scan);



        v3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                startScan();
                qrSwitcher=false;
            }
        });
*/


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
        String imageURL =  Endpoints.URL_IMAGE+user.getEmail();

       // if (user.getImage() != null && !user.getImage().isEmpty()) {
            //Endpoints.IMAGE_URL.replace(Endpoints.IMG_HOLDER, user.getImage());
       // }

        Log.d("GuestActivity", "imageUrl: " + imageURL);
        Glide.with(this)
                .load(imageURL)
                .transform(new CircleTransform(this))
                .diskCacheStrategy(DiskCacheStrategy.NONE)
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
            if (id == R.id.nav_sales) {

                startActivity(new Intent(this, MainActivity.class));

            } else if (id == R.id.nav_manageusers) {
                clearCart();
                startActivity(new Intent(this, EmployeeListActivity.class));

            } else if (id == R.id.nav_receipts) {
                clearCart();
                startActivity(new Intent(this, ReceiptListActivity.class));
            } else if (id == R.id.nav_items) {
                clearCart();
                startActivity(new Intent(this, ItemActivity.class));
            } else if (id == R.id.nav_inventory) {
                clearCart();
                startActivity(new Intent(this, InventoryActivity.class));
            } else if (id == R.id.nav_report) {
                clearCart();
                startActivity(new Intent(this, ReportsActivity.class));
            } else if (id == R.id.nav_qrcode) {
                clearCart();
                startActivity(new Intent(this, ProductQrListActivity.class));
            } else if (id == R.id.nav_profile) {
                clearCart();
                startActivity(new Intent(this, ProfileActivity.class));
            }
             else if (id == R.id.nav_businessprofile) {
                clearCart();
                  startActivity(new Intent(this, EditBusinessActivity.class));
            }
            else if (id == R.id.nav_logout) {
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
                productsList = realm.copyFromRealm(employeeRealmResults.where().notEqualTo("productStatus","D").findAll());

            } else {
                if(filterCategory.equalsIgnoreCase("")||filterCategory.equalsIgnoreCase("0"))
                {
                    productsList = realm.copyFromRealm(employeeRealmResults.where()
                            .contains("productName", searchText, Case.INSENSITIVE)
                            .or()
                            .contains("productDescription", searchText, Case.INSENSITIVE)
                            .or()
                            .contains("productPrice", searchText, Case.INSENSITIVE)
                            .notEqualTo("productStatus","D").findAll());


                }else {

                    productsList = realm.copyFromRealm(employeeRealmResults.where()
                            .contains("categoryId", filterCategory, Case.INSENSITIVE)
                            .notEqualTo("productStatus","D").findAll());

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

            case R.id.item_trscan:

                if((user.getPosition()).equalsIgnoreCase("inventory custodian"))
                {
                    showError("Permission to Process Transaction Denied");
                }else {
                    qrSwitcher = false;
                    startScan();
                }
            return  true;

            case R.id.item_qrscan:

                if((user.getPosition()).equalsIgnoreCase("inventory custodian"))
                {
                    showError("Permission to Process Transaction Denied");
                }else {
                    qrSwitcher = true;
                    startScan();
                }
                return  true;


            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
          //  qrSwitcher=true;
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
    public void onTransactionSuccess(final Transaction transaction) {


        qrSwitcher = true;
        if(qrSwitcher)
        showError("Payment Successful!");



         dialogTC = new Dialog(this);
        dialogBindingSuccess = DataBindingUtil.inflate(
                getLayoutInflater(),
                R.layout.dialog_receipt,
                null,
                false);




        dialogBindingSuccess.recyclerView.setLayoutManager(new LinearLayoutManager(this));
        dialogBindingSuccess.recyclerView.setItemAnimator(new DefaultItemAnimator());

        adapterCart2 = new ReceiptActivityAdapter(this, getMvpView());
        dialogBindingSuccess.recyclerView.setAdapter(adapterCart2);
        adapterCart2.setProductList(presenter.StringtoList(transaction.getTransactionIdList()),presenter.StringtoList(transaction.getTransactionNameList()),presenter.StringtoList(transaction.getTransactionQuantityList()),presenter.StringtoList(transaction.getTransactionPriceList()));
        adapterCart2.notifyDataSetChanged();


        dialogBindingSuccess.orbName.setText(business.getBusinessName());
        dialogBindingSuccess.orbAddress.setText(business.getBusinessAddress());
        dialogBindingSuccess.orbContact.setText(business.getBusinessContact());
        dialogBindingSuccess.orDate.setText(DateTimeUtils.toReadable(transaction.getDate()));
        dialogBindingSuccess.orTime.setText(DateTimeUtils.getTimeOnly(transaction.getDate()));
        dialogBindingSuccess.orPayment.setText("Payment Method: "+transaction.getTransactionCode());
        dialogBindingSuccess.orPrice.setText("Php: "+transaction.getTransactionPrice());
        dialogBindingSuccess.orCashierName.setText("Cashier Name: "+transaction.getUserName());
        dialogBindingSuccess.orChange.setText("Php: "+String.valueOf(DateTimeUtils.parseDoubleTL(transactChange)));
        dialogBindingSuccess.orCash.setText("Php: "+String.valueOf(DateTimeUtils.parseDoubleTL(transactCash)));




        if(transaction.getTransactionDiscount().equalsIgnoreCase(""))
            dialogBindingSuccess.viewDiscount.setVisibility(View.GONE);
        else {
            dialogBindingSuccess.orDiscount.setText(" Php: -" + transaction.getTransactionDiscount());
            dialogBindingSuccess.orDiscountName.setText(transaction.getDiscountName());
            dialogBindingSuccess.viewDiscount.setVisibility(View.VISIBLE);
        }







        dialogBindingSuccess.cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                dialogTC.dismiss();
                MainActivity.this.recreate();

            }
        });


        dialogBindingSuccess.etReceiptEmail.setVisibility(View.VISIBLE);
        dialogBindingSuccess.send.setVisibility(View.VISIBLE);

        if(!qrSwitcher) {
            dialogBindingSuccess.send.setText("Confirm Checkout");
        }

        dialogBindingSuccess.send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

//                if(dialogBindingSuccess.etReceiptEmail.getText().toString().equalsIgnoreCase(""))
//                {
//                    showError("No Valid Email Address");
//                }else {


                    if(!qrSwitcher)
                    {

                      //  presenter.updateTransaction(user.getBusiness_id(),String.valueOf(transaction.getTransactionId()),String.valueOf(user.getUserId()),user.getFullName(), String.valueOf(DateTimeUtils.getCurrentTimeStamp()),transaction.getTransactionIdList(),transaction.getTransactionNameList(),transaction.getTransactionQuantityList(),transaction.getTransactionPriceList());
                        qrSwitcher=true;

                    }
                    else {
                        dialogBindingSuccess.cancel.setVisibility(View.GONE);
                        dialogBindingSuccess.etReceiptEmail.setVisibility(View.GONE);
                        dialogBindingSuccess.send.setVisibility(View.GONE);
                        takeScreenshot(dialogBindingSuccess.sendLayout, dialogBindingSuccess.etReceiptEmail.getText().toString(),business.getBusinessName(),user.getFullName());
                        dialogTC.dismiss();
                        MainActivity.this.recreate();

                    }
               // }

            }
        });

        dialogTC.setContentView(dialogBindingSuccess.getRoot());
        dialogTC.setCancelable(false);
        dialogTC.show();

    }

    @Override
    public void  onSelfSuccess() {

        if(!(dialogBindingSuccess.etReceiptEmail.getText().toString().equalsIgnoreCase("")))
            takeScreenshot(dialogBindingSuccess.sendLayout, dialogBindingSuccess.etReceiptEmail.getText().toString(),business.getBusinessName(),user.getFullName());

        dialogBindingSuccess.cancel.setVisibility(View.GONE);
        dialogBindingSuccess.etReceiptEmail.setVisibility(View.GONE);
        dialogBindingSuccess.send.setVisibility(View.GONE);
        dialogTC.dismiss();
        MainActivity.this.recreate();

    }



    @Override
    public void  onAddDiscount(Discount discount) {


        if(discountId.equals("A")) {
            vtsPrice = String.valueOf(oldTotal);
            dialogBinding.cartItemPrice.setText(oldTotal);
            dialogBinding.viewDiscount.setVisibility(View.GONE);
        }else if(!(discountId.equalsIgnoreCase("")))
        {
            vtsPrice = String.valueOf(oldTotal);
            dialogBinding.cartItemPrice.setText(oldTotal);
            dialogBinding.viewDiscount.setVisibility(View.GONE);
        }


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
            dialogBinding.cartDiscountPrice.setText("Php: "+String.valueOf(DateTimeUtils.parseDoubleTL(discounted)));


            oldTotal = vtsPrice;
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

        MainActivity.this.invalidateOptionsMenu();
        double cartPrice = 0;

        for(int a=0;a<prodPricecart.size();a++)
        {
            cartPrice += Double.parseDouble(prodPricecart.get(a).replace(",",""));
        }
        // Log.d("TAG",">>>"+DateTimeUtils.parseDoubleTL(cartPrice));
        vtsPrice = String.valueOf(cartPrice);
        dialogBinding.cartItemPrice.setText("Php: "+DateTimeUtils.parseDoubleTL(cartPrice));

    }


    @Override
    public void onScanTransact(PreTransaction transact) {



        onScantransactid = String.valueOf(transact.getPretransactionId());

        productList.clear();
        prodIdcart.clear();
        prodNamecart.clear();
        prodQuantitycart.clear();
        prodPricecart.clear();

        List<Products> productsList2 = realm.copyFromRealm(employeeRealmResults.where().notEqualTo("productStatus","D").findAll());


        ArrayList<String> prodIdcart2 = presenter.StringtoList(transact.getPretransactionIdList());
         ArrayList<String> prodNamecart2 = presenter.StringtoList(transact.getPretransactionNameList());
         ArrayList<String> prodQuantitycart2 = presenter.StringtoList(transact.getPretransactionQuantityList());
         ArrayList<String> prodPricecart2 =presenter.StringtoList(transact.getPretransactionPriceList());

         int skuLimiter2 = 0;
         Products currProduct2 = null;

        for(int a =0; a<prodIdcart2.size();a++)
        {

            for(int b=0;b<productsList2.size();b++)
            {
                if((String.valueOf(productsList2.get(b).getProductId())).equalsIgnoreCase(prodIdcart2.get(a)))
                  currProduct2 = productsList2.get(b);
            }


              if(currProduct2 != null) {
                Log.d(">>>>>",currProduct2.getProductId()+"  >>>>"+currProduct2.getProductName()+" >>>"+currProduct2.getProductSKU());
                  Log.d(">>>>>",prodIdcart2.get(a)+"  >>>>"+prodNamecart2.get(a)+" >>>"+prodQuantitycart2.get(a));

                  if (currProduct2.getProductSKU().equalsIgnoreCase("0") || currProduct2.getProductSKU().equalsIgnoreCase("")) {


                      showError("Out of Stock! for Product "+ currProduct2.getProductName());
                  } else if (((Integer.parseInt(currProduct2.getProductSKU())) - (Integer.parseInt(prodQuantitycart2.get(a)))) < 0 ) {


                      showError("Not Enough Stock! for Product: "+ currProduct2.getProductName());
                      Double newPrice2 = (Double.parseDouble(currProduct2.getProductPrice()) * Integer.parseInt(currProduct2.getProductSKU()));


                      productList.add(currProduct2);
                      prodIdcart.add(currProduct2.getProductId() + "");
                      prodNamecart.add(currProduct2.getProductName());
                      prodQuantitycart.add(currProduct2.getProductSKU());
                      prodPricecart.add(String.valueOf(DateTimeUtils.parseDoubleTL(newPrice2)));

                      MainActivity.this.invalidateOptionsMenu();
                  }
                  else
                  {
                     Double newPrice2 = (Double.parseDouble(currProduct2.getProductPrice()) * Integer.parseInt(prodQuantitycart2.get(a)));


                      productList.add(currProduct2);
                      prodIdcart.add(currProduct2.getProductId() + "");
                      prodNamecart.add(currProduct2.getProductName());
                      prodQuantitycart.add(prodQuantitycart2.get(a));
                      prodPricecart.add(String.valueOf(DateTimeUtils.parseDoubleTL(newPrice2)));

                      MainActivity.this.invalidateOptionsMenu();
                  }



              }

        }

        checkout();

    }


    @Override
    public void OnButtonAddtoCart() {


            skuLimiter=0;
        for(int a=0;a<prodIdcart.size();a++)
        {
            if((prodIdcart.get(a)).equalsIgnoreCase(String.valueOf(currProduct.getProductId())))
                   skuLimiter = Integer.parseInt(prodQuantitycart.get(a));
        }




        if(currProduct.getProductSKU().equalsIgnoreCase("0")||currProduct.getProductSKU().equalsIgnoreCase(""))
        {
            showError("Out of Stock!");
        }
        else if(String.valueOf(Integer.parseInt(currProduct.getProductSKU())-skuLimiter).equalsIgnoreCase("0")||String.valueOf((Integer.parseInt(currProduct.getProductSKU())-skuLimiter)).equalsIgnoreCase(""))
        {
            showError("Out of Stock! Product is already in the cart");
        }
        else {
            dialog = new Dialog(MainActivity.this);
            final DialogAddCartBinding dialogBinding = DataBindingUtil.inflate(
                    getLayoutInflater(),
                    R.layout.dialog_add_cart,
                    null,
                    false);

            String prodCode;
            String prodCode2;
            if (currProduct.getProductCode().equalsIgnoreCase("E")) {
                prodCode = "pcs.";
                prodCode2 = "pc.";
            } else {
                prodCode = "kg";
                prodCode2 = "kilo";
            }





            dialogBinding.viewItemPrice.setText("Php: " + currProduct.getProductPrice() + " per " + prodCode2);
            dialogBinding.viewItemQuantity.setText("Remaining Quantity:  " + (Integer.parseInt(currProduct.getProductSKU())-skuLimiter) + prodCode);


            dialogBinding.setProduct(currProduct);
            dialogBinding.buyItemPrice.setText("Php: " + DateTimeUtils.parseDoubleTL(Double.parseDouble(currProduct.getProductPrice())));

            dialogBinding.buyItemQuantity.setText("1");
            dialogBinding.buyItemQuantity.setSelectAllOnFocus(true);
            dialogBinding.buyItemQuantity.selectAll();
           // dialogBinding.buyItemQuantity.requestFocus();

            newPrice = Double.parseDouble(currProduct.getProductPrice());

            dialogBinding.buyItemQuantity.addTextChangedListener(new TextWatcher() {

                public void afterTextChanged(Editable s) {
                    // Log.d("TAG<>>>",currProduct.getProductPrice()+"< >"+dialogBinding.buyItemQuantity.getText().toString());
                    if (!dialogBinding.buyItemQuantity.getText().toString().equalsIgnoreCase("")) {
                        if (Integer.parseInt(dialogBinding.buyItemQuantity.getText().toString()) <= (Integer.parseInt(currProduct.getProductSKU())-skuLimiter)) {
                            newPrice = (Double.parseDouble(currProduct.getProductPrice()) * Integer.parseInt(dialogBinding.buyItemQuantity.getText().toString()));
                            dialogBinding.buyItemPrice.setText("Php: " + newPrice);
                        } else {
                            showError("Not Enough Stocks!");
                            dialogBinding.buyItemQuantity.setText(String.valueOf((Integer.parseInt(currProduct.getProductSKU())-skuLimiter)));
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

            Log.d("HERE>>>>>",scanChecker+"");

            if(scanChecker)
                dialogBinding.buy.setVisibility(View.VISIBLE);

                dialogBinding.buy.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        productList.add(currProduct);
                        prodIdcart.add(currProduct.getProductId() + "");
                        prodNamecart.add(currProduct.getProductName());
                        prodQuantitycart.add(dialogBinding.buyItemQuantity.getText().toString());
                        prodPricecart.add(String.valueOf(DateTimeUtils.parseDoubleTL(newPrice)));

                        MainActivity.this.invalidateOptionsMenu();

                        dialog.dismiss();

                        qrSwitcher=true;
                        startScan();
                    }
                });

            dialogBinding.send.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    productList.add(currProduct);
                    prodIdcart.add(currProduct.getProductId() + "");
                    prodNamecart.add(currProduct.getProductName());
                    prodQuantitycart.add(dialogBinding.buyItemQuantity.getText().toString());
                    prodPricecart.add(String.valueOf(DateTimeUtils.parseDoubleTL(newPrice)));

                    MainActivity.this.invalidateOptionsMenu();
                    scanChecker=false;
                    dialog.dismiss();
                }
            });
            dialogBinding.cancel.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    scanChecker=false;
                    dialog.dismiss();

                }
            });

            dialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
            dialog.setContentView(dialogBinding.getRoot());
            dialog.setCancelable(false);
            dialog.show();
        }


    }

    @Override
    public void onItemClick(Products product) {



        if((user.getPosition()).equalsIgnoreCase("inventory custodian"))
        {
            showError("Permission to Process Transaction Denied");
        }else {
            currProduct = product;
            binding.appBarMain.itemView.setVisibility(View.VISIBLE);
            binding.appBarMain.remove.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    currProduct = null;
                    binding.appBarMain.itemView.setVisibility(View.GONE);
                }
            });
            String imageURL = Endpoints.URL_IMAGE + currProduct.getProductId() + "prod";
            // String imageURL = Endpoints.URL_IMAGE +currProduct.getProductName();
            Glide.with(this)
                    .load(imageURL)
                    //   .skipMemoryCache(true)
                    .diskCacheStrategy(DiskCacheStrategy.NONE)
                    .centerCrop()
                    .error(R.drawable.placeholder)
                    .into(binding.appBarMain.productImage);
            Log.d("TAG", imageURL);

            binding.appBarMain.viewItemDesc.setText(currProduct.getProductDescription());
            binding.appBarMain.viewItemPrice.setText("Php: " + DateTimeUtils.parseDoubleTL(Double.parseDouble(currProduct.getProductPrice())));
            binding.appBarMain.viewItemName.setText(currProduct.getProductName());

            String prodCode;
            if (currProduct.getProductCode().equalsIgnoreCase("E"))
                prodCode = "pcs.";
            else
                prodCode = "kg";
            binding.appBarMain.viewItemQuantity.setText("Quantity: " + currProduct.getProductSKU() + prodCode);
        }


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

        double cartPrice = 0;

        for(int a=0;a<prodPricecart.size();a++)
        {
            cartPrice += Double.parseDouble(prodPricecart.get(a).replace(",",""));
        }
       // Log.d("TAG",">>>"+DateTimeUtils.parseDoubleTL(cartPrice));
        vtsPrice = String.valueOf(cartPrice);
        dialogBinding.cartItemPrice.setText("Php: "+DateTimeUtils.parseDoubleTL(cartPrice));

        if(!onScantransactid.equalsIgnoreCase("0"))
        dialogBinding.cartItems.setText("Items of Transaction ID: "+onScantransactid);


        dialogBinding.calcuCash.setText("0.00");
        dialogBinding.calcuCash.setSelectAllOnFocus(true);
        dialogBinding.calcuCash.selectAll();

        dialogBinding.calcuCash.addTextChangedListener(new TextWatcher() {

            public void afterTextChanged(Editable s) {

                if(!dialogBinding.calcuCash.getText().toString().equals("")) {

                    try {
                        if (Double.parseDouble(dialogBinding.calcuCash.getText().toString()) < (Double.parseDouble(vtsPrice))) {

                            showError("Cash Not Enough!");
                            // dialogBinding.calcuCash.setText("0.00");
                            changeChecker = false;

                        } else {
                            changeChecker = true;
                            Double change = (Double.parseDouble(dialogBinding.calcuCash.getText().toString())) - (Double.parseDouble(vtsPrice));
                            dialogBinding.calcuChange.setText("Php: " + String.valueOf(DateTimeUtils.parseDoubleTL(change)));
                                transactChange = change;
                                transactCash = Double.parseDouble(dialogBinding.calcuCash.getText().toString());
                        }
                    }
                    catch (Exception e)
                    {
                        showError("Invalid Value!");
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

                if(!changeChecker)
                    showError("Cash Not Enough!");
//                else if(!onScantransactid.equalsIgnoreCase("0"))
//                {
//                    presenter.addPreTransaction(
//                            vtsPrice,
//                            cashCode, vtsDiscount,
//                            // dialogBinding.cartDiscountPrice.getText().toString(),
//                            presenter.listToString(prodIdcart),
//                            presenter.listToString(prodNamecart),
//                            presenter.listToString(prodQuantitycart),
//                            presenter.listToString(prodPricecart),
//                            discountId,
//                            discountName,
//                            String.valueOf(user.getUserId()),
//                            user.getFullName(),
//                            DateTimeUtils.getCurrentTimeStamp(),
//                            user.getBusiness_id());
//
//
//                    //   showError("here3");
//
//                    dialog.dismiss();
//                }
                else
                {
                    if(discountId.equalsIgnoreCase("A")||discountId.equalsIgnoreCase(""))
                    {
                        vtsDiscount="";
                        discountId="";
                        discountName="";
                    }

                    presenter.addTransaction(
                            vtsPrice,
                            cashCode, vtsDiscount,
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


                    //   showError("here3");

                    dialog.dismiss();
                }
            }
        });

        dialogBinding.cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                dialog.dismiss();

            }
        });

        dialogBinding.clear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                onScantransactid = "0";

                productList.clear();
                prodIdcart.clear();
                prodNamecart.clear();
                prodQuantitycart.clear();
                prodPricecart.clear();

                MainActivity.this.invalidateOptionsMenu();


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


            oldTotal = vtsPrice;
            newPrice = Double.parseDouble(vtsPrice) - discounted;
            if(newPrice<0)
                newPrice = 0;

            vtsPrice = String.valueOf(newPrice);
            dialogBinding.cartItemPrice.setText("Php: "+String.valueOf(newPrice));

        }


        dialogBinding.removeDiscountPrice.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                discountId = "A";
                vtsPrice = String.valueOf(oldTotal);
                dialogBinding.cartItemPrice.setText("PHP: "+oldTotal);
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


    private void takeScreenshot(View v1,String email,String bname, String cname) {

        try {
            // image naming and path  to include sd card  appending name you choose for file
            String mPath = Environment.getExternalStorageDirectory().toString() + "/Download/" +email+DateTimeUtils.getCurrentTimeStamp2()+ ".jpg";

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

            sendMail(mPath,email,bname,cname);
            //openScreenshot(imageFile);
        } catch (Throwable e) {
            // Several error may come out with file handling or DOM
            e.printStackTrace();
        }
    }


//    public void createImage(Bitmap bmp) {
//        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
//        bmp.compress(Bitmap.CompressFormat.JPEG, 40, bytes);
//        File file = new File(Environment.getExternalStorageDirectory() +
//                "/capturedscreenandroid.jpg");
//        try {
//            file.createNewFile();
//            FileOutputStream outputStream = new FileOutputStream(file);
//            outputStream.write(bytes.toByteArray());
//            outputStream.close();
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//    }



    public void sendMail(String path,String email,String bname, String cname) {
        Intent emailIntent = new Intent(android.content.Intent.ACTION_SEND);
        emailIntent.putExtra(android.content.Intent.EXTRA_EMAIL,
                new String[] { email });
        emailIntent.putExtra(android.content.Intent.EXTRA_SUBJECT,
                "E-Receipt from "+bname);
        emailIntent.putExtra(android.content.Intent.EXTRA_TEXT,
                "Hi,\n" +
                        "\n" +
                        "Thank you for buying goods from "+bname+". Your E-Receipt is attached below.\n" +
                        "\n" +
                        "Best Regards,\n" +
                        cname+"\n" +
                        bname);
        emailIntent.setType("image/png");
        Uri myUri = Uri.parse("file://" + path);
        emailIntent.putExtra(Intent.EXTRA_STREAM, myUri);
        startActivity(Intent.createChooser(emailIntent, "Send mail..."));
    }


     public void clearCart() {

        onScantransactid = "0";

        productList.clear();
        prodIdcart.clear();
        prodNamecart.clear();
        prodQuantitycart.clear();
        prodPricecart.clear();

        MainActivity.this.invalidateOptionsMenu();


    }


}
