package es.dsrroma.garantator.data.helpers;

import android.annotation.TargetApi;
import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import es.dsrroma.garantator.data.contracts.BaseContract;
import es.dsrroma.garantator.data.contracts.BrandContract.BrandEntry;
import es.dsrroma.garantator.data.contracts.CategoryContract.CategoryEntry;
import es.dsrroma.garantator.data.contracts.ProductContract.ProductEntry;
import es.dsrroma.garantator.data.contracts.WarrantyContract.WarrantyEntry;
import es.dsrroma.garantator.data.contracts.WarrantyViewContract.WarrantyViewEntry;

import static es.dsrroma.garantator.data.contracts.BaseContract.BASE_CONTENT_URI;
import static es.dsrroma.garantator.data.contracts.BaseContract.QUERY_ALL;
import static es.dsrroma.garantator.data.contracts.BaseContract.QUERY_BY_ID;
import static es.dsrroma.garantator.data.contracts.BaseContract.QUERY_BY_NAME;
import static es.dsrroma.garantator.data.contracts.BrandContract.BRAND_CODE;
import static es.dsrroma.garantator.data.contracts.BrandContract.BRAND_CODE_BY_ID;
import static es.dsrroma.garantator.data.contracts.BrandContract.BRAND_CODE_BY_NAME;
import static es.dsrroma.garantator.data.contracts.BrandContract.BRAND_PATH;
import static es.dsrroma.garantator.data.contracts.BrandContract.BaseEntry;
import static es.dsrroma.garantator.data.contracts.BrandContract.SQL_PARAM;
import static es.dsrroma.garantator.data.contracts.CategoryContract.CATEGORY_CODE;
import static es.dsrroma.garantator.data.contracts.CategoryContract.CATEGORY_CODE_BY_ID;
import static es.dsrroma.garantator.data.contracts.CategoryContract.CATEGORY_CODE_BY_NAME;
import static es.dsrroma.garantator.data.contracts.CategoryContract.CATEGORY_PATH;
import static es.dsrroma.garantator.data.contracts.PictureContract.PICTURE_CODE;
import static es.dsrroma.garantator.data.contracts.PictureContract.PICTURE_CODE_BY_ID;
import static es.dsrroma.garantator.data.contracts.PictureContract.PICTURE_PATH;
import static es.dsrroma.garantator.data.contracts.PictureContract.PictureEntry;
import static es.dsrroma.garantator.data.contracts.ProductContract.PRODUCT_CODE;
import static es.dsrroma.garantator.data.contracts.ProductContract.PRODUCT_CODE_BY_ID;
import static es.dsrroma.garantator.data.contracts.ProductContract.PRODUCT_PATH;
import static es.dsrroma.garantator.data.contracts.WarrantyContract.WARRANTY_CODE;
import static es.dsrroma.garantator.data.contracts.WarrantyContract.WARRANTY_CODE_BY_ID;
import static es.dsrroma.garantator.data.contracts.WarrantyContract.WARRANTY_PATH;
import static es.dsrroma.garantator.data.contracts.WarrantyViewContract.WARRANTY_VIEW_CODE;
import static es.dsrroma.garantator.data.contracts.WarrantyViewContract.WARRANTY_VIEW_CODE_BY_ID;
import static es.dsrroma.garantator.data.contracts.WarrantyViewContract.WARRANTY_VIEW_PATH;


public class WarrantiesProvider extends ContentProvider {

    public static final String NUM_PARAM = "/#";
    public static final String STRING_PARAM = "/*";

    private static final UriMatcher uriMatcher = buildUriMatcher();

    private WarrantiesDbHelper openHelper;

