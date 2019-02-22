package adri.suys.un_mutescan.apirest;

import android.util.Log;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Observable;

import adri.suys.un_mutescan.activities.EventActivity;
import adri.suys.un_mutescan.model.Event;
import adri.suys.un_mutescan.model.User;

public class RestEvents extends Observable {

    private static final String BASE_URL = "http://192.168.1.33:8888/GroopyMusic/web/app_dev.php/getevents?";
    private RestService rest;

    public RestEvents(EventActivity eventActivity){
        rest = new RestService(eventActivity);
        this.addObserver(eventActivity);
    }

    public void collectEvents(User user){
        String url = makeEventUrl(user.getId());
        JsonArrayRequest eventsRequest = createEventsRequest(Request.Method.GET, url);
        rest.getRequestQueue().add(eventsRequest);
    }

    private String makeEventUrl(int id) {
        return BASE_URL + "id=" + id;
    }

    private JsonArrayRequest createEventsRequest(int requestMethod, String url){
        JsonArrayRequest jor = new JsonArrayRequest(requestMethod, url, null,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        List<Event> events;
                        try {
                            events = getEventsFromJSON(response);
                            setChanged();
                            notifyObservers(events);
                        } catch (JSONException e) {
                            e.printStackTrace();
                            try {
                                setChanged();
                                notifyObservers(getError(response));
                            } catch (JSONException e1) {
                                e1.printStackTrace();
                            }
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }
                        Log.d("RESPONSE", response.toString());
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.d("ERROR", error.toString());
                        setChanged();
                        notifyObservers(error);
                    }
                }
        );
        return jor;
    }

    private String getError(JSONArray response) throws JSONException {
        return ((JSONObject) response.get(0)).getString("error");
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
            String dateStr = ((JSONObject) jsonObject.get("date")).getString("date");
            dateStr = dateStr.substring(0, dateStr.length() - 7);
            Date date = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(dateStr);
            events.add(new Event(id, name, nbTotalTicket, nbScannedTicket, nbSoldTicket, nbTicketSoldOnSite, date));
        }
        return events;
    }


}
