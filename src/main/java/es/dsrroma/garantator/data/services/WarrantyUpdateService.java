package es.dsrroma.garantator.data.services;

import android.app.IntentService;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.Nullable;

import es.dsrroma.garantator.data.contracts.BaseContract;
import es.dsrroma.garantator.utils.NotifyUserRunnable;

import static es.dsrroma.garantator.data.contracts.WarrantyContract.WARRANTY_CONTENT_URI;

public class WarrantyUpdateService extends IntentService {

    private static final String TAG = WarrantyUpdateService.class.getSimpleName();

    //Intent actions
    public static final String ACTION_INSERT = TAG + ".INSERT";
    public static final String ACTION_UPDATE = TAG + ".UPDATE";
    public static final String ACTION_DELETE = TAG + ".DELETE";

    public static final String EXTRA_VALUES = TAG + ".ContentValues";

    public WarrantyUpdateService() {
        super(TAG);
    }

    public static void insertNewWarranty(Context context, ContentValues values) {
        long now = System.currentTimeMillis();
        values.put(BaseContract.BaseEntry.COLUMN_CREATED_AT, now);
        values.put(BaseContract.BaseEntry.COLUMN_UPDATED_AT, now);

        Intent intent = new Intent(context, WarrantyUpdateService.class);
        intent.setAction(ACTION_INSERT);
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
            performInsert(values);
        } else if (ACTION_UPDATE.equals(intent.getAction())) {
            ContentValues values = intent.getParcelableExtra(EXTRA_VALUES);
            performUpdate(intent.getData(), values);
        } else if (ACTION_DELETE.equals(intent.getAction())) {
            performDelete(intent.getData());
        }
    }

    private void performInsert(ContentValues values) {
        try {
            if (getContentResolver().insert(WARRANTY_CONTENT_URI, values) == null) {
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

    private void notifyMessage(String s) {
        Handler handler = new Handler(Looper.getMainLooper());
        handler.post(new NotifyUserRunnable(getApplicationContext(), s));
    }

    private void notifyProblem(Throwable t) {
        Handler handler = new Handler(Looper.getMainLooper());
        handler.post(new NotifyUserRunnable(getApplicationContext(), t));
    }
}
