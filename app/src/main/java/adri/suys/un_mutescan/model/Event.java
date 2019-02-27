package adri.suys.un_mutescan.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class Event implements Serializable {

    private final int id;
    private final String name;
    private final int nbTotalTicket;
    private final int nbSoldTicket;
    private int nbScannedTicket;
    private int nbTicketSoldOnSite;
    private final Date date;
    private List<Counterpart> counterparts = new ArrayList<>();

    public Event(int id, String name, int nbTotalTicket, int nbScannedTicket, int nbSoldTicket, int nbTicketSoldOnSite, Date date){
        this.id = id;
        this.name = name;
        this.nbTotalTicket = nbTotalTicket;
        this.nbScannedTicket = nbScannedTicket;
        this.nbSoldTicket = nbSoldTicket;
        this.nbTicketSoldOnSite = nbTicketSoldOnSite;
        this.date = date;
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

    public boolean isToday(){
        Date today = new Date();
        return date.equals(today);
    }

    public int getNbTicketSoldOnSite() {
        return nbTicketSoldOnSite;
    }

    public void scanTicket() {
        nbScannedTicket++;
    }

    public void sellTicketOnSite() {
        nbTicketSoldOnSite++;
    }

    public int getRemainingTicketToBeSold(){
        return nbTotalTicket - nbSoldTicket - nbTicketSoldOnSite;
    }

    public List<Counterpart> getCounterparts() {
        return counterparts;
    }

    public void setCounterparts(List<Counterpart> counterparts) {
        this.counterparts = counterparts;
    }
}
