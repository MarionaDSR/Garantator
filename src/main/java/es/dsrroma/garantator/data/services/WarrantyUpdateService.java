package es.dsrroma.garantator.data.services;

import android.app.IntentService;
import android.content.ContentProviderOperation;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.Nullable;

import java.util.ArrayList;

import es.dsrroma.garantator.data.contracts.BaseContract;
import es.dsrroma.garantator.data.contracts.ProductContract;
import es.dsrroma.garantator.data.contracts.WarrantyContract;
import es.dsrroma.garantator.data.model.AbstractBaseModel;
import es.dsrroma.garantator.data.model.Brand;
import es.dsrroma.garantator.data.model.Category;
import es.dsrroma.garantator.data.model.Product;
import es.dsrroma.garantator.data.model.Warranty;
import es.dsrroma.garantator.utils.NotifyUserRunnable;

import static es.dsrroma.garantator.data.contracts.BaseContract.BaseEntry.COLUMN_ID;
import static es.dsrroma.garantator.data.contracts.BaseContract.BaseEntry.COLUMN_NAME;
import static es.dsrroma.garantator.data.contracts.BaseContract.CONTENT_AUTHORITY;
import static es.dsrroma.garantator.data.contracts.BrandContract.BRAND_CONTENT_URI;
import static es.dsrroma.garantator.data.contracts.CategoryContract.CATEGORY_CONTENT_URI;
import static es.dsrroma.garantator.data.contracts.ProductContract.PRODUCT_CONTENT_URI;
import static es.dsrroma.garantator.data.contracts.WarrantyContract.WARRANTY_CONTENT_URI;

public class WarrantyUpdateService extends IntentService {

    private static final String TAG = WarrantyUpdateService.class.getSimpleName();

    //Intent actions
    public static final String ACTION_INSERT = TAG + ".INSERT";
    public static final String ACTION_UPDATE = TAG + ".UPDATE";
    public static final String ACTION_DELETE = TAG + ".DELETE";

    public static final String ACTION_INSERT_BATCH = ACTION_INSERT + ".BATCH";

    public static final String EXTRA_VALUES = TAG + ".ContentValues";
    public static final String EXTRA_WARRANTY = TAG + ".Warranty";


    public WarrantyUpdateService() {
        super(TAG);
    }

    public static void insertNewWarrantyBatch(Context context, Warranty warranty) {
        Intent intent = new Intent(context, WarrantyUpdateService.class);
        intent.setAction(ACTION_INSERT_BATCH);
        intent.putExtra(EXTRA_WARRANTY, warranty);
        context.startService(intent);
    }

    private static ContentValues newContentValues(AbstractBaseModel model, long now) {
        ContentValues values = new ContentValues();
        values.put(BaseContract.BaseEntry.COLUMN_CREATED_AT, now);
        values.put(BaseContract.BaseEntry.COLUMN_UPDATED_AT, now);
        if (model.getId() > 0) {
            values.put(COLUMN_ID, model.getId());
        }
        values.put(COLUMN_NAME, model.getName());
        return values;
    }

    public static void insertNewWarranty(Context context, Uri uri, ContentValues values) {
        long now = System.currentTimeMillis();
        values.put(BaseContract.BaseEntry.COLUMN_CREATED_AT, now);
        values.put(BaseContract.BaseEntry.COLUMN_UPDATED_AT, now);

        Intent intent = new Intent(context, WarrantyUpdateService.class);
        intent.setAction(ACTION_INSERT);
        intent.setData(uri);
        intent.putExtra(EXTRA_VALUES, values);
        context.startService(intent);
    }

    public static void updateWarranty(Context context, Uri uri, ContentValues values) {
        long now = System.currentTimeMillis();
        values.put(BaseContract.BaseEntry.COLUMN_UPDATED_AT, now);

        Intent intent = new Intent(context, WarrantyUpdateService.class);
        intent.setAction(ACTION_UPDATE);
        intent.setData(uri);
        intent.putExtra(EXTRA_VALUES, values);
        context.startService(intent);
    }

