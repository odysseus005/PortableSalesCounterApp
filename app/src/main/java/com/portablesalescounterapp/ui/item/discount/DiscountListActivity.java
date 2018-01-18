package com.portablesalescounterapp.ui.item.discount;

import android.app.Dialog;
import android.app.ProgressDialog;
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
import com.portablesalescounterapp.databinding.ActivityDiscountListBinding;
import com.portablesalescounterapp.databinding.DialogAddDiscountBinding;
import com.portablesalescounterapp.databinding.DialogEditDiscountBinding;
import com.portablesalescounterapp.model.data.Discount;
import com.portablesalescounterapp.model.data.Products;
import com.portablesalescounterapp.model.data.User;

import java.util.List;

import io.realm.Case;
import io.realm.Realm;
import io.realm.RealmChangeListener;
import io.realm.RealmResults;
import pl.aprilapps.easyphotopicker.EasyImage;


public class DiscountListActivity
        extends MvpViewStateActivity<DiscountListView, DiscountListPresenter>
        implements SwipeRefreshLayout.OnRefreshListener, DiscountListView {



    private ActivityDiscountListBinding binding;
    private Realm realm;
    private User user;
    private DiscountListAdapter adapterPromo;
    private RealmResults<Discount> discountRealmResults;
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
        user = realm.where(User.class).findFirst();
        binding = DataBindingUtil.setContentView(this, R.layout.activity_discount_list);
        setSupportActionBar(binding.toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Discount");

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
        adapterPromo = new DiscountListAdapter(this, getMvpView(),user.getEmail());
        binding.recyclerView.setAdapter(adapterPromo);
        discountRealmResults = realm.where(Discount.class).findAllAsync();
        discountRealmResults.addChangeListener(new RealmChangeListener<RealmResults<Discount>>() {
            @Override
            public void onChange(RealmResults<Discount> element) {
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
        discountRealmResults.removeChangeListeners();
        realm.close();
        super.onDestroy();
    }

    @NonNull
    @Override
    public DiscountListPresenter createPresenter() {
        return new DiscountListPresenter();
    }

    @NonNull
    @Override
    public ViewState<DiscountListView> createViewState() {
        setRetainInstance(true);
        return new DiscountViewState();
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
    public void OnButtonDelete(Discount category) {
        presenter.deleteContact(Integer.toString(category.getDiscountId()),user.getBusiness_id());
    }

    @Override
    public void OnButtonEdit(final Discount emergency) {

        emerID = emergency.getDiscountId();

        dialog = new Dialog(DiscountListActivity.this);
        final DialogEditDiscountBinding dialogBinding = DataBindingUtil.inflate(
                getLayoutInflater(),
                R.layout.dialog_edit_discount,
                null,
                false);

        if(emergency.getDiscountCode().equalsIgnoreCase("S"))
        {
            dialogBinding.sigma.setBackgroundColor(ContextCompat.getColor(DiscountListActivity.this, R.color.colorPrimary));
            dialogBinding.percent.setBackgroundColor(ContextCompat.getColor(DiscountListActivity.this, R.color.lightGray));

        }


        dialogBinding.percent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                discountCode = "P";
                dialogBinding.percent.setBackgroundColor(ContextCompat.getColor(DiscountListActivity.this, R.color.colorPrimary));
                dialogBinding.sigma.setBackgroundColor(ContextCompat.getColor(DiscountListActivity.this, R.color.lightGray));

            }
        });

        dialogBinding.sigma.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                discountCode = "S";
                dialogBinding.sigma.setBackgroundColor(ContextCompat.getColor(DiscountListActivity.this, R.color.colorPrimary));
                dialogBinding.percent.setBackgroundColor(ContextCompat.getColor(DiscountListActivity.this, R.color.lightGray));

            }
        });



        dialogBinding.setDiscount(emergency);

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


                presenter.updateContact(Integer.toString(emergency.getDiscountId()),
                        dialogBinding.etName.getText().toString(),
                        dialogBinding.etValue.getText().toString(),
                        discountCode,
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
        dialog = new Dialog(DiscountListActivity.this);
        final DialogAddDiscountBinding dialogBinding = DataBindingUtil.inflate(
                getLayoutInflater(),
                R.layout.dialog_add_discount,
                null,
                false);

        dialogBinding.percent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                discountCode = "P";
                dialogBinding.percent.setBackgroundColor(ContextCompat.getColor(DiscountListActivity.this, R.color.colorPrimary));
                dialogBinding.sigma.setBackgroundColor(ContextCompat.getColor(DiscountListActivity.this, R.color.lightGray));

            }
        });

        dialogBinding.sigma.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                discountCode = "S";
                dialogBinding.sigma.setBackgroundColor(ContextCompat.getColor(DiscountListActivity.this, R.color.colorPrimary));
                dialogBinding.percent.setBackgroundColor(ContextCompat.getColor(DiscountListActivity.this, R.color.lightGray));

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
                presenter.addContact(
                        user.getBusiness_id(),
                        dialogBinding.etName.getText().toString(),
                        dialogBinding.etValue.getText().toString(),
                        discountCode
                        );
                //dialog.dismiss();
            }
        });
        dialog.setContentView(dialogBinding.getRoot());
        dialog.setCancelable(false);
        dialog.show();

    }

    private void prepareList() {

        if (discountRealmResults.isLoaded() && discountRealmResults.isValid()) {
            List<Discount> productsList;
            if (searchText.isEmpty()) {
                productsList = realm.copyFromRealm(discountRealmResults);
            } else {
                productsList = realm.copyFromRealm(discountRealmResults.where()
                        .contains("discountName", searchText, Case.INSENSITIVE)
                        .or()
                        .contains("discountValue", searchText, Case.INSENSITIVE)
                        .or()
                        .contains("discountCode", searchText, Case.INSENSITIVE)
                        .findAll());
            }
            adapterPromo.setDiscountList(productsList);
            adapterPromo.notifyDataSetChanged();
        }
    }


}


