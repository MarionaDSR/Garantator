package es.dsrroma.garantator.data.contracts;

import android.database.Cursor;
import android.net.Uri;
import android.provider.BaseColumns;

import es.dsrroma.garantator.data.model.Brand;
import es.dsrroma.garantator.data.model.Category;
import es.dsrroma.garantator.data.model.Product;
import es.dsrroma.garantator.data.model.Warranty;

import static es.dsrroma.garantator.data.contracts.BaseContract.BaseEntry.COLUMN_ID;
import static es.dsrroma.garantator.data.contracts.BaseContract.BaseEntry.COLUMN_NAME;
import static es.dsrroma.garantator.data.contracts.BrandContract.BrandEntry;
import static es.dsrroma.garantator.data.contracts.CategoryContract.CategoryEntry;
import static es.dsrroma.garantator.data.contracts.ProductContract.ProductEntry;
import static es.dsrroma.garantator.data.contracts.WarrantyContract.WarrantyEntry;
import static es.dsrroma.garantator.data.contracts.WarrantyViewContract.WarrantyViewEntry.BRAND_ALIAS;
import static es.dsrroma.garantator.data.contracts.WarrantyViewContract.WarrantyViewEntry.CATEGORY_ALIAS;
import static es.dsrroma.garantator.data.contracts.WarrantyViewContract.WarrantyViewEntry.COLUMN_BRAND_ID;
import static es.dsrroma.garantator.data.contracts.WarrantyViewContract.WarrantyViewEntry.COLUMN_BRAND_NAME;
import static es.dsrroma.garantator.data.contracts.WarrantyViewContract.WarrantyViewEntry.COLUMN_CATEGORY_ID;
import static es.dsrroma.garantator.data.contracts.WarrantyViewContract.WarrantyViewEntry.COLUMN_CATEROGY_NAME;
import static es.dsrroma.garantator.data.contracts.WarrantyViewContract.WarrantyViewEntry.COLUMN_PRODUCT_ID;
import static es.dsrroma.garantator.data.contracts.WarrantyViewContract.WarrantyViewEntry.COLUMN_PRODUCT_MODEL;
import static es.dsrroma.garantator.data.contracts.WarrantyViewContract.WarrantyViewEntry.COLUMN_PRODUCT_NAME;
import static es.dsrroma.garantator.data.contracts.WarrantyViewContract.WarrantyViewEntry.COLUMN_PRODUCT_SERIAL_NUMBER;
import static es.dsrroma.garantator.data.contracts.WarrantyViewContract.WarrantyViewEntry.COLUMN_WARRANTY_NAME;
import static es.dsrroma.garantator.data.contracts.WarrantyViewContract.WarrantyViewEntry.COLUMN_WARRANTY_START_DATE;
import static es.dsrroma.garantator.data.contracts.WarrantyViewContract.WarrantyViewEntry.PRODUCT_ALIAS;
import static es.dsrroma.garantator.data.contracts.WarrantyViewContract.WarrantyViewEntry.VIEW_NAME;
import static es.dsrroma.garantator.data.contracts.WarrantyViewContract.WarrantyViewEntry.WARRANTY_ALIAS;

public class WarrantyViewContract extends BaseContract {
    
