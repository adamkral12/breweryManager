package cz.muni.fi.pv168.brewerymanager.backend;

import java.time.LocalDate;
import java.time.LocalTime;
/**
 *
 * @author Adam Kral, Petra Mikova
 */
public class Work {
    private Long id;
    private Long employeeId;
    private Long kegId;
    private LocalTime startTime;
    private LocalTime endTime;
    private LocalDate startDate;
    private LocalDate endDate;
    

public Long getId(){
return id;
}

public void setId(Long id){
    this.id = id;
}

public Long getEmployeeId(){
    return employeeId;
}

public void setEmployeeId(Long employeeId){
    this.employeeId = employeeId;
}

public Long getKegId(){
    return kegId;
}

public void setKegId(Long kegId){
    this.kegId = kegId;
}

public LocalTime getStartTime(){
    return startTime;
}

public void setStartTime(LocalTime startTime){
    this.startTime = startTime;
}

public LocalTime getEndTime(){
    return endTime;
}

public void setEndTime(LocalTime endTime){
    this.endTime = endTime;
}


public LocalDate getStartDate(){
    return startDate;
}

public void setStartDate(LocalDate startDate){
    this.startDate = startDate;
}

public LocalDate getEndDate(){
    return endDate;
}

public void setEndDate(LocalDate endDate){
    this.endDate = endDate;
}

public boolean isEnded(){
    return this.endDate != null && this.endTime != null;
}
   @Override
    public String toString() {
        return "Work{" +
                ", keg id=" + kegId +
                ", employee id=" + employeeId +
                ", start=" + startDate + " " + startTime +
                ", end='" + endDate + " " + endTime + '\'' +
                '}';
    }
}
