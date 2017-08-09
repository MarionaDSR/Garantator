package es.dsrroma.garantator;

import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import es.dsrroma.garantator.data.model.Warranty;
import es.dsrroma.garantator.data.services.WarrantyUpdateService;
import es.dsrroma.garantator.utils.CursorToBeanUtils;

import static es.dsrroma.garantator.data.contracts.WarrantyContract.WARRANTY_CONTENT_URI;

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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.detail, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case R.id.action_edit:
                Toast.makeText(this, "Edit!! " + warranty.getName(), Toast.LENGTH_SHORT).show();
                editWarranty();
                return true;
            case R.id.action_delete:
                Toast.makeText(this, "Delete!! " + warranty.getId(), Toast.LENGTH_SHORT).show();
                deleteWarranty();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void deleteWarranty() {
        Uri uri = WARRANTY_CONTENT_URI.buildUpon().appendPath(warranty.getId() + "").build();
        WarrantyUpdateService.deleteWarranty(this, uri);
        finish();
    }

    private void editWarranty() {
        Intent intent = new Intent(this, AddWarrantyActivity.class);
        Uri uri = WARRANTY_CONTENT_URI.buildUpon().appendPath(Long.toString(warranty.getId())).build();
        intent.setData(uri);
        startActivity(intent);
    }
}
