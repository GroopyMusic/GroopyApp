package adri.suys.un_mutescan.presenter;

import android.support.test.espresso.idling.CountingIdlingResource;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkError;
import com.android.volley.NoConnectionError;
import com.android.volley.ParseError;
import com.android.volley.ServerError;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import adri.suys.un_mutescan.R;
import adri.suys.un_mutescan.activities.EventListActivity;
import adri.suys.un_mutescan.apirest.RestService;
import adri.suys.un_mutescan.dataholder.UnMuteDataHolder;
import adri.suys.un_mutescan.model.Event;
import adri.suys.un_mutescan.model.User;

public class EventPresenter {

    private List<Event> events = new ArrayList<>();
    private List<Event> filteredEvents = new ArrayList<>();
    private User user;
    private Event currentEvent;
    private EventListActivity view;
    private RestService restCommunication;
    private CountingIdlingResource countingIdlingResource = new CountingIdlingResource("name");

    public EventPresenter(EventListActivity view){
        this.view = view;
        user = UnMuteDataHolder.getUser();
        restCommunication = new RestService(view);
        restCommunication.setEventPresenter(this);
    }

    public void onViewEventAtPosition(int position, EventListActivity.EventHolder view, boolean isFilteredList){
        if (isFilteredList){
            this.currentEvent = filteredEvents.get(position);
        } else {
            this.currentEvent = events.get(position);
        }
        view.setEventName(currentEvent.getName());
    }

    public void persistEvent(){
        UnMuteDataHolder.setEvent(currentEvent);
    }

    public int getItemCount(boolean isFiltered){
        if (isFiltered) {
            return filteredEvents.size();
        } else {
            return events.size();
        }
    }

    public void collectEvents() {
        countingIdlingResource.increment();
        restCommunication.collectEvents(user.getId());
    }

    public void handleVolleyError(VolleyError error){
        String message = "";
        if (error instanceof NoConnectionError || error instanceof TimeoutError){
            message = view.getResources().getString(R.string.volley_error_no_connexion);
        } else if (error instanceof AuthFailureError){
            message = view.getResources().getString(R.string.volley_error_server_error);
        } else if (error instanceof ServerError){
            message = view.getResources().getString(R.string.volley_error_server_error);
        } else if (error instanceof NetworkError) {
            message = view.getResources().getString(R.string.volley_error_server_error);
        } else if (error instanceof ParseError){
            message = view.getResources().getString(R.string.volley_error_server_error);
        }
        view.showToast(message);
        view.hideProgressBar();
        countingIdlingResource.decrement();
    }

    public void handleJSONArray(JSONArray response) {
        try {
            events = getEventsFromJSON(response);
            Collections.sort(events, new Comparator<Event>() {
                @Override
                public int compare(Event event, Event t1) {
                    if (event.getDate() != null && t1.getDate() != null){
                        return event.getDate().compareTo(t1.getDate());
                    }
                    return 0;
                }
            });
        } catch (JSONException e) {
            try {
                String error = ((JSONObject) response.get(0)).getString("error");
                view.showToast(error);
            } catch (JSONException e1) {
                e1.printStackTrace();
            }
            e.printStackTrace();
        } catch (ParseException e) {
            e.printStackTrace();
        }
        view.hideProgressBar();
        view.updateEventsList(false);
        countingIdlingResource.decrement();
    }

    private List<Event> getEventsFromJSON(JSONArray response) throws JSONException, ParseException {
        List<Event> events = new ArrayList<>();
        JSONObject[] array = new JSONObject[response.length()];
        for (int i = 0; i < response.length(); i++){
            array[i] = (JSONObject) response.get(i);
        }
        for (JSONObject jsonObject : array){
            int id = jsonObject.getInt("id");
            String name = jsonObject.getString("name");
            int nbTotalTicket = jsonObject.getInt("nbTotalTicket");
            int nbScannedTicket = jsonObject.getInt("nbScannedTicket");
            int nbSoldTicket = jsonObject.getInt("nbSoldTicket");
            int nbTicketSoldOnSite = jsonObject.getInt("nbBoughtOnSiteTicket");
            int nbTicketPaidInCash = jsonObject.getInt("nbTicketBoughtInCash");
            if (jsonObject.get("date") instanceof String){
                events.add(new Event(id, name, nbTotalTicket, nbScannedTicket, nbSoldTicket, nbTicketSoldOnSite, null, nbTicketPaidInCash));
            } else {
                String dateStr = ((JSONObject) jsonObject.get("date")).getString("date");
                dateStr = dateStr.substring(0, dateStr.length() - 7);
                Date date = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.FRANCE).parse(dateStr);
                events.add(new Event(id, name, nbTotalTicket, nbScannedTicket, nbSoldTicket, nbTicketSoldOnSite, date, nbTicketPaidInCash));
            }
        }
        return events;
    }

    public List<Event> getFilteredResult(String pattern) {
        List<Event> filteredEvents = new ArrayList<>();
        if (pattern.isEmpty()){
            filteredEvents = events;
        } else {
            for (Event event : events){
                if (event.getName().toLowerCase().contains(pattern.toLowerCase())){
                    filteredEvents.add(event);
                }
            }
        }
        return filteredEvents;
    }

    public void notifyChanged(List<Event> filteredEvents) {
        this.filteredEvents = filteredEvents;
        view.updateEventsList(true);
    }

    public CountingIdlingResource getCountingIdlingResource() {
        return countingIdlingResource;
    }
}
