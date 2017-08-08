package es.dsrroma.garantator;

import android.content.ContentValues;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.Toast;

import es.dsrroma.garantator.data.contracts.WarrantyContract;
import es.dsrroma.garantator.data.services.WarrantyUpdateService;

public class AddWarrantyActivity extends AppCompatActivity {

    private EditText etName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_warranty);

        etName = (EditText) findViewById(R.id.etWarrantyName);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.add, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_add) {
            Toast.makeText(this, "Add!! " + etName.getText(), Toast.LENGTH_SHORT).show();
            addWarranty();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void addWarranty() {
        ContentValues cv = new ContentValues(); // TODO use bean Warranty
        cv.put(WarrantyContract.WarrantyEntry.COLUMN_NAME, etName.getText().toString());
        WarrantyUpdateService.insertNewWarranty(this, cv);
        finish();
    }
}
