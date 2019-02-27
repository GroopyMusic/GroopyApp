package adri.suys.un_mutescan.activities;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
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
import adri.suys.un_mutescan.dataholder.UnMuteDataHolder;
import adri.suys.un_mutescan.model.Event;

public class BarcodeScannerActivity extends Activity {

    private SurfaceView surfaceView;
    private TextView barcodeText;
    private BarcodeDetector detector;
    private CameraSource cameraSrc;
    private static final int REQUEST_CAMERA_PERMISSION = 201;
    private String intentData = "";
    private Event currentEvent;

    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_barcode_scanner);
        configActionBar();
        setViewElements();
        currentEvent = UnMuteDataHolder.getEvent();
    }

    @Override
    protected void onPause(){
        super.onPause();
        cameraSrc.release();
    }

    @Override
    protected void onResume(){
        super.onResume();
        currentEvent = UnMuteDataHolder.getEvent();
        initializeDetectorAndCameraSource();
    }

    @Override
    public void onBackPressed(){
        startActivity(new Intent(this, EventStatActivity.class));
    }

    /////////////////////
    // private methods //
    /////////////////////

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
                    if (ActivityCompat.checkSelfPermission(BarcodeScannerActivity.this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
                        cameraSrc.start(surfaceView.getHolder());
                    } else {
                        ActivityCompat.requestPermissions(BarcodeScannerActivity.this, new String[]{Manifest.permission.CAMERA}, REQUEST_CAMERA_PERMISSION);
                    }
                } catch (IOException ex){
                    ex.printStackTrace();
                    showToast(getResources().getString(R.string.sth_wrong));
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
                            Intent intent = new Intent(BarcodeScannerActivity.this, TicketInfosActivity.class);
                            intent.putExtra("barcode", intentData);
                            intent.putExtra("alert", true);
                            startActivity(intent);
                        }
                    });
                }
            }
        });
    }

}
