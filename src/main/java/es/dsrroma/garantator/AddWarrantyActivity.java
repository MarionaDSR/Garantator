package es.dsrroma.garantator;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.text.format.DateFormat;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AutoCompleteTextView;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.FilterQueryProvider;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.SimpleCursorAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.crashlytics.android.Crashlytics;

import java.util.Date;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import es.dsrroma.garantator.adapters.PictureAdapter;
import es.dsrroma.garantator.data.contracts.PictureContract;
import es.dsrroma.garantator.data.contracts.WarrantyViewContract;
import es.dsrroma.garantator.data.model.Brand;
import es.dsrroma.garantator.data.model.Category;
import es.dsrroma.garantator.data.model.Picture;
import es.dsrroma.garantator.data.model.Product;
import es.dsrroma.garantator.data.model.Warranty;
import es.dsrroma.garantator.data.services.WarrantyUpdateService;
import es.dsrroma.garantator.photo.PhotoManager;
import es.dsrroma.garantator.photo.PhotoSourceDialog;
import es.dsrroma.garantator.view.DatePickerFragment;

import static android.content.pm.PackageManager.PERMISSION_GRANTED;
import static es.dsrroma.garantator.data.contracts.BaseContract.BaseEntry.COLUMN_ID;
import static es.dsrroma.garantator.data.contracts.BaseContract.BaseEntry.COLUMN_NAME;
import static es.dsrroma.garantator.data.contracts.BrandContract.BRAND_CONTENT_URI;
import static es.dsrroma.garantator.data.contracts.CategoryContract.CATEGORY_CONTENT_URI;
import static es.dsrroma.garantator.data.contracts.PictureContract.PICTURE_CONTENT_URI;
import static es.dsrroma.garantator.data.helpers.WarrantiesProvider.WARRANTY_FILTER;
import static es.dsrroma.garantator.photo.PhotoSourceDialog.REQUEST_WRITE_EXTERNAL_STORAGE_PERMISSION;
import static es.dsrroma.garantator.utils.MyDateUtils.calculateExpirationDate;
import static es.dsrroma.garantator.utils.MyDateUtils.getNoonTime;
import static es.dsrroma.garantator.utils.MyStringUtils.isNotEmpty;
import static es.dsrroma.garantator.utils.MyStringUtils.notEmpty;

