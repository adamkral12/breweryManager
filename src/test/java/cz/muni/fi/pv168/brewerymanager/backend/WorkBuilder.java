package cz.muni.fi.pv168.brewerymanager.backend;
import java.time.LocalDate;
import java.time.LocalTime;
/**
 *
 * @author adam
 */
public class WorkBuilder {
      private Long id;
    private Long employeeId;
    private Long kegId;
    private LocalTime startTime;
    private LocalDate startDate;
    private LocalTime endTime;
    private LocalDate endDate;

    public WorkBuilder id(Long id) {
        this.id = id;
        return this;
    }

    public WorkBuilder employeeId(Long employeeId) {
        this.employeeId = employeeId;
        return this;
    }

    public WorkBuilder kegId(Long kegId) {
        this.kegId = kegId;
        return this;
    }

    public WorkBuilder startTime(LocalTime startTime) {
        this.startTime = startTime;
        return this;
    }

    public WorkBuilder startDate(LocalDate startDate) {
        this.startDate = startDate;
        return this;
    }
        public WorkBuilder endTime(LocalTime endTime) {
        this.endTime = endTime;
        return this;
    }

    public WorkBuilder endDate(LocalDate endDate) {
        this.endDate = endDate;
        return this;
    }


    public Work build() {
        Work work = new Work();
        work.setId(id);
        work.setEmployeeId(employeeId);
        work.setKegId(kegId);
        work.setStartTime(startTime);
        work.setStartDate(startDate);
        work.setEndTime(endTime);
        work.setEndDate(endDate);
        return work;
    }  
}
