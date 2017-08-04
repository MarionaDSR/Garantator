package es.dsrroma.garantator.data.contracts;

import static es.dsrroma.garantator.data.contracts.BrandContract.BrandEntry;
import static es.dsrroma.garantator.data.contracts.CategoryContract.CategoryEntry;

public class ProductContract extends BaseContract {

    // SQL Constants
    /**
         CREATE TABLE Products (
             _id          INTEGER PRIMARY KEY AUTOINCREMENT,
             name         STRING  UNIQUE NOT NULL,
             categoryId   INTEGER REFERENCES Categories (_id),
             brandId      INTEGER REFERENCES Brands (_id),
             model        STRING,
             serialNumber STRING
         );
     */
    public static final String SQL_CREATE_PRODUCT_TABLE =
            SQL_CREATE_TABLE + ProductEntry.TABLE_NAME + SQL_OPEN + SQL_BASE_FIELDS + SQL_COMMA +
            ProductEntry.COLUMN_CATEGORY_ID + SQL_INTEGER +
                    SQL_REFERENCES + CategoryEntry.TABLE_NAME + SQL_OPEN + CategoryEntry.COLUMN_ID + SQL_CLOSE + SQL_COMMA +
            ProductEntry.COLUMN_BRAND_ID + SQL_INTEGER +
                    SQL_REFERENCES + BrandEntry.TABLE_NAME + SQL_OPEN + BrandEntry.COLUMN_ID + SQL_CLOSE + SQL_COMMA +
            ProductEntry.COLUMN_MODEL + SQL_STRING + SQL_COMMA +
            ProductEntry.COLUMN_SERIAL_NUMBER + SQL_STRING + SQL_CLOSE + SQL_END;

    public static final class ProductEntry extends BaseContract.BaseEntry {
        public static final String TABLE_NAME = "Products";
        public static final String COLUMN_CATEGORY_ID = "categoryId";
        public static final String COLUMN_BRAND_ID = "brandId";
        public static final String COLUMN_MODEL = "model";
        public static final String COLUMN_SERIAL_NUMBER = "serialNumber";
    }

    // Content Provider Constants
    public static final String PRODUCT_PATH = "warranty";
    public static final int PRODUCT_CODE = BASE_CODE * 2;
    public static final int PRODUCT_CODE_BY_ID = PRODUCT_CODE + QUERY_BY_ID;
}