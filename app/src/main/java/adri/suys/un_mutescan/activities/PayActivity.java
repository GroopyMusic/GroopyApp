package adri.suys.un_mutescan.activities;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.vision.barcode.Barcode;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;

import adri.suys.un_mutescan.R;

public class PayActivity extends Activity {

    private ImageView qrcodeImg;
    private TextView amountToPay;
    private double price;

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pay);
        configActionBar();
        qrcodeImg = findViewById(R.id.bancontact_qrcode);
        amountToPay = findViewById(R.id.amount_to_pay);
        initPrice();
    }

    @Override
    protected void onResume(){
        super.onResume();
        initPrice();
    }

    public void payInCash(View v){
        startActivity(new Intent(this, EventStatActivity.class));
    }

    public void goBackToStat(View v){
        startActivity(new Intent(this, EventStatActivity.class));
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
        price = getIntent().getDoubleExtra("price", 0);
        amountToPay.setText(getResources().getString(R.string.amount_to_pay, price));
        generateQrCode();
    }

}
