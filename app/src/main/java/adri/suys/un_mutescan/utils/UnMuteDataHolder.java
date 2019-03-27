package adri.suys.un_mutescan.utils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

import adri.suys.un_mutescan.model.Counterpart;
import adri.suys.un_mutescan.model.Event;
import adri.suys.un_mutescan.model.Ticket;
import adri.suys.un_mutescan.model.User;

/**
 * Holds the datas that are shared through the whole app
 */
public class UnMuteDataHolder {

    private static User user;
    private static Event event;
    private static int currentFragment = 0;
    private static List<Event> events;
    private static List<String> requestURLs;

    public static User getUser() {
        return user;
    }

    public static void setUser(User user) {
        UnMuteDataHolder.user = user;
    }

    public static Event getEvent() {
        return event;
    }

    public static void setEvent(Event event) {
        UnMuteDataHolder.event = event;
    }

    public static int getCurrentFragment() {
        return currentFragment;
    }

    public static void setCurrentFragment(int currentFragment) {
        UnMuteDataHolder.currentFragment = currentFragment;
    }

    public static List<Counterpart> getCounterparts() {
        return event.getCounterparts();
    }

    public static List<Event> getEvents() {
        return events;
    }

    public static void setEvents(List<Event> events) {
        List<Event> sorted = sortEvents(events);
        UnMuteDataHolder.events = sorted;
    }

    public static List<Ticket> getAudience(){
        return event.getAudience();
    }

    public static void reinit(){
        user = null;
        event = null;
        currentFragment = 0;
        events = null;
    }

    public static void addTickets(){
        for (Counterpart cp : event.getCounterparts()){
            for (int i = 0; i < cp.getQuantity(); i++) {
                Ticket t = new Ticket("anonyme", cp.getName(), "", "000", "", "vrai");
                event.getAudience().add(t);
            }
        }
        Collections.sort(event.getAudience(), new Comparator<Ticket>() {
            @Override
            public int compare(Ticket ticket, Ticket t1) {
                return ticket.getName().compareTo(t1.getName());
            }
        });
    }

    public static List<String> getRequestURLs(){
        return requestURLs;
    }

    public static void addRequest(String url){
        requestURLs.add(url);
    }

    public static void setRequestURLs(List<String> requestURLs){
        UnMuteDataHolder.requestURLs = requestURLs;
    }

    private static List<Event> sortEvents(List<Event> events){
        List<Event> sorted = new ArrayList<>();
        List<Event> past = new ArrayList<>();
        List<Event> future = new ArrayList<>();
        List<Event> undetermined = new ArrayList<>();
        for (Event e : events){
            if (e.getDate() == null){
                undetermined.add(e);
            } else if (e.getDate().compareTo(new Date()) >= 0){
                future.add(e);
            } else {
                past.add(e);
            }
        }
        Collections.sort(future, new Comparator<Event>() {
            @Override
            public int compare(Event event, Event t1) {
                return event.getDate().compareTo(t1.getDate());
            }
        });
        Collections.sort(past, new Comparator<Event>() {
            @Override
            public int compare(Event event, Event t1) {
                return t1.getDate().compareTo(event.getDate());
            }
        });
        sorted.addAll(future);
        sorted.addAll(past);
        return sorted;
    }

    public static int isValidatedTicket(String barcode){
        for (int i = 0; i < getAudience().size(); i++){
            if (getAudience().get(i).getBarcodeText().equals(barcode)){
                return i;
            }
        }
        return -2;
    }
}
