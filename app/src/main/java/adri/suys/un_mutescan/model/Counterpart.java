package adri.suys.un_mutescan.model;

public class Counterpart {

    private int id;
    private String name;
    private double price;
    private int quantity = 0;

    public Counterpart(int id, String name, double price) {
        this.id = id;
        this.name = name;
        this.price = price;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public double getPrice() {
        return price;
    }

    public int getId() {
        return id;
    }

    public int getQuantity(){
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public void addTicket(){
        quantity++;
    }

    public void removeTicket(){
        if (quantity > 0){
            quantity--;
        }
    }
}
