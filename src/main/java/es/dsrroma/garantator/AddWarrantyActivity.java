package es.dsrroma.garantator;

import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.Toast;

import es.dsrroma.garantator.data.contracts.WarrantyContract;
import es.dsrroma.garantator.data.model.Warranty;
import es.dsrroma.garantator.data.services.WarrantyUpdateService;
import es.dsrroma.garantator.utils.CursorToBeanUtils;

import static es.dsrroma.garantator.data.contracts.WarrantyContract.WARRANTY_CONTENT_URI;

public class AddWarrantyActivity extends AppCompatActivity {

    private EditText etName;
    private boolean editMode;

    private Warranty warranty;
    private Cursor cursor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_warranty);

        etName = (EditText) findViewById(R.id.etWarrantyName);

        final Uri warrantyUri = getIntent().getData();
        editMode = warrantyUri != null;
        if (editMode) {
            cursor = getContentResolver().query(warrantyUri, null, null, null, null, null);

            if (cursor.moveToFirst()) {
                warranty = CursorToBeanUtils.cursorToBean(cursor, 0, Warranty.class);
                etName.setText(warranty.getName());
            }
        } else { // add mode
            warranty = new Warranty();
        }

        setListeners();
    }

    private void setListeners() {
        etName.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                // Nothing to do
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // Nothing to do
            }

            @Override
            public void afterTextChanged(Editable s) {
                warranty.setName(s.toString());
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.add, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case R.id.action_done:
                if (validateContents()) {
                    ContentValues cv = warrantyToContentValues();
                    if (editMode) {
                        updateWarranty(cv);
                    } else { // add mode
                        addWarranty(cv);
                    }
                }
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void addWarranty(ContentValues cv) {
        WarrantyUpdateService.insertNewWarranty(this, cv);
        finish();
    }

    private void updateWarranty(ContentValues cv) {
        Uri uri = WARRANTY_CONTENT_URI.buildUpon().appendPath(warranty.getId() + "").build();
        WarrantyUpdateService.updateWarranty(this, uri, cv);
        finish();
    }

    private boolean validateContents() {
        boolean res = true;
        if (etName.getText().toString().isEmpty()) {
            res = false;
            String message = getString(R.string.error_empty_input, getString(R.string.warranty_name_hint));
            Toast.makeText(this, message, Toast.LENGTH_LONG).show(); // TODO manage errors
        }
        return res;
    }

    private ContentValues warrantyToContentValues() {
        ContentValues cv = new ContentValues();
        cv.put(WarrantyContract.WarrantyEntry.COLUMN_NAME, warranty.getName());
        return cv;
    }
}
