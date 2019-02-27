package adri.suys.un_mutescan.apirest;

import android.util.Log;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Observable;

import adri.suys.un_mutescan.activities.BuyTicketOnSiteActivity;
import adri.suys.un_mutescan.model.Event;
import adri.suys.un_mutescan.model.Counterpart;

public class RestCounterpart extends Observable {

    private RestService rest;
    private static final String BASE_URL = "http://192.168.1.33:8888/GroopyMusic/web/app_dev.php/getcounterpart?";
    private static final String BASE_URL_STAT = "http://192.168.1.33:8888/GroopyMusic/web/app_dev.php/addticket?";


    public RestCounterpart(BuyTicketOnSiteActivity activity){
        rest = new RestService(activity);
        this.addObserver(activity);
    }

    public void collectTicketType(Event event) {
        String url = makeEventUrl(event.getId());
        JsonArrayRequest request = createRequest(url);
        rest.getRequestQueue().add(request);
    }

    public void addTicket(int eventID, int price){
        //TODO
    }

    /////////////////////
    // private methods //
    /////////////////////

    private String makeEventUrl(int id) {
        return BASE_URL + "id=" + id;
    }

    private JsonArrayRequest createRequest(String url){
        return new JsonArrayRequest(Request.Method.GET, url, null,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        List<Counterpart> counterpartList;
                        try {
                            counterpartList = getTicketsFromJSON(response);
                            setChanged();
                            notifyObservers(counterpartList);
                        } catch (JSONException e) {
                            e.printStackTrace();
                            try {
                                setChanged();
                                notifyObservers(getError(response));
                            } catch (JSONException e1) {
                                e1.printStackTrace();
                            }
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
                });
    }

    private List<Counterpart> getTicketsFromJSON(JSONArray response) throws JSONException {
        List<Counterpart> tickets = new ArrayList<>();
        JSONObject[] array = new JSONObject[response.length()];
        for (int i = 0; i < response.length(); i++){
            array[i] = (JSONObject) response.get(i);
        }
        for (JSONObject jsonObject : array){
            int id = jsonObject.getInt("id");
            String name = jsonObject.getString("name");
            double price = jsonObject.getDouble("price");
            Counterpart counterpart = new Counterpart(id, name, price);
            tickets.add(counterpart);
        }
        return tickets;
    }

    private String getError(JSONArray response) throws JSONException {
        return ((JSONObject) response.get(0)).getString("error");
    }

}
