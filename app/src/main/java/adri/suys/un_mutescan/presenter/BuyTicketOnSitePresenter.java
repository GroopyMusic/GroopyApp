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
import java.util.List;
import java.util.Objects;

import adri.suys.un_mutescan.R;
import adri.suys.un_mutescan.activities.Activity;
import adri.suys.un_mutescan.apirest.RestService;
import adri.suys.un_mutescan.utils.UnMuteDataHolder;
import adri.suys.un_mutescan.fragments.BuyTicketOnSiteFragment;
import adri.suys.un_mutescan.model.Counterpart;
import adri.suys.un_mutescan.model.Event;

public class BuyTicketOnSitePresenter{

    private final Event event;
    private final RestService restCommunication;
    private List<Counterpart> counterparts = new ArrayList<>();
    private double cartAmount = 0;
    private int totalTicketSold = 0;
    private final BuyTicketOnSiteFragment view;

    public BuyTicketOnSitePresenter(BuyTicketOnSiteFragment view){
        this.view = view;
        this.restCommunication = new RestService((Activity)view.getActivity());
        restCommunication.setCounterpartPresenter(this);
        this.event = UnMuteDataHolder.getEvent();
    }

    // FETCHES ALL THE CP IN THE DB

    /**
     * Fetches in the DB all the counterparts that are linked to the currentEvent
     */
    public void collectCounterparts(){
        restCommunication.collectCounterparts(UnMuteDataHolder.getUser().getId(), event.getId());
    }

    /**
     * Transforms the JSONArray into a list of counterparts.
     * Set the counterparts in the DataHolder so we can share them through the app
     * @param response the json array
     */
    public void handleJSONArray(JSONArray response) {
        try {
            counterparts = getCounterpartsFromJSON(response);
            UnMuteDataHolder.setCounterparts(counterparts);
        } catch (JSONException e) {
            try {
                String error = ((JSONObject) response.get(0)).getString("error");
                ((Activity)(Objects.requireNonNull(view.getActivity()))).showToast(error);
            } catch (JSONException e1) {
                e1.printStackTrace();
            }
            e.printStackTrace();
        }
        view.hideProgressBar();
        view.updateTicketTypesList();
    }

    /**
     * If the Volley connection encounters problem, it displays an error message to the user
     * @param error the error
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
        ((Activity) Objects.requireNonNull(view.getActivity())).showToast(message);
        view.hideProgressBar();
    }

    private List<Counterpart> getCounterpartsFromJSON(JSONArray response) throws JSONException {
        List<Counterpart> tickets = new ArrayList<>();
        JSONObject[] array = new JSONObject[response.length()];
        for (int i = 0; i < response.length(); i++){
            array[i] = (JSONObject) response.get(i);
        }
        for (JSONObject jsonObject : array){
            int id = jsonObject.getInt("id");
            String name = jsonObject.getString("name");
            double price = jsonObject.getDouble("price");
            Counterpart counterpart = new Counterpart(id, name, price);
            tickets.add(counterpart);
        }
        return tickets;
    }

    // SELL TICKET AT THE GATES

    /**
     * Add tickets to the database
     */
    public void addTicket() {
        double amountToPay = cartAmount;
        int numberTicketsSold = totalTicketSold;
        reinit();
        view.changeScreen(amountToPay, numberTicketsSold);
    }

    // OTHERS

    public double getCartAmount(){
        return this.cartAmount;
    }

    public int getItemCount() {
        return counterparts.size();
    }

    /**
     * Displays the informations for each counterpart (name, price, and +/- button)
     * @param i the position
     * @param view the viewHolder
     */
    public void onViewCounterpartAtPosition(int i, BuyTicketOnSiteFragment.TicketTypeHolder view) {
        Counterpart currentCounterpart = counterparts.get(i);
        view.displayInfos(currentCounterpart.getName(), Double.toString(currentCounterpart.getPrice()));
    }

    /**
     * Checks if the event is soldout (if we still can sell tickets)
     * @return a boolean indicating if the event is soldout
     */
    public boolean isEventSoldout(int toBeSold){
        return event.getRemainingTicketToBeSold() <= toBeSold;
    }

    public int getTotalTicketSold(){
        return totalTicketSold;
    }

    private void reinit(){
        totalTicketSold = 0;
        cartAmount = 0;
    }

    public void setQuantityToCp(String name, double price, int quantity) {
        for (Counterpart cp : UnMuteDataHolder.getCounterparts()){
            if (cp.getName().equals(name) && cp.getPrice() == price){
                cp.setQuantity(quantity);
                cartAmount += price * quantity;
                totalTicketSold += quantity;
            }
        }
    }
}
