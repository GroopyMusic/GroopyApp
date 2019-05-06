package adri.suys.un_mutescan.presenter;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkError;
import com.android.volley.NoConnectionError;
import com.android.volley.ParseError;
import com.android.volley.ServerError;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;

import java.util.List;

import adri.suys.un_mutescan.R;
import adri.suys.un_mutescan.activities.PayActivity;
import adri.suys.un_mutescan.apirest.RestService;
import adri.suys.un_mutescan.utils.UnMuteDataHolder;
import adri.suys.un_mutescan.model.Counterpart;
import adri.suys.un_mutescan.model.Event;
import adri.suys.un_mutescan.viewinterfaces.PayViewInterface;

public class PayPresenter {

    private final PayViewInterface view;
    private final RestService restCommunication;
    private final Event event;
    private int totalTicketSold;
    private boolean cashPayment;

    public PayPresenter(PayActivity view){
        this.view = view;
        this.restCommunication = new RestService(view);
        restCommunication.setPayPresenter(this);
        event = UnMuteDataHolder.getEvent();
    }

    public void addTickets(boolean paidInCash){
        cashPayment = paidInCash;
        restCommunication.addTicket(paidInCash);
    }

    /**
     * Notifies that the tickets have been well added
     * Reinit all the value
     * Goes to the screen where the buyer can pay
     */
    public void notifyTicketsWellAdded(int cpt, int nbUrls) {
        if (cpt == nbUrls-1){
            // fini
            view.showTicketAddedToast();
            UnMuteDataHolder.addTickets();
            event.addTicketSoldOnSite(totalTicketSold);
            if (cashPayment){
                event.addTicketPaidInCash(totalTicketSold);
                view.hideProgressBar();
                view.onBackPressed();
            } else {
                // si bancontact / plus applicable
            }
        } else {
            restCommunication.addAnotherTicket();
        }
    }

    /**
     * Notifies that the tickets have not been well added (error)
     * Reinit all the value
     * Invites the user to try again
     */
    public void notifyTicketsNotAdded(int cpt) {
        StringBuilder msg;
        if (cpt == 0){
            view.hideProgressBar();
            view.showTicketNotAddedToast();
        } else {
            List<Counterpart> cps = UnMuteDataHolder.getCounterparts();
            msg = new StringBuilder("Les tickets ");
            for (int i = 0; i < cpt; i++){
                msg.append(cps.get(cpt).getName());
                if (i != cpt - 1){
                    msg.append(", ");
                } else {
                    msg.append(" ");
                }
            }
            msg.append("ont été ajoutés. Pour les autres, il y a eu un problème...");
            view.hideProgressBar();
            view.showToast(msg.toString());
        }
    }

    /**
     * If the Volley connection encounters problem, it displays an error message to the user
     * @param error the error
     */
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
    }

    public void setTotalTicketSold(int totalTicketSold) {
        this.totalTicketSold = totalTicketSold;
    }
}
