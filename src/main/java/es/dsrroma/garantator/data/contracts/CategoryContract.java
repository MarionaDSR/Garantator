package es.dsrroma.garantator.data.contracts;

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
    public static final String CATEGORY_PATH = "warranty";
    public static final int CATEGORY_CODE = BASE_CODE * 4;
    public static final int CATEGORY_CODE_BY_ID = CATEGORY_CODE+ QUERY_BY_ID;
}
