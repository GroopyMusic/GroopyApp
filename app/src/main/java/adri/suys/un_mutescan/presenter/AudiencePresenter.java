package adri.suys.un_mutescan.presenter;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkError;
import com.android.volley.NoConnectionError;
import com.android.volley.ParseError;
import com.android.volley.ServerError;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;

import adri.suys.un_mutescan.R;
import adri.suys.un_mutescan.activities.Activity;
import adri.suys.un_mutescan.apirest.RestService;
import adri.suys.un_mutescan.utils.UnMuteDataHolder;
import adri.suys.un_mutescan.fragments.AudienceFragment;
import adri.suys.un_mutescan.model.Event;
import adri.suys.un_mutescan.model.Ticket;
import adri.suys.un_mutescan.model.User;
import adri.suys.un_mutescan.viewinterfaces.AudienceViewInterface;

public class AudiencePresenter {

    private final RestService restCommunication;
    private final AudienceViewInterface view;
    private List<Ticket> audienceToBeDisplayed = new ArrayList<>();
    private static final int ALL = 1;
    private static final int IN = 2;
    private static final int OUT = 3;

    public AudiencePresenter(AudienceFragment view){
        this.view = view;
        this.restCommunication = new RestService((Activity) view.getActivity());
    }

    public void getAudience(int options) {
        if (options == ALL){
            audienceToBeDisplayed = UnMuteDataHolder.getAudience();
        } else {
            if (options == IN){
                audienceToBeDisplayed = sortAudienceIn();
            } else if (options == OUT) {
                audienceToBeDisplayed = sortAudienceOut();
            }
        }
        view.hideProgressBar();
        view.updateAudienceList();
    }

    public void onViewCounterpartAtPosition(int i, AudienceFragment.AudienceHolder audienceHolder) {
        Ticket currentCustomer = audienceToBeDisplayed.get(i);
        String name = currentCustomer.getName() + " (" + currentCustomer.getBarcodeText() + ")";
        audienceHolder.displayInfos(name, currentCustomer.getTicketType(), currentCustomer.getSeatValue());
    }

    public int getItemCount() {
        return audienceToBeDisplayed.size();
    }

    public int getNbAll(){
        return UnMuteDataHolder.getEvent().getNbTotalTicket() - UnMuteDataHolder.getEvent().getRemainingTicketToBeSold();
    }

    public int getNbIn(){
        return UnMuteDataHolder.getEvent().getNbScannedTicket() + UnMuteDataHolder.getEvent().getNbTicketSoldOnSite();
    }

    public int getNbOut(){
        return getNbAll() - getNbIn();
    }

    // private method

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


    public String getBarcodeValue(int currentPosition) {
        return audienceToBeDisplayed.get(currentPosition).getBarcodeText();
    }
}
