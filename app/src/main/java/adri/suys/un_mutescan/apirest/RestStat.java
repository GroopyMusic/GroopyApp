package adri.suys.un_mutescan.apirest;

import android.util.Log;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Observable;

import adri.suys.un_mutescan.activities.StatActivity;
import adri.suys.un_mutescan.model.Ticket;

public class RestStat extends Observable {

    private static final String BASE_URL = "http://192.168.1.33:8888/GroopyMusic/web/app_dev.php/addticket?";
    private RestService rest;

    public RestStat(StatActivity statActivity){
        this.addObserver(statActivity);
        rest = new RestService(statActivity);
    }

    public void addTicket(int eventID, int price){
        String url = makeStatUrl(eventID, price);
        JsonObjectRequest addTicketRequest = createAddTicketRequest(Request.Method.POST, url);
        rest.getRequestQueue().add(addTicketRequest);
    }

    private JsonObjectRequest createAddTicketRequest(int post, String url) {
        return new JsonObjectRequest(post, url, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            String msg = response.getString("response");
                            setChanged();
                            notifyObservers(msg);
                        } catch (JSONException e) {
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
    }

    private String makeStatUrl(int eventID, int price) {
        return BASE_URL + "id=" + eventID + "&price=" + price;
    }

}
