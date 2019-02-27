package adri.suys.un_mutescan.activities;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.android.volley.NoConnectionError;

import java.util.ArrayList;
import java.util.List;
import java.util.Observable;
import java.util.Observer;

import adri.suys.un_mutescan.R;
import adri.suys.un_mutescan.apirest.RestCounterpart;
import adri.suys.un_mutescan.dataholder.UnMuteDataHolder;
import adri.suys.un_mutescan.model.Event;
import adri.suys.un_mutescan.model.Counterpart;

public class BuyTicketOnSiteActivity extends Activity implements Observer {

    private ProgressBar progressBar;
    private RecyclerView recyclerView;
    private Event event;
    private RestCounterpart restCommunication;
    private List<Counterpart> counterparts = new ArrayList<>();
    private TicketTypeAdapter adapter;
    private double cartAmount = 0;
    private int totalTicketSold = 0;
    private static final String BANCONTACT_PACKAGE_NAME = "mobi.inthepocket.bcmc.bancontact";

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_buy_ticket_onsite);
        configActionBar();
        progressBar = findViewById(R.id.progressBar_ticket_type);
        progressBar.setVisibility(View.VISIBLE);
        restCommunication = new RestCounterpart(this);
        recyclerView = findViewById(R.id.ticket_type_recycler_view);
        recyclerView.setHasFixedSize(true);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        event = UnMuteDataHolder.getEvent();
        getTicketTypes();
    }

    @Override
    protected void onResume(){
        super.onResume();
        progressBar.setVisibility(View.VISIBLE);
        event = UnMuteDataHolder.getEvent();
        getTicketTypes();
    }

    @Override
    public void update(Observable observable, Object o) {
        if (o instanceof NoConnectionError){
            String message = getResources().getString(R.string.no_connexion);
            showToast(message);
        } else if (o instanceof String){
            String errorMsg = (String) o;
            showToast(errorMsg);
        } else if (o instanceof List) {
            // si ce qu'on recoit de l'API est une List<Counterpart>

            counterparts.clear();
            for (Object obj : (List) o){
                if (obj instanceof Counterpart){
                    counterparts.add((Counterpart) obj);
                }
            }
            event.setCounterparts(counterparts);
        } else {
            // si ce n'est ni une chaine, ni une list de counterpart

            showToast(getResources().getString(R.string.ticket_added));
            Intent i = new Intent(this, PayActivity.class);
            i.putExtra("price", cartAmount);
            startActivity(i);
        }
        progressBar.setVisibility(View.GONE);
        updateTicketTypesList();
    }

    public void validate(View v){
        if (cartAmount > 0){
            String message = getResources().getString(R.string.order_recap, totalTicketSold, cartAmount);
            AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
            dialogBuilder.setMessage(message).setTitle("");
            dialogBuilder.setCancelable(false)
                    .setPositiveButton(R.string.validate, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            dialogInterface.cancel();
                            restCommunication.addTicket(0, 0);

                        }
                    })
                    .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
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

    /////////////////////
    // private methods //
    /////////////////////

    private void getTicketTypes(){
        restCommunication.collectTicketType(event);
    }

    private void updateTicketTypesList() {
        if (adapter == null) {
            adapter = new BuyTicketOnSiteActivity.TicketTypeAdapter(counterparts);
            recyclerView.setAdapter(adapter);
        } else {
            adapter.setCounterparts(counterparts);
            adapter.notifyDataSetChanged();
        }
    }

    private class TicketTypeHolder extends RecyclerView.ViewHolder {

        private TextView ticketName, ticketPrice, numberOfTicketWanted;
        private Button minusBtn, plusBtn;
        private Counterpart counterpart;

        public TicketTypeHolder(LayoutInflater inflater, ViewGroup parent){
            super(inflater.inflate(R.layout.counterpart_item, parent, false));
            initViewElements();
        }

        void bind(Counterpart counterpart){
            this.counterpart = counterpart;
            ticketName.setText(counterpart.getName());
            ticketPrice.setText(Double.toString(counterpart.getPrice()));
        }

        private void initViewElements(){
            ticketName = findViewById(R.id.item_ticket_name);
            ticketPrice = findViewById(R.id.item_ticket_price);
            numberOfTicketWanted = findViewById(R.id.item_ticket_nb_purchases);
            minusBtn = findViewById(R.id.item_ticket_minus_btn);
            plusBtn = findViewById(R.id.item_ticket_plus_btn);
            setClickActions();
        }

        private void setClickActions(){
            minusBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    int currentNbOfPurchase = Integer.parseInt(numberOfTicketWanted.getText().toString());
                    if (cartAmount == 0 && currentNbOfPurchase == 0){
                        // on ne peut pas aller en-dessous de 0
                    } else {
                        cartAmount -= counterpart.getPrice();
                        currentNbOfPurchase--;
                        numberOfTicketWanted.setText(Integer.toString(currentNbOfPurchase));
                        totalTicketSold--;
                        //TODO counterpart.removeTicket();
                    }
                }
            });
            plusBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    int currentNbOfPurchase = Integer.parseInt(numberOfTicketWanted.getText().toString());
                    if (event.getRemainingTicketToBeSold() > currentNbOfPurchase){
                        currentNbOfPurchase ++;
                        cartAmount += counterpart.getPrice();
                        numberOfTicketWanted.setText(Integer.toString(currentNbOfPurchase));
                        totalTicketSold++;
                        //TODO counterpart.addTicket();
                    } else {
                        // on n'a plus rien à vendre
                    }
                }
            });
        }
    }

    private class TicketTypeAdapter extends RecyclerView.Adapter<TicketTypeHolder> {

        private List<Counterpart> counterparts;

        TicketTypeAdapter(List<Counterpart> counterparts){
            this.counterparts = counterparts;
        }

        @NonNull
        @Override
        public TicketTypeHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
            LayoutInflater layoutInflater = LayoutInflater.from(BuyTicketOnSiteActivity.this);
            return new TicketTypeHolder(layoutInflater, viewGroup);
        }

        @Override
        public void onBindViewHolder(@NonNull TicketTypeHolder ticketTypeHolder, int i) {
            Counterpart counterpart = counterparts.get(i);
            ticketTypeHolder.bind(counterpart);
        }

        @Override
        public int getItemCount() {
            return counterparts.size();
        }

        public void setCounterparts(List<Counterpart> counterparts) {
            this.counterparts = counterparts;
        }
    }

}
