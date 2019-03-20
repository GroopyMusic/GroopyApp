package adri.suys.un_mutescan.model;

import adri.suys.un_mutescan.R;

/**
 * A ticket is a ticket generated, sold, bought for an event
 */
public class Ticket {

    private final String buyer;
    private final String ticket_type;
    private final String seat_type;
    private final String barcode;
    private final String error;
    private final boolean is_scanned;

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
        this.is_scanned = isScanned.equals("vrai") ? true : false;
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
        return is_scanned;
    }

    /**
     * Get the resource string associated to the error message.
     * @return the int value of the resource string
     */
    public int getErrorMessageAsResource(){
        switch (error){
            case "Ce ticket n'existe pas." :
                return R.string.scan_error_ticket_not_exist;
            case "Cet événement n\\'existe pas." :
                return R.string.scan_error_event_not_exist;
            case "Cet événement n\\'a pas lieu aujourd'hui." :
                return R.string.scan_error_incorrect_date;
            case "Ce ticket ne correspond pas à l\\'évenement sélectionné" :
                return R.string.scan_error_no_match_event_tix;
            case "Ce ticket a été remboursé et n\\'est donc plus valide." :
                return R.string.scan_error_tix_refund;
            case "Ce ticket a déjà été scanné." :
                return R.string.scan_error_already_scanned;
            default :
                return R.string.ticket_ok_dialog;
        }
    }

    public boolean isValid(){
        return error.equals("");
    }

    @Override
    public String toString() {
        return buyer + " - " + is_scanned;
    }
}
