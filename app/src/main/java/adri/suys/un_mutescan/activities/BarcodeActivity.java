package adri.suys.un_mutescan.activities;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.SparseArray;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.vision.CameraSource;
import com.google.android.gms.vision.Detector;
import com.google.android.gms.vision.barcode.Barcode;
import com.google.android.gms.vision.barcode.BarcodeDetector;

import java.io.IOException;

import adri.suys.un_mutescan.R;
import adri.suys.un_mutescan.model.Event;

public class BarcodeActivity extends Activity {

    SurfaceView surfaceView;
    TextView barcodeText;
    private BarcodeDetector detector;
    private CameraSource cameraSrc;
    private static final int REQUEST_CAMERA_PERMISSION = 201;
    String intentData = "";
    Event currentEvent;

    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_barcode);
        configActionBar();
        setViewElements();
        currentEvent = (Event) getIntent().getSerializableExtra("event");
    }

    @Override
    protected void onPause(){
        super.onPause();
        cameraSrc.release();
    }

    @Override
    protected void onResume(){
        super.onResume();
        System.out.println("Je passe par ici");
        currentEvent = (Event) getIntent().getSerializableExtra("event");
        System.out.println(currentEvent);
        initializeDetectorAndCameraSource();
    }

    public void onBackPressed(){
        Intent intent = new Intent(this, StatActivity.class);
        intent.putExtra("event", currentEvent);
        startActivity(intent);
    }

    // private methods

    private void setViewElements() {
        surfaceView = findViewById(R.id.surfaceView);
        barcodeText = findViewById(R.id.txtBarcodeValue);
    }

    private void initializeDetectorAndCameraSource(){
        detector = new BarcodeDetector.Builder(this).setBarcodeFormats(Barcode.ALL_FORMATS).build();
        cameraSrc = new CameraSource.Builder(this, detector).setRequestedPreviewSize(1920, 1080).setAutoFocusEnabled(true).build();
        addCallbackToSurfaceView();
        setProcessor();
    }

    private void addCallbackToSurfaceView(){
        surfaceView.getHolder().addCallback(new SurfaceHolder.Callback() {
            @Override
            public void surfaceCreated(SurfaceHolder surfaceHolder) {
                try {
                    if (ActivityCompat.checkSelfPermission(BarcodeActivity.this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
                        cameraSrc.start(surfaceView.getHolder());
                    } else {
                        ActivityCompat.requestPermissions(BarcodeActivity.this, new String[]{Manifest.permission.CAMERA}, REQUEST_CAMERA_PERMISSION);
                    }
                } catch (IOException ex){
                    ex.printStackTrace();
                    String error = getResources().getString(R.string.error_barcode);
                    Toast.makeText(getApplicationContext(), error, Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void surfaceChanged(SurfaceHolder surfaceHolder, int i, int i1, int i2) {
                // do nothing
            }

            @Override
            public void surfaceDestroyed(SurfaceHolder surfaceHolder) {
                cameraSrc.stop();
            }
        });
    }

    private void setProcessor(){
        detector.setProcessor(new Detector.Processor<Barcode>() {
            @Override
            public void release() {
            }

            @Override
            public void receiveDetections(Detector.Detections<Barcode> detections) {
                final SparseArray<Barcode> barcodes = detections.getDetectedItems();
                if (barcodes.size() != 0){
                    barcodeText.post(new Runnable() {
                        @Override
                        public void run() {
                            intentData = barcodes.valueAt(0).displayValue;
                            barcodeText.setText(intentData);
                            Intent intent = new Intent(BarcodeActivity.this, TicketActivity.class);
                            intent.putExtra("barcode", intentData);
                            intent.putExtra("event", (Event) getIntent().getSerializableExtra("event"));
                            intent.putExtra("alert", true);
                            startActivity(intent);
                        }
                    });
                }
            }
        });
    }

}
