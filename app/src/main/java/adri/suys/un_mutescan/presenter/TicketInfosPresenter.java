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

import adri.suys.un_mutescan.activities.TicketInfosActivity;
import adri.suys.un_mutescan.apirest.RestService;
import adri.suys.un_mutescan.model.Event;
import adri.suys.un_mutescan.model.Ticket;
import adri.suys.un_mutescan.utils.UnMuteDataHolder;
import adri.suys.un_mutescan.viewinterfaces.TicketInfosViewInterface;

public class TicketInfosPresenter {

    private final Event currentEvent;
    private final TicketInfosViewInterface view;
    private String barcode;
    private Ticket ticket;

    public TicketInfosPresenter(TicketInfosViewInterface view){
        this.view = view;
        this.currentEvent = UnMuteDataHolder.getEvent();
    }

    public void updateDB(){
        ticket.setIs_validated(true);
        currentEvent.scanTicket();
        view.scanTicket(UnMuteDataHolder.getUser().getId(), currentEvent.getId(), barcode);
    }

    public boolean validateBarcode(String barcodeValue){
        barcode = barcodeValue;
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
                ticket.setIs_validated(true);
            }
            view.hideProgressBar();
            view.displayEventName(currentEvent.getName());
            view.displayTicket(isValid, isScanned, ticket.getBarcodeText(), ticket.getName(), ticket.getTicketType(), ticket.getSeatValue());
            view.displayAlertMsg(isScanned);
        }
        return isValid;
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
