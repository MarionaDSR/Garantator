package es.dsrroma.garantator.data.contracts;

import android.database.Cursor;
import android.net.Uri;

import java.util.List;

import es.dsrroma.garantator.data.model.Picture;
import es.dsrroma.garantator.utils.CursorToBeanUtils;

import static es.dsrroma.garantator.data.contracts.PictureContract.PictureEntry.COLUMN_FILE_NAME;
import static es.dsrroma.garantator.data.contracts.PictureContract.PictureEntry.COLUMN_POSITION;
import static es.dsrroma.garantator.data.contracts.PictureContract.PictureEntry.COLUMN_WARRANTY_ID;
import static es.dsrroma.garantator.data.contracts.WarrantyContract.WarrantyEntry;

public class PictureContract extends BaseContract {

    // SQL Constants
    /**
     CREATE TABLE Pictures (
     _id        INTEGER PRIMARY KEY AUTOINCREMENT,
     filename   STRING  UNIQUE NOT NULL,
     warrantyId INTEGER REFERENCES Products (_id),
     position   INTEGER NOT NULL
     );
     */
    public static final String SQL_CREATE_PICTURE_TABLE =
            SQL_CREATE_TABLE + PictureEntry.TABLE_NAME + SQL_OPEN + SQL_BASE_ID_FIELDS + SQL_COMMA +
                    COLUMN_FILE_NAME + SQL_STRING + SQL_UNIQUE_NOT_NULL + SQL_COMMA +
                    COLUMN_WARRANTY_ID + SQL_INTEGER + SQL_REFERENCES + WarrantyEntry.TABLE_NAME +
                    SQL_OPEN + WarrantyEntry.COLUMN_ID + SQL_CLOSE + SQL_COMMA +
                    COLUMN_POSITION + SQL_INTEGER + SQL_NOT_NULL +
                    SQL_CLOSE + SQL_END;


    public static final class PictureEntry {
        public static final String TABLE_NAME = "Pictures";
        public static final String COLUMN_FILE_NAME = "filename";
        public static final String COLUMN_WARRANTY_ID = "warrantyId";
        public static final String COLUMN_POSITION = "position";
    }

    // Content Provider Constants
    public static final String PICTURE_PATH = "brand";
    public static final int PICTURE_CODE = BASE_CODE * PICTURE_BASE_CODE;
    public static final int PICTURE_CODE_BY_ID = PICTURE_CODE + QUERY_BY_ID;
    public static final int PICTURE_CODE_BY_WARRANTY = PICTURE_CODE + QUERY_BY_WARRANTY;
    public static final Uri PICTURE_CONTENT_URI = BASE_CONTENT_URI.buildUpon().appendPath(PICTURE_PATH).build();

    public static Picture getBeanFromCursor(Cursor cursor, int position) {
        return CursorToBeanUtils.cursorToBean(cursor, position, Picture.class);
    }

    public static List<Picture> getBeansFromCursor(Cursor cursor) {
        return CursorToBeanUtils.cursorToBeans(cursor, Picture.class);
    }
}
