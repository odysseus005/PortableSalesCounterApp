package com.portablesalescounterapp.ui.business;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.hannesdorfmann.mosby.mvp.viewstate.MvpViewStateActivity;
import com.hannesdorfmann.mosby.mvp.viewstate.ViewState;
import com.portablesalescounterapp.R;
import com.portablesalescounterapp.app.Constants;
import com.portablesalescounterapp.databinding.ActivityBusinessListBinding;
import com.portablesalescounterapp.model.data.Business;
import com.portablesalescounterapp.model.data.User;
import com.portablesalescounterapp.ui.guest.GuestActivity;

import java.util.List;

import io.realm.Case;
import io.realm.Realm;
import io.realm.RealmChangeListener;
import io.realm.RealmResults;
import pl.aprilapps.easyphotopicker.EasyImage;


public class BusinessListActivity
        extends MvpViewStateActivity<BusinessListView, BusinessListPresenter>
        implements SwipeRefreshLayout.OnRefreshListener, BusinessListView {



    private ActivityBusinessListBinding binding;
    private Realm realm;
    private BusinessListAdapter adapterPromo;
    private RealmResults<Business> discountRealmResults;
    private Dialog dialog;
    private ProgressDialog progressDialog;
    private int emerID=0;
    private String discountCode="P";
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
        binding = DataBindingUtil.setContentView(this, R.layout.activity_business_list);
        setSupportActionBar(binding.toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Business List");

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
        adapterPromo = new BusinessListAdapter(this, getMvpView());
        binding.recyclerView.setAdapter(adapterPromo);
        discountRealmResults = realm.where(Business.class).findAllAsync();
        discountRealmResults.addChangeListener(new RealmChangeListener<RealmResults<Business>>() {
            @Override
            public void onChange(RealmResults<Business> element) {
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
        discountRealmResults.removeChangeListeners();
        realm.close();
        super.onDestroy();
    }

    @NonNull
    @Override
    public BusinessListPresenter createPresenter() {
        return new BusinessListPresenter();
    }

    @NonNull
    @Override
    public ViewState<BusinessListView> createViewState() {
        setRetainInstance(true);
        return new BusinessViewState();
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
    public void OnButtonClick(Business business) {

        Intent intent = new Intent(this, GuestActivity.class);
        intent.putExtra(Constants.ID, business.getBusinessId());
        startActivity(intent);

    }



    @Override
    public void onRefresh() {
        presenter.load();
    }


    private void prepareList() {

        if (discountRealmResults.isLoaded() && discountRealmResults.isValid()) {
            List<Business> productsList;
            if (searchText.isEmpty()) {
                productsList = realm.copyFromRealm(discountRealmResults);
            } else {
                productsList = realm.copyFromRealm(discountRealmResults.where()
                        .contains("businessName", searchText, Case.INSENSITIVE)
                        .or()
                        .contains("businessContact", searchText, Case.INSENSITIVE)
                        .or()
                        .contains("businessAddress", searchText, Case.INSENSITIVE)
                        .findAll());
            }
            adapterPromo.setDiscountList(productsList);
            adapterPromo.notifyDataSetChanged();
        }
    }


}


