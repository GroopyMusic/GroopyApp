package adri.suys.un_mutescan.presenter;

import adri.suys.un_mutescan.R;
import adri.suys.un_mutescan.dataholder.UnMuteDataHolder;
import adri.suys.un_mutescan.fragments.EventStatFragment;
import adri.suys.un_mutescan.model.Event;

public class EventStatPresenter {

    private Event event;
    private EventStatFragment view;

    public EventStatPresenter(EventStatFragment view){
        this.event = UnMuteDataHolder.getEvent();
        this.view = view;
        String totalTicket = view.getResources().getString(R.string.ticket_total, event.getNbTotalTicket());
        String totalSold = view.getResources().getString(R.string.ticket_sold, event.getNbSoldTicket());

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
