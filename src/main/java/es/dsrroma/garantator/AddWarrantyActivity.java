package es.dsrroma.garantator;

import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.SimpleCursorAdapter;
import android.widget.Toast;

import es.dsrroma.garantator.data.contracts.BrandContract;
import es.dsrroma.garantator.data.contracts.CategoryContract;
import es.dsrroma.garantator.data.contracts.WarrantyContract;
import es.dsrroma.garantator.data.model.Warranty;
import es.dsrroma.garantator.data.services.WarrantyUpdateService;
import es.dsrroma.garantator.utils.CursorToBeanUtils;

import static es.dsrroma.garantator.data.contracts.BrandContract.BRAND_CONTENT_URI;
import static es.dsrroma.garantator.data.contracts.CategoryContract.CATEGORY_CONTENT_URI;
import static es.dsrroma.garantator.data.contracts.WarrantyContract.WARRANTY_CONTENT_URI;

public class AddWarrantyActivity extends AppCompatActivity implements
        LoaderManager.LoaderCallbacks<Cursor> {

    private SimpleCursorAdapter brandAdapter;
    private SimpleCursorAdapter categoryAdapter;

    private EditText etName;
    private AutoCompleteTextView actvBrand;
    private AutoCompleteTextView actvCategory;

    private boolean editMode;

    private Warranty warranty;
    private Cursor cursor;

    private static final int BRAND_LOADER_ID = 2;
    private static final int CATEGORY_LOADER_ID = 3;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_warranty);

        etName = (EditText) findViewById(R.id.etWarrantyName);
        actvBrand = (AutoCompleteTextView) findViewById(R.id.actvBrand);
        actvCategory = (AutoCompleteTextView) findViewById(R.id.actvCategory);

        brandAdapter = new SimpleCursorAdapter(this, android.R.layout.simple_dropdown_item_1line, null,
                new String[] {BrandContract.BrandEntry.COLUMN_NAME},
                new int[] { android.R.id.text1 }, 0);
        brandAdapter.setStringConversionColumn(1);
        actvBrand.setAdapter(brandAdapter);

        categoryAdapter = new SimpleCursorAdapter(this, android.R.layout.simple_dropdown_item_1line, null,
                new String[] {CategoryContract.CategoryEntry.COLUMN_NAME},
                new int[] { android.R.id.text1 }, 0);
        categoryAdapter.setStringConversionColumn(1);
        actvCategory.setAdapter(categoryAdapter);

        final Uri warrantyUri = getIntent().getData();
        editMode = warrantyUri != null;
        if (editMode) {
            cursor = getContentResolver().query(warrantyUri, null, null, null, null, null);

            if (cursor.moveToFirst()) {
                warranty = CursorToBeanUtils.cursorToBean(cursor, 0, Warranty.class);
                etName.setText(warranty.getName());
            }
        } else { // add mode
            warranty = new Warranty();
        }

        setListeners();
        getSupportLoaderManager().initLoader(BRAND_LOADER_ID, null, this);
        getSupportLoaderManager().initLoader(CATEGORY_LOADER_ID, null, this);
    }

    private void setListeners() {
        etName.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                // Nothing to do
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // Nothing to do
            }

            @Override
            public void afterTextChanged(Editable s) {
                warranty.setName(s.toString());
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.add, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case R.id.action_done:
                if (validateContents()) {
                    ContentValues cv = warrantyToContentValues();
                    if (editMode) {
                        updateWarranty(cv);
                    } else { // add mode
                        addWarranty(cv);
                    }
                }
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        switch (id) {
            case BRAND_LOADER_ID:
                return new CursorLoader(this, BRAND_CONTENT_URI, null, null, null, null);
            case CATEGORY_LOADER_ID:
                return new CursorLoader(this, CATEGORY_CONTENT_URI, null, null, null, null);
        }
        return null;
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        switch (loader.getId()) {
            case BRAND_LOADER_ID:
                brandAdapter.swapCursor(data);
            case CATEGORY_LOADER_ID:
                categoryAdapter.swapCursor(data);
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        switch (loader.getId()) {
            case BRAND_LOADER_ID:
                brandAdapter.swapCursor(null);
            case CATEGORY_LOADER_ID:
                categoryAdapter.swapCursor(null);
        }
    }

    private void addWarranty(ContentValues cv) {
        WarrantyUpdateService.insertNewWarranty(this, WARRANTY_CONTENT_URI, cv);
        finish();
    }

    private void updateWarranty(ContentValues cv) {
        Uri uri = WARRANTY_CONTENT_URI.buildUpon().appendPath(warranty.getId() + "").build();
        WarrantyUpdateService.updateWarranty(this, uri, cv);
        finish();
    }

    private boolean validateContents() {
        boolean res = true;
        if (etName.getText().toString().isEmpty()) {
            res = false;
            String message = getString(R.string.error_empty_input, getString(R.string.warranty_name_hint));
            Toast.makeText(this, message, Toast.LENGTH_LONG).show(); // TODO manage errors
        }
        return res;
    }

    private ContentValues warrantyToContentValues() {
        ContentValues cv = new ContentValues();
        cv.put(WarrantyContract.WarrantyEntry.COLUMN_NAME, warranty.getName());
        return cv;
    }
}
