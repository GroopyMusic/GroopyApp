package adri.suys.un_mutescan.activities;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import adri.suys.un_mutescan.R;
import adri.suys.un_mutescan.presenter.TicketInfosPresenter;
import adri.suys.un_mutescan.utils.UnMuteDataHolder;

public class TicketInfosActivity extends Activity {

    private TextView barcode, name, ticketType, seatType, eventName, ticketError;
    private AlertDialog.Builder dialogBuilder;
    private TicketInfosPresenter presenter;
    private ProgressBar progressBar;
    private LinearLayout frame;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ticket_infos);
        configActionBar();
        initViewElements();
        presenter = new TicketInfosPresenter(this);
        dialogBuilder = new AlertDialog.Builder(this, R.style.AppCompatAlertDialogStyle);
        displayInfos();
    }

    @Override
    public void onBackPressed(){
        startActivity(new Intent(this, OneEventActivity.class));
    }

    /**
     * Handle a pressure on the back button -> goes to the previous screen
     * @param v the view the Activity is linked to
     */
    public void goBack(View v){
        startActivity(new Intent(this, OneEventActivity.class));
    }

    /**
     * Displays the name of the event
     * @param eventName the name of the event
     */
    public void displayEventName(String eventName) {
        this.eventName.setText(eventName);
    }

    public void displayTicket(boolean isValid, String message, String barcodeText, String name, String ticketType, String seatType) {
        frame.setBackgroundResource(R.drawable.dark_green_border);
        barcode.setText(barcodeText);
        this.name.setText(name);
        this.ticketType.setText(ticketType);
        this.seatType.setText(seatType);
        if (isValid){
            int green = ContextCompat.getColor(this, R.color.green);
            ticketError.setTextColor(green);
            ticketError.setText(message);
        } else {
            int red = ContextCompat.getColor(this, R.color.red);
            ticketError.setTextColor(red);
            ticketError.setText(message);
        }
    }

    public void displayTicketUnknwn(String message, String barcodeText){
        frame.setBackgroundResource(R.drawable.dark_green_border);
        int red = ContextCompat.getColor(this, R.color.red);
        ticketError.setTextColor(red);
        ticketError.setText(message);
        barcode.setText(barcodeText);
    }

    /**
     * When a ticket is scanned, a popup opens saying if the ticket is valid or not
     * The user can then choose between going back to the scan or see more infos on the ticket
     * @param message the message indicating if the ticket is valid or not
     */
    public void displayAlert(final String message){
        dialogBuilder.setMessage(message).setTitle("");
        dialogBuilder.setCancelable(false)
                .setPositiveButton(R.string.back_to_scan, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.cancel();
                        if (message.equals(getResources().getString(R.string.ticket_ok_dialog))){
                            presenter.updateDB();
                        }
                        startActivity(new Intent(TicketInfosActivity.this, OneEventActivity.class));
                    }
                })
                .setNegativeButton(R.string.see_more, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.cancel();
                        if (message.equals(getResources().getString(R.string.ticket_ok_dialog))){
                            presenter.updateDB();
                        }
                    }
                });
        AlertDialog alert = dialogBuilder.create();
        alert.setTitle("");
        alert.show();
    }

    /**
     * Hide progress bar
     */
    public void hideProgressBar(){
        progressBar.setVisibility(View.GONE);
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
        progressBar = findViewById(R.id.ticket_progress_bar);
        frame = findViewById(R.id.frame_ticket);
        frame.setBackgroundResource(0);
    }

    private void displayInfos(){
        String barcodeValue = getIntent().getStringExtra("barcode");
        presenter.validateBarcode(barcodeValue);
    }


}