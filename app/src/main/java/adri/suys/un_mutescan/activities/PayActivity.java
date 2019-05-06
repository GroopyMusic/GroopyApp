package adri.suys.un_mutescan.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.AppCompatButton;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import adri.suys.un_mutescan.R;
import adri.suys.un_mutescan.presenter.PayPresenter;
import adri.suys.un_mutescan.viewinterfaces.PayViewInterface;

import static android.view.View.GONE;

public class PayActivity extends Activity implements PayViewInterface {

    private TextView amountToPay;
    private PayPresenter presenter;
    private ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pay);
        configActionBar();
        presenter = new PayPresenter(this);
        amountToPay = findViewById(R.id.amount_to_pay);
        progressBar = findViewById(R.id.pay_progressBar);
        hideProgressBar();
        setButtons();
        initPrice();
    }

    @Override
    protected void onResume(){
        super.onResume();
        initPrice();
    }

    @Override
    public void onBackPressed(){
        startActivity(new Intent(this, OneEventActivity.class));
    }

    @Override
    public void showNoConnectionRetryToast() {
        showToast(getResources().getString(R.string.volley_error_no_connexion));
    }

    @Override
    public void showServerConnectionProblemToast() {
        showToast(getResources().getString(R.string.volley_error_server_error));
    }

    @Override
    public void showTicketAddedToast() {
        showToast(getResources().getString(R.string.tickets_well_added));
    }

    @Override
    public void showTicketNotAddedToast() {
        showToast(getResources().getString(R.string.tickets_not_well_added));
    }

    public void hideProgressBar(){
        progressBar.setVisibility(GONE);
    }

    /////////////////////
    // private methods //
    /////////////////////

    private void initPrice(){
        presenter.setTotalTicketSold(getIntent().getIntExtra("nbTicketsSold", 0));
        double price = getIntent().getDoubleExtra("price", 0);
        amountToPay.setText(getResources().getString(R.string.amount_to_pay, price));
    }

    private void setButtons(){
        AppCompatButton cashBtn = findViewById(R.id.pay_cash_btn);
        cashBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                progressBar.setVisibility(View.VISIBLE);
                presenter.addTickets(true);
            }
        });
    }
}
