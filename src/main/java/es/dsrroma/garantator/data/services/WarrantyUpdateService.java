package es.dsrroma.garantator.data.services;

import android.app.IntentService;
import android.content.ContentProviderOperation;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.OperationApplicationException;
import android.net.Uri;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.Nullable;

import java.util.ArrayList;
import java.util.List;

import es.dsrroma.garantator.data.contracts.PictureContract;
import es.dsrroma.garantator.data.contracts.ProductContract;
import es.dsrroma.garantator.data.contracts.WarrantyContract;
import es.dsrroma.garantator.data.model.AbstractBaseIdModel;
import es.dsrroma.garantator.data.model.AbstractBaseModel;
import es.dsrroma.garantator.data.model.Brand;
import es.dsrroma.garantator.data.model.Category;
import es.dsrroma.garantator.data.model.Picture;
import es.dsrroma.garantator.data.model.Product;
import es.dsrroma.garantator.data.model.Warranty;
import es.dsrroma.garantator.utils.NotifyUserRunnable;

import static es.dsrroma.garantator.data.contracts.BaseContract.BaseEntry.COLUMN_CREATED_AT;
import static es.dsrroma.garantator.data.contracts.BaseContract.BaseEntry.COLUMN_ID;
import static es.dsrroma.garantator.data.contracts.BaseContract.BaseEntry.COLUMN_NAME;
import static es.dsrroma.garantator.data.contracts.BaseContract.BaseEntry.COLUMN_UPDATED_AT;
import static es.dsrroma.garantator.data.contracts.BaseContract.CONTENT_AUTHORITY;
import static es.dsrroma.garantator.data.contracts.BrandContract.BRAND_CONTENT_URI;
import static es.dsrroma.garantator.data.contracts.CategoryContract.CATEGORY_CONTENT_URI;
import static es.dsrroma.garantator.data.contracts.PictureContract.PICTURE_CONTENT_URI;
import static es.dsrroma.garantator.data.contracts.PictureContract.PictureEntry;
import static es.dsrroma.garantator.data.contracts.ProductContract.PRODUCT_CONTENT_URI;
import static es.dsrroma.garantator.data.contracts.ProductContract.ProductEntry;
import static es.dsrroma.garantator.data.contracts.WarrantyContract.WARRANTY_CONTENT_URI;
import static es.dsrroma.garantator.data.contracts.WarrantyContract.WarrantyEntry;
import static es.dsrroma.garantator.data.helpers.WarrantiesProvider.WARRANTY_FILTER;
import static es.dsrroma.garantator.utils.MyStringUtils.isEmpty;

public class WarrantyUpdateService extends IntentService {

    private static final String TAG = WarrantyUpdateService.class.getSimpleName();

    //Intent actions
    public static final String ACTION_INSERT = TAG + ".INSERT";
    public static final String ACTION_UPDATE = TAG + ".UPDATE";
    public static final String ACTION_DELETE = TAG + ".DELETE";

    public static final String ACTION_INSERT_BATCH = ACTION_INSERT + ".BATCH";
    public static final String ACTION_UPDATE_BATCH = ACTION_UPDATE + ".BATCH";
    public static final String ACTION_DELETE_BATCH = ACTION_DELETE + ".BATCH";

    public static final String EXTRA_VALUES = TAG + ".ContentValues";
    public static final String EXTRA_WARRANTY = TAG + ".Warranty";
    public static final String EXTRA_OLD_WARRANTY = TAG + ".OldWarranty";


    public WarrantyUpdateService() {
        super(TAG);
    }

    public static void insertNewWarrantyBatch(Context context, Warranty warranty) {
        Intent intent = new Intent(context, WarrantyUpdateService.class);
        intent.setAction(ACTION_INSERT_BATCH);
        intent.putExtra(EXTRA_WARRANTY, warranty);
        context.startService(intent);
    }

    public static void updateWarrantyBatch(Context context, Warranty newWarranty, Warranty oldWarranty) {
        Intent intent = new Intent(context, WarrantyUpdateService.class);
        intent.setAction(ACTION_UPDATE_BATCH);
        intent.putExtra(EXTRA_WARRANTY, newWarranty);
        intent.putExtra(EXTRA_OLD_WARRANTY, oldWarranty);
        context.startService(intent);
    }

