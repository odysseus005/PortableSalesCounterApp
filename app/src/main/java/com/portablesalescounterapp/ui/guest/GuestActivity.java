package com.portablesalescounterapp.ui.guest;

import android.Manifest;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
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

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.integration.android.IntentIntegrator;
import com.hannesdorfmann.mosby.mvp.viewstate.MvpViewStateActivity;
import com.hannesdorfmann.mosby.mvp.viewstate.ViewState;
import com.journeyapps.barcodescanner.BarcodeEncoder;
import com.portablesalescounterapp.BuildConfig;
import com.portablesalescounterapp.R;
import com.portablesalescounterapp.app.Constants;
import com.portablesalescounterapp.app.Endpoints;
import com.portablesalescounterapp.databinding.ActivityGuestListBinding;
import com.portablesalescounterapp.databinding.ActivityMainBinding;
import com.portablesalescounterapp.databinding.DialogAddCartBinding;
import com.portablesalescounterapp.databinding.DialogAddCartGuestBinding;
import com.portablesalescounterapp.databinding.DialogCartBinding;
import com.portablesalescounterapp.databinding.DialogDiscountBinding;
import com.portablesalescounterapp.databinding.DialogEditProductQrBinding;
import com.portablesalescounterapp.databinding.DialogTransactionQrBinding;
import com.portablesalescounterapp.model.data.Category;
import com.portablesalescounterapp.model.data.Discount;
import com.portablesalescounterapp.model.data.Products;
import com.portablesalescounterapp.model.data.Transaction;
import com.portablesalescounterapp.model.data.User;
import com.portablesalescounterapp.ui.inventory.InventoryActivity;
import com.portablesalescounterapp.ui.item.ItemActivity;
import com.portablesalescounterapp.ui.login.LoginActivity;
import com.portablesalescounterapp.ui.manageqr.ProductQrListActivity;
import com.portablesalescounterapp.ui.manageuser.EmployeeListActivity;
import com.portablesalescounterapp.ui.profile.ProfileActivity;
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


