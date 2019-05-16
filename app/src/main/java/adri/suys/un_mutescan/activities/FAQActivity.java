package adri.suys.un_mutescan.activities;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import java.util.Objects;

import adri.suys.un_mutescan.R;

public class FAQActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_faq);
        configActionBar();
    }

    void configActionBar(){
        Objects.requireNonNull(getSupportActionBar()).setTitle("FAQ");
        getSupportActionBar().setElevation(0);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    @Override
    public boolean onSupportNavigateUp(){
        finish();
        return true;
    }

    @Override
    public void onResume() {
        super.onResume();
    }

}
