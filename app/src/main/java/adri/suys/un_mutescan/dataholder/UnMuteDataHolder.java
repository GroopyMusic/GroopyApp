package adri.suys.un_mutescan.dataholder;

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
    private static List<Counterpart> counterparts;
    private static List<Ticket> audience;
    private static List<Event> events;

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
        return counterparts;
    }

    public static void setCounterparts(List<Counterpart> counterparts) {
        UnMuteDataHolder.counterparts = counterparts;
    }

    public static List<Ticket> getAudience() {
        return audience;
    }

    public static void setAudience(List<Ticket> audience) {
        UnMuteDataHolder.audience = audience;
    }

    public static List<Event> getEvents() {
        return events;
    }

    public static void setEvents(List<Event> events) {
        UnMuteDataHolder.events = events;
    }
}
