package es.dsrroma.garantator.data.contracts;

import android.database.Cursor;
import android.net.Uri;

import java.util.List;

import es.dsrroma.garantator.data.model.Warranty;
import es.dsrroma.garantator.utils.CursorToBeanUtils;

import static es.dsrroma.garantator.data.contracts.BaseContract.BaseEntry.COLUMN_ID;
import static es.dsrroma.garantator.data.contracts.ProductContract.ProductEntry;

public class WarrantyContract extends BaseContract {

    // SQL Constants
    /**
         CREATE TABLE Warranties (
             _id       INTEGER PRIMARY KEY AUTOINCREMENT,
             name      STRING  UNIQUE NOT NULL,
             productId INTEGER REFERENCES Products (_id),
             startDate DATE
         );
     */
    public static final String SQL_CREATE_WARRANTY_TABLE =
            SQL_CREATE_TABLE + WarrantyEntry.TABLE_NAME + SQL_OPEN + SQL_BASE_FIELDS + SQL_COMMA +
            WarrantyEntry.COLUMN_PRODUCT_ID + SQL_INTEGER +
                    SQL_REFERENCES + ProductEntry.TABLE_NAME + SQL_OPEN + COLUMN_ID + SQL_CLOSE + SQL_COMMA +
            WarrantyEntry.COLUMN_START_DATE + SQL_DATE +
            SQL_CLOSE + SQL_END;


    public static final class WarrantyEntry extends BaseContract.BaseEntry {
        public static final String TABLE_NAME = "Warranties";
        public static final String COLUMN_PRODUCT_ID = "productId";
        public static final String COLUMN_START_DATE = "startDate";
    }

    // Content Provider Constants
    public static final String WARRANTY_PATH = "warranty";
    public static final int WARRANTY_CODE = BASE_CODE * WARRANTY_BASE_CODE;
    public static final int WARRANTY_CODE_BY_ID = WARRANTY_CODE + QUERY_BY_ID;
    public static final Uri WARRANTY_CONTENT_URI = BASE_CONTENT_URI.buildUpon().appendPath(WARRANTY_PATH).build();


    public static Warranty getBeanFromCursor(Cursor cursor, int position) {
        return CursorToBeanUtils.cursorToBean(cursor, position, Warranty.class);
    }

    public static List<Warranty> getBeansFromCursor(Cursor cursor) {
        return CursorToBeanUtils.cursorToBeans(cursor, Warranty.class);
    }
}
