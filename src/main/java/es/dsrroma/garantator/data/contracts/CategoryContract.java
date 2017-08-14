package es.dsrroma.garantator.data.contracts;

import android.database.Cursor;
import android.net.Uri;

import java.util.List;

import es.dsrroma.garantator.data.model.Category;
import es.dsrroma.garantator.utils.CursorToBeanUtils;

public class CategoryContract extends BaseContract {

    // SQL Constants
    /**
         CREATE TABLE Categories (
             _id  INTEGER PRIMARY KEY AUTOINCREMENT,
             name STRING  UNIQUE NOT NULL
         );
     */
    public static final String SQL_CREATE_CATEGORY_TABLE =
            SQL_CREATE_TABLE + CategoryEntry.TABLE_NAME + SQL_OPEN + SQL_BASE_FIELDS +
            SQL_CLOSE + SQL_END;


    public static final class CategoryEntry extends BaseContract.BaseEntry {
        public static final String TABLE_NAME = "Categories";
    }

    // Content Provider Constants
    public static final String CATEGORY_PATH = "category";
    public static final int CATEGORY_CODE = BASE_CODE * CATEGORY_BASE_CODE;
    public static final int CATEGORY_CODE_BY_ID = CATEGORY_CODE + QUERY_BY_ID;
    public static final Uri CATEGORY_CONTENT_URI = BASE_CONTENT_URI.buildUpon().appendPath(CATEGORY_PATH).build();


    public static Category getBeanFromCursor(Cursor cursor, int position) {
        return CursorToBeanUtils.cursorToBean(cursor, position, Category.class);
    }

    public static List<Category> getBeansFromCursor(Cursor cursor) {
        return CursorToBeanUtils.cursorToBeans(cursor, Category.class);
    }
}
