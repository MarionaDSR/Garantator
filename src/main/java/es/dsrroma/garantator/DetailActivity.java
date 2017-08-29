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

import java.util.ArrayList;
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
import static es.dsrroma.garantator.data.contracts.PictureContract.PictureEntry.COLUMN_POSITION;
import static es.dsrroma.garantator.data.contracts.WarrantyViewContract.WARRANTY_VIEW_CONTENT_URI;
import static es.dsrroma.garantator.data.contracts.WarrantyViewContract.getBeanFromCursor;
import static es.dsrroma.garantator.data.helpers.WarrantiesProvider.WARRANTY_FILTER;
import static es.dsrroma.garantator.utils.Constants.EXTRA_WARRANTY_ID;
import static es.dsrroma.garantator.utils.Constants.PICTURES_LOADER_ID;
import static es.dsrroma.garantator.utils.Constants.WARRANTY_LOADER_ID;

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
    private Bundle extras;

    private PictureAdapter pictureAdapter;

    boolean toRefresh = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        ButterKnife.bind(this);

        extras = getIntent().getExtras();
        getSupportLoaderManager().initLoader(WARRANTY_LOADER_ID, extras, this);
        getSupportLoaderManager().initLoader(PICTURES_LOADER_ID, extras, this);

        pictureAdapter = new PictureAdapter(this, new ArrayList(), false);
        gvPictures.setAdapter(pictureAdapter);
    }

    @Override
    protected void onResume() {
        super.onResume();

        if (toRefresh) {
            getSupportLoaderManager().restartLoader(WARRANTY_LOADER_ID, extras, this);
            getSupportLoaderManager().restartLoader(PICTURES_LOADER_ID, extras, this);
            toRefresh = false;
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        toRefresh = true;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.detail, menu);
        return true;
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        long warrantyId = args.getLong(EXTRA_WARRANTY_ID);
        switch (id) {
            case WARRANTY_LOADER_ID:
                return new CursorLoader(this, getWarrantyUri(warrantyId), null, null, null, null);
            case PICTURES_LOADER_ID:
                return new CursorLoader(this, getPicturesUri(warrantyId), null, null, null, COLUMN_POSITION);
        }
        return null;
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        switch (loader.getId()) {
            case WARRANTY_LOADER_ID:
                showWarranty(data);
                break;
            case PICTURES_LOADER_ID:
                loadPictures(data);
                break;
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        switch (loader.getId()) {
            case WARRANTY_LOADER_ID:
                warranty = new Warranty();
                break;
            case PICTURES_LOADER_ID:
                warranty.setPictures(new ArrayList<Picture>());
                break;
        }
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
        intent.putExtra(EXTRA_WARRANTY_ID, warranty.getId());
        startActivity(intent);
    }

    private Uri getWarrantyUri(long warrantyId) {
        return WARRANTY_VIEW_CONTENT_URI.buildUpon().appendPath(Long.toString(warrantyId)).build();
    }

    private Uri getPicturesUri(long warrantyId) {
        return PICTURE_CONTENT_URI.buildUpon().appendPath(WARRANTY_FILTER).appendPath(Long.toString(warrantyId)).build();
    }

    private void showWarranty(Cursor cursor) {
        if (cursor.moveToFirst()) {
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
        }
    }

    private void loadPictures(Cursor cursor) {
        List<Picture> pictures = PictureContract.getBeansFromCursor(cursor);
        if (warranty == null) {
            warranty = new Warranty();
        }
        warranty.setPictures(pictures);
        pictureAdapter.resetPictures(pictures);
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
