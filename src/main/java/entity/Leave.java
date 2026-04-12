package entity;

import jakarta.persistence.*;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

@Entity
@Table(name = "employee_leave")
public class Leave {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "leave_id")
    private int id;

    private LocalDate startDate;
    private LocalDate endDate;
    private String type;
    private String reason;
    private Integer duration;

    @ManyToOne
    @JoinColumn(name = "employee_id")
    private Employee employee;

    @OneToMany(mappedBy = "leave", cascade = CascadeType.ALL, orphanRemoval = true)
    private java.util.List<LeaveTranslation> translations = new java.util.ArrayList<>();

    public Leave() {
    }

    public Leave(LocalDate startDate, LocalDate endDate, String type, String reason, Employee employee) {
        this.startDate = startDate;
        this.endDate = endDate;
        this.type = type;
        this.reason = reason;
        this.employee = employee;

        // Auto-calculate duration
        if (startDate != null && endDate != null) {
            this.duration = (int) ChronoUnit.DAYS.between(startDate, endDate) + 1;
        }
    }

    public LeaveTranslation getTranslation(String languageCode) {
        if (translations == null) return null;

        for (LeaveTranslation t : translations) {
            if (t.getLanguageCode().equalsIgnoreCase(languageCode)) {
                return t;
            }
        }
        return null;
    }

    public String getTranslatedType(String langCode) {
        LeaveTranslation t = getTranslation(langCode);
        return t != null ? t.getType() : this.type;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public LocalDate getStartDate() {
        return startDate;
    }

    public void setStartDate(LocalDate startDate) {
        this.startDate = startDate;
        updateDuration();
    }

    public LocalDate getEndDate() {
        return endDate;
    }


    public String getTranslatedStartDate(String langCode){
        LeaveTranslation t = getTranslation(langCode);
        return t != null ? t.getStartDateText() : "";
    }

    public String getTranslatedEndDate(String langCode){
        LeaveTranslation t = getTranslation(langCode);
        return t != null ? t.getEndDateText() : "";
    }

    public String getTranslatedDuration(String langCode){
        LeaveTranslation t = getTranslation(langCode);
        return t != null ? t.getDurationText() : "";
    }

    public void setEndDate(LocalDate endDate) {
        this.endDate = endDate;
        updateDuration();
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public Employee getEmployee() {
        return employee;
    }

    public void setEmployee(Employee employee) {
        this.employee = employee;
    }

    public Integer getDuration() {
        return duration;
    }

    private void updateDuration() {
        if (startDate != null && endDate != null) {
            this.duration = (int) ChronoUnit.DAYS.between(startDate, endDate) + 1;
        }
    }
}