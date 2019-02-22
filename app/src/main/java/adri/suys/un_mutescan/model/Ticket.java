package adri.suys.un_mutescan.model;

public class Ticket {

    private String buyer;
    private String ticket_type;
    private String seat_type;
    private String barcode;
    private String error;

    public Ticket(String barcodeText, String name, String ticketType, String seatType, String errorMessage) {
        this.buyer = name;
        this.ticket_type = ticketType;
        this.seat_type = seatType;
        this.barcode = barcodeText;
        this.error = errorMessage;
    }

    public String getName() {
        return buyer;
    }

    public void setName(String name) {
        this.buyer = name;
    }

    public String getTicketType() {
        return ticket_type;
    }

    public void setTicketType(String ticketType) {
        this.ticket_type = ticketType;
    }

    public String getSeatType() {
        return seat_type;
    }

    public void setSeatType(String seatType) {
        this.seat_type = seatType;
    }

    public String getBarcodeText() {
        return barcode;
    }

    public void setBarcodeText(String barcodeText) {
        this.barcode = barcodeText;
    }

    public String getErrorMessage() {
        return error;
    }

    public void setErrorMessage(String errorMessage) {
        this.error = errorMessage;
    }
}
