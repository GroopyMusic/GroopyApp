package adri.suys.un_mutescan.model;

import java.io.Serializable;
import java.util.Date;
import java.util.Objects;

public class Event implements Serializable {

    private int id;
    private String name;
    private int nbTotalTicket;
    private int nbSoldTicket;
    private int nbScannedTicket;
    private int nbTicketSoldOnSite;
    private Date date;

    public Event(int id, String name, int nbTotalTicket, int nbScannedTicket, int nbSoldTicket, int nbTicketSoldOnSite, Date date){
        this.id = id;
        this.name = name;
        this.nbTotalTicket = nbTotalTicket;
        this.nbScannedTicket = nbScannedTicket;
        this.nbSoldTicket = nbSoldTicket;
        this.nbTicketSoldOnSite = nbTicketSoldOnSite;
        this.date = new Date();
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getNbTotalTicket() {
        return nbTotalTicket;
    }

    public void setNbTotalTicket(int nbTotalTicket) {
        this.nbTotalTicket = nbTotalTicket;
    }

    public int getNbScannedTicket() {
        return nbScannedTicket;
    }

    public void setNbScannedTicket(int nbScannedTicket) {
        this.nbScannedTicket = nbScannedTicket;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public int getNbSoldTicket() {
        return nbSoldTicket;
    }

    public void setNbSoldTicket(int nbSoldTicket) {
        this.nbSoldTicket = nbSoldTicket;
    }

    public boolean isToday(){
        Date today = new Date();
        return date.equals(today);
    }

    public int getNbTicketSoldOnSite() {
        return nbTicketSoldOnSite;
    }

    public void setNbTicketSoldOnSite(int nbTicketSoldOnSite) {
        this.nbTicketSoldOnSite = nbTicketSoldOnSite;
    }

    public void scanTicket() {
        nbScannedTicket++;
    }

    public void sellTicketOnSite() {
        nbTicketSoldOnSite++;
    }
}
