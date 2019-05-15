package adri.suys.un_mutescan.presenter;

import android.support.annotation.NonNull;
import android.support.test.espresso.idling.CountingIdlingResource;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkError;
import com.android.volley.NoConnectionError;
import com.android.volley.ParseError;
import com.android.volley.ServerError;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import adri.suys.un_mutescan.R;
import adri.suys.un_mutescan.activities.EventListActivity;
import adri.suys.un_mutescan.apirest.RestService;
import adri.suys.un_mutescan.model.Counterpart;
import adri.suys.un_mutescan.model.Ticket;
import adri.suys.un_mutescan.utils.UnMuteDataHolder;
import adri.suys.un_mutescan.model.Event;
import adri.suys.un_mutescan.model.User;
import adri.suys.un_mutescan.viewinterfaces.EventListViewInterface;
import adri.suys.un_mutescan.viewinterfaces.EventRowViewInterface;

public class EventPresenter {

    private List<Event> events = new ArrayList<>();
    private List<Event> filteredEvents = new ArrayList<>();
    private final User user;
    private Event currentEvent;
    private final EventListViewInterface view;
    private final RestService restCommunication;
    private final CountingIdlingResource countingIdlingResource = new CountingIdlingResource("name");
    private boolean isFiltered;

    public EventPresenter(EventListActivity view){
        this.view = view;
        user = UnMuteDataHolder.getUser();
        restCommunication = new RestService(view);
        restCommunication.setEventPresenter(this);
    }

    public void onViewEventAtPosition(int position, EventRowViewInterface view, boolean isFilteredList){
        isFiltered = isFilteredList;
        if (isFilteredList){
            this.currentEvent = filteredEvents.get(position);
        } else {
            this.currentEvent = events.get(position);
        }
        if (!currentEvent.isToday() && currentEvent.isPassed()){
            view.setPastEventName(currentEvent.getName());
            view.setEventNameInRed();
        } else {
            view.setEventNameInGreen();
            view.setEventName(currentEvent.getName());
        }
    }

    public void persistEvent(int position){
        if (isFiltered){
            currentEvent = filteredEvents.get(position);
        } else {
            currentEvent = events.get(position);
        }
        UnMuteDataHolder.setEvent(currentEvent);
    }

    public int getItemCount(boolean isFiltered){
        if (isFiltered) {
            return filteredEvents.size();
        } else {
            return events.size();
        }
    }

    public void collectEvents(boolean forceRefresh, boolean isInternetConnected) {
        countingIdlingResource.increment();
        if (UnMuteDataHolder.getEvent() != null){
            if (forceRefresh){
                fetchEventsInDB(isInternetConnected);
            } else {
                events = UnMuteDataHolder.getEvents();
                view.hideProgressBar();
                view.updateEventsList(false);
                countingIdlingResource.decrement();
            }
        } else {
            fetchEventsInDB(isInternetConnected);
        }
    }

    public void handleVolleyError(VolleyError error){
        if (error instanceof NoConnectionError || error instanceof TimeoutError){
            view.showNoConnectionRetryToast();
        } else if (error instanceof AuthFailureError){
            view.showServerConnectionProblemToast();
        } else if (error instanceof ServerError){
            view.showServerConnectionProblemToast();
        } else if (error instanceof NetworkError) {
            view.showServerConnectionProblemToast();
        } else if (error instanceof ParseError){
            view.showServerConnectionProblemToast();
        }
        view.hideProgressBar();
        countingIdlingResource.decrement();
    }

    public void handleJSONArray(JSONArray response) {
        try {
            events = getEventsFromJSON(response);
            UnMuteDataHolder.setEvents(events);
            view.backUpEvents();
            events = UnMuteDataHolder.getEvents();
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
            Event e;
            int id = jsonObject.getInt("id");
            String name = jsonObject.getString("name");
            int nbTotalTicket = jsonObject.getInt("nbTotalTicket");
            int nbScannedTicket = jsonObject.getInt("nbScannedTicket");
            int nbSoldTicket = jsonObject.getInt("nbSoldTicket");
            int nbTicketSoldOnSite = jsonObject.getInt("nbBoughtOnSiteTicket");
            int nbTicketPaidInCash = jsonObject.getInt("nbTicketBoughtInCash");
            if (jsonObject.get("date") instanceof String){
                e = new Event(id, name, nbTotalTicket, nbScannedTicket, nbSoldTicket, nbTicketSoldOnSite, null, nbTicketPaidInCash);
            } else {
                String dateStr = ((JSONObject) jsonObject.get("date")).getString("date");
                dateStr = dateStr.substring(0, dateStr.length() - 7);
                Date date = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.FRANCE).parse(dateStr);
                e = new Event(id, name, nbTotalTicket, nbScannedTicket, nbSoldTicket, nbTicketSoldOnSite, date, nbTicketPaidInCash);
            }
            JSONArray audienceJSON = (JSONArray) jsonObject.get("audience");
            List<Ticket> audience = new Gson().fromJson(audienceJSON.toString(), new TypeToken<List<Ticket>>(){}.getType());
            e.setAudience(audience);
            JSONArray counterpartJSON = (JSONArray) jsonObject.get("counterparts");
            List<Counterpart> counterparts = new Gson().fromJson(counterpartJSON.toString(), new TypeToken<List<Counterpart>>(){}.getType());
            e.setCounterparts(counterparts);
            JSONArray detailsTixPerCp = (JSONArray) jsonObject.get("detailsTixPerCp");
            Map<String, String> map = retrieveDetailsTixPerCp(detailsTixPerCp);
            e.setMap(map);
            events.add(e);
        }
        return events;
    }

    public List<Event> getFilteredResult(String pattern) {
        List<Event> filteredEvents = new ArrayList<>();
        if (pattern.isEmpty() || pattern == null){
            filteredEvents = events;
        } else {
            for (Event event : events){
                if (event.getName().toLowerCase().contains(pattern)){
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

    private void fetchEventsInDB(boolean isInternetConnected){
        System.out.println("fecthEvents");
        if (isInternetConnected){
            restCommunication.collectEvents(user.getId());
        } else {
            events = view.retrieveEvents();
            if (events != null){
                UnMuteDataHolder.setEvents(events);
                events = UnMuteDataHolder.getEvents();
                view.updateEventsList(false);
            }
            view.hideProgressBar();
        }
    }

    private Map<String, String> retrieveDetailsTixPerCp(JSONArray jsonArray){
        Map<String, String> detailsTixPerCp = new HashMap<>();
        for (int i=0; i<jsonArray.length(); i++){
            try {
                JSONObject jsonObject = jsonArray.getJSONObject(i);
                Iterator it = jsonObject.keys();
                while(it.hasNext()){
                    String cpId = (String) it.next();
                    String nbTix = jsonObject.getString(cpId);
                    detailsTixPerCp.put(cpId, nbTix);
                }
            } catch (JSONException e1) {
                e1.printStackTrace();
            }
        }
        return detailsTixPerCp;
    }
}
