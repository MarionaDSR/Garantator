package es.dsrroma.garantator;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.AsyncTaskLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import es.dsrroma.garantator.adapters.WarrantyAdapter;
import es.dsrroma.garantator.data.contracts.BrandContract;
import es.dsrroma.garantator.data.contracts.CategoryContract;
import es.dsrroma.garantator.data.contracts.ProductContract;
import es.dsrroma.garantator.data.contracts.WarrantyContract;
import es.dsrroma.garantator.data.model.Warranty;

public class MainActivity extends AppCompatActivity implements
        WarrantyAdapter.WarrantyAdapterOnClickHandler,
        LoaderManager.LoaderCallbacks<List<Warranty>> {

    private WarrantyAdapter warrantyAdapter;

    private RecyclerView rvWarrantiesList;
    private TextView tvErrorMessage;
    private ProgressBar pbLoadingIndicator;

    private static final int WARRANTY_LOADER_ID = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        rvWarrantiesList = (RecyclerView) findViewById(R.id.rvWarrantiesList);
        tvErrorMessage = (TextView) findViewById(R.id.tvErrorMessage);
        pbLoadingIndicator = (ProgressBar) findViewById(R.id.pbLoadingIndicator);

        LinearLayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        rvWarrantiesList.setLayoutManager(layoutManager);
        rvWarrantiesList.setHasFixedSize(true); // all items will have the same size

        warrantyAdapter = new WarrantyAdapter(this);
        rvWarrantiesList.setAdapter(warrantyAdapter);

        getSupportLoaderManager().initLoader(WARRANTY_LOADER_ID, null, MainActivity.this);
    }

    @Override
    public void onClick(Warranty warranty) {
        Intent intentDetailActivity = new Intent(this, DetailActivity.class);
        intentDetailActivity.putExtra(DetailActivity.EXTRA_WARRANTY, warranty);
        startActivity(intentDetailActivity);
    }

    @Override
    public Loader<List<Warranty>> onCreateLoader(int id, Bundle args) {
        return new AsyncTaskLoader<List<Warranty>>(this) {

            List<Warranty> warranties = null;

            @Override
            protected void onStartLoading() {
                if (warranties != null) {
                    deliverResult(warranties);
                } else {
                    pbLoadingIndicator.setVisibility(View.VISIBLE);
                    forceLoad();
                }
            }

            @Override
            public List<Warranty> loadInBackground() {
                return dummyWarranties(); // TODO load from DB
            }

            @Override
            public void deliverResult(List<Warranty> data) {
                warranties = data;
                super.deliverResult(data);
            }
        };
    }

    @Override
    public void onLoadFinished(Loader<List<Warranty>> loader, List<Warranty> data) {
        pbLoadingIndicator.setVisibility(View.INVISIBLE);
        warrantyAdapter.setWarranties(data);
        if (data == null) {
            showErrorMessage();
        } else {
            showList();
        }
    }

    @Override
    public void onLoaderReset(Loader<List<Warranty>> loader) {
        // Nothing to do
    }

    private void showList() {
        rvWarrantiesList.setVisibility(View.VISIBLE);
        tvErrorMessage.setVisibility(View.INVISIBLE);
    }

    private void showErrorMessage() {
        rvWarrantiesList.setVisibility(View.INVISIBLE);
        tvErrorMessage.setVisibility(View.VISIBLE);
    }

    @Deprecated
    private List<Warranty> dummyWarranties() {
        List<Warranty> warranties = new ArrayList<>();

        Warranty warranty0 = new Warranty();
        warranty0.setId(1);
        warranty0.setName(CategoryContract.SQL_CREATE_CATEGORY_TABLE);
        warranty0.setStartDate(new Date());
        warranties.add(warranty0);

        Warranty warranty1 = new Warranty();
        warranty1.setId(2);
        warranty1.setName(BrandContract.SQL_CREATE_BRAND_TABLE);
        warranty1.setStartDate(new Date());
        warranties.add(warranty1);

        Warranty warranty2 = new Warranty();
        warranty2.setId(3);
        warranty2.setName(ProductContract.SQL_CREATE_PRODUCT_TABLE);
        warranty2.setStartDate(new Date());
        warranties.add(warranty2);

        Warranty warranty3 = new Warranty();
        warranty3.setId(4);
        warranty3.setName(WarrantyContract.SQL_CREATE_WARRANTY_TABLE);
        warranty3.setStartDate(new Date());
        warranties.add(warranty3);

        for (int i = 0; i < 50000; i ++) { // to simulate loading time
            System.out.print(".");
        }

        return warranties;
    }
}
