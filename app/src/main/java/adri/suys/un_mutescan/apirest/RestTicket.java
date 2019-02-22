package adri.suys.un_mutescan.apirest;

import android.util.Log;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import org.json.JSONObject;
import java.util.Observable;
import adri.suys.un_mutescan.activities.TicketActivity;
import adri.suys.un_mutescan.model.Ticket;

public class RestTicket extends Observable {

    private static final String BASE_URL = "http://192.168.1.33:8888/GroopyMusic/web/app_dev.php/scanticket?";
    private RestService rest;

    public RestTicket(TicketActivity ticketActivity){
        this.addObserver(ticketActivity);
        rest = new RestService(ticketActivity);
    }

    public void scanTicket(int eventID, String barcodeTxt){
        String url = makeTicketUrl(eventID, barcodeTxt);
        JsonObjectRequest ticketRequest = createTicketRequest(Request.Method.GET, url);
        rest.getRequestQueue().add(ticketRequest);
    }

    private String makeTicketUrl(int eventID, String barcodeTxt) {
        return BASE_URL + "event_id=" + eventID + "&barcode=" + barcodeTxt;
    }

    private JsonObjectRequest createTicketRequest(int requestMethod, String url){
        return new JsonObjectRequest(requestMethod, url, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Ticket ticket = getTicketFromJSON(response);
                        setChanged();
                        notifyObservers(ticket);
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

    private Ticket getTicketFromJSON(JSONObject json){
        return rest.getGson().fromJson(json.toString(), Ticket.class);
    }


}
