package adri.suys.un_mutescan.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Locale;

import adri.suys.un_mutescan.R;
import adri.suys.un_mutescan.dataholder.UnMuteDataHolder;
import adri.suys.un_mutescan.model.Event;

public class EventStatActivity extends Activity {

    private Event currentEvent;
    private TextView eventName, eventDate, totalTicket, soldTicket, scannedTicket, onSiteTicket;
    private ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event_stat);
        configActionBar();
        currentEvent = UnMuteDataHolder.getEvent();
        setViewElements();
        displayEvent();
    }

    @Override
    protected void onResume(){
        super.onResume();
        progressBar.setVisibility(View.GONE);
        currentEvent = UnMuteDataHolder.getEvent();
        displayEvent();
    }

    @Override
    public void onBackPressed(){
        startActivity(new Intent(this, EventListActivity.class));
    }

    /**
     * Handle a pressure on the back button -> goes to the previous screen
     * @param v the view the Activity is linked to
     */
    public void goBack(View v){
        startActivity(new Intent(this, EventListActivity.class));
    }

    /**
     * Open the scanner in order to scan qr-code
     * @param v the view the Activity is linked to
     */
    public void openScan(View v){
        startActivity(new Intent(this, BarcodeScannerActivity.class));
    }

    /**
     * Generate a ticket in the database. Used when a customer come to the event without a presale ticket.
     * @param v the view the Activity is linked to
     */
    public void generateTicketOnSite(View v){
        startActivity(new Intent(this, BuyTicketOnSiteActivity.class));
    }

    /////////////////////
    // private methods //
    /////////////////////

    private void setViewElements(){
        eventName = findViewById(R.id.stat_event_name);
        eventDate = findViewById(R.id.stat_event_date);
        totalTicket = findViewById(R.id.stat_event_total_ticket);
        soldTicket = findViewById(R.id.stat_event_total_ticket_sold);
        scannedTicket = findViewById(R.id.stat_event_total_ticket_scanned);
        onSiteTicket = findViewById(R.id.stat_event_total_ticket_onsite);
        progressBar = findViewById(R.id.progressBar_stat);
        progressBar.setVisibility(View.GONE);
        Button scanBtn = findViewById(R.id.stat_event_btn_scan);
        Button addTicketBtn = findViewById(R.id.stat_event_btn_add);
        if (!currentEvent.isToday()){
            scanBtn.setVisibility(View.GONE);
            addTicketBtn.setVisibility(View.GONE);
        }
    }

    private void displayEvent(){
        eventName.setText(currentEvent.getName());
        DateFormat df = new SimpleDateFormat("dd/MM/yyyy", Locale.FRANCE);
        eventDate.setText(df.format(currentEvent.getDate()));
        totalTicket.setText(getResources().getString(R.string.ticket_total, currentEvent.getNbTotalTicket()));
        scannedTicket.setText(getResources().getString(R.string.ticket_scanned, currentEvent.getNbScannedTicket()));
        soldTicket.setText(getResources().getString(R.string.ticket_sold, currentEvent.getNbSoldTicket()));
        onSiteTicket.setText(getResources().getString(R.string.ticket_on_site, currentEvent.getNbTicketSoldOnSite()));
    }
}
