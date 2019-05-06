package adri.suys.un_mutescan.viewinterfaces;

public interface TicketInfosViewInterface {

    void hideProgressBar();
    void displayEventName(String eventName);
    void displayTicketUnknwn(String barcodeText);
    void displayAlert();
    void displayTicket(boolean isValid, boolean isScanned, String barcodeText, String name, String ticketType, String seatType);
    void showNoConnectionRetryToast();
    void showServerConnectionProblemToast();
    void displayAlertMsg(boolean isScanned);

}
