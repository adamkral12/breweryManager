package cz.muni.fi.pv168.brewerymanager.backend;

import java.util.Objects;

/**
 *
 * @author Adam Kral, Petra Mikova
 */
public class Employee {
    
    private Long id;
    private String name;
    private String email;
    private String phoneNumber;
    private int salary;
    private Position position;


  public Long getId(){
        return id;
    }
    
    public void setId(Long id){
        this.id = id;
    }
    
    public String getName(){
        return name;
    }
    
    public void setName(String name){
        this.name = name;
    }
    
    public String getEmail(){
        return email;
    }
    
    public void setEmail(String email){
        this.email = email;
    }
    
    public String getPhoneNumber(){
        return phoneNumber;
    }
   
    public void setPhoneNumber(String phoneNumber){
        this.phoneNumber = phoneNumber;
    }
    
    public int getSalary(){
        return salary;
    }
    // zeptat se jestli to davat tady do classy nebo az do employeemanagerImpl ^^
    public void setSalary(int salary){
        if (salary < 0){
            throw new IllegalArgumentException("Salary must be higher than 0");
        }        
        this.salary = salary;
    }
    
    public Position getPosition(){
        return position;
    }
    
    public void setPosition(Position position){
        this.position = position;
    }
    
        
       @Override
    public String toString() {
        return "Employee{" +
                ", name=" + name    +
                ", email=" + email +
                ", phoneNumber=" + phoneNumber +
                ", salary='" + salary + '\'' +
                ", position='" + position + '\'' +
                '}';
    }
    
    
     @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Employee)) return false;

        Employee emp = (Employee) o;

        if (!getName().equals(emp.getName())) return false;
        if (!getEmail().equals(emp.getEmail())) return false;
        if (!getPhoneNumber().equals(emp.getPhoneNumber())) return false;
        if(getSalary() != emp.getSalary()) return false;
        if (getId() != null ? !getId().equals(emp.getId()) : emp.getId() != null) return false;
        
        return getPosition() != null ? getPosition().equals(emp.getPosition()) : emp.getPosition() == null;

    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 71 * hash + Objects.hashCode(this.id);
        return hash;
    }
}