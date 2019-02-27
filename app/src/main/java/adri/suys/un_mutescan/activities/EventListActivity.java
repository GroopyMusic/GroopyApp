package adri.suys.un_mutescan.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
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
import adri.suys.un_mutescan.apirest.RestEvents;
import adri.suys.un_mutescan.dataholder.UnMuteDataHolder;
import adri.suys.un_mutescan.model.Event;
import adri.suys.un_mutescan.model.User;

public class EventListActivity extends Activity implements Observer {

    private RecyclerView recyclerView;
    private EventAdapter adapter;
    private List<Event> events = new ArrayList<>();
    private User user;
    private RestEvents restCommunication;
    private ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event_list);
        configActionBar();
        progressBar = findViewById(R.id.progressBar_event);
        progressBar.setVisibility(View.VISIBLE);
        restCommunication = new RestEvents(this);
        recyclerView = findViewById(R.id.event_recycler_view);
        recyclerView.setHasFixedSize(true);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        user = UnMuteDataHolder.getUser();
        createEvents();
    }

    @Override
    protected void onResume(){
        super.onResume();
        progressBar.setVisibility(View.VISIBLE);
        user = UnMuteDataHolder.getUser();
        createEvents();
    }

    @SuppressWarnings("unchecked")
    @Override
    public void update(Observable observable, Object o) {
        if (o instanceof NoConnectionError){
            String message = getResources().getString(R.string.no_connexion);
            showToast(message);
        } else if (o instanceof String){
            String errorMsg = (String) o;
            showToast(errorMsg);
        } else {
            this.events = (List<Event>) o;
        }
        progressBar.setVisibility(View.GONE);
        updateEventsList();
    }

    @Override
    public void onBackPressed() {
        // do nothing
    }

    /////////////////////
    // private methods //
    /////////////////////

    private void updateEventsList() {
        if (adapter == null) {
            adapter = new EventAdapter(events);
            recyclerView.setAdapter(adapter);
        } else {
            adapter.setEvents(events);
            adapter.notifyDataSetChanged();
        }
    }

    private void createEvents() {
        restCommunication.collectEvents(user);
    }

    private class EventHolder extends RecyclerView.ViewHolder {

        private Event event;
        private TextView eventName;
        private Button statBtn;

        EventHolder(LayoutInflater inflater, ViewGroup parent) {
            super(inflater.inflate(R.layout.event_item, parent, false));
            initViewElements();
        }

        void bind(Event event) {
            this.event = event;
            eventName.setText(event.getName());
        }

        private void initViewElements() {
            eventName = itemView.findViewById(R.id.event_info_name);
            statBtn = itemView.findViewById(R.id.event_info_stat);
            setClickActions();
        }

        void setClickActions() {
            statBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(EventListActivity.this, EventStatActivity.class);
                    UnMuteDataHolder.setEvent(event);
                    startActivity(intent);
                }
            });
        }

    }

    private class EventAdapter extends RecyclerView.Adapter<EventHolder> {

        private List<Event> events;

        EventAdapter(List<Event> events) {
            this.events = events;
        }

        @NonNull
        @Override
        public EventHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
            LayoutInflater layoutInflater = LayoutInflater.from(EventListActivity.this);
            return new EventHolder(layoutInflater, viewGroup);
        }

        @Override
        public void onBindViewHolder(@NonNull EventHolder eventHolder, int i) {
            Event event = events.get(i);
            eventHolder.bind(event);
        }

        @Override
        public int getItemCount() {
            return events.size();
        }

        void setEvents(List<Event> events) {
            this.events = events;
        }
    }

}
