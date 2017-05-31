
package cz.muni.fi.pv168.brewerymanager.backend;

import java.util.Objects;

/**
 *
 * @author Adam Kral, Petra Mikova
 */
public class Keg {
    private Long id;
    private String brand;
    private boolean isFilled;
    private int capacity;
    private int price;
    private int deposit;
    
    
    public Long getId(){
        return id;
    }
    
    public void setId(Long id){
        this.id = id;
    }
    
    public boolean isFilled(){
        return isFilled;
    }
    /*
    public void fill(){
        asi az do impl?
    }
    */
    
    public int getCapacity(){
        return capacity;
    }
    
    public void setCapacity(int capacity){
        this.capacity = capacity;
    }
    
    public int getPrice(){
        return price;
    }
    
    public void setPrice(int price){
        this.price = price;
    }
    
    public int getDeposit(){
        return deposit;
    }
    
    public void setDeposit(int deposit){
        this.deposit = deposit;
    }
    
    public String getBrand(){
        return brand;
    }
    
    public void setBrand(String brand){
        this.brand = brand;
    }
    
    
       @Override
    public String toString() {
        return "Keg{" +
                ", isFilled=" + isFilled +
                ", price=" + price +
                ", capacity=" + capacity +
                ", deposit='" + deposit  +
                ", brand='" + brand + '\'' +
                '}';
    }
    
    /* Jake vsechny aspekty davat do hashCode a equals? */
    @Override
    public boolean equals(Object o){
        if (this == o) return true;
        if (!(o instanceof Keg)) return false;
        
        Keg keg = (Keg) o;
        
        if (getCapacity() != keg.getCapacity() ) return false;
        if (getPrice() != keg.getPrice())        return false;
        if (getDeposit() != keg.getDeposit())    return false;
        
        return (getId() != null ? getId().equals(keg.getId()) : keg.getId() != null);
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 47 * hash + Objects.hashCode(this.id);
        return hash;
    }
}
