package es.dsrroma.garantator;

import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import es.dsrroma.garantator.adapters.WarrantyAdapter;
import es.dsrroma.garantator.data.model.Warranty;

import static es.dsrroma.garantator.data.contracts.WarrantyContract.WARRANTY_CONTENT_URI;

public class MainActivity extends AppCompatActivity implements
        WarrantyAdapter.OnItemClickListener,
        LoaderManager.LoaderCallbacks<Cursor> {

    private WarrantyAdapter warrantyAdapter;

    private RecyclerView rvWarrantiesList;
    private TextView tvErrorMessage;
    private ProgressBar pbLoadingIndicator;

    private static final int WARRANTY_LOADER_ID = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        rvWarrantiesList = (RecyclerView) findViewById(R.id.rvWarrantiesList);
        tvErrorMessage = (TextView) findViewById(R.id.tvErrorMessage);
        pbLoadingIndicator = (ProgressBar) findViewById(R.id.pbLoadingIndicator);

        rvWarrantiesList.setLayoutManager(new LinearLayoutManager(this));
        rvWarrantiesList.setHasFixedSize(true); // all items will have the same size

        warrantyAdapter = new WarrantyAdapter(null, this);
        rvWarrantiesList.setAdapter(warrantyAdapter);

        getSupportLoaderManager().initLoader(WARRANTY_LOADER_ID, null, this);
    }

    @Override
    public void onItemClick(View v, int position) {
        Warranty warranty = warrantyAdapter.getItem(position);
        Intent intent = new Intent(this, DetailActivity.class);
        Uri uri = WARRANTY_CONTENT_URI.buildUpon().appendPath(Long.toString(warranty.getId())).build();
        intent.setData(uri);
        startActivity(intent);
    }

    /** Click events in Floating Action Button */
    public void addWarranty(@SuppressWarnings("UnusedParameters") View view) {
        Intent intent = new Intent(this, AddWarrantyActivity.class);
        startActivity(intent);
    }

        @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
            return new CursorLoader(this, WARRANTY_CONTENT_URI, null, null, null, null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        pbLoadingIndicator.setVisibility(View.INVISIBLE);
        warrantyAdapter.swapCursor(data);
        if (data == null) {
            showErrorMessage(R.string.error_message);
        } else if (data.getCount() == 0) {
            showErrorMessage(R.string.error_empty_list);
        } else {
            showList();
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        warrantyAdapter.swapCursor(null);
    }

    private void showList() {
        rvWarrantiesList.setVisibility(View.VISIBLE);
        tvErrorMessage.setVisibility(View.INVISIBLE);
    }

    private void showErrorMessage(int errorMessage, String ... s) {
        tvErrorMessage.setText(getString(errorMessage, s));
        rvWarrantiesList.setVisibility(View.INVISIBLE);
        tvErrorMessage.setVisibility(View.VISIBLE);
    }
}
