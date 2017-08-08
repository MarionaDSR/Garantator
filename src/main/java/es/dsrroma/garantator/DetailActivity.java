package es.dsrroma.garantator;

import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

import es.dsrroma.garantator.data.model.Warranty;
import es.dsrroma.garantator.utils.CursorToBeanUtils;

public class DetailActivity extends AppCompatActivity {

    private TextView tvWarrantyName;

    private Warranty warranty;
    private Cursor cursor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        tvWarrantyName = (TextView) findViewById(R.id.tvWarrantyName);

        final Uri warrantyUri = getIntent().getData();
        cursor = getContentResolver().query(warrantyUri, null, null, null, null, null);

        if (cursor.moveToFirst()) {
            warranty = CursorToBeanUtils.cursorToBean(cursor, 0, Warranty.class);
            tvWarrantyName.setText(warranty.getName());
        }

    }
}
