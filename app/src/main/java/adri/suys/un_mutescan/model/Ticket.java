package adri.suys.un_mutescan.model;

import java.io.Serializable;

import adri.suys.un_mutescan.R;

/**
 * A ticket is a ticket generated, sold, bought for an event
 */
public class Ticket implements Serializable {

    private final String buyer;
    private final String ticket_type;
    private final String seat_type;
    private final String barcode;
    private final String error;
    private String is_validated;
    private int event;

    /**
     *
     * @param barcodeText the barcode value written on the ticket
     * @param name the name of the buyer
     * @param ticketType the type of the ticket (vip, child, adult, etc.)
     * @param seatType the seat number (nothing if it is a stand up ticket)
     * @param errorMessage the error message (nothing if the ticket is valid) if the ticket is unvalid
     */
    public Ticket(String name, String ticketType, String seatType, String barcodeText, String errorMessage, String isScanned) {
        this.buyer = name;
        this.ticket_type = ticketType;
        this.seat_type = seatType;
        this.barcode = barcodeText;
        this.error = errorMessage;
        this.is_validated = isScanned;
    }

    public String getName() {
        return buyer;
    }

    public String getTicketType() {
        return ticket_type;
    }

    public String getSeatType() {
        return seat_type;
    }

    public String getBarcodeText() {
        return barcode;
    }

    public String getErrorMessage() {
        return error;
    }

    public boolean isScanned(){
        return is_validated.equals("vrai");
    }

    public void setIs_validated(boolean scanned){
        is_validated = scanned ? "vrai" : "faux";
    }

    public void setEvent(int id){
        this.event = id;
    }

    @Override
    public String toString() {
        return buyer + " - " + is_validated;
    }
}