    public static void deleteWarrantyBatch(Context context, Warranty warranty) {
        Intent intent = new Intent(context, WarrantyUpdateService.class);
        intent.setAction(ACTION_DELETE_BATCH);
        intent.putExtra(EXTRA_WARRANTY, warranty);
        context.startService(intent);
    }

    public static void insertNewWarranty(Context context, Uri uri, ContentValues values) {
        long now = System.currentTimeMillis();
        values.put(COLUMN_CREATED_AT, now);
        values.put(COLUMN_UPDATED_AT, now);

        Intent intent = new Intent(context, WarrantyUpdateService.class);
        intent.setAction(ACTION_INSERT);
        intent.setData(uri);
        intent.putExtra(EXTRA_VALUES, values);
        context.startService(intent);
    }

    public static void updateWarranty(Context context, Uri uri, ContentValues values) {
        long now = System.currentTimeMillis();
        values.put(COLUMN_UPDATED_AT, now);

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
        } else if (ACTION_UPDATE_BATCH.equals(intent.getAction())) {
            Warranty newWarranty = intent.getParcelableExtra(EXTRA_WARRANTY);
            Warranty oldWarranty = intent.getParcelableExtra(EXTRA_OLD_WARRANTY);
            performUpdateBatch(newWarranty, oldWarranty);
        } else if (ACTION_DELETE_BATCH.equals(intent.getAction())) {
            Warranty warranty = intent.getParcelableExtra(EXTRA_WARRANTY);
            performDeleteBatch(warranty);
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
        int productPos = brandPos + 1;
        int warrantyPos = productPos + 1;


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
        if (warranty.getStartDate() != null) {
            warrantyCV.put(WarrantyEntry.COLUMN_START_DATE, warranty.getStartDate().getTime());
        }
        if (warranty.getEndDate() != null) {
            warrantyCV.put(WarrantyEntry.COLUMN_END_DATE, warranty.getEndDate().getTime());
        }
        warrantyCV.put(WarrantyEntry.COLUMN_LENGTH, warranty.getLength());
        warrantyCV.put(WarrantyEntry.COLUMN_PERIOD, warranty.getPeriod());


        ops.add(ContentProviderOperation.newInsert(WARRANTY_CONTENT_URI)
                .withValueBackReference(WarrantyContract.WarrantyEntry.COLUMN_PRODUCT_ID, productPos)
                .withValues(warrantyCV)
                .build());

        List<Picture> pictures = warranty.getPictures();
        for (Picture picture: pictures) {
            ContentValues pictureCV = newContentValuesNoName(picture, now);
            pictureCV.put(PictureEntry.COLUMN_FILE_NAME, picture.getFileName());
            pictureCV.put(PictureEntry.COLUMN_POSITION, picture.getPosition());
            ops.add(ContentProviderOperation.newInsert(PICTURE_CONTENT_URI)
                    .withValueBackReference(PictureContract.PictureEntry.COLUMN_WARRANTY_ID, warrantyPos)
                    .withValues(pictureCV)
                    .build());
        }

        try {
            getContentResolver().applyBatch(CONTENT_AUTHORITY, ops);
        } catch (final Throwable t) {
            notifyProblem(t);
        }
    }

