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

public class PayPresenter {

    private PayActivity view;
    private RestService restCommunication;
    private Event event;
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
            String msg = view.getResources().getString(R.string.tickets_well_added);
            view.showToast(msg);
            event.addTicketSoldOnSite(totalTicketSold);
            if (cashPayment){
                event.addTicketPaidInCash(totalTicketSold);
                view.hideProgressBar();
                view.onBackPressed();
            } else {
                view.setUpBancontactPayment();
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
        String msg;
        if (cpt == 0){
            msg = view.getResources().getString(R.string.tickets_not_well_added);
            view.hideProgressBar();
            view.showToast(msg);
        } else {
            List<Counterpart> cps = UnMuteDataHolder.getCounterparts();
            msg = "Les tickets ";
            for (int i = 0; i < cpt; i++){
                msg = msg + cps.get(cpt).getName();
                if (i != cpt - 1){
                    msg = msg + ", ";
                } else {
                    msg = msg + " ";
                }
            }
            msg = msg + "ont été ajoutés. Pour les autres, il y a eu un problème...";
            view.hideProgressBar();
            view.showToast(msg);
        }
    }

    /**
     * If the Volley connection encounters problem, it displays an error message to the user
     * @param error
     */
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
        view.showToast(message);
    }

    public void setTotalTicketSold(int totalTicketSold) {
        this.totalTicketSold = totalTicketSold;
    }
}
