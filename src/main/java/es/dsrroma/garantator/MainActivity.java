package es.dsrroma.garantator;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import es.dsrroma.garantator.adapters.WarrantyAdapter;
import es.dsrroma.garantator.data.model.Warranty;

public class MainActivity extends AppCompatActivity implements WarrantyAdapter.WarrantyAdapterOnClickHandler {

    private WarrantyAdapter warrantyAdapter;

    private RecyclerView rvWarrantiesList;
    private TextView tvErrorMessage;
    private ProgressBar pbLoadingIndicator;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        rvWarrantiesList = (RecyclerView) findViewById(R.id.rvWarrantiesList);
        tvErrorMessage = (TextView) findViewById(R.id.tvErrorMessage);
        pbLoadingIndicator = (ProgressBar) findViewById(R.id.pbLoadingIndicator);

        LinearLayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        rvWarrantiesList.setLayoutManager(layoutManager);
        rvWarrantiesList.setHasFixedSize(true); // all items will have the same size

        warrantyAdapter = new WarrantyAdapter(this);
        rvWarrantiesList.setAdapter(warrantyAdapter);

        warrantyAdapter.setWarranties(dummyWarranties());
    }

    @Override
    public void onClick(Warranty warranty) {
        Intent intentDetailActivity = new Intent(this, DetailActivity.class);
        intentDetailActivity.putExtra(DetailActivity.EXTRA_WARRANTY, warranty);
        startActivity(intentDetailActivity);
    }

    @Deprecated
    private List<Warranty> dummyWarranties() {
        List<Warranty> warranties = new ArrayList<>();

        Warranty warranty0 = new Warranty();
        warranty0.setId(1);
        warranty0.setName("First one");
        warranty0.setStartDate(new Date());
        warranties.add(warranty0);

        Warranty warranty1 = new Warranty();
        warranty1.setId(2);
        warranty1.setName("The second");
        warranty0.setStartDate(new Date());
        warranties.add(warranty1);

        return warranties;
    }
}