    private void performUpdateBatch(Warranty newWarranty, Warranty oldWarranty) {
        long now = System.currentTimeMillis();

        ArrayList<ContentProviderOperation> ops = new ArrayList<>();
        ContentValues warrantyCV = updateContentValues(newWarranty, oldWarranty, now);

        // update product // product may not be null, neither may change id
        Product newProduct = newWarranty.getProduct();
        Product oldProduct = oldWarranty.getProduct();
        ContentValues productCV = updateContentValues(newProduct, oldProduct, now);

        int categoryPos = -1;
        int brandPos = -1;

        // update product category
        Category newCategory = newProduct.getCategory();
        Category oldCategory = oldProduct.getCategory();
        if (newCategory == null) {
            productCV.putNull(ProductEntry.COLUMN_CATEGORY_ID);
        } else {
            if (newCategory.getId() <= 0) {
                ops.add(ContentProviderOperation.newInsert(CATEGORY_CONTENT_URI)
                        .withValues(newContentValues(newCategory, now))
                        .build());
                categoryPos = 0;
            } else if (oldCategory == null || newCategory.getId() != oldProduct.getCategory().getId()) {
                productCV.put(ProductEntry.COLUMN_CATEGORY_ID, newCategory.getId());
            }
        }

        // update product brand
        Brand newBrand = newProduct.getBrand();
        Brand oldBrand = oldProduct.getBrand();
        if (newBrand == null) {
            productCV.putNull(ProductEntry.COLUMN_BRAND_ID);
        } else {
            if (newBrand.getId() <= 0) {
                ops.add(ContentProviderOperation.newInsert(BRAND_CONTENT_URI)
                        .withValues(newContentValues(newBrand, now))
                        .build());
                brandPos = categoryPos + 1;
            } else if (oldBrand == null || newBrand.getId() != oldProduct.getBrand().getId()) {
                productCV.put(ProductEntry.COLUMN_BRAND_ID, newBrand.getId());
            }
        }

        // update pictures TODO

        Uri updateProductUri = PRODUCT_CONTENT_URI.buildUpon().appendPath(Long.toString(newProduct.getId())).build();
        ContentProviderOperation.Builder productUpdateOp = ContentProviderOperation.newUpdate(updateProductUri);
        if (categoryPos != -1) {
            productUpdateOp.withValueBackReference(ProductEntry.COLUMN_CATEGORY_ID, categoryPos);
        }
        if (brandPos != -1) {
            productUpdateOp.withValueBackReference(ProductEntry.COLUMN_BRAND_ID, brandPos);
        }
        if (productCV.size() != 0) {
            productUpdateOp.withValues(productCV);
            ops.add(productUpdateOp.build());
        }

        if (warrantyCV.size() != 0) {
            Uri updateWarrantyUri = WARRANTY_CONTENT_URI.buildUpon().appendPath(Long.toString(newWarranty.getId())).build();
            ContentProviderOperation.Builder warrantyUpdateOp = ContentProviderOperation.newUpdate(updateWarrantyUri).
                    withValues(warrantyCV);
            ops.add(warrantyUpdateOp.build());
        }

        try {
            getContentResolver().applyBatch(CONTENT_AUTHORITY, ops);
        } catch (final Throwable t) {
            notifyProblem(t);
        }
    }

    private void performDeleteBatch(Warranty warranty) {
        Product product = warranty.getProduct();
        performDeleteCategoryBatch(product.getCategory());
        performDeleteBrandBatch(product.getBrand());
        performDeleteProductAndWarranty(warranty);
    }

    /**
     * Deletes the product of this warranty, and the warranty itself.
     * @param warranty
     */
    private void performDeleteProductAndWarranty(Warranty warranty) {
        Product product = warranty.getProduct();
        ArrayList<ContentProviderOperation> ops = new ArrayList<>();
        Uri productQuery = PRODUCT_CONTENT_URI.buildUpon().appendPath(Long.toString(product.getId())).build();
        ops.add(ContentProviderOperation.newDelete(productQuery).build());

        Uri pictureQuery = PICTURE_CONTENT_URI.buildUpon().appendPath(WARRANTY_FILTER).
                appendPath(Long.toString(warranty.getId())).build();
        ops.add(ContentProviderOperation.newDelete(pictureQuery).build());

        Uri warrantyQuery = WARRANTY_CONTENT_URI.buildUpon().appendPath(Long.toString(warranty.getId())).build();
        ops.add(ContentProviderOperation.newDelete(warrantyQuery).build());

        try {
            getContentResolver().applyBatch(CONTENT_AUTHORITY, ops);
        } catch (final Throwable t) {
            notifyProblem(t);
        }
    }

    /**
     * Deletes this brand if it isn't used by any product.
     * @param brand
     */
    private void performDeleteBrandBatch(Brand brand) {
        if (brand != null) {
            ArrayList<ContentProviderOperation> deleteBrandOps = new ArrayList<>();
            Uri brandQuery = BRAND_CONTENT_URI.buildUpon().appendPath(Long.toString(brand.getId())).build();
            deleteBrandOps.add(ContentProviderOperation.newAssertQuery(brandQuery)
                    .withExpectedCount(1)
                    .build());
            deleteBrandOps.add(ContentProviderOperation.newDelete(brandQuery).build());

            try {
                getContentResolver().applyBatch(CONTENT_AUTHORITY, deleteBrandOps);
            } catch (OperationApplicationException oae) {
                // brand not to be deleted
            } catch (final Throwable t) {
                notifyProblem(t);
            }
        }
    }

