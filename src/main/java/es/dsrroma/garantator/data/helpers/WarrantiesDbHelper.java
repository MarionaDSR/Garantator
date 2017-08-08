package es.dsrroma.garantator.data.helpers;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import es.dsrroma.garantator.data.contracts.BrandContract;
import es.dsrroma.garantator.data.contracts.CategoryContract;
import es.dsrroma.garantator.data.contracts.ProductContract;
import es.dsrroma.garantator.data.contracts.WarrantyContract;

public class WarrantiesDbHelper extends SQLiteOpenHelper {

    public static final String DATABASE_NAME = "warranties.db";

    private static final int DATABASE_VERSION = 1;

    public WarrantiesDbHelper(Context context){
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        try {
            db.execSQL(BrandContract.SQL_CREATE_BRAND_TABLE);
            db.execSQL(CategoryContract.SQL_CREATE_CATEGORY_TABLE);
            db.execSQL(ProductContract.SQL_CREATE_PRODUCT_TABLE);
            db.execSQL(WarrantyContract.SQL_CREATE_WARRANTY_TABLE);

            ContentValues cv = new ContentValues();
            cv.put(WarrantyContract.WarrantyEntry.COLUMN_NAME, "test" + System.currentTimeMillis());
            db.insert(WarrantyContract.WarrantyEntry.TABLE_NAME, null, cv);

            Cursor cursor = db.rawQuery("select count(*) from Warranties", null);
            cursor.moveToFirst();
            int i = cursor.getInt(0);
            cursor.close();
        } catch (SQLException e) {
            Log.d("WarrantiesDbHelper", "onCreate", e);
            throw e;
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Nothing to do by now
    }
}
