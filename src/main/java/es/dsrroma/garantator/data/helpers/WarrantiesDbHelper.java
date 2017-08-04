package es.dsrroma.garantator.data.helpers;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

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
        db.execSQL(BrandContract.SQL_CREATE_BRAND_TABLE);
        db.execSQL(CategoryContract.SQL_CREATE_CATEGORY_TABLE);
        db.execSQL(ProductContract.SQL_CREATE_PRODUCT_TABLE);
        db.execSQL(WarrantyContract.SQL_CREATE_WARRANTY_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Nothing to do by now
    }
}