public class GuestActivity
        extends MvpViewStateActivity<GuestActivityView, GuestActivityPresenter>
        implements SwipeRefreshLayout.OnRefreshListener , GuestActivityView {

    private final int PERMISSION_CODE = 9235;
    private static final String TAG = GuestActivity.class.getSimpleName();
    private ActivityGuestListBinding binding;
    private GuesttDiscountListAdapter adapterDiscount;
    private GuestActivityAdapter adapterPromo;
    private CartGuestActivityAdapter adapterCart;
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
    public int skuLimiter=0;
    private Realm realm;
    // private User user;
    private Dialog dialog, dialog2;
    DialogCartBinding dialogBinding;
    private ProgressDialog progressDialog;
    private Products currProduct;
    String cashCode = "Cash", discountId = "", discountName = "", discountValue = "0", discountCode, oldTotal;
    String vtsPrice = "", vtsDiscount = "";
    public double newPrice;
    private String searchText;
    private String user_id;
    private  String  filterCategory="";

    @SuppressWarnings("ConstantConditions")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);

        final int id = getIntent().getIntExtra(Constants.ID, -1);
        if (id == -1) {
            Log.d(TAG, "no int extra found");
            Toast.makeText(this, "Error on getting Business Details", Toast.LENGTH_SHORT).show();
            finish();
        }

        user_id = String.valueOf(getIntent().getIntExtra(Constants.ID, -1));

        searchText = "";
        realm = Realm.getDefaultInstance();

        productList = new ArrayList<>();
        prodIdcart = new ArrayList<>();
        prodNamecart = new ArrayList<>();
        prodQuantitycart = new ArrayList<>();
        prodPricecart = new ArrayList<>();


        binding = DataBindingUtil.setContentView(this, R.layout.activity_guest_list);

        setSupportActionBar(binding.toolbar);
        getSupportActionBar().setTitle(BuildConfig.DEBUG ? "PSC Ap" : "PSC App");


        presenter.onStart();
        //binding.swipeRefreshLayout.setColorSchemeColors(getResources().getIntArray(R.array.swipe_refresh_layout_color_scheme));
        binding.swipeRefreshLayout.setOnRefreshListener(this);
        binding.recyclerView.setLayoutManager(new GridLayoutManager(this, 2));
        binding.recyclerView.setItemAnimator(new DefaultItemAnimator());
        int spanCount = 2; // 3 columns
        int spacing = 20; // 50px
        boolean includeEdge = true;
        binding.recyclerView.addItemDecoration(new GridSpacingItemDecoration(spanCount, spacing, includeEdge));


        binding.recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                int topRowVerticalPosition = (recyclerView == null || recyclerView.getChildCount() == 0)
                        ? 0 : recyclerView.getChildAt(0).getTop();
                binding.swipeRefreshLayout.setEnabled(topRowVerticalPosition >= 0);
            }
        });
        adapterPromo = new GuestActivityAdapter(this, getMvpView());
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



        categoryIdList = new ArrayList<>();
        final List<String> promoList = new ArrayList<>();

         //presenter.getCategory(user_id);
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


                ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(GuestActivity.this, android.R.layout.simple_spinner_item, promoList);
                arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                binding.spCategory.setAdapter(arrayAdapter);

            }


        });

        binding.spCategory.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
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


        binding.send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                OnButtonAddtoCart2();
            }
        });


    }


    @RequiresApi(api = Build.VERSION_CODES.M)
    public void ScanBar(View view) {
        requestScan();
    }

    // fucntion to scan barcode
    @RequiresApi(api = Build.VERSION_CODES.M)
    public void requestScan() {
        if (checkSelfPermission(Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.CAMERA}, PERMISSION_CODE);
        } else {
            startScan();
        }
    }


    public void startScan() {

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
    protected void onActivityResult(int requestCode, int resultCode, Intent in) {
        // TODO Auto-generated method stub

        if (requestCode == IntentIntegrator.REQUEST_CODE) {


            if (resultCode == RESULT_OK) {
                String contents = in.getStringExtra("SCAN_RESULT");
                currProduct = presenter.getProductQr(contents);
                if (currProduct.isLoaded() && currProduct.isValid())
                    OnButtonAddtoCart2();

            }
        }
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PERMISSION_CODE) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                startScan();
            } else {
                Toast.makeText(this, "Failed", Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.guest_menu, menu);


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

        /*

        LinearLayout v2 = (LinearLayout) menu.findItem(R.id.item_qrscan).getActionView();

        Button count2 = (Button) v2.findViewById(R.id.scan);


        count2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                startScan();
            }
        });
*/

        LinearLayout v = (LinearLayout) menu.findItem(R.id.item_samplebadge).getActionView();

        Button count1 = (Button) v.findViewById(R.id.counter);


        count1.setText(String.valueOf(prodIdcart.size()));

        count1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (prodIdcart.size() > 0)
                    checkout();
                else
                    showError("No Item on the Cart!");
            }
        });

        return true;
    }

    //GuestActivity Methods start


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


    }

    @Override
    protected void onDestroy() {
        presenter.onStop();
//        discountRealmResults.removeChangeListeners();
 //       employeeRealmResults.removeChangeListeners();
        realm.close();
        super.onDestroy();
    }

    @NonNull
    @Override
    public GuestActivityPresenter createPresenter() {
        return new GuestActivityPresenter();
    }

    @Override
    public void onRefresh() {
         presenter.load(user_id);
         presenter.getCategory(user_id);
         presenter.getDiscount(user_id);

    }




    @Override
    public void stopRefresh() {
        binding.swipeRefreshLayout.setRefreshing(false);
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
    public ViewState<GuestActivityView> createViewState() {
        setRetainInstance(true);
        return new GuestViewState();
    }

    @Override
    public void onNewViewStateInstance() {
        binding.swipeRefreshLayout.post(new Runnable() {
            @Override
            public void run() {
                binding.swipeRefreshLayout.setRefreshing(true);
                onRefresh();
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

        showError("Order Successful!");
        dialog = new Dialog(GuestActivity.this);
        final DialogTransactionQrBinding dialogBinding = DataBindingUtil.inflate(
                getLayoutInflater(),
                R.layout.dialog_transaction_qr,
                null,
                false);




        dialogBinding.setTransaction(transaction);

        try {



            Bitmap bitmap =  TextToImageEncode((transaction.getTransactionId())+"",GuestActivity.this);

            dialogBinding.productQR.setImageBitmap(bitmap);
            dialogBinding.productQRPrice.setText("Php: "+transaction.getTransactionPrice());
            dialogBinding.productQRcodeView.setText(""+(transaction.getTransactionId()));
        }
        catch (Exception e) {
            showAlert("Error Displaying Qr Code");
            Log.d("TAG>>",e+"");
            e.printStackTrace();
        }


        dialogBinding.send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                takeScreenshot(dialogBinding.qrScreenshot,transaction.getTransactionPrice());

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
    public void onAddDiscount(Discount discount) {

        discountId = String.valueOf(discount.getDiscountId());
        discountCode = discount.getDiscountCode();
        discountName = discount.getDiscountName();
        discountValue = discount.getDiscountValue();
        if (!discountId.equalsIgnoreCase("")) {
            dialogBinding.viewDiscount.setVisibility(View.VISIBLE);
            dialogBinding.cartDiscountList.setText(discountName);
            double discounted = 0;
            if (discountCode.equalsIgnoreCase("P")) {
                discounted = Double.parseDouble(vtsPrice) * (Double.parseDouble(discountValue) / 100);
            } else {
                discounted = Double.parseDouble(discountValue);
            }

            vtsDiscount = String.valueOf(discounted);
            //dialogBinding.cartDiscountPrice.setText("Php: " + String.valueOf(discounted));
            dialogBinding.cartDiscountPrice.setText("Php: "+String.valueOf(DateTimeUtils.parseDoubleTL(discounted)));


            oldTotal = dialogBinding.cartItemPrice.getText().toString();
            newPrice = Double.parseDouble(vtsPrice) - discounted;
            if (newPrice < 0)
                newPrice = 0;

            vtsPrice = String.valueOf(newPrice);
            dialogBinding.cartItemPrice.setText("Php: " + String.valueOf(newPrice));

        }
        dialog2.dismiss();

    }

    @Override
    public void onItemRemove(Products products) {

        for (int a = 0; a < prodIdcart.size(); a++) {
            if (String.valueOf(products.getProductId()).equalsIgnoreCase(prodIdcart.get(a))) {
                productList.remove(a);
                prodIdcart.remove(a);
                prodNamecart.remove(a);
                prodQuantitycart.remove(a);
                prodPricecart.remove(a);

                adapterCart.setProductList(productList, prodIdcart, prodNamecart, prodQuantitycart, prodPricecart);
                adapterCart.notifyDataSetChanged();
            }
        }
    }


    @Override
    public void OnButtonAddtoCart2() {



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

            dialog = new Dialog(GuestActivity.this);
            final DialogAddCartGuestBinding dialogBinding = DataBindingUtil.inflate(
                    getLayoutInflater(),
                    R.layout.dialog_add_cart_guest,
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
            //  dialogBinding.buyItemPrice.setText("Php: " + currProduct.getProductPrice());
            dialogBinding.buyItemPrice.setText("Php: " + DateTimeUtils.parseDoubleTL(Double.parseDouble(currProduct.getProductPrice())));

            dialogBinding.buyItemQuantity.setText("1");
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
                            dialogBinding.buyItemQuantity.setText(String.valueOf(Integer.parseInt(currProduct.getProductSKU())-skuLimiter));
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
                    prodIdcart.add(currProduct.getProductId() + "");
                    prodNamecart.add(currProduct.getProductName());
                    prodQuantitycart.add(dialogBinding.buyItemQuantity.getText().toString());
                    prodPricecart.add(String.valueOf(DateTimeUtils.parseDoubleTL(newPrice)));
                    //  prodPricecart.add(String.valueOf(newPrice));

                    GuestActivity.this.invalidateOptionsMenu();

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


    }

    @Override
    public void onItemClick(Products product) {

        currProduct = product;

        binding.itemView.setVisibility(View.VISIBLE);
        binding.remove.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                currProduct = null;
                binding.itemView.setVisibility(View.GONE);
            }
        });
        String imageURL = Endpoints.URL_IMAGE + currProduct.getProductId() + "prod";
        //String imageURL = Endpoints.URL_IMAGE + currProduct.getProductName()+"prod";
        Glide.with(this)
                .load(imageURL)
                .diskCacheStrategy(DiskCacheStrategy.NONE)
                .centerCrop()
                .error(R.drawable.placeholder)
                .into(binding.productImage);
        Log.d("TAG", imageURL);

        binding.viewItemDesc.setText(currProduct.getProductDescription());
        binding.viewItemPrice.setText("Php: "+ DateTimeUtils.parseDoubleTL(Double.parseDouble(currProduct.getProductPrice())));
      //  binding.viewItemPrice.setText("Php: " + currProduct.getProductPrice());
        binding.viewItemName.setText(currProduct.getProductName());

        String prodCode;
        if (currProduct.getProductCode().equalsIgnoreCase("E"))
            prodCode = "pcs.";
        else
            prodCode = "kg";
        binding.viewItemQuantity.setText("Quantity: " + currProduct.getProductSKU() + prodCode);


    }


    public void checkout() {


        dialog = new Dialog(GuestActivity.this, android.R.style.Theme_Black_NoTitleBar_Fullscreen);
        dialogBinding = DataBindingUtil.inflate(
                getLayoutInflater(),
                R.layout.dialog_cart,
                null,
                false);




        dialogBinding.recyclerView.setLayoutManager(new LinearLayoutManager(this));
        dialogBinding.recyclerView.setItemAnimator(new DefaultItemAnimator());

        dialogBinding.cartDiscount.setVisibility(View.GONE);
        adapterCart = new CartGuestActivityAdapter(this, getMvpView());
        dialogBinding.recyclerView.setAdapter(adapterCart);
        adapterCart.setProductList(productList, prodIdcart, prodNamecart, prodQuantitycart, prodPricecart);
        adapterCart.notifyDataSetChanged();

        double cartPrice = 0;

        for (int a = 0; a < prodPricecart.size(); a++) {
           // cartPrice += Double.parseDouble(prodPricecart.get(a));
            cartPrice += Double.parseDouble(prodPricecart.get(a).replace(",",""));

        }
        vtsPrice = String.valueOf(cartPrice);
       // dialogBinding.cartItemPrice.setText("Php: " + cartPrice + "");
        dialogBinding.cartItemPrice.setText("Php: "+DateTimeUtils.parseDoubleTL(cartPrice));



        dialogBinding.cash.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cashCode = "Cash";
                dialogBinding.cash.setBackgroundColor(ContextCompat.getColor(GuestActivity.this, R.color.lightGray));
                dialogBinding.card.setBackgroundColor(ContextCompat.getColor(GuestActivity.this, R.color.colorPrimary));

            }
        });

        dialogBinding.card.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cashCode = "Card";
                dialogBinding.card.setBackgroundColor(ContextCompat.getColor(GuestActivity.this, R.color.lightGray));
                dialogBinding.cash.setBackgroundColor(ContextCompat.getColor(GuestActivity.this, R.color.colorPrimary));

            }
        });

        dialogBinding.send.setText("Generate Qr Code");

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
                        "0",
                        "Self-Checkout",
                        DateTimeUtils.getCurrentTimeStamp(),
                        user_id);


                dialog.dismiss();
            }
        });

        dialogBinding.cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                dialog.dismiss();

            }
        });


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


                dialog2 = new Dialog(GuestActivity.this);
                final DialogDiscountBinding dialogBinding = DataBindingUtil.inflate(
                        getLayoutInflater(),
                        R.layout.dialog_discount,
                        null,
                        false);

                dialogBinding.recyclerView.setLayoutManager(new LinearLayoutManager(GuestActivity.this));
                dialogBinding.recyclerView.setItemAnimator(new DefaultItemAnimator());

                adapterDiscount = new GuesttDiscountListAdapter(GuestActivity.this, getMvpView());
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


        dialog.setContentView(dialogBinding.getRoot());
        dialog.setCancelable(false);
        dialog.show();


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


}
