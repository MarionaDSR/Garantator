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
import android.view.View;
import android.widget.AdapterView;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.FilterQueryProvider;
import android.widget.SimpleCursorAdapter;
import android.widget.Toast;

import butterknife.BindView;
import butterknife.ButterKnife;
import es.dsrroma.garantator.data.contracts.WarrantyContract;
import es.dsrroma.garantator.data.model.Brand;
import es.dsrroma.garantator.data.model.Category;
import es.dsrroma.garantator.data.model.Product;
import es.dsrroma.garantator.data.model.Warranty;
import es.dsrroma.garantator.data.services.WarrantyUpdateService;
import es.dsrroma.garantator.utils.CursorToBeanUtils;

import static es.dsrroma.garantator.data.contracts.BaseContract.BaseEntry.COLUMN_ID;
import static es.dsrroma.garantator.data.contracts.BaseContract.BaseEntry.COLUMN_NAME;
import static es.dsrroma.garantator.data.contracts.BrandContract.BRAND_CONTENT_URI;
import static es.dsrroma.garantator.data.contracts.CategoryContract.CATEGORY_CONTENT_URI;
import static es.dsrroma.garantator.data.contracts.WarrantyContract.WARRANTY_CONTENT_URI;
import static es.dsrroma.garantator.utils.MyStringUtils.notEmpty;

public class AddWarrantyActivity extends AppCompatActivity implements
        LoaderManager.LoaderCallbacks<Cursor> {

    private SimpleCursorAdapter brandAdapter;
    private SimpleCursorAdapter categoryAdapter;

    @SuppressWarnings("WeakerAccess")
    @BindView(R.id.etWarrantyName)
    EditText etName;

    @SuppressWarnings("WeakerAccess")
    @BindView(R.id.etProductName)
    EditText etProductName;

    @SuppressWarnings("WeakerAccess")
    @BindView(R.id.actvCategory)
    AutoCompleteTextView actvCategory;

    @SuppressWarnings("WeakerAccess")
    @BindView(R.id.actvBrand)
    AutoCompleteTextView actvBrand;

    @SuppressWarnings("WeakerAccess")
    @BindView(R.id.etModel)
    EditText etModel;

    @SuppressWarnings("WeakerAccess")
    @BindView(R.id.etSerialNumber)
    EditText etSerialNumber;

    private boolean editMode;

    private Warranty warranty;
    private Product product;
    private Brand brand;
    private Category category;

    private Cursor cursor;

    private static final int BRAND_LOADER_ID = 2;
    private static final int CATEGORY_LOADER_ID = 3;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_warranty);

        ButterKnife.bind(this);

        brandAdapter = prepareAutocompleteAdapter(BRAND_CONTENT_URI);
        actvBrand.setAdapter(brandAdapter);
        actvBrand.setThreshold(1);

        categoryAdapter = prepareAutocompleteAdapter(CATEGORY_CONTENT_URI);
        actvCategory.setAdapter(categoryAdapter);
        actvCategory.setThreshold(1);

        final Uri warrantyUri = getIntent().getData();
        editMode = warrantyUri != null;
        if (editMode) {
            setTitle(R.string.edit_warranty_title);
            cursor = getContentResolver().query(warrantyUri, null, null, null, null, null);
            if (cursor.moveToFirst()) {
                fillWarranty();
            }
        } else { // add mode
            setTitle(R.string.add_warranty_title);
            newWarranty();
        }

        setListeners();

        getSupportLoaderManager().initLoader(BRAND_LOADER_ID, null, this);
        getSupportLoaderManager().initLoader(CATEGORY_LOADER_ID, null, this);
    }

    private void newWarranty() {
        warranty = new Warranty();
        product = new Product();
        warranty.setProduct(product);
        category = new Category();
        product.setCategory(category);
        brand = new Brand();
        product.setBrand(brand);
    }

    private void fillWarranty() {
        warranty = CursorToBeanUtils.cursorToBean(cursor, 0, Warranty.class);
        etName.setText(warranty.getName());
        product = warranty.getProduct();
        if (product != null) {
            etProductName.setText(product.getName());
            category = product.getCategory();
            if (category != null) {
                actvCategory.setText(category.getName());
            }
            brand = product.getBrand();
            if (brand != null) {
                actvBrand.setText(notEmpty(brand.getName()));
            }
            etModel.setText(notEmpty(product.getModel()));
            etSerialNumber.setText(notEmpty(product.getSerialNumber()));

        } else {
            category = new Category();
            brand = new Brand();
        }
    }

    private SimpleCursorAdapter prepareAutocompleteAdapter(final Uri uri) {
        SimpleCursorAdapter adapter = new SimpleCursorAdapter(this, R.layout.dropdown_item_name_id, null,
                new String[] {COLUMN_NAME, COLUMN_ID},
                new int[] { R.id.dropdown_name, R.id.dropdown_id}, 0);
        adapter.setStringConversionColumn(1);
        adapter.setFilterQueryProvider(new FilterQueryProvider() {

            public Cursor runQuery(CharSequence constraint) {
                String partialItemName = "";
                if (constraint != null) {
                    partialItemName = constraint.toString();
                }

                return getContentResolver().query(uri, new String[] {},
                        COLUMN_NAME + " LIKE '%" + partialItemName + "%'",
                        null, null);
            }
        });
        return adapter;
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

        actvBrand.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Cursor cursor = (Cursor)parent.getItemAtPosition(position);
                if (cursor.moveToPosition(position)) {
                    String name = cursor.getString(cursor.getColumnIndex(COLUMN_NAME));
                    brand.setName(name);
                    brand.setId(id);
                }
            }
        });

        actvCategory.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Cursor cursor = (Cursor)parent.getItemAtPosition(position);
                if (cursor.moveToPosition(position)) {
                    String name = cursor.getString(cursor.getColumnIndex(COLUMN_NAME));
                    category.setName(name);
                    category.setId(id);
                }
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
                        warranty.setName(etName.getText().toString());
                        product.setName(etProductName.getText().toString());
                        product.setModel(etModel.getText().toString());
                        product.setSerialNumber(etSerialNumber.getText().toString());
                        brand.setName(actvBrand.getText().toString());
                        category.setName(actvCategory.getText().toString());
                        addWarranty();
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

    private void addWarranty() {
        WarrantyUpdateService.insertNewWarrantyBatch(this, warranty);
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
        cv.put(COLUMN_NAME, warranty.getName());
        cv.put(WarrantyContract.WarrantyEntry.COLUMN_PRODUCT_ID, product.getId());
        return cv;
    }
}