    public static void deleteWarranty(Context context, Uri uri) {
        Intent intent = new Intent(context, WarrantyUpdateService.class);
        intent.setAction(ACTION_DELETE);
        intent.setData(uri);
        context.startService(intent);
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        if (ACTION_INSERT.equals(intent.getAction())) {
            ContentValues values = intent.getParcelableExtra(EXTRA_VALUES);
            performInsert(intent.getData(), values);
        } else if (ACTION_UPDATE.equals(intent.getAction())) {
            ContentValues values = intent.getParcelableExtra(EXTRA_VALUES);
            performUpdate(intent.getData(), values);
        } else if (ACTION_DELETE.equals(intent.getAction())) {
            performDelete(intent.getData());
        } else if (ACTION_INSERT_BATCH.equals(intent.getAction())) {
            Warranty warranty = intent.getParcelableExtra(EXTRA_WARRANTY);
            performInsertBatch(warranty);
        }
    }

    private void performInsert(Uri uri, ContentValues values) {
        try {
            if (getContentResolver().insert(uri, values) == null) {
                notifyMessage("Error inserting new warranty");
            }
        } catch (final Throwable t) {
            notifyProblem(t);
        }
    }

    private void performUpdate(Uri uri, ContentValues values) {
        try {
            int count = getContentResolver().update(uri, values, null, null);
            notifyMessage("Updated " + count + " warranty items");
        } catch (final Throwable t) {
            notifyProblem(t);
        }
    }

    private void performDelete(Uri uri) {
        try {
            int count = getContentResolver().delete(uri, null, null);
            notifyMessage("Deleted " + count + " warranties");
        } catch (final Throwable t) {
            notifyProblem(t);
        }
    }

    private void performInsertBatch(Warranty warranty) {
        long now = System.currentTimeMillis();
        Product product = warranty.getProduct();
        Category category = product.getCategory();
        long categoryId = category.getId();
        int categoryPos = -1;
        Brand brand = product.getBrand();
        long brandId = brand.getId();
        int brandPos = -1;

        ArrayList<ContentProviderOperation> ops = new ArrayList<>();
        if (categoryId == 0) { // only insert the category if new
            ops.add(ContentProviderOperation.newInsert(CATEGORY_CONTENT_URI)
                    .withValues(newContentValues(category, now))
                    .build());
            categoryPos = 0;
        }
        if (brandId == 0) { // only insert the brand if new
            ops.add(ContentProviderOperation.newInsert(BRAND_CONTENT_URI)
                    .withValues(newContentValues(brand, now))
                    .build());
            brandPos = categoryPos + 1;
        }

        ContentValues productCV = newContentValues(product, now);
        productCV.put(ProductContract.ProductEntry.COLUMN_MODEL, product.getModel());
        productCV.put(ProductContract.ProductEntry.COLUMN_SERIAL_NUMBER, product.getSerialNumber());
        if (categoryId != 0) { // set old category id to product
            productCV.put(ProductContract.ProductEntry.COLUMN_CATEGORY_ID, categoryId);
        }
        if (brandId != 0) { // set old brand id to product
            productCV.put(ProductContract.ProductEntry.COLUMN_BRAND_ID, brandId);
        }

        ContentProviderOperation.Builder productOp = ContentProviderOperation.newInsert(PRODUCT_CONTENT_URI);
        if (categoryId == 0) { // set new category id to product
            productOp.withValueBackReference(ProductContract.ProductEntry.COLUMN_CATEGORY_ID, categoryPos);
        }
        if (brandId == 0) { // set new brand id to product
            productOp.withValueBackReference(ProductContract.ProductEntry.COLUMN_BRAND_ID, brandPos);
        }
        ops.add(productOp
                .withValues(productCV)
                .build());

        ContentValues warrantyCV = newContentValues(warranty, now);
        warrantyCV.put(WarrantyContract.WarrantyEntry.COLUMN_START_DATE, now); // TODO set user data

        ops.add(ContentProviderOperation.newInsert(WARRANTY_CONTENT_URI)
                .withValueBackReference(WarrantyContract.WarrantyEntry.COLUMN_PRODUCT_ID, brandPos + 1)
                .withValues(warrantyCV)
                .build());

        try {
            getContentResolver().applyBatch(CONTENT_AUTHORITY, ops);
        } catch (final Throwable t) {
            notifyProblem(t);
        }
    }

    private void notifyMessage(String s) {
        Handler handler = new Handler(Looper.getMainLooper());
        handler.post(new NotifyUserRunnable(getApplicationContext(), s));
    }

    private void notifyProblem(Throwable t) {
        Handler handler = new Handler(Looper.getMainLooper());
        handler.post(new NotifyUserRunnable(getApplicationContext(), t));
    }
}
