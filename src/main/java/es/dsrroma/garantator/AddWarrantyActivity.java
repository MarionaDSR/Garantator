package es.dsrroma.garantator;

import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
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
import android.widget.TextView;
import android.widget.Toast;

import butterknife.BindView;
import butterknife.ButterKnife;
import es.dsrroma.garantator.data.contracts.WarrantyViewContract;
import es.dsrroma.garantator.data.model.Brand;
import es.dsrroma.garantator.data.model.Category;
import es.dsrroma.garantator.data.model.Product;
import es.dsrroma.garantator.data.model.Warranty;
import es.dsrroma.garantator.data.services.WarrantyUpdateService;

import static es.dsrroma.garantator.data.contracts.BaseContract.BaseEntry.COLUMN_ID;
import static es.dsrroma.garantator.data.contracts.BaseContract.BaseEntry.COLUMN_NAME;
import static es.dsrroma.garantator.data.contracts.BrandContract.BRAND_CONTENT_URI;
import static es.dsrroma.garantator.data.contracts.CategoryContract.CATEGORY_CONTENT_URI;
import static es.dsrroma.garantator.utils.MyStringUtils.isNotEmpty;
import static es.dsrroma.garantator.utils.MyStringUtils.notEmpty;

public class AddWarrantyActivity extends AppCompatActivity implements
        LoaderManager.LoaderCallbacks<Cursor> {

    private SimpleCursorAdapter brandAdapter;
    private SimpleCursorAdapter categoryAdapter;

    @SuppressWarnings("WeakerAccess")
    @BindView(R.id.tvWarrantyName)
    TextView tvWarrantyName;

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

    private Warranty oldWarranty;

    private Warranty warranty;
    private Product product;
    private Brand brand;
    private Category category;

    private long brandId;
    private long categoryId;

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
                try {
                    oldWarranty = (Warranty)warranty.clone();
                } catch (CloneNotSupportedException e) {
                    throw new IllegalStateException(e); // warraties are clonable
                }
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
        warranty = WarrantyViewContract.getBeanFromCursor(cursor);
        tvWarrantyName.setText(warranty.getName());
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
        actvBrand.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    String newBrand = actvBrand.getText().toString();
                    Uri brandUri = BRAND_CONTENT_URI.buildUpon().appendPath(newBrand).build();
                    brand.setName(newBrand);

                    Cursor brandCursor = getContentResolver().query(brandUri, null, null, null, null, null);
                    if (brandCursor.moveToNext()) {
                        brandId = brandCursor.getLong(brandCursor.getColumnIndex(COLUMN_ID));
                        brand.setId(brandId);
                    }
                    Toast.makeText(getApplicationContext(), brand.toString(), Toast.LENGTH_SHORT).show();
                } else {
                    brandId = -1;
                }
            }
        });

        actvCategory.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    String newCategory = actvCategory.getText().toString();
                    Uri categoryUri = CATEGORY_CONTENT_URI.buildUpon().appendPath(newCategory).build();
                    category.setName(newCategory);

                    Cursor categoryCursor = getContentResolver().query(categoryUri, null, null, null, null, null);
                    if (categoryCursor.moveToNext()) {
                        categoryId = categoryCursor.getLong(categoryCursor.getColumnIndex(COLUMN_ID));
                        category.setId(categoryId);
                    }
                    Toast.makeText(getApplicationContext(), category.toString(), Toast.LENGTH_SHORT).show();
                } else {
                    categoryId = -1;
                }
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
                    brandId = id;
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
                    categoryId = id;
                }
            }
        });

        etProductName.addTextChangedListener(warrantyNameFragmentTextWatcher());
        actvBrand.addTextChangedListener(warrantyNameFragmentTextWatcher());
        actvCategory.addTextChangedListener(warrantyNameFragmentTextWatcher());
    }

    @NonNull
    private TextWatcher warrantyNameFragmentTextWatcher() {
        return new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                String name = etProductName.getText().toString();
                if (isNotEmpty(actvCategory.getText().toString())) {
                    name += getString(R.string.warranty_name_separator) + actvCategory.getText();
                }
                if (isNotEmpty(actvBrand.getText().toString())) {
                    name += getString(R.string.warranty_name_separator) + actvBrand.getText();
                }
                tvWarrantyName.setText(name);
            }
        };
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
                    warranty.setName(tvWarrantyName.getText().toString());
                    product.setName(etProductName.getText().toString());
                    product.setModel(etModel.getText().toString());
                    product.setSerialNumber(etSerialNumber.getText().toString());
                    if (editMode) {
                        Product oldProduct = oldWarranty.getProduct();
                        String newBrandName = actvBrand.getText().toString();
                        Brand oldBrand = oldProduct.getBrand();
                        if (!newBrandName.equals(oldBrand.getName())) {
                            if (this.brand.getId() == oldBrand.getId()) {
                                this.brand.setId(-1);
                            }
                        }
                        String newCategoryName = actvCategory.getText().toString();
                        Category oldCategory = oldProduct.getCategory();
                        if (!newCategoryName.equals(oldCategory.getName())) {
                            if (this.category.getId() == oldCategory.getId()) {
                                this.category.setId(-1);
                            }
                        }
                        updateWarranty();
                    } else { // add mode
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

    private void updateWarranty() {
        WarrantyUpdateService.updateWarrantyBatch(this, warranty, oldWarranty);
        finish();
    }

    private boolean validateContents() {
        boolean res = true;
        if (etProductName.getText().toString().isEmpty()) {
            res = false;
            String message = getString(R.string.error_empty_input, getString(R.string.product_name_hint));
            Toast.makeText(this, message, Toast.LENGTH_LONG).show(); // TODO manage errors
        }
        return res;
    }
}
