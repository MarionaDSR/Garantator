package es.dsrroma.garantator;

import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.text.format.DateFormat;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.GridView;
import android.widget.TextView;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import es.dsrroma.garantator.adapters.PictureAdapter;
import es.dsrroma.garantator.data.contracts.PictureContract;
import es.dsrroma.garantator.data.model.Brand;
import es.dsrroma.garantator.data.model.Category;
import es.dsrroma.garantator.data.model.Picture;
import es.dsrroma.garantator.data.model.Product;
import es.dsrroma.garantator.data.model.Warranty;
import es.dsrroma.garantator.data.services.WarrantyUpdateService;

import static es.dsrroma.garantator.data.contracts.PictureContract.PICTURE_CONTENT_URI;
import static es.dsrroma.garantator.data.contracts.WarrantyViewContract.WARRANTY_VIEW_CONTENT_URI;
import static es.dsrroma.garantator.data.contracts.WarrantyViewContract.getBeanFromCursor;
import static es.dsrroma.garantator.data.helpers.WarrantiesProvider.WARRANTY_FILTER;

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

    @SuppressWarnings("WeakerAccess")
    @BindView(R.id.tvStartDate)
    TextView tvStartDate;

    @SuppressWarnings("WeakerAccess")
    @BindView(R.id.tvWarrantyLength)
    TextView tvWarrantyLength;

    @SuppressWarnings("WeakerAccess")
    @BindView(R.id.tvEndDate)
    TextView tvEndDate;

    @SuppressWarnings("WeakerAccess")
    @BindView(R.id.gvPictures)
    GridView gvPictures;

    private Warranty warranty;
    private Cursor cursor;

    private PictureAdapter pictureAdapter;

    boolean toRefresh = true;

    private static final int WARRANTY_LOADER_ID = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        ButterKnife.bind(this);

        refreshDetail();
        getSupportLoaderManager().initLoader(WARRANTY_LOADER_ID, null, this);

        pictureAdapter = new PictureAdapter(this, warranty, false);
        gvPictures.setAdapter(pictureAdapter);
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
        if (warranty.getStartDate() != null) {
            CharSequence formatted = DateFormat.format(getString(R.string.date_format), warranty.getStartDate());
            tvStartDate.setText(formatted);
        }
        if (warranty.getEndDate() != null) {
            CharSequence formatted = DateFormat.format(getString(R.string.date_format), warranty.getEndDate());
            tvEndDate.setText(formatted);
        }
        tvWarrantyLength.setText(warranty.getLength() + " " + getPeriodLabel(warranty.getPeriod()));

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
        loadPictures();
    }

    private void loadPictures() {
        Uri picturesUri = PICTURE_CONTENT_URI.buildUpon().appendPath(WARRANTY_FILTER).appendPath(Long.toString(warranty.getId())).build();
        cursor = getContentResolver().query(picturesUri, null, null, null, null, null);
        List<Picture> pictures = PictureContract.getBeansFromCursor(cursor);
        warranty.setPictures(pictures);
    }

    private String getPeriodLabel(String period) {
        String[] values = getResources().getStringArray(R.array.warranty_period_values);
        String[] labels = getResources().getStringArray(R.array.warranty_period_labels);
        for (int i = 0; i < values.length; i ++) {
            if (values[i].equals(period)) {
                return labels[i];
            }
        }
        return ""; // Not possible
    }
}