    /**
         CREATE VIEW WarrantiesData AS
             SELECT w._id AS warrantyId,
                 w.name AS warrantyName,
                 w.startDate AS warrantyStartDate,
                 w.productId AS productId,
                 p.name AS productName,
                 p.model AS productModel,
                 p.serialNumber AS productSerialNumber,
                 p.categoryId AS categoryId,
                 c.name AS caterogyName,
                 p.brandId AS brandId,
                 b.name AS brandName
             FROM warranties AS w
                 INNER JOIN
                 products AS p ON w.productId = p._id
                 INNER JOIN
                 categories AS c ON p.categoryId = c._id
                 INNER JOIN
                 brands AS b ON p.brandId = b._id;
     */
    public static final String SQL_CREATE_WARRANTY_VIEW = SQL_CREATE_VIEW + VIEW_NAME + SQL_AS +
        SQL_SELECT + WARRANTY_ALIAS + SQL_DOT + COLUMN_ID + SQL_AS + COLUMN_ID + SQL_COMMA +
        WARRANTY_ALIAS + SQL_DOT + COLUMN_NAME + SQL_AS + COLUMN_WARRANTY_NAME + SQL_COMMA +
        WARRANTY_ALIAS + SQL_DOT + WarrantyEntry.COLUMN_START_DATE + SQL_AS + COLUMN_WARRANTY_START_DATE + SQL_COMMA +
        WARRANTY_ALIAS + SQL_DOT + WarrantyEntry.COLUMN_PRODUCT_ID + SQL_AS + COLUMN_PRODUCT_ID + SQL_COMMA +
        PRODUCT_ALIAS + SQL_DOT + COLUMN_NAME + SQL_AS + COLUMN_PRODUCT_NAME + SQL_COMMA +
        PRODUCT_ALIAS + SQL_DOT + ProductEntry.COLUMN_MODEL + SQL_AS + COLUMN_PRODUCT_MODEL + SQL_COMMA +
        PRODUCT_ALIAS + SQL_DOT + ProductEntry.COLUMN_SERIAL_NUMBER + SQL_AS + COLUMN_PRODUCT_SERIAL_NUMBER + SQL_COMMA +
        PRODUCT_ALIAS + SQL_DOT + ProductEntry.COLUMN_CATEGORY_ID + SQL_AS + COLUMN_CATEGORY_ID + SQL_COMMA +
        CATEGORY_ALIAS + SQL_DOT + COLUMN_NAME + SQL_AS + COLUMN_CATEROGY_NAME + SQL_COMMA +
        PRODUCT_ALIAS + SQL_DOT + ProductEntry.COLUMN_BRAND_ID + SQL_AS + COLUMN_BRAND_ID + SQL_COMMA +
        BRAND_ALIAS + SQL_DOT + COLUMN_NAME + SQL_AS + COLUMN_BRAND_NAME  +
        SQL_FROM + WarrantyEntry.TABLE_NAME + SQL_AS + WARRANTY_ALIAS +
        SQL_INNER_JOIN + ProductEntry.TABLE_NAME + SQL_AS + PRODUCT_ALIAS + SQL_ON +
        WARRANTY_ALIAS + SQL_DOT + WarrantyEntry.COLUMN_PRODUCT_ID + SQL_EQ + PRODUCT_ALIAS + SQL_DOT + COLUMN_ID +
        SQL_INNER_JOIN + CategoryEntry.TABLE_NAME + SQL_AS + CATEGORY_ALIAS + SQL_ON +
        PRODUCT_ALIAS + SQL_DOT + ProductEntry.COLUMN_CATEGORY_ID + SQL_EQ + CATEGORY_ALIAS + SQL_DOT + COLUMN_ID +
        SQL_INNER_JOIN + BrandEntry.TABLE_NAME + SQL_AS + BRAND_ALIAS + SQL_ON +
        PRODUCT_ALIAS + SQL_DOT + ProductEntry.COLUMN_BRAND_ID + SQL_EQ + BRAND_ALIAS + SQL_DOT + COLUMN_ID + SQL_END;

    public static final class WarrantyViewEntry implements BaseColumns {
        public static final String VIEW_NAME = "WarrantiesView";
        public static final String PRODUCT_ALIAS = "p";
        public static final String WARRANTY_ALIAS = "w";
        public static final String CATEGORY_ALIAS = "c";
        public static final String BRAND_ALIAS = "b";

        public static final String COLUMN_WARRANTY_NAME = "warrantyName";
        public static final String COLUMN_WARRANTY_START_DATE = "warrantyStartDate";
        public static final String COLUMN_PRODUCT_ID = "productId";
        public static final String COLUMN_PRODUCT_NAME = "productName";
        public static final String COLUMN_PRODUCT_MODEL = "productModel";
        public static final String COLUMN_PRODUCT_SERIAL_NUMBER = "productSerialNumber";
        public static final String COLUMN_CATEGORY_ID = "categoryId";
        public static final String COLUMN_CATEROGY_NAME = "caterogyName";
        public static final String COLUMN_BRAND_ID = "brandId";
        public static final String COLUMN_BRAND_NAME = "brandName";
    }

    public static Warranty getBeanFromCursor(Cursor c) {
        Warranty w = new Warranty();
        if (c.moveToFirst()) {
            w.setId(c.getLong(c.getColumnIndex(COLUMN_ID)));
            w.setName(c.getString(c.getColumnIndex(COLUMN_WARRANTY_NAME)));
            w.setStartDate(c.getLong(c.getColumnIndex(COLUMN_WARRANTY_START_DATE)));
            Product p = new Product();
            p.setId(c.getLong(c.getColumnIndex(COLUMN_PRODUCT_ID)));
            p.setName(c.getString(c.getColumnIndex(COLUMN_PRODUCT_NAME)));
            p.setModel(c.getString(c.getColumnIndex(COLUMN_PRODUCT_MODEL)));
            p.setSerialNumber(c.getString(c.getColumnIndex(COLUMN_PRODUCT_SERIAL_NUMBER)));
            Category cat = new Category();
            cat.setId(c.getLong(c.getColumnIndex(COLUMN_CATEGORY_ID)));
            cat.setName(c.getString(c.getColumnIndex(COLUMN_CATEROGY_NAME)));
            p.setCategory(cat);
            Brand b = new Brand();
            b.setId(c.getLong(c.getColumnIndex(COLUMN_BRAND_ID)));
            b.setName(c.getString(c.getColumnIndex(COLUMN_BRAND_NAME)));
            p.setBrand(b);
            w.setProduct(p);
        }
        return w;
    }

    // Content Provider Constants
    public static final String WARRANTY_VIEW_PATH = "warrantyView";
    public static final int WARRANTY_VIEW_CODE = BASE_CODE * WARRANTY_VIEW_BASE_CODE;
    public static final int WARRANTY_VIEW_CODE_BY_ID = WARRANTY_VIEW_CODE + QUERY_BY_ID;
    public static final Uri WARRANTY_VIEW_CONTENT_URI = BASE_CONTENT_URI.buildUpon().appendPath(WARRANTY_VIEW_PATH).build();
}