    /**
     * Deletes this category if it isn't used by any product.
     * @param category
     */
    private void performDeleteCategoryBatch(Category category) {
        if (category != null) {
            ArrayList<ContentProviderOperation> deleteCategoryOps = new ArrayList<>();
            Uri categoryQuery = CATEGORY_CONTENT_URI.buildUpon().appendPath(Long.toString(category.getId())).build();
            deleteCategoryOps.add(ContentProviderOperation.newAssertQuery(categoryQuery)
                    .withExpectedCount(1)
                    .build());
            deleteCategoryOps.add(ContentProviderOperation.newDelete(categoryQuery).build());
            try {
                getContentResolver().applyBatch(CONTENT_AUTHORITY, deleteCategoryOps);
            } catch (OperationApplicationException oae) {
                // category not to be deleted
            } catch (final Throwable t) {
                notifyProblem(t);
            }
        }
    }

    private static ContentValues updateContentValues(Warranty newWarranty, Warranty oldWarranty, long now) {
        ContentValues values = new ContentValues();

        // update warranty name
        if (!newWarranty.getName().equals(oldWarranty.getName())) {
            values.put(COLUMN_NAME, newWarranty.getName());
        }
        // update warranty startdate
        if (newWarranty.getStartDate() == null) {
            if (oldWarranty.getStartDate() != null) {
                values.putNull(WarrantyEntry.COLUMN_START_DATE);
            }
        } else if (!newWarranty.getStartDate().equals(oldWarranty.getStartDate())) {
            values.put(WarrantyEntry.COLUMN_START_DATE, newWarranty.getStartDate().getTime());
        }
        // update warranty enddate
        if (newWarranty.getEndDate() == null) {
            if (oldWarranty.getEndDate() != null) {
                values.putNull(WarrantyEntry.COLUMN_END_DATE);
            }
        } else if (!newWarranty.getEndDate().equals(oldWarranty.getEndDate())) {
            values.put(WarrantyEntry.COLUMN_END_DATE, newWarranty.getEndDate().getTime());
        }
        // update warranty length
        if (newWarranty.getLength() != oldWarranty.getLength()) {
            values.put(WarrantyEntry.COLUMN_LENGTH, newWarranty.getLength());
        }
        // update warranty period
        if (!newWarranty.getPeriod().equals(oldWarranty.getPeriod())) {
            values.put(WarrantyEntry.COLUMN_PERIOD, newWarranty.getPeriod());
        }

        if (values.size() > 0) {
            values.put(COLUMN_UPDATED_AT, now);
        }
        return values;
    }

    private static ContentValues updateContentValues(Product newProduct, Product oldProduct, long now) {
        ContentValues values = new ContentValues();

        // update product name
        if (!newProduct.getName().equals(oldProduct.getName())) {
            values.put(COLUMN_NAME, newProduct.getName());
        }
        // update product model
        if (isEmpty(newProduct.getModel())) {
            values.putNull(ProductEntry.COLUMN_MODEL);
        } else if (!newProduct.getModel().equals(oldProduct.getModel())) {
            values.put(ProductEntry.COLUMN_MODEL, newProduct.getModel());
        }
        // update product serial number
        if (isEmpty(newProduct.getSerialNumber())) {
            values.putNull(ProductEntry.COLUMN_SERIAL_NUMBER);
        } else if (!newProduct.getSerialNumber().equals(oldProduct.getSerialNumber())) {
            values.put(ProductEntry.COLUMN_SERIAL_NUMBER, newProduct.getSerialNumber());
        }

        if (values.size() > 0) {
            values.put(COLUMN_UPDATED_AT, now);
        }
        return values;
    }

    private static ContentValues newContentValuesNoName(AbstractBaseIdModel model, long now) {
        ContentValues values = new ContentValues();
        values.put(COLUMN_CREATED_AT, now);
        values.put(COLUMN_UPDATED_AT, now);
        if (model.getId() > 0) {
            values.put(COLUMN_ID, model.getId());
        }
        return values;
    }

    private static ContentValues newContentValues(AbstractBaseModel model, long now) {
        ContentValues values = newContentValuesNoName(model, now);
        values.put(COLUMN_NAME, model.getName());
        return values;
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
