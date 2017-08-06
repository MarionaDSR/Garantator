package es.dsrroma.garantator.data.contracts;

import android.database.Cursor;
import android.net.Uri;

import java.util.List;

import es.dsrroma.garantator.data.model.Brand;
import es.dsrroma.garantator.utils.CursorToBeanUtils;

public class BrandContract extends BaseContract {

    // SQL Constants
    /**
         CREATE TABLE Brands (
             _id  INTEGER PRIMARY KEY AUTOINCREMENT,
             name STRING  UNIQUE NOT NULL
         );
     */
    public static final String SQL_CREATE_BRAND_TABLE =
            SQL_CREATE_TABLE + BrandEntry.TABLE_NAME + SQL_OPEN + SQL_BASE_FIELDS +
            SQL_CLOSE + SQL_END;

    public static final class BrandEntry extends BaseContract.BaseEntry {
        public static final String TABLE_NAME = "Brands";
    }

    // Content Provider Constants
    public static final String BRAND_PATH = "warranty";
    public static final int BRAND_CODE = BASE_CODE * 3;
    public static final int BRAND_CODE_BY_ID = BRAND_CODE + QUERY_BY_ID;
    public static final Uri BRAND_CONTENT_URI = BASE_CONTENT_URI.buildUpon().appendPath(BRAND_PATH).build();


    public static Brand getBeanFromCursor(Cursor cursor) {
        return CursorToBeanUtils.cursorToBean(cursor, Brand.class);
    }

    public static List<Brand> getBeansFromCursor(Cursor cursor) {
        return CursorToBeanUtils.cursorToBeans(cursor, Brand.class);
    }
}
