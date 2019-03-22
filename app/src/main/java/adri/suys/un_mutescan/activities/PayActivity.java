package adri.suys.un_mutescan.activities;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;

import adri.suys.un_mutescan.R;
import adri.suys.un_mutescan.presenter.PayPresenter;

import static android.view.View.GONE;

public class PayActivity extends Activity {

    private ImageView qrcodeImg;
    private TextView amountToPay;
    private Button doneBtn, cashBtn, bcBtn;
    private double price;
    private PayPresenter presenter;
    private ProgressBar progressBar;
    private static final String BANCONTACT_INTENT = "mobi.inthepocket.bcmc.bancontact";

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pay);
        configActionBar();
        presenter = new PayPresenter(this);
        qrcodeImg = findViewById(R.id.bancontact_qrcode);
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

    public void setUpBancontactPayment() {
        //qrcodeImg.setVisibility(View.VISIBLE);
        doneBtn.setVisibility(View.VISIBLE);
        cashBtn.setVisibility(GONE);
        bcBtn.setVisibility(GONE);
        hideProgressBar();
        Intent bcIntent = getPackageManager().getLaunchIntentForPackage(BANCONTACT_INTENT);
        if (bcIntent == null){
            bcIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + BANCONTACT_INTENT));
        }
        startActivity(bcIntent);
    }

    public void hideProgressBar(){
        progressBar.setVisibility(GONE);
    }

    /////////////////////
    // private methods //
    /////////////////////

    private void generateQrCode(){
        QRCodeWriter qrCodeWriter = new QRCodeWriter();
        try {
            BitMatrix bitMatrix = qrCodeWriter.encode(Double.toString(price), BarcodeFormat.QR_CODE, 180, 180);
            int width = bitMatrix.getWidth();
            int height = bitMatrix.getHeight();
            Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.RGB_565);
            for (int i = 0; i < width; i++){
                for (int j = 0; j < height; j++){
                    bitmap.setPixel(i, j, bitMatrix.get(i, j) ? ContextCompat.getColor(this, R.color.dark_Green) : Color.WHITE);
                }
            }
            qrcodeImg.setImageBitmap(bitmap);
        } catch (WriterException e) {
            e.printStackTrace();
        }

    }

    private void initPrice(){
        presenter.setTotalTicketSold(getIntent().getIntExtra("nbTicketsSold", 0));
        price = getIntent().getDoubleExtra("price", 0);
        amountToPay.setText(getResources().getString(R.string.amount_to_pay, price));
        generateQrCode();
    }

    private void setButtons(){
        doneBtn = findViewById(R.id.pay_done_btn);
        cashBtn = findViewById(R.id.pay_cash_btn);
        bcBtn = findViewById(R.id.pay_bancontact_btn);
        qrcodeImg.setVisibility(GONE);
        doneBtn.setVisibility(GONE);
        doneBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
        cashBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                progressBar.setVisibility(View.VISIBLE);
                presenter.addTickets(true);
            }
        });
        bcBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                progressBar.setVisibility(View.VISIBLE);
                presenter.addTickets(false);
            }
        });
    }
}
