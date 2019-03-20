package adri.suys.un_mutescan.apirest;

import android.support.v7.app.AppCompatActivity;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import adri.suys.un_mutescan.dataholder.UnMuteDataHolder;
import adri.suys.un_mutescan.model.Counterpart;
import adri.suys.un_mutescan.model.Event;
import adri.suys.un_mutescan.model.User;
import adri.suys.un_mutescan.presenter.AudiencePresenter;
import adri.suys.un_mutescan.presenter.BuyTicketOnSitePresenter;
import adri.suys.un_mutescan.presenter.EventPresenter;
import adri.suys.un_mutescan.presenter.LoginPresenter;
import adri.suys.un_mutescan.presenter.PayPresenter;
import adri.suys.un_mutescan.presenter.TicketInfosPresenter;

public class RestService {

    private static final int SCAN_TICKET = 0;
    private static final int COUNTERPART = 1;
    private static final int USER = 2;
    private static final int EVENT = 3;
    private static final int AUDIENCE = 5;
    private static final int AUDIENCE_IN = 6;
    private static final int AUDIENCE_OUT = 7;

    private static final String BASE_URL_USER = "http://192.168.1.33:8888/GroopyMusic/web/app_dev.php/loginuser?";
    private static final String BASE_URL_SCAN_TICKET = "http://192.168.1.33:8888/GroopyMusic/web/app_dev.php/scanticket?";
    private static final String BASE_URL_EVENTS = "http://192.168.1.33:8888/GroopyMusic/web/app_dev.php/getevents?";
    private static final String BASE_URL_COUNTERPARTS = "http://192.168.1.33:8888/GroopyMusic/web/app_dev.php/getcounterpart?";
    private static final String BASE_URL_ADD_TICKET = "http://192.168.1.33:8888/GroopyMusic/web/app_dev.php/addticket?";
    private static final String BASE_URL_AUDIENCE = "http://192.168.1.33:8888/GroopyMusic/web/app_dev.php/getaudience?";

    /*private static final String BASE_URL_USER = "http://192.168.1.205:8888/GroopyMusic/web/app_dev.php/loginuser?";
    private static final String BASE_URL_SCAN_TICKET = "http://192.168.1.205:8888/GroopyMusic/web/app_dev.php/scanticket?";
    private static final String BASE_URL_EVENTS = "http://192.168.1.205:8888/GroopyMusic/web/app_dev.php/getevents?";
    private static final String BASE_URL_COUNTERPARTS = "http://192.168.1.205:8888/GroopyMusic/web/app_dev.php/getcounterpart?";
    private static final String BASE_URL_ADD_TICKET = "http://192.168.1.205:8888/GroopyMusic/web/app_dev.php/addticket?";
    private static final String BASE_URL_AUDIENCE = "http://192.168.1.205:8888/GroopyMusic/web/app_dev.php/getaudience?";*/

    private TicketInfosPresenter ticketPresenter;
    private LoginPresenter userPresenter;
    private EventPresenter eventPresenter;
    private BuyTicketOnSitePresenter counterpartPresenter;
    private AudiencePresenter audiencePresenter;
    private PayPresenter payPresenter;

    private final RequestQueue requestQueue;
    private final Gson gson;
    private int cpt;
    private List<String> urls = new ArrayList<>();

    public RestService(AppCompatActivity activity){
        requestQueue = Volley.newRequestQueue(activity);
        GsonBuilder gsonBuilder = new GsonBuilder();
        gsonBuilder.setDateFormat("M/d/yy hh:mm a");
        gson = gsonBuilder.create();
    }

    /**
     * Creates the url we need to reach the api
     * Create a request with that url
     * According to the username, get back the user with all its infos.
     * @param username the username of the user that tries to log in
     */
    public void loginUser(String username){
        String url = BASE_URL_USER + "username=" + username;
        createJsonObjectRequest(Request.Method.GET, url, USER);
    }

    /**
     * Creates the url we need to reach the api
     * Create a request with that url
     * According the userID, the eventID and the barcode, validates a ticket
     * @param userID the id of the current user
     * @param eventID the id of the current event
     * @param barcodeValue the barcode value scanned
     */
    public void scanTicket(int userID, int eventID, String barcodeValue){
        String url = BASE_URL_SCAN_TICKET + "user_id=" + userID + "&event_id=" + eventID + "&barcode=" + barcodeValue;
        createJsonObjectRequest(Request.Method.GET, url, SCAN_TICKET);
    }

    /**
     * Creates the url we need to reach the api
     * Create a request with that url
     * According to the id of the current user, fetches all the event he organises.
     * @param id the id of the current user
     */
    public void collectEvents(int id){
        String url = BASE_URL_EVENTS + "id=" + id;;
        createJsonArrayRequest(Request.Method.GET, url, EVENT);
    }

    /**
     * Creates the url we need to reach the api
     * Create a request with that url
     * According to the event id, fetches all the counterparts (type of tickets) linked to that event
     * @param userID the id of the current user
     * @param eventID the id of the event we want the counterpart for
     */
    public void collectCounterparts(int userID, int eventID) {
        String url = BASE_URL_COUNTERPARTS + "user_id=" + userID + "&event_id=" + eventID;
        createJsonArrayRequest(Request.Method.GET, url, COUNTERPART);
    }

    /**
     * Creates the url we need to reach the api
     * Create a request with that url
     * Add a/some ticket(s) to the DB --> the tickets that are sold on site during the event
     */
    public void addTicket(boolean paidInCash){
        urls = getUrlFromEvent(paidInCash);
        cpt = 0;
        createAddRequest(urls.get(cpt));
    }

    public void addAnotherTicket(){
        cpt++;
        createAddRequest(urls.get(cpt));
    }

    /**
     * Creates the url we need to reach the api
     * Create a request with that url
     * According to the event id, fetches all the tickets
     * @param userID the id of the current user
     * @param eventID the if of the event we want the ticket for
     */
    public void collectAudience(int userID, int eventID){
        String url = BASE_URL_AUDIENCE + "user_id=" + userID + "&event_id=" + eventID;
        createJsonArrayRequest(Request.Method.GET, url, AUDIENCE);
    }

    //|||||||||||||||||||||//
    //||SETTERS & GETTERS||//
    //|||||||||||||||||||||//

    public void setTicketPresenter(TicketInfosPresenter ticketPresenter) {
        this.ticketPresenter = ticketPresenter;
    }

    public void setUserPresenter(LoginPresenter userPresenter) {
        this.userPresenter = userPresenter;
    }

    public void setEventPresenter(EventPresenter eventPresenter) {
        this.eventPresenter = eventPresenter;
    }

    public void setCounterpartPresenter(BuyTicketOnSitePresenter counterpartPresenter) {
        this.counterpartPresenter = counterpartPresenter;
    }

    public void setAudiencePresenter(AudiencePresenter audiencePresenter){
        this.audiencePresenter = audiencePresenter;
    }

    public void setPayPresenter(PayPresenter payPresenter) {
        this.payPresenter = payPresenter;
    }

    //|||||||||||//
    //||PRIVATE||//
    //|||||||||||//

    private void createJsonObjectRequest(int httpMethod, String url, final int hint){
        JsonObjectRequest request = new JsonObjectRequest(httpMethod, url, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        switch (hint){
                            case SCAN_TICKET:
                                ticketPresenter.handleJSONObject(response, gson);
                                break;
                            case USER:
                                userPresenter.handleJSONObject(response, gson);
                                break;
                            default:
                                // nothing
                                break;
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        switch (hint){
                            case SCAN_TICKET:
                                ticketPresenter.handleVolleyError(error);
                                break;
                            case USER:
                                userPresenter.handleVolleyError(error);
                                break;
                            default:
                                // nothing
                                break;
                        }
                    }
                });
        request.setShouldCache(false);
        request.setRetryPolicy(new DefaultRetryPolicy(
                0,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        requestQueue.add(request);
    }

    private void createJsonArrayRequest(int httpMethod, String url, final int hint){
        JsonArrayRequest request = new JsonArrayRequest(httpMethod, url, null,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        switch(hint){
                            case EVENT:
                                eventPresenter.handleJSONArray(response);
                                break;
                            case COUNTERPART:
                                counterpartPresenter.handleJSONArray(response);
                                break;
                            case AUDIENCE: case AUDIENCE_IN: case AUDIENCE_OUT:
                                audiencePresenter.handleJSONArray(response, gson);
                                break;
                        }
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        switch(hint){
                            case EVENT:
                                eventPresenter.handleVolleyError(error);
                                break;
                            case COUNTERPART:
                                counterpartPresenter.handleVolleyError(error);
                                break;
                            case AUDIENCE: case AUDIENCE_IN : case AUDIENCE_OUT:
                                audiencePresenter.handleVolleyError(error);
                                break;
                        }
                    }
                });
        if (hint == AUDIENCE || hint == EVENT){
            request.setShouldCache(false);
        }
        requestQueue.add(request);
    }

    private void createAddRequest(String url){
        JsonObjectRequest jsonArrayRequest = new JsonObjectRequest(Request.Method.POST, url, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            String feedback = response.getString("error");
                            if (feedback.equals("Tout s'est bien passé !")){
                                payPresenter.notifyTicketsWellAdded(cpt, urls.size());
                            } else {
                                payPresenter.notifyTicketsNotAdded(cpt);
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        payPresenter.handleVolleyError(error);
                    }
                }
        );
        jsonArrayRequest.setShouldCache(false);
        requestQueue.add(jsonArrayRequest);
    }

    private List<String> getUrlFromEvent(boolean paidInCash){
        List<String> urls = new ArrayList<>();
        User user = UnMuteDataHolder.getUser();
        Event event = UnMuteDataHolder.getEvent();
        for (Counterpart cp : UnMuteDataHolder.getCounterparts()){
            if (cp.getQuantity() > 0){
                String url = getUrlFromCounterpart(user.getId(), cp, event.getId(), paidInCash);
                urls.add(url);
            }
        }
        return urls;
    }

    private String getUrlFromCounterpart(int userID, Counterpart cp, int eventID, boolean paidInCash) {
        String mode = paidInCash ? "cash" : "bancontact";
        return BASE_URL_ADD_TICKET + "user_id=" + userID + "&event_id=" + eventID + "&counterpart_id=" + cp.getId() + "&quantity=" + cp.getQuantity() + "&mode=" + mode;
    }

}
