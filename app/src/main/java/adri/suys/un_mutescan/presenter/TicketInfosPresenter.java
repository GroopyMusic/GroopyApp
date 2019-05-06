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
import adri.suys.un_mutescan.viewinterfaces.TicketInfosViewInterface;

public class TicketInfosPresenter {

    private final Event currentEvent;
    private final RestService restCommunication;
    private final TicketInfosViewInterface view;
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
            view.hideProgressBar();
            view.displayEventName(currentEvent.getName());
            view.displayTicketUnknwn(barcodeValue);
            view.displayAlert();
        } else {
            ticket = UnMuteDataHolder.getAudience().get(i);
            boolean isScanned;
            if (ticket.isScanned()){
                isScanned = true;
            } else {
                isScanned = false;
                isValid = true;
            }
            view.hideProgressBar();
            view.displayEventName(currentEvent.getName());
            view.displayTicket(isValid, isScanned, ticket.getBarcodeText(), ticket.getName(), ticket.getTicketType(), ticket.getSeatValue());
            view.displayAlertMsg(isScanned);
        }
    }

    public void handleJSONObject(JSONObject response, Gson gson) {
        System.out.println("J'ai fini ma requete");
        // do nothing
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
    }
}
