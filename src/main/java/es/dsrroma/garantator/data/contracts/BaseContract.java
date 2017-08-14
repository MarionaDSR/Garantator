package es.dsrroma.garantator.data.contracts;

import android.net.Uri;
import android.provider.BaseColumns;

public abstract class BaseContract {

    // SQL Constants
    public static final String SQL_CREATE_TABLE = "CREATE TABLE ";
    public static final String SQL_CREATE_VIEW = "CREATE VIEW ";
    public static final String SQL_SELECT = "SELECT ";
    public static final String SQL_FROM = " FROM ";
    public static final String SQL_INNER_JOIN = " INNER JOIN ";

    public static final String SQL_OPEN = " (";
    public static final String SQL_CLOSE = ")";
    public static final String SQL_END = ";";
    public static final String SQL_EQ = " = ";

    public static final String SQL_INTEGER = " INTEGER ";
    public static final String SQL_STRING = " STRING ";
    public static final String SQL_DATE = " DATE ";

    public static final String SQL_AS = " AS ";
    public static final String SQL_ON = " ON ";

    public static final String SQL_COMMA = ", ";
    public static final String SQL_DOT = ".";
    public static final String SQL_PK = " PRIMARY KEY AUTOINCREMENT ";
    public static final String SQL_UNIQUE_NOT_NULL = "  UNIQUE NOT NULL ";
    public static final String SQL_REFERENCES = " REFERENCES ";
    public static final String SQL_PARAM = " = ? ";

    public static final String SQL_BASE_FIELDS =
            BaseEntry.COLUMN_ID + SQL_INTEGER + SQL_PK + SQL_COMMA +
                    BaseEntry.COLUMN_NAME + SQL_STRING + SQL_UNIQUE_NOT_NULL + SQL_COMMA +
            BaseEntry.COLUMN_CREATED_AT + SQL_DATE + SQL_COMMA +
                    BaseEntry.COLUMN_UPDATED_AT + SQL_DATE;

    public static class BaseEntry implements BaseColumns {
        public static final String COLUMN_ID = "_id";
        public static final String COLUMN_NAME = "name";
        public static final String COLUMN_CREATED_AT = "createdAt";
        public static final String COLUMN_UPDATED_AT = "updatedAt";
    }

    // Content Provider Constants
    public static final int BASE_CODE = 1000;
    public static final int WARRANTY_BASE_CODE = 1;
    public static final int PRODUCT_BASE_CODE = 2;
    public static final int BRAND_BASE_CODE = 3;
    public static final int CATEGORY_BASE_CODE = 4;
    public static final int WARRANTY_VIEW_BASE_CODE = 5;

    public static final int QUERY_ALL = 0;
    public static final int QUERY_BY_ID = 1;

    public static final String CONTENT_AUTHORITY = "es.dsrroma.garantator"; // TODO use resources
    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);
}
