package adri.suys.un_mutescan.activities;

import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ProgressBar;

import java.util.List;
import java.util.Objects;

import adri.suys.un_mutescan.R;
import adri.suys.un_mutescan.apirest.RestService;
import adri.suys.un_mutescan.model.Event;
import adri.suys.un_mutescan.presenter.EventPresenter;
import adri.suys.un_mutescan.viewinterfaces.EventListViewInterface;
import adri.suys.un_mutescan.viewinterfaces.EventRowViewInterface;

public class EventListActivity extends Activity implements EventListViewInterface{

    private RecyclerView recyclerView;
    private EventAdapter adapter;
    private ProgressBar progressBar;
    private EventPresenter presenter;
    private SearchView searchView;
    private SwipeRefreshLayout swipeRefreshLayout;
    private RestService restCommunication;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event_list);
        configActionBar();
        setElements();
        createEvents(false);
        handleSearch();
        handlePullToRefresh();
    }

    @Override
    public void onBackPressed() {
        // do nothing
    }

    /**
     * Hide the ProgressBar
     */
    public void hideProgressBar(){
        progressBar.setVisibility(View.GONE);
    }

    /**
     * Update the adapter with the current state of the Presenter
     * @param isFilteredList a boolean indicating if we must pass the filtered list or
     *                       the normal list to the adapter
     */
    public void updateEventsList(boolean isFilteredList) {
        if (adapter == null) {
            adapter = new EventAdapter(presenter, isFilteredList);
            recyclerView.setAdapter(adapter);
        } else {
            adapter.setPresenter(presenter);
            adapter.setFilteredList(isFilteredList);
            adapter.notifyDataSetChanged();
        }
    }

    @Override
    public void showNoConnectionRetryToast() {
        showToast(getResources().getString(R.string.volley_error_no_connexion));
    }

    @Override
    public void showServerConnectionProblemToast() {
        showToast(getResources().getString(R.string.volley_error_server_error));
    }

    @Override
    public void collectEventsInDB(int id) {
        restCommunication.collectEvents(id);
    }

    public EventPresenter getPresenter() {
        return presenter;
    }

    /////////////////////
    // private methods //
    /////////////////////

    private void createEvents(boolean forceRefresh) {
        presenter.collectEvents(forceRefresh, isInternetConnected());
    }

    private void handleSearch(){
        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        searchView.setSearchableInfo(Objects.requireNonNull(searchManager).getSearchableInfo(getComponentName()));
        searchView.setQueryHint(getResources().getString(R.string.search_hint));
        searchView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                searchView.onActionViewExpanded();
            }
        });
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String query) {
                adapter.getFilter().filter(query);
                return false;
            }
        });
    }

    private void setElements(){
        progressBar = findViewById(R.id.progressBar_event);
        progressBar.setVisibility(View.VISIBLE);
        searchView = findViewById(R.id.searchview);
        searchView.setImeOptions(EditorInfo.IME_ACTION_DONE);
        swipeRefreshLayout = findViewById(R.id.swipe_refresh_layout);
        presenter = new EventPresenter(this);
        restCommunication = new RestService(this);
        restCommunication.setEventPresenter(presenter);
        recyclerView = findViewById(R.id.event_recycler_view);
        recyclerView.setHasFixedSize(true);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
    }

    private void handlePullToRefresh(){
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                createEvents(true);
                swipeRefreshLayout.setRefreshing(false);
            }
        });
    }

    ////////////
    // HOLDER //
    ////////////

    public class EventHolder extends RecyclerView.ViewHolder implements EventRowViewInterface {

        private Button eventBtn;

        EventHolder(LayoutInflater inflater, ViewGroup parent) {
            super(inflater.inflate(R.layout.item_event, parent, false));
            initViewElements();
        }

        /**
         * Displays the event name
         * @param name the name of the event
         */
        public void setEventName(String name){
            eventBtn.setText(name);
        }

        @Override
        public void setPastEventName(String eventName) {
            this.eventBtn.setText(getResources().getString(R.string.already_passed) + eventName);
        }

        private void initViewElements() {
            eventBtn = itemView.findViewById(R.id.event);
            setClickActions();
        }

        /**
         * When the user click on the STATS button, it changes the screen and the screen that
         * contains the stat of the event is displayed.
         */
        void setClickActions() {
            eventBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    myAction();
                }
            });
        }

        private void myAction(){
            int currentPosition = this.getAdapterPosition();
            presenter.persistEvent(currentPosition);
            Intent intent = new Intent(EventListActivity.this, OneEventActivity.class);
            startActivity(intent);
        }

        public void setEventNameInRed() {
            eventBtn.setTextColor(Color.RED);
        }

        public void setEventNameInGreen(){
            eventBtn.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.reef_encounter));
        }
    }

    /////////////
    // ADAPTER //
    /////////////

    @SuppressWarnings("unchecked")
    private class EventAdapter extends RecyclerView.Adapter<EventHolder> implements Filterable {

        private EventPresenter presenter;
        private boolean isFilteredList;

        EventAdapter(EventPresenter presenter, boolean isFilteredList) {
            this.presenter = presenter;
            this.isFilteredList = isFilteredList;
        }

        @NonNull
        @Override
        public EventHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
            LayoutInflater layoutInflater = LayoutInflater.from(EventListActivity.this);
            return new EventHolder(layoutInflater, viewGroup);
        }

        @Override
        public void onBindViewHolder(@NonNull EventHolder eventHolder, int i) {
            presenter.onViewEventAtPosition(i, eventHolder, isFilteredList);
        }

        @Override
        public int getItemCount() {
            return presenter.getItemCount(isFilteredList);
        }

        void setPresenter(EventPresenter presenter) {
            this.presenter = presenter;
        }

        void setFilteredList(boolean filteredList) {
            isFilteredList = filteredList;
        }

        @Override
        public Filter getFilter() {
            return new Filter() {
                @Override
                protected FilterResults performFiltering(CharSequence charSequence) {
                    String pattern = charSequence.toString().toLowerCase().trim();
                    FilterResults filterResults = new FilterResults();
                    filterResults.values = presenter.getFilteredResult(pattern);
                    return filterResults;
                }
                @Override
                protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
                    List<Event> filteredEvents = (List<Event>) filterResults.values;
                    presenter.notifyChanged(filteredEvents);
                }
            };
        }
    }

}
