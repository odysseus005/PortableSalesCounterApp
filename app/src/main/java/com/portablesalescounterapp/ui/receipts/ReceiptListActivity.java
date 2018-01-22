package com.portablesalescounterapp.ui.receipts;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;

import com.hannesdorfmann.mosby.mvp.viewstate.MvpViewStateActivity;
import com.hannesdorfmann.mosby.mvp.viewstate.ViewState;
import com.portablesalescounterapp.R;
import com.portablesalescounterapp.databinding.ActivityReceiptListBinding;
import com.portablesalescounterapp.databinding.DialogCartBinding;
import com.portablesalescounterapp.databinding.DialogReceiptBinding;
import com.portablesalescounterapp.model.data.Business;
import com.portablesalescounterapp.model.data.Products;
import com.portablesalescounterapp.model.data.Transaction;
import com.portablesalescounterapp.model.data.User;
import com.portablesalescounterapp.util.DateTimeUtils;

import java.util.ArrayList;
import java.util.List;

import io.realm.Case;
import io.realm.Realm;
import io.realm.RealmChangeListener;
import io.realm.RealmResults;
import io.realm.Sort;
import pl.aprilapps.easyphotopicker.EasyImage;


public class ReceiptListActivity
        extends MvpViewStateActivity<ReceiptListView, ReceiptListPresenter>
        implements SwipeRefreshLayout.OnRefreshListener, ReceiptListView {



    private ActivityReceiptListBinding binding;
    private Realm realm;
    private User user;
    private Business business;
    private ReceiptListAdapter adapterPromo;
    private RealmResults<Transaction> employeeRealmResults;
    private ArrayList<Integer> categoryIdList;
    private Dialog dialog;
    private CartreceiptActivityAdapter adapterCart;
    DialogReceiptBinding dialogBinding;
    private ProgressDialog progressDialog;
    private String searchText;
    private String sorter="Date Descending";

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
        business = realm.where(Business.class).findFirst();
        user = realm.where(User.class).findFirst();
        binding = DataBindingUtil.setContentView(this, R.layout.activity_receipt_list);
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
        adapterPromo = new ReceiptListAdapter(this, getMvpView(),user.getEmail());
        binding.recyclerView.setAdapter(adapterPromo);
        employeeRealmResults = realm.where(Transaction.class).findAllAsync();
        employeeRealmResults.addChangeListener(new RealmChangeListener<RealmResults<Transaction>>() {
            @Override
            public void onChange(RealmResults<Transaction> element) {
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
        categories.add("Date Descending");
        categories.add("Date Ascending");
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
       // employeeRealmResults.removeChangeListeners();
        realm.close();
        super.onDestroy();
    }

    @NonNull
    @Override
    public ReceiptListPresenter createPresenter() {
        return new ReceiptListPresenter();
    }

    @NonNull
    @Override
    public ViewState<ReceiptListView> createViewState() {
        setRetainInstance(true);
        return new ReceiptViewState();
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
    public void onTransactionClicked(Transaction transaction) {



        dialog = new Dialog(this);
        dialogBinding = DataBindingUtil.inflate(
                getLayoutInflater(),
                R.layout.dialog_receipt,
                null,
                false);






        dialogBinding.recyclerView.setLayoutManager(new LinearLayoutManager(this));
        dialogBinding.recyclerView.setItemAnimator(new DefaultItemAnimator());

        adapterCart = new CartreceiptActivityAdapter(this, getMvpView());
        dialogBinding.recyclerView.setAdapter(adapterCart);
        adapterCart.setProductList(presenter.StringtoList(transaction.getTransactionIdList()),presenter.StringtoList(transaction.getTransactionNameList()),presenter.StringtoList(transaction.getTransactionQuantityList()),presenter.StringtoList(transaction.getTransactionPriceList()));
        adapterCart.notifyDataSetChanged();


        dialogBinding.orbName.setText(business.getBusinessName());
        dialogBinding.orbAddress.setText(business.getBusinessAddress());
        dialogBinding.orbContact.setText(business.getBusinessContact());
        dialogBinding.orDate.setText(DateTimeUtils.toReadable(transaction.getDate()));
        dialogBinding.orTime.setText(DateTimeUtils.getTimeOnly(transaction.getDate()));
        dialogBinding.orPayment.setText("Payment Method: "+transaction.getTransactionCode());
        dialogBinding.orPrice.setText("Php: "+transaction.getTransactionPrice());
        dialogBinding.orCashierName.setText("Cashier Name: "+transaction.getUserName());


        dialogBinding.etReceiptEmail.setVisibility(View.GONE);

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

            }
        });


        dialog.setContentView(dialogBinding.getRoot());
        dialog.setCancelable(false);
        dialog.show();












    }

    @Override
    public void dismiss() {
       dialog.dismiss();
    }


    @Override
    public void onButtonRefund(Transaction transact) {

            presenter.refundTransaction(user.getBusiness_id(),String.valueOf(transact.getTransactionId()),transact.getTransactionIdList(),transact.getTransactionQuantityList());
    }





    @Override
    public void onRefresh() {
        presenter.load(""+user.getBusiness_id());
    }




    private void prepareList() {

        if (employeeRealmResults.isLoaded() && employeeRealmResults.isValid()) {
            List<Transaction> productsList;
            if (searchText.isEmpty()) {

                if(sorter.equalsIgnoreCase("Date Descending"))
                productsList = realm.copyFromRealm(employeeRealmResults.sort("date", Sort.DESCENDING));
                else
                    productsList = realm.copyFromRealm(employeeRealmResults.sort("date", Sort.ASCENDING));

            } else {

                if (sorter.equalsIgnoreCase("Date Descending"))
                {
                    productsList = realm.copyFromRealm(employeeRealmResults.where()
                            .contains("transactionPrice", searchText, Case.INSENSITIVE)
                            .or()
                            .contains("transactionDiscount", searchText, Case.INSENSITIVE)
                            .or()
                            .contains("transactionCode", searchText, Case.INSENSITIVE)
                            .or()
                            .contains("date", searchText, Case.INSENSITIVE)
                            .or()
                            .contains("discountName", searchText, Case.INSENSITIVE)
                            .or()
                            .contains("userName", searchText, Case.INSENSITIVE)
                            .findAll().sort("transactionId", Sort.DESCENDING));

            }
            else
            {
                productsList = realm.copyFromRealm(employeeRealmResults.where()
                        .contains("transactionPrice", searchText, Case.INSENSITIVE)
                        .or()
                        .contains("transactionDiscount", searchText, Case.INSENSITIVE)
                        .or()
                        .contains("transactionCode", searchText, Case.INSENSITIVE)
                        .or()
                        .contains("date", searchText, Case.INSENSITIVE)
                        .or()
                        .contains("discountName", searchText, Case.INSENSITIVE)
                        .or()
                        .contains("userName", searchText, Case.INSENSITIVE)
                        .findAll().sort("transactionId", Sort.ASCENDING));

            }
            }
            adapterPromo.setProductList(productsList);
            adapterPromo.notifyDataSetChanged();
        }
    }





}