public class AddWarrantyActivity extends AppCompatActivity implements
        LoaderManager.LoaderCallbacks<Cursor>,
        DatePickerDialog.OnDateSetListener,
        PhotoSourceDialog.PhotoSourceDialogListener {

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

    @SuppressWarnings("WeakerAccess")
    @BindView(R.id.tvStartDate)
    TextView tvStartDate;

    @SuppressWarnings("WeakerAccess")
    @BindView(R.id.etWarrantyLength)
    EditText etWarrantyLength;

    @SuppressWarnings("WeakerAccess")
    @BindView(R.id.spWarrantyPeriod)
    Spinner spWarrantyPeriod;

    @SuppressWarnings("WeakerAccess")
    @BindView(R.id.tvEndDate)
    TextView tvEndDate;

    @SuppressWarnings("WeakerAccess")
    @BindView(R.id.gvPictures)
    GridView gvPictures;

    @SuppressWarnings("WeakerAccess")
    @BindView(R.id.ivPicturesIcon)
    ImageView ivPicturesIcon;

    private boolean editMode;

    private Warranty oldWarranty;

    private Warranty warranty;
    private Product product;
    private Brand brand;
    private Category category;

    private long brandId;
    private long categoryId;
    private long startDate = Long.MAX_VALUE;

    private Cursor cursor;

    private SimpleCursorAdapter brandAdapter;
    private SimpleCursorAdapter categoryAdapter;

    // Photos
    private PhotoManager photoManager;
    private PictureAdapter pictureAdapter;

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
            warranty = WarrantyViewContract.getBeanFromCursor(cursor);
            try {
                oldWarranty = (Warranty)warranty.clone();
            } catch (CloneNotSupportedException e) {
                Crashlytics.logException(e);
                throw new IllegalStateException(e); // warraties are clonable
            }
            loadPictures();
        } else { // add mode
            setTitle(R.string.add_warranty_title);
            newWarranty();
        }
        fillWarranty();

        pictureAdapter = new PictureAdapter(this, warranty.getPictures());
        gvPictures.setAdapter(pictureAdapter);
        setListeners();

        getSupportLoaderManager().initLoader(BRAND_LOADER_ID, null, this);
        getSupportLoaderManager().initLoader(CATEGORY_LOADER_ID, null, this);

        photoManager = new PhotoManager(AddWarrantyActivity.this);
    }

    private void loadPictures() {
        Uri picturesUri = PICTURE_CONTENT_URI.buildUpon().appendPath(WARRANTY_FILTER).appendPath(Long.toString(warranty.getId())).build();
        cursor = getContentResolver().query(picturesUri, null, null, null, null, null);
        List<Picture> pictures = PictureContract.getBeansFromCursor(cursor);
        warranty.setPictures(pictures);
    }

    private void newWarranty() {
        warranty = new Warranty();
        product = new Product();
        warranty.setProduct(product);
        category = new Category();
        product.setCategory(category);
        brand = new Brand();
        product.setBrand(brand);
        // set default values
        warranty.setStartDate(getNoonTime(new Date()));
        warranty.setLength(getResources().getInteger(R.integer.warranty_time_default));
        warranty.setPeriod(getString(R.string.warranty_period_default));
    }

    private void fillWarranty() {
        tvWarrantyName.setText(warranty.getName());
        if (warranty.getStartDate() != null) {
            CharSequence formatted = DateFormat.format(getString(R.string.date_format), warranty.getStartDate());
            tvStartDate.setText(formatted);
        }
        if (warranty.getEndDate() != null) {
            CharSequence formatted = DateFormat.format(getString(R.string.date_format), warranty.getEndDate());
            tvEndDate.setText(formatted);
        }
        etWarrantyLength.setText(Integer.toString(warranty.getLength()));
        spWarrantyPeriod.setSelection(getPeriodPosition(warranty.getPeriod()));

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
        actvBrand.setOnFocusChangeListener(brandOnFocusChangeListener());
        actvCategory.setOnFocusChangeListener(categoryOnFocusChangeListener());

        actvBrand.setOnItemClickListener(brandOnItemClickListener());
        actvCategory.setOnItemClickListener(categoryOnItemClickListener());

        etProductName.addTextChangedListener(warrantyNameFragmentTextWatcher());
        actvBrand.addTextChangedListener(warrantyNameFragmentTextWatcher());
        actvCategory.addTextChangedListener(warrantyNameFragmentTextWatcher());

        tvStartDate.setOnClickListener(startDateOnClickListener());
        etWarrantyLength.addTextChangedListener(lengthTextChangedListener());
        spWarrantyPeriod.setOnItemSelectedListener(periodOnItemSelectedListener());

        ivPicturesIcon.setOnClickListener(pictureIconOnClickListener());
    }

    @NonNull
    private View.OnClickListener pictureIconOnClickListener() {
        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // When user click on Avatar Image create new dialog
                // to show two options:
                // - Choose image from Gallery or
                // - Take a picture.
                if (ActivityCompat.checkSelfPermission(AddWarrantyActivity.this, android.Manifest.permission.WRITE_EXTERNAL_STORAGE)
                        != PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(AddWarrantyActivity.this, new String[]{android.Manifest.permission.WRITE_EXTERNAL_STORAGE}, REQUEST_WRITE_EXTERNAL_STORAGE_PERMISSION);
                } else {
                    PhotoSourceDialog dialog = new PhotoSourceDialog();
                    dialog.show(getSupportFragmentManager(), PhotoSourceDialog.PHOTO_SOURCE_DIALOG);
                }
            }
        };
    }

    @NonNull
    private View.OnClickListener startDateOnClickListener() {
        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DatePickerFragment dialogFragment = new DatePickerFragment();
                dialogFragment.show(getSupportFragmentManager(), "datePicker");
            }
        };
    }

    @NonNull
    private AdapterView.OnItemSelectedListener periodOnItemSelectedListener() {
        return new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                warranty.setPeriod(getResources().getStringArray(R.array.warranty_period_values)[position]);
                refreshEndDate();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        };
    }

    @NonNull
    private TextWatcher lengthTextChangedListener() {
        return new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                warranty.setLength(s.toString());
                refreshEndDate();
            }
        };
    }

    @NonNull
    private AdapterView.OnItemClickListener categoryOnItemClickListener() {
        return new AdapterView.OnItemClickListener() {
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
        };
    }

    @NonNull
    private AdapterView.OnItemClickListener brandOnItemClickListener() {
        return new AdapterView.OnItemClickListener() {
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
        };
    }

    @NonNull
    private View.OnFocusChangeListener categoryOnFocusChangeListener() {
        return new View.OnFocusChangeListener() {
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
                } else {
                    categoryId = -1;
                }
            }
        };
    }

    @NonNull
    private View.OnFocusChangeListener brandOnFocusChangeListener() {
        return new View.OnFocusChangeListener() {
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
                } else {
                    brandId = -1;
                }
            }
        };
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

    /* Date set events from dialog */
    @Override
    public void onDateSet(DatePicker view, int year, int month, int day) {
        setDateSelection(getNoonTime(year, month, day));
    }

    /* Manage the selected date value */
    public void setDateSelection(long selectedTimestamp) {
        startDate = selectedTimestamp;
        warranty.setStartDate(selectedTimestamp);
        updateDateDisplay(startDate, tvStartDate);
        refreshEndDate();
    }

    private void refreshEndDate() {
        calculateExpirationDate(warranty);
        if (warranty.getEndDate() != null) {
            updateDateDisplay(warranty.getEndDate().getTime(), tvEndDate);
        }
    }

    private void updateDateDisplay(long time, TextView tv) {
        if (time == Long.MAX_VALUE) {
            tv.setText(R.string.date_empty);
        } else {
            CharSequence formatted = DateFormat.format(getString(R.string.date_format), time);
            tv.setText(formatted);
        }
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

    @Override
    public void onGalleryClick() {
        photoManager.getPhotoFromGallery();
    }

    @Override
    public void onCameraClick() {
        photoManager.getPhotoFromCamera();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case PhotoManager.LOAD_IMAGE_REQUEST_CODE:
            case PhotoManager.CAPTURE_IMAGE_REQUEST_CODE:
                String photoPath = photoManager.onActivityResult(requestCode, resultCode, data);
                if (photoPath != null) {
                    addPicture(photoPath);
                }
                break;
            default:
                super.onActivityResult(requestCode, requestCode, data);
        }
    }

    public void addPicture(String picturePath) {
        Picture picture = new Picture();
        picture.setToCreate(true);
        picture.setWarranty(warranty);
        picture.setFilename(picturePath);
        picture.setPosition(warranty.getPicturesSize());
        List<Picture> pictures = warranty.getPictures();
        pictures.add(picture);
        pictureAdapter.notifyDataSetChanged();
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

    private int getPeriodPosition(String period) {
        String[] values = getResources().getStringArray(R.array.warranty_period_values);
        for (int i = 0; i < values.length; i ++) {
            if (values[i].equals(period)) {
                return i;
            }
        }
        return -1; // Not possible
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        Toast.makeText(this, "¡Gracias ???? ! " + requestCode + "(" + (requestCode & 0x0000ffff) + ")", Toast.LENGTH_SHORT).show();
        switch (requestCode & 0x0000ffff) { // to manage requestCode from fragments
            case REQUEST_WRITE_EXTERNAL_STORAGE_PERMISSION: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    onGalleryClick();
                } else {
                    Toast.makeText(this, "Si no me das permisos, no puedo hacerlo!!", Toast.LENGTH_LONG).show();
                }
            }
            break;
            case PhotoSourceDialog.REQUEST_CAMERA_PERMISSION: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    onCameraClick();
                } else {
                    Toast.makeText(this, "Si no me das permisos, no puedo hacerlo!!", Toast.LENGTH_LONG).show();
                }
            }
            break;
            default:
                super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }

    }
}
