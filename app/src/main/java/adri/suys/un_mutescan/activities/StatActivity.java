package adri.suys.un_mutescan.activities;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.InputType;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.NoConnectionError;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Observable;
import java.util.Observer;

import adri.suys.un_mutescan.R;
import adri.suys.un_mutescan.apirest.RestStat;
import adri.suys.un_mutescan.model.Event;
import adri.suys.un_mutescan.model.User;

public class StatActivity extends Activity implements Observer {

    private Event currentEvent;
    private User currentUser;
    private TextView eventName, eventDate, totalTicket, soldTicket, scannedTicket, onSiteTicket;
    private Button scanBtn, backBtn, addTicketBtn;
    private RestStat restCommunication;
    private ProgressBar progressBar;

    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stat);
        configActionBar();
        restCommunication = new RestStat(this);
        currentEvent = (Event) getIntent().getSerializableExtra("event");
        currentUser = (User) getIntent().getSerializableExtra("user");
        setViewElements();
        displayEvent();
    }

    protected void onResume(){
        super.onResume();
        progressBar.setVisibility(View.GONE);
        currentEvent = (Event) getIntent().getSerializableExtra("event");
        displayEvent();
    }

    public void onBackPressed(){
        Intent i = new Intent(this, EventActivity.class);
        i.putExtra("user", currentUser);
        startActivity(i);
    }

    @Override
    public void update(Observable observable, Object o) {
        if (o instanceof NoConnectionError){
            String message = getResources().getString(R.string.no_connexion);
            Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
        } else {
            progressBar.setVisibility(View.GONE);
            currentEvent.sellTicketOnSite();
            Toast.makeText(this, R.string.ticket_added, Toast.LENGTH_SHORT).show();
        }
    }

    private void setViewElements(){
        eventName = findViewById(R.id.stat_event_name);
        eventDate = findViewById(R.id.stat_event_date);
        totalTicket = findViewById(R.id.stat_event_total_ticket);
        soldTicket = findViewById(R.id.stat_event_total_ticket_sold);
        scannedTicket = findViewById(R.id.stat_event_total_ticket_scanned);
        onSiteTicket = findViewById(R.id.stat_event_total_ticket_onsite);
        scanBtn = findViewById(R.id.stat_event_btn_scan);
        backBtn = findViewById(R.id.stat_event_btn_back);
        addTicketBtn = findViewById(R.id.stat_event_btn_add);
        progressBar = findViewById(R.id.progressBar_stat);
        progressBar.setVisibility(View.GONE);
        /*if (!currentEvent.isToday()){
            scanBtn.setVisibility(View.GONE);
            addTicketBtn.setVisibility(View.GONE);
        }*/
        setClickActions();
    }

    private void setClickActions() {
        scanBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(StatActivity.this, BarcodeActivity.class);
                intent.putExtra("event", currentEvent);
                startActivity(intent);
            }
        });
        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(StatActivity.this, EventActivity.class);
                i.putExtra("user", currentUser);
                startActivity(i);
            }
        });
        addTicketBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openAddTicketPopup();
            }
        });
    }

    private void displayEvent(){
        eventName.setText(currentEvent.getName());
        DateFormat df = new SimpleDateFormat("dd/MM/yyyy");
        eventDate.setText(df.format(currentEvent.getDate()));
        totalTicket.setText(getResources().getString(R.string.ticket_total) + " " + currentEvent.getNbTotalTicket());
        scannedTicket.setText(getResources().getString(R.string.ticket_scanned) + " " + currentEvent.getNbScannedTicket());
        soldTicket.setText(getResources().getString(R.string.ticket_sold) + " " + currentEvent.getNbSoldTicket());
        onSiteTicket.setText(getResources().getString(R.string.ticket_on_site) + " " + currentEvent.getNbTicketSoldOnSite());
    }

    private void openAddTicketPopup(){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(R.string.add_price);
        final EditText input = new EditText(this);
        input.setInputType(InputType.TYPE_CLASS_TEXT);
        builder.setView(input);
        builder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String price = input.getText().toString();
                addTicket(price);
            }
        });
        builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        builder.show();
    }

    private void addTicket(String price){
        int priceAsInt = Integer.parseInt(price);
        restCommunication.addTicket(currentEvent.getId(), priceAsInt);
        progressBar.setVisibility(View.VISIBLE);
    }
}
