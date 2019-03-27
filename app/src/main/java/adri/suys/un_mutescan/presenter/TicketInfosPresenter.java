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
    private String barcode;
    private Ticket ticket;

    public TicketInfosPresenter(TicketInfosActivity view){
        this.view = view;
        this.currentEvent = UnMuteDataHolder.getEvent();
        this.restCommunication = new RestService(view);
        restCommunication.setTicketPresenter(this);
    }

    public void updateDB(){
        ticket.setIs_validated(true);
        currentEvent.scanTicket();
        restCommunication.scanTicket(UnMuteDataHolder.getUser().getId(), currentEvent.getId(), barcode);
    }

    public void validateBarcode(String barcodeValue){
        barcode = barcodeValue;
        String message;
        boolean isValid = false;
        int i = UnMuteDataHolder.isValidatedTicket(barcodeValue);
        if (i == -2){
            message = view.getResources().getString(R.string.scan_error_no_match_event_tix);
            view.hideProgressBar();
            view.displayEventName(currentEvent.getName());
            view.displayTicketUnknwn(message, barcodeValue);
            view.displayAlert(message);
        } else {
            ticket = UnMuteDataHolder.getAudience().get(i);
            if (ticket.isScanned()){
                message = view.getResources().getString(R.string.scan_error_already_scanned);
            } else {
                message = view.getResources().getString(R.string.ticket_ok_dialog);
                isValid = true;
            }
            view.hideProgressBar();
            view.displayEventName(currentEvent.getName());
            view.displayTicket(isValid, message, ticket.getBarcodeText(), ticket.getName(), ticket.getTicketType(), ticket.getSeatType());
            view.displayAlert(message);
        }
    }

    public void handleJSONObject(JSONObject response, Gson gson) {
        System.out.println("J'ai fini ma requete");
        // do nothing
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
