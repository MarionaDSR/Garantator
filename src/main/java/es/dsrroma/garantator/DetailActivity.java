package es.dsrroma.garantator;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

import es.dsrroma.garantator.data.model.Warranty;

public class DetailActivity extends AppCompatActivity {

    public static final String EXTRA_WARRANTY = "extraWarranty";
    private TextView tvWarrantyName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        tvWarrantyName = (TextView) findViewById(R.id.tvWarrantyName);

        Intent intent = getIntent();
        if (intent != null) {
            if (intent.hasExtra(EXTRA_WARRANTY)) {
                Warranty warranty = intent.getParcelableExtra(EXTRA_WARRANTY);
                tvWarrantyName.setText(warranty.getName());
            }
        }
    }
}
