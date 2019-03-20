package adri.suys.un_mutescan.model;

import android.text.format.DateUtils;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * A event is a event organized by an user for which he/she wants to scan ticket at the gates
 * It has an id, a name and a date
 */
public class Event implements Serializable {

    private final int id;
    private final String name;
    private final int nbTotalTicket;
    private int nbSoldTicket;
    private int nbScannedTicket;
    private int nbTicketSoldOnSite;
    private final Date date;
    private int nbTicketBoughtInCash;

    /**
     *
     * @param id
     * @param name
     * @param nbTotalTicket the number of people the room can fit (the global soldout)
     * @param nbScannedTicket the number of tickets that are already scanned
     * @param nbSoldTicket the number of tickets sold prior the event (pre-sale)
     * @param nbTicketSoldOnSite the number of tickets sold on site at the gates
     * @param date
     */
    public Event(int id, String name, int nbTotalTicket, int nbScannedTicket, int nbSoldTicket, int nbTicketSoldOnSite, Date date, int nbTicketBoughtInCash){
        this.id = id;
        if (name.contains("[Ticked-it]")){
            this.name = name.substring(12);
        } else {
            this.name = name;
        }
        this.nbTotalTicket = nbTotalTicket;
        this.nbScannedTicket = nbScannedTicket;
        this.nbSoldTicket = nbSoldTicket;
        this.nbTicketSoldOnSite = nbTicketSoldOnSite;
        this.date = date;
        this.nbTicketBoughtInCash = nbTicketBoughtInCash;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public int getNbTotalTicket() {
        return nbTotalTicket;
    }

    public int getNbScannedTicket() {
        return nbScannedTicket;
    }

    public Date getDate() {
        return date;
    }

    public int getNbSoldTicket() {
        return nbSoldTicket;
    }

    public int getNbTicketSoldOnSite() {
        return nbTicketSoldOnSite;
    }

    public void scanTicket() {
        nbScannedTicket++;
    }

    public int getRemainingTicketToBeSold(){
        return nbTotalTicket - nbSoldTicket - nbTicketSoldOnSite;
    }

    public void addTicketSoldOnSite(int totalTicketSold) {
        this.nbTicketSoldOnSite += totalTicketSold;
    }

    public int getNbTicketBoughtInCash() {
        return nbTicketBoughtInCash;
    }

    public void addTicketPaidInCash(int ticketPaidInCash){
        this.nbTicketBoughtInCash += ticketPaidInCash;
    }

    public int getNbTicketBoughtViaCard(){
        return this.nbTicketSoldOnSite - this.nbTicketBoughtInCash;
    }

    public boolean isToday(){
        if (date == null){
            return false;
        } else {
            return DateUtils.isToday(date.getTime());
        }
    }
}
