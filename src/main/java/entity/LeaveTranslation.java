package entity;

import jakarta.persistence.*;

import java.time.LocalDate;

@Entity
@Table(name = "leave_translation")
public class LeaveTranslation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    private String languageCode;
    private String type;

    private String startDateText;
    private String endDateText;
    private String durationText;

    @ManyToOne
    @JoinColumn(name = "leave_id")
    private Leave leave;

    public LeaveTranslation() {}

    public LeaveTranslation(String languageCode, String type, Leave leave, String startDateText, String endDateText, String durationText) {
        this.languageCode = languageCode;
        this.type = type;
        this.leave = leave;
        this.startDateText = startDateText;
        this.endDateText = endDateText;
        this.durationText = durationText;
    }

    public int getId() {
        return id;
    }

    public String getLanguageCode() {
        return languageCode;
    }


    public String getStartDateText() {
        return startDateText;
    }

    public String getEndDateText() {
        return endDateText;
    }

    public String getDurationText() {
        return durationText;
    }

    public String getType() { return type; }

    public Leave getLeave() {
        return leave;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setLanguageCode(String languageCode) {
        this.languageCode = languageCode;
    }

    public void setReason(String type) {
        this.type = type;
    }

    public void setLeave(Leave leave) {
        this.leave = leave;
    }
}