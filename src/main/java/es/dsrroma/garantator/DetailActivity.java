package es.dsrroma.garantator;

import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import es.dsrroma.garantator.data.model.Warranty;
import es.dsrroma.garantator.data.services.WarrantyUpdateService;
import es.dsrroma.garantator.utils.CursorToBeanUtils;

import static es.dsrroma.garantator.data.contracts.WarrantyContract.WARRANTY_CONTENT_URI;

public class DetailActivity extends AppCompatActivity implements
        LoaderManager.LoaderCallbacks<Cursor> {

    // TODO user butterKnife
    // TODO load show all fields

    private TextView tvName;

    private Warranty warranty;
    private Cursor cursor;

    private static final int WARRANTY_LOADER_ID = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        tvName = (TextView) findViewById(R.id.tvName);

        final Uri warrantyUri = getIntent().getData();
        cursor = getContentResolver().query(warrantyUri, null, null, null, null, null);

        if (cursor.moveToFirst()) {
            showWarranty();
        }

        getSupportLoaderManager().initLoader(WARRANTY_LOADER_ID, null, this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.detail, menu);
        return true;
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        return new CursorLoader(this, getUri(), null, null, null, null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        if (this.cursor != null) {
            this.cursor.close();
        }
        this.cursor = data;
        showWarranty();
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        warranty = new Warranty();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case R.id.action_edit:
                editWarranty();
                return true;
            case R.id.action_delete:
                deleteWarranty();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void deleteWarranty() {
        WarrantyUpdateService.deleteWarrantyBatch(this, warranty);
        finish();
    }

    private void editWarranty() {
        Intent intent = new Intent(this, AddWarrantyActivity.class);
        intent.setData(getUri());
        startActivity(intent);
    }

    private Uri getUri() {
        return WARRANTY_CONTENT_URI.buildUpon().appendPath(Long.toString(warranty.getId())).build();
    }

    private void showWarranty() {
        warranty = CursorToBeanUtils.cursorToBean(cursor, Warranty.class);
        tvName.setText(warranty.getName());
    }
}
