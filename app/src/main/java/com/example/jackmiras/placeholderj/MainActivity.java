package com.example.jackmiras.placeholderj;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.TextView;

import com.example.jackmiras.placeholderj.adapter.MainAdapter;
import com.example.jackmiras.placeholderj.api.ApiClient;
import com.example.jackmiras.placeholderj.library.PlaceHolderJ;
import com.example.jackmiras.placeholderj.models.CouponResponse;

import butterknife.Bind;
import butterknife.ButterKnife;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

public class MainActivity extends AppCompatActivity {
    private final static int NUM_COLUNS = 2;

    @Bind(R.id.toolbar)
    Toolbar toolbar;
    @Bind(R.id.recyclerview_cupon)
    RecyclerView recyclerView;

    private PlaceHolderJ placeHolderJ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        TextView textView = (TextView) toolbar.findViewById(R.id.textview_toolbar_title);
        textView.setText(R.string.app_name);
        placeHolderJ = new PlaceHolderJ();
        placeHolderJ.init(this, R.id.recyclerview_cupon, R.id.view_loading, R.id.view_empty, R.id.view_error);
        setupViews();
    }

    public void setupViews() {
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new GridLayoutManager(this, NUM_COLUNS));
        requestUserCoupons();

    }

    public void showErrorView(RetrofitError error) {
        placeHolderJ.hideLoading();
        placeHolderJ.showError(error, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                placeHolderJ.hideError();
                placeHolderJ.showLoading();
                requestUserCoupons();
            }
        });
    }

    private void requestUserCoupons() {
        placeHolderJ.showLoading();
        ApiClient.getServices().getUserCoupons(new Callback<CouponResponse>() {
            @Override
            public void success(CouponResponse couponResponse, Response response) {
                placeHolderJ.hideLoading();
                if (couponResponse.result != null && couponResponse.result.size() >0) {
                    recyclerView.setAdapter(new MainAdapter(MainActivity.this, couponResponse.result));
                } else {
                    placeHolderJ.showEmpty(R.string.activity_redeem_empty, null, null);
                }
            }

            @Override
            public void failure(RetrofitError error) {
                showErrorView(error);
            }
        });
    }
}
