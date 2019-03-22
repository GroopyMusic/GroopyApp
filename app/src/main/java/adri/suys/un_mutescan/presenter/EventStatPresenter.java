package adri.suys.un_mutescan.presenter;

import adri.suys.un_mutescan.R;
import adri.suys.un_mutescan.utils.UnMuteDataHolder;
import adri.suys.un_mutescan.fragments.EventStatFragment;
import adri.suys.un_mutescan.model.Event;

public class EventStatPresenter {

    private final Event event;

    public EventStatPresenter(EventStatFragment view){
        this.event = UnMuteDataHolder.getEvent();
        double presalePercentageOnOne = ((double) event.getNbSoldTicket()) / event.getNbTotalTicket();
        float presalePercentageOnHundred = (float) (presalePercentageOnOne * 100);
        String totalTicket = view.getResources().getString(R.string.ticket_total, event.getNbTotalTicket());
        String totalSold = view.getResources().getString(R.string.ticket_sold, event.getNbSoldTicket(), presalePercentageOnHundred);
        view.displayEvent(event.getName(), event.getDate(), totalTicket, totalSold);
    }

    public int getTotalScanned(){
        return event.getNbScannedTicket();
    }

    public int getTotalSoldOnSite(){
        return event.getNbTicketSoldOnSite();
    }

    public int getTotalToBeScanned(){
        return event.getNbSoldTicket() - event.getNbScannedTicket();
    }

    public int getTotalRemaining(){
        return event.getRemainingTicketToBeSold();
    }

    public int getTotalPaidInCash(){
        return event.getNbTicketBoughtInCash();
    }

}
