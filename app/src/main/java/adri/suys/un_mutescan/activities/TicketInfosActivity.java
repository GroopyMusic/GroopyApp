package adri.suys.un_mutescan.activities;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.android.volley.NoConnectionError;

import java.util.Observable;
import java.util.Observer;

import adri.suys.un_mutescan.R;
import adri.suys.un_mutescan.apirest.RestTicket;
import adri.suys.un_mutescan.dataholder.UnMuteDataHolder;
import adri.suys.un_mutescan.model.Event;
import adri.suys.un_mutescan.model.Ticket;

public class TicketInfosActivity extends Activity implements Observer {

    private TextView barcode, name, ticketType, seatType, eventName, ticketError;
    private Event currentEvent;
    private AlertDialog.Builder dialogBuilder;
    private RestTicket restCommunication;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ticket_infos);
        configActionBar();
        restCommunication = new RestTicket(this);
        initViewElements();
        currentEvent = UnMuteDataHolder.getEvent();
        dialogBuilder = new AlertDialog.Builder(this);
    }

    @Override
    protected void onResume(){
        super.onResume();
        currentEvent = UnMuteDataHolder.getEvent();
        displayInfos();
    }

    @Override
    public void onBackPressed(){
        startActivity(new Intent(this, EventStatActivity.class));
    }

    @Override
    public void update(Observable observable, Object o) {
        if (o instanceof NoConnectionError){
            String message = getResources().getString(R.string.no_connexion);
            showToast(message);
        } else {
            Ticket ticket = (Ticket) o;
            try {
                displayTicket(ticket);
                displayEventName();
                Boolean mustDisplayAlert = getIntent().getBooleanExtra("alert", false);
                if (mustDisplayAlert) {
                    displayAlert(getResources().getString(ticket.getErrorMessageAsResource()));
                }
            } catch (NullPointerException ex) {
                showToast(getResources().getString(R.string.sth_wrong));
            }
        }
    }

    /**
     * Handle a pressure on the back button -> goes to the previous screen
     * @param v the view the Activity is linked to
     */
    public void goBack(View v){
        startActivity(new Intent(this, EventStatActivity.class));
    }

    /**
     * Open the scanner in order to scan qr-code
     * @param v the view the Activity is linked to
     */
    public void openScan(View v){
        startActivity(new Intent(this, BarcodeScannerActivity.class));
    }

    /////////////////////
    // private methods //
    /////////////////////

    private void initViewElements() {
        barcode = findViewById(R.id.ticket_barcode);
        name = findViewById(R.id.ticket_name);
        ticketType = findViewById(R.id.ticket_type_ticket);
        seatType = findViewById(R.id.ticket_type_seat);
        eventName = findViewById(R.id.event_name);
        ticketError = findViewById(R.id.ticket_error);
    }

    private void displayInfos(){
        String barcodeValue = getIntent().getStringExtra("barcode");
        validateTicket(barcodeValue);
    }

    private void validateTicket(String barcodeValue){
        restCommunication.scanTicket(currentEvent.getId(), barcodeValue);
    }

    private void displayEventName() {
        if (currentEvent == null){
            throw new NullPointerException("L'événement n'existe pas");
        }
        eventName.setText(currentEvent.getName());
    }

    private void displayTicket(Ticket ticket) {
        if (ticket == null){
            throw new NullPointerException("Le ticket n'existe pas");
        }
        if (ticket.getErrorMessage().equals("")){
            ticketError.setText(getResources().getString(R.string.ticket_ok_dialog));
            int green = ContextCompat.getColor(this, R.color.green);
            ticketError.setTextColor(green);
            barcode.setText(ticket.getBarcodeText());
            name.setText(ticket.getName());
            ticketType.setText(ticket.getTicketType());
            seatType.setText(ticket.getSeatType());
            currentEvent.scanTicket(); // nb of scanned tix + 1
        } else {
            int red = ContextCompat.getColor(this, R.color.red);
            ticketError.setTextColor(red);
            ticketError.setText(ticket.getErrorMessageAsResource());
        }
    }

    private void displayAlert(String error){
        dialogBuilder.setMessage(error).setTitle("");
        if (error.equals(getResources().getString(R.string.ticket_ok_dialog))){
            currentEvent.scanTicket();
        }
        dialogBuilder.setCancelable(false)
                .setPositiveButton(R.string.back_to_scan, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.cancel();
                        startActivity(new Intent(TicketInfosActivity.this, BarcodeScannerActivity.class));
                    }
                })
                .setNegativeButton(R.string.see_more, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.cancel();
                    }
                });
        AlertDialog alert = dialogBuilder.create();
        alert.setTitle("");
        alert.show();
    }
}