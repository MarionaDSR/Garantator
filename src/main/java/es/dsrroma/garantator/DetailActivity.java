package es.dsrroma.garantator;

import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import butterknife.BindView;
import butterknife.ButterKnife;
import es.dsrroma.garantator.data.model.Brand;
import es.dsrroma.garantator.data.model.Category;
import es.dsrroma.garantator.data.model.Product;
import es.dsrroma.garantator.data.model.Warranty;
import es.dsrroma.garantator.data.services.WarrantyUpdateService;

import static es.dsrroma.garantator.data.contracts.WarrantyViewContract.WARRANTY_VIEW_CONTENT_URI;
import static es.dsrroma.garantator.data.contracts.WarrantyViewContract.getBeanFromCursor;

public class DetailActivity extends AppCompatActivity implements
        LoaderManager.LoaderCallbacks<Cursor> {

    @SuppressWarnings("WeakerAccess")
    @BindView(R.id.tvWarrantyName)
    TextView tvWarrantyName;

    @SuppressWarnings("WeakerAccess")
    @BindView(R.id.tvProductName)
    TextView tvProductName;

    @SuppressWarnings("WeakerAccess")
    @BindView(R.id.tvCategory)
    TextView tvCategory;

    @SuppressWarnings("WeakerAccess")
    @BindView(R.id.tvBrand)
    TextView tvBrand;

    @SuppressWarnings("WeakerAccess")
    @BindView(R.id.tvModel)
    TextView tvModel;

    @SuppressWarnings("WeakerAccess")
    @BindView(R.id.tvSerialNumber)
    TextView tvSerialNumber;

    private Warranty warranty;
    private Cursor cursor;

    boolean toRefresh = true;

    private static final int WARRANTY_LOADER_ID = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        ButterKnife.bind(this);

        refreshDetail();
        getSupportLoaderManager().initLoader(WARRANTY_LOADER_ID, null, this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        refreshDetail();
    }

    @Override
    protected void onPause() {
        super.onPause();
        toRefresh = true;
    }

    private void refreshDetail() {
        if (toRefresh) {
            final Uri warrantyUri = getIntent().getData();
            cursor = getContentResolver().query(warrantyUri, null, null, null, null, null);

            if (cursor.moveToFirst()) {
                showWarranty();
            }
            toRefresh = false;
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.detail, menu);
        return true;
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        return new CursorLoader(this, getUri(), null, null, null, null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        if (this.cursor != null) {
            this.cursor.close();
        }
        this.cursor = data;
        showWarranty();
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        warranty = new Warranty();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case R.id.action_edit:
                editWarranty();
                return true;
            case R.id.action_delete:
                deleteWarranty();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void deleteWarranty() {
        WarrantyUpdateService.deleteWarrantyBatch(this, warranty);
        finish();
    }

    private void editWarranty() {
        Intent intent = new Intent(this, AddWarrantyActivity.class);
        intent.setData(getUri());
        startActivity(intent);
    }

    private Uri getUri() {
        return WARRANTY_VIEW_CONTENT_URI.buildUpon().appendPath(Long.toString(warranty.getId())).build();
    }

    private void showWarranty() {
        warranty = getBeanFromCursor(cursor);
        tvWarrantyName.setText(warranty.getName());
        Product product = warranty.getProduct();
        if (product != null) {
            tvProductName.setText(product.getName());
            Category category = product.getCategory();
            if (category != null) {
                tvCategory.setText(category.getName());
            }
            Brand brand = product.getBrand();
            if (brand != null) {
                tvBrand.setText(brand.getName());
            }
            tvModel.setText(product.getModel());
            tvSerialNumber.setText(product.getSerialNumber());
        }
    }
}
