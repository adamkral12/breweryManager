package cz.muni.fi.pv168.brewerymanager.backend;

/**
 *
 * @author Adam Kral
 */
public class KegBuilder {

    private Long id;
    private String brand;
    private boolean isFilled;
    private int capacity;
    private int price;
    private int deposit;

    public KegBuilder id(Long id) {
        this.id = id;
        return this;
    }

    public KegBuilder price(int price) {
        this.price = price;
        return this;
    }

    public KegBuilder deposit(int deposit) {
        this.deposit = deposit;
        return this;
    }

    public KegBuilder capacity(int capacity) {
        this.capacity = capacity;
        return this;
    }

    public KegBuilder brand(String brand) {
        this.brand = brand;
        return this;
    }

    public Keg build() {
        Keg keg = new Keg();
        keg.setId(id);
        keg.setPrice(price);
        keg.setDeposit(deposit);
        keg.setCapacity(capacity);
        keg.setBrand(brand);
        return keg;
    }
}