    public static UriMatcher buildUriMatcher() {
        final UriMatcher matcher = new UriMatcher(UriMatcher.NO_MATCH);
        final String authority = BaseContract.CONTENT_AUTHORITY;

        matcher.addURI(authority, WARRANTY_PATH, WARRANTY_CODE);
        matcher.addURI(authority, WARRANTY_PATH + NUM_PARAM, WARRANTY_CODE_BY_ID);

        matcher.addURI(authority, PRODUCT_PATH, PRODUCT_CODE);
        matcher.addURI(authority, PRODUCT_PATH + NUM_PARAM, PRODUCT_CODE_BY_ID);

        matcher.addURI(authority, BRAND_PATH, BRAND_CODE);
        matcher.addURI(authority, BRAND_PATH + NUM_PARAM, BRAND_CODE_BY_ID);
        matcher.addURI(authority, BRAND_PATH + STRING_PARAM, BRAND_CODE_BY_NAME);

        matcher.addURI(authority, CATEGORY_PATH, CATEGORY_CODE);
        matcher.addURI(authority, CATEGORY_PATH + NUM_PARAM, CATEGORY_CODE_BY_ID);
        matcher.addURI(authority, CATEGORY_PATH + STRING_PARAM, CATEGORY_CODE_BY_NAME);

        matcher.addURI(authority, WARRANTY_VIEW_PATH, WARRANTY_VIEW_CODE);
        matcher.addURI(authority, WARRANTY_VIEW_PATH + NUM_PARAM, WARRANTY_VIEW_CODE_BY_ID);

        matcher.addURI(authority, PICTURE_PATH, PICTURE_CODE);
        matcher.addURI(authority, PICTURE_PATH + NUM_PARAM, PICTURE_CODE_BY_ID);

        return matcher;
    }

    @Override
    public boolean onCreate() {
        openHelper = new WarrantiesDbHelper(getContext());
        return true;
    }

    @Nullable
    @Override
    public Cursor query(@NonNull Uri uri, @Nullable String[] projection, @Nullable String selection,
                        @Nullable String[] selectionArgs, @Nullable String sortOrder) {
        int uriCode = uriMatcher.match(uri);
        String tableName = getTableName(uriCode);
        int queryType = getQueryType(uriCode);

        Cursor cursor;
        switch (queryType) {
            case QUERY_ALL:
                cursor = queryAll(tableName, projection, selection, selectionArgs, sortOrder);
                break;
            case QUERY_BY_ID:
                cursor = queryById(tableName, uri, projection, sortOrder);
                break;
            case QUERY_BY_NAME:
                cursor = queryByName(tableName, uri, projection, sortOrder);
                break;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
        cursor.setNotificationUri(getContext().getContentResolver(), uri);
        return cursor;
    }

    @Override
    public int delete(@NonNull Uri uri, @Nullable String selection, @Nullable String[] selectionArgs) {
        int uriCode = uriMatcher.match(uri);
        int queryType = getQueryType(uriCode);
        int deleted;
        switch (queryType) {
            case QUERY_BY_ID:
                SQLiteDatabase db = openHelper.getWritableDatabase();
                String id = uri.getPathSegments().get(1);
                String tableName = getTableName(uriCode);

                deleted = db.delete(tableName, BaseEntry.COLUMN_ID + SQL_PARAM, new String[]{id});
                break;
            default:
                throw new UnsupportedOperationException("Unable to delete uri: " + uri);
        }
        if (deleted != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }

        return deleted;
    }

    @Nullable
    @Override
    public Uri insert(@NonNull Uri uri, @Nullable ContentValues values) {
        int uriCode = uriMatcher.match(uri);
        int queryType = getQueryType(uriCode);
        Uri returnUri;
        switch (queryType) {
            case QUERY_ALL:
                SQLiteDatabase db = openHelper.getWritableDatabase();
                String tableName = getTableName(uriCode);
                Uri contentUri = getContentUri(uriCode);

                long id = db.insertOrThrow(tableName, null, values);
                if (id > 0) {
                    returnUri = ContentUris.withAppendedId(contentUri, id);
                } else {
                    throw new SQLException("Failed to insert row into " + uri);
                }
                break;
            default:
                throw new UnsupportedOperationException("Unable to insert uri: " + uri);
        }
        getContext().getContentResolver().notifyChange(uri, null);
        return returnUri;
    }

    @Override
    public int update(@NonNull Uri uri, @Nullable ContentValues values, @Nullable String selection,
                      @Nullable String[] selectionArgs) {
        int uriCode = uriMatcher.match(uri);
        int queryType = getQueryType(uriCode);
        int updated;
        switch (queryType) {
            case QUERY_BY_ID:
                SQLiteDatabase db = openHelper.getWritableDatabase();
                String id = uri.getPathSegments().get(1);
                String tableName = getTableName(uriCode);

                updated = db.updateWithOnConflict(tableName, values, BaseEntry.COLUMN_ID + SQL_PARAM,
                        new String[]{id}, SQLiteDatabase.CONFLICT_NONE);
                break;
            default:
                throw new UnsupportedOperationException("Unable to update uri: " + uri);
        }
        if (updated != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }

        return updated;
    }

    @Nullable
    @Override
    public String getType(@NonNull Uri uri) {
        throw new UnsupportedOperationException("Not yet implemented: " + uri);
    }

    @Override
    @TargetApi(11)
    public void shutdown() {
        openHelper.close();
        super.shutdown();
    }

    private static int getQueryType(int code) {
        return code % BaseContract.BASE_CODE;
    }

    private static int getTableCode(int code) {
        return (code / BaseContract.BASE_CODE) * BaseContract.BASE_CODE;
    }

    private static String getTableName(int code) {
        int baseCode = getTableCode(code);
        String tableName;
        switch (baseCode) {
            case WARRANTY_CODE:
                tableName = WarrantyEntry.TABLE_NAME;
                break;
            case PRODUCT_CODE:
                tableName = ProductEntry.TABLE_NAME;
                break;
            case BRAND_CODE:
                tableName = BrandEntry.TABLE_NAME;
                break;
            case CATEGORY_CODE:
                tableName = CategoryEntry.TABLE_NAME;
                break;
            case WARRANTY_VIEW_CODE:
                tableName = WarrantyViewEntry.VIEW_NAME;
                break;
            case PICTURE_CODE:
                tableName = PictureEntry.TABLE_NAME;
                break;
            default:
                throw new UnsupportedOperationException("Unknown code: " + code);
        }
        return tableName;
    }

    private static Uri getContentUri(int code) {
        int baseCode = getTableCode(code);
        String path;
        switch (baseCode) {
            case WARRANTY_CODE:
                path = WARRANTY_PATH;
                break;
            case PRODUCT_CODE:
                path = PRODUCT_PATH;
                break;
            case BRAND_CODE:
                path = BRAND_PATH;
                break;
            case CATEGORY_CODE:
                path = CATEGORY_PATH;
                break;
            case WARRANTY_VIEW_CODE:
                path = WARRANTY_VIEW_PATH;
                break;
            case PICTURE_CODE:
                path = PICTURE_PATH;
                break;
            default:
                throw new UnsupportedOperationException("Unknown code: " + code);
        }
        return BASE_CONTENT_URI.buildUpon().appendPath(path).build();
    }

    private Cursor queryById(@NonNull String tableName, @NonNull Uri uri, @Nullable String[] projection,
                             @Nullable String sortOrder) {
        Cursor cursor;
        String id = uri.getLastPathSegment();
        String[] newSelectionArgs = new String[] {id};
        cursor = openHelper.getReadableDatabase().query(tableName, projection,
                BaseContract.BaseEntry.COLUMN_ID + BaseContract.SQL_PARAM,
                newSelectionArgs, null, null, sortOrder);
        return cursor;
    }

    private Cursor queryByName(@NonNull String tableName, @NonNull Uri uri, @Nullable String[] projection,
                             @Nullable String sortOrder) {
        Cursor cursor;
        String id = uri.getLastPathSegment();
        String[] newSelectionArgs = new String[] {id};
        cursor = openHelper.getReadableDatabase().query(tableName, projection,
                BaseEntry.COLUMN_NAME + BaseContract.SQL_PARAM,
                newSelectionArgs, null, null, sortOrder);
        return cursor;
    }

    private Cursor queryAll(@NonNull String tableName, @Nullable String[] projection, @Nullable String selection,
                            @Nullable String[] selectionArgs, @Nullable String sortOrder) {
        return openHelper.getReadableDatabase().query(
                tableName, projection, selection, selectionArgs, null, null, sortOrder);
    }

}
