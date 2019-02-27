package adri.suys.un_mutescan.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

import adri.suys.un_mutescan.R;
import adri.suys.un_mutescan.dataholder.UnMuteDataHolder;
import adri.suys.un_mutescan.model.Event;

public class BarcodeManualInputActivity extends Activity {

    private EditText input;
    private Event currentEvent;

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_barcode_manually_input);
        configActionBar();
        input = findViewById(R.id.input_barcode_value);
        currentEvent = UnMuteDataHolder.getEvent();
    }

    @Override
    protected void onResume(){
        super.onResume();
        currentEvent = (Event) getIntent().getSerializableExtra("event");
    }

    public void askForTicketValidation(View v){
        if (input.getText().toString().equals("")){
            showToast(getResources().getString(R.string.no_barcode_input));
        } else {
            String barcodeValue = input.getText().toString();
            Intent intent = new Intent(this, TicketInfosActivity.class);
            intent.putExtra("barcode", barcodeValue);
            intent.putExtra("alert", true);
            startActivity(intent);
        }
    }

    public void goBack(View v){
        startActivity(new Intent(this, EventStatActivity.class));
    }

}
