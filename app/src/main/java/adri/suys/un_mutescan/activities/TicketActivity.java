package adri.suys.un_mutescan.activities;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.NoConnectionError;

import java.util.Observable;
import java.util.Observer;

import adri.suys.un_mutescan.R;
import adri.suys.un_mutescan.apirest.RestTicket;
import adri.suys.un_mutescan.model.Event;
import adri.suys.un_mutescan.model.Ticket;

public class TicketActivity extends Activity implements Observer {

    private TextView barcode, name, ticketType, seatType, eventName, eventScanningStatus, ticketError;
    private Button backHomeBtn, backScanBtn;
    private Event currentEvent;
    private AlertDialog.Builder dialogBuilder;
    private RestTicket restCommunication;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ticket);
        configActionBar();
        restCommunication = new RestTicket(this);
        initViewElements();
        currentEvent = (Event) getIntent().getSerializableExtra("event");
        dialogBuilder = new AlertDialog.Builder(this);
    }

    @Override
    protected void onResume(){
        super.onResume();
        currentEvent = (Event) getIntent().getSerializableExtra("event");
        displayInfos();
    }

    public void onBackPressed(){
        Intent intent = new Intent(TicketActivity.this, StatActivity.class);
        intent.putExtra("event", currentEvent);
        startActivity(intent);
    }

    @Override
    public void update(Observable observable, Object o) {
        if (o instanceof NoConnectionError){
            String message = getResources().getString(R.string.no_connexion);
            Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
        } else {
            Ticket ticket = (Ticket) o;
            try {
                displayTicket(ticket);
                displayEvent();
                Boolean mustDisplayAlert = getIntent().getBooleanExtra("alert", false);
                if (mustDisplayAlert) {
                    displayAlert(ticket.getErrorMessage());
                }
            } catch (NullPointerException ex) {
                Toast.makeText(this, getResources().getString(R.string.sth_wrong), Toast.LENGTH_SHORT).show();
                Log.d("TICKET OU EVENT NULL", ex.getMessage());
            }
        }
    }

    // private methods

    private void initViewElements() {
        barcode = findViewById(R.id.ticket_barcode);
        name = findViewById(R.id.ticket_name);
        ticketType = findViewById(R.id.ticket_type_ticket);
        seatType = findViewById(R.id.ticket_type_seat);
        backHomeBtn = findViewById(R.id.back_home_btn);
        backScanBtn = findViewById(R.id.back_scan_btn);
        eventName = findViewById(R.id.event_name);
        ticketError = findViewById(R.id.ticket_error);
        setOnClickActions();
    }

    private void setOnClickActions() {
        backHomeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(TicketActivity.this, StatActivity.class);
                intent.putExtra("event", currentEvent);
                startActivity(intent);
            }
        });
        backScanBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(TicketActivity.this, BarcodeActivity.class);
                System.out.println("TicketActivity - avant d'envoyer l'évent");
                System.out.println(currentEvent);
                intent.putExtra("event", currentEvent);
                startActivity(intent);
            }
        });
    }

    private void displayInfos(){
        String barcodeValue = getIntent().getStringExtra("barcode");
        validateTicket(barcodeValue);
    }

    private void validateTicket(String barcodeValue){
        restCommunication.scanTicket(/*currentEvent.getId()*/3, barcodeValue);
    }

    private void displayEvent() {
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
            ticketError.setTextColor(getResources().getColor(R.color.green));
            barcode.setText(ticket.getBarcodeText());
            name.setText(ticket.getName());
            ticketType.setText(ticket.getTicketType());
            seatType.setText(ticket.getSeatType());
        } else {
            ticketError.setTextColor(getResources().getColor(R.color.red));
            ticketError.setText(ticket.getErrorMessage());
        }
    }

    private void displayAlert(String error){
        if (error.equals("")){
            dialogBuilder.setMessage(getResources().getString(R.string.ticket_ok)).setTitle("");
            currentEvent.scanTicket();
        } else {
            dialogBuilder.setMessage(error).setTitle("");
        }
        dialogBuilder.setCancelable(false)
                .setPositiveButton(R.string.back_to_scan, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.cancel();
                        Intent intent = new Intent(TicketActivity.this, BarcodeActivity.class);
                        intent.putExtra("event", currentEvent);
                        startActivity(intent);
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