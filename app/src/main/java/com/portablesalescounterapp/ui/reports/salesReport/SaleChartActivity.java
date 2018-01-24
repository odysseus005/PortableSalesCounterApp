package com.portablesalescounterapp.ui.reports.salesReport;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.MenuItem;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;
import com.github.mikephil.charting.formatter.LargeValueFormatter;
import com.hannesdorfmann.mosby.mvp.MvpActivity;
import com.portablesalescounterapp.R;
import com.portablesalescounterapp.app.Constants;
import com.portablesalescounterapp.databinding.ActivitySalechartBinding;
import com.portablesalescounterapp.model.data.User;
import com.portablesalescounterapp.ui.login.LoginActivity;
import com.portablesalescounterapp.ui.register.RegisterActivity;

import java.util.ArrayList;

import io.realm.Realm;


public class SaleChartActivity extends MvpActivity<SaleChartView, SaleChartPresenter> implements SaleChartView {

    private ActivitySalechartBinding binding;
    private ProgressDialog progressDialog;
    String url="";
    private Realm realm;
    private User user;
    private String title;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_salechart);

        realm = Realm.getDefaultInstance();
        user = realm.where(User.class).findFirst();
        binding = DataBindingUtil.setContentView(this, R.layout.activity_salechart);
        binding.setView(getMvpView());
        setSupportActionBar(binding.toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Generate Reports");
        if (progressDialog == null) {
            progressDialog = new ProgressDialog(this);
            progressDialog.setMessage("Loading...");
            progressDialog.setCancelable(false);
        }
        progressDialog.show();

        final int id = getIntent().getIntExtra(Constants.REPORT_TYPE, -1);
        if (id == -1) {
            Log.d("", "no int extra found");
            Toast.makeText(this, "Error on getting Report", Toast.LENGTH_SHORT).show();
            finish();
        }
        if(id==1)
        {
            url="https://findingodysseus.000webhostapp.com/portablesalescounter/reports/pages/index.php?business_id="+user.getBusiness_id();
            //url="https://findingodysseus.000webhostapp.com/portablesalescounter/reports/pages/index.php";
            title="";
        }else if(id==2)
        {
            url="https://findingodysseus.000webhostapp.com/portablesalescounter/reports/pages/top-products.php?business_id="+user.getBusiness_id();
            //url="https://findingodysseus.000webhostapp.com/portablesalescounter/reports/pages/top-products.php";
            title="";
        }
        else if(id==3)
        {
            url="https://findingodysseus.000webhostapp.com/portablesalescounter/reports/pages/monthly-predictions.php?business_id="+user.getBusiness_id();
           // url="https://findingodysseus.000webhostapp.com/portablesalescounter/reports/pages/monthly-predictions.php";
            title="";
        }



        binding.webview.clearCache(true);
        binding.webview.clearHistory();
        binding.webview.getSettings().setJavaScriptEnabled(true);
        binding.webview.getSettings().setJavaScriptCanOpenWindowsAutomatically(true);


        binding.webview.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                view.loadUrl(url);
                return true;
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                if (progressDialog != null) {
                    if (progressDialog.isShowing()) {
                        progressDialog.dismiss();
                    }
                }
            }

            @Override
            public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {

                showAlert("Unable to Connect to Server!");

            }
        });
        binding.webview.loadUrl(url);




    }

    @Override
    public void showAlert(String message) {
        new AlertDialog.Builder(this)
                .setTitle("Loading Error")
                .setMessage(message)
                .setCancelable(false)
                .setPositiveButton("Close", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        SaleChartActivity.this.finish();
                        // Toast.makeText(UserRegisterActivity.this, "An email has been sent to your email for verification!", Toast.LENGTH_SHORT).show();
                       // startActivity(new Intent(SaleChartActivity.this, LoginActivity.class));
                        finish();
                    }
                })
                .show();
    }


    @NonNull
    @Override
    public SaleChartPresenter createPresenter() {
        return new SaleChartPresenter();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
