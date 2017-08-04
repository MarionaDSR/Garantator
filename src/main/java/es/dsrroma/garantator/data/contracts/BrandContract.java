package es.dsrroma.garantator.data.contracts;

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
}
