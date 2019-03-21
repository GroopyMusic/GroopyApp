package adri.suys.un_mutescan.presenter;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkError;
import com.android.volley.NoConnectionError;
import com.android.volley.ParseError;
import com.android.volley.ServerError;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.google.gson.Gson;

import org.json.JSONObject;

import adri.suys.un_mutescan.R;
import adri.suys.un_mutescan.activities.TicketInfosActivity;
import adri.suys.un_mutescan.apirest.RestService;
import adri.suys.un_mutescan.utils.UnMuteDataHolder;
import adri.suys.un_mutescan.model.Event;
import adri.suys.un_mutescan.model.Ticket;

public class TicketInfosPresenter {

    private Event currentEvent;
    private RestService restCommunication;
    private TicketInfosActivity view;
    private Ticket ticket;
    private int cpt = 0;

    public TicketInfosPresenter(TicketInfosActivity view){
        this.view = view;
        this.currentEvent = UnMuteDataHolder.getEvent();
        this.restCommunication = new RestService(view);
        restCommunication.setTicketPresenter(this);
    }

    public void validateTicket(){
        if (ticket.isValid()) currentEvent.scanTicket();
    }

    public void validateBarcode(String barcodeValue){
        restCommunication.scanTicket(UnMuteDataHolder.getUser().getId(), currentEvent.getId(), barcodeValue);
    }

    public TicketInfosActivity getView() {
        return view;
    }


    public void handleJSONObject(JSONObject response, Gson gson) {
        ticket = gson.fromJson(response.toString(), Ticket.class);
        try {
            String validatedMsg = view.getResources().getString(R.string.ticket_ok_dialog);
            if (ticket != null) {
                view.hideProgressBar();
                view.displayTicket(ticket.getErrorMessage(), validatedMsg, ticket.getBarcodeText(), ticket.getName(), ticket.getTicketType(), ticket.getSeatType(), ticket.getErrorMessageAsResource());
            }
            if (currentEvent != null) {
                view.displayEventName(currentEvent.getName());
            }
            Boolean mustDisplayAlert = view.getIntent().getBooleanExtra("alert", false);
            if (mustDisplayAlert) {
                view.displayAlert(view.getResources().getString(ticket.getErrorMessageAsResource()));
            }
        } catch (NullPointerException ex) {
            view.hideProgressBar();
            view.showToast(view.getResources().getString(R.string.sth_wrong));
        }
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
        view.hideProgressBar();
        view.showToast(message);
    }
}
