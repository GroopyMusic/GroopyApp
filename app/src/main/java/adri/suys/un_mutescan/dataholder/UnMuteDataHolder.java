package adri.suys.un_mutescan.dataholder;

import adri.suys.un_mutescan.model.Event;
import adri.suys.un_mutescan.model.User;

public class UnMuteDataHolder {

    private static User user;
    private static Event event;

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
}
