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

    private final Event currentEvent;
    private final RestService restCommunication;
    private final TicketInfosActivity view;
    private Ticket ticket;

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
        String message;
        for (Ticket t : UnMuteDataHolder.getAudience()){
            if (t.getBarcodeText().equals(barcodeValue)){
                String errorMsg;
                if (t.isScanned()){
                    message = view.getResources().getString(R.string.scan_error_already_scanned);
                    errorMsg = message;
                } else {
                    t.setIs_validated(true);
                    restCommunication.scanTicket(UnMuteDataHolder.getUser().getId(), currentEvent.getId(), barcodeValue);
                    message = view.getResources().getString(R.string.ticket_ok_dialog);
                    errorMsg = "";
                }
                view.hideProgressBar();
                view.displayTicket(errorMsg, message, t.getBarcodeText(), t.getName(), t.getTicketType(), t.getSeatType());
                view.displayEventName(currentEvent.getName());
                view.displayAlert(view.getResources().getString(ticket.getErrorMessageAsResource()));
            } else {
                message = view.getResources().getString(R.string.scan_error_no_match_event_tix);
                view.displayAlert(message);
            }
        }
    }

    public void handleJSONObject(JSONObject response, Gson gson) {
        /*ticket = gson.fromJson(response.toString(), Ticket.class);
        try {
            String validatedMsg = view.getResources().getString(R.string.ticket_ok_dialog);
            if (ticket != null) {
                view.hideProgressBar();
                view.displayTicket(ticket.getErrorMessage(), validatedMsg, ticket.getBarcodeText(), ticket.getName(), ticket.getTicketType(), ticket.getSeatType(), ticket.getErrorMessageAsResource());
                UnMuteDataHolder.scanTicket(ticket);
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
        }*/
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
