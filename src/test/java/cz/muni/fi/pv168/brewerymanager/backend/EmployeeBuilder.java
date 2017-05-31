package cz.muni.fi.pv168.brewerymanager.backend;

/**
 * Method that is supposed to help us creating test employees.
 * @author alice
 */
public class EmployeeBuilder {
    
    private Long id;
    private String name;
    private String email;
    private String phoneNumber;
    private int salary;
    private Position position;
    
    public EmployeeBuilder id(Long id) {
        this.id = id;
        return this;
        
    }
    public EmployeeBuilder name(String name){
        this.name = name;
        return this;
    
    }
    
    public EmployeeBuilder email(String email){
        this.email = email;
        return this;
    }
    
    public EmployeeBuilder phoneNumber(String pn) {
        this.phoneNumber = pn;
        return this;
    }
    public EmployeeBuilder salary(int salary) {
        this.salary = salary;
        return this;
        
    }
    public EmployeeBuilder position(Position position){
        this.position = position;
        return this;
        
    }
    
    public Employee build(){
        Employee employee = new Employee();
        employee.setId(id);
        employee.setName(name);
        employee.setEmail(email);
        employee.setPhoneNumber(phoneNumber);
        employee.setSalary(salary);
        employee.setPosition(position);
        return employee;
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getEmail() {
        return email;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public int getSalary() {
        return salary;
    }

    public Position getPosition() {
        return position;
    }
    
    
    
    
    
}
