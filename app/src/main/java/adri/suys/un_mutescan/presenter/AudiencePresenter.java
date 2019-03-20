package adri.suys.un_mutescan.presenter;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkError;
import com.android.volley.NoConnectionError;
import com.android.volley.ParseError;
import com.android.volley.ServerError;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import adri.suys.un_mutescan.R;
import adri.suys.un_mutescan.activities.Activity;
import adri.suys.un_mutescan.apirest.RestService;
import adri.suys.un_mutescan.dataholder.UnMuteDataHolder;
import adri.suys.un_mutescan.fragments.AudienceFragment;
import adri.suys.un_mutescan.model.Event;
import adri.suys.un_mutescan.model.Ticket;
import adri.suys.un_mutescan.model.User;

public class AudiencePresenter {

    private RestService restCommunication;
    private AudienceFragment view;
    private List<Ticket> audienceToBeDisplayed = new ArrayList<>();
    private List<Ticket> audience;
    private List<Ticket> audienceIn = new ArrayList<>();
    private List<Ticket> audienceOut = new ArrayList<>();
    private Ticket currentCustomer;
    private static final int ALL = 1;
    private static final int IN = 2;
    private static final int OUT = 3;

    public AudiencePresenter(AudienceFragment view){
        this.view = view;
        this.restCommunication = new RestService((Activity) view.getActivity());
        restCommunication.setAudiencePresenter(this);
    }

    public void getAudience(int options, boolean starting) {
        Event event = UnMuteDataHolder.getEvent();
        User user = UnMuteDataHolder.getUser();
        if (options == ALL){
            if (starting) {
                restCommunication.collectAudience(user.getId(), event.getId());
            } else {
                audienceToBeDisplayed = UnMuteDataHolder.getAudience();
                view.hideProgressBar();
                view.updateAudienceList();
            }
        } else {
            if (options == IN){
                audienceToBeDisplayed = sortAudienceIn();
            } else {
                audienceToBeDisplayed = sortAudienceOut();
            }
            view.hideProgressBar();
            view.updateAudienceList();
        }
    }

    public void onViewCounterpartAtPosition(int i, AudienceFragment.AudienceHolder audienceHolder) {
        this.currentCustomer = audienceToBeDisplayed.get(i);
        audienceHolder.displayInfos(currentCustomer.getName(), currentCustomer.getTicketType(), currentCustomer.getSeatType());
    }

    public int getItemCount() {
        return audienceToBeDisplayed.size();
    }

    public void handleJSONArray(JSONArray response, Gson gson){
        try {
            UnMuteDataHolder.setAudience(getAudienceFromJSON(response, gson));
            audienceToBeDisplayed = UnMuteDataHolder.getAudience();
        } catch (JSONException e) {
            try {
                String error = ((JSONObject) response.get(0)).getString("error");
                ((Activity)(view.getActivity())).showToast(error);
            } catch (JSONException e1) {
                e1.printStackTrace();
            }
            e.printStackTrace();
        }
        view.hideProgressBar();
        view.updateAudienceList();
    }

    public void handleVolleyError(VolleyError error) {
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
        ((Activity)view.getActivity()).showToast(message);
        view.hideProgressBar();
    }

    private List<Ticket> getAudienceFromJSON(JSONArray response, Gson gson) throws JSONException {
        List<Ticket> audience = new ArrayList<>();
        JSONObject[] array = new JSONObject[response.length()];
        for (int i = 0; i < response.length(); i++){
            array[i] = (JSONObject) response.get(i);
        }
        for (JSONObject jsonObject : array){
            Ticket ticket = createTicketFromJSON(jsonObject);
            audience.add(ticket);
        }
        return audience;
    }

    private Ticket createTicketFromJSON(JSONObject obj){
        try {
            String buyer = obj.getString("buyer");
            String ticketType = obj.getString("ticket_type");
            String seatType = obj.getString("seat_type");
            String barcode = obj.getString("barcode");
            String error = obj.getString("error");
            String isScanned = obj.getString("is_validated");
            return new Ticket(buyer, ticketType, seatType, barcode, error, isScanned);
        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        }
    }

    private List<Ticket> sortAudienceIn() {
        List<Ticket> audienceSorted = new ArrayList<>();
        for (Ticket t : UnMuteDataHolder.getAudience()){
            System.out.println(t);
            if (t.isScanned()){
                audienceSorted.add(t);
            }
        }
        return audienceSorted;
    }

    private List<Ticket> sortAudienceOut() {
        List<Ticket> audienceSorted = new ArrayList<>();
        for (Ticket t : UnMuteDataHolder.getAudience()){
            if (!t.isScanned()){
                audienceSorted.add(t);
            }
        }
        return audienceSorted;
    }


}