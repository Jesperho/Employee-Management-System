
package entity;

import jakarta.persistence.*;
        import java.time.LocalDate;

@Entity
@Table(name = "change_dept")
public class ChangeDept {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "date")
    private LocalDate date;

    @Column(name = "message_en")
    private String messageEn;

    @Column(name = "message_fa")
    private String messageFa;

    @Column(name = "message_ar")
    private String messageAr;

    @Column(name = "message_es")
    private String messageEs;

    @ManyToOne
    @JoinColumn(name = "admin_id")
    private Admin admin;

    @ManyToOne
    @JoinColumn(name = "department_id")
    private Department department;

    public ChangeDept() {}

    public ChangeDept(LocalDate date,
                      String messageEn,
                      String messageFa,
                      String messageAr,
                      String messageEs,
                      Admin admin,
                      Department department) {
        this.date = date;
        this.messageEn = messageEn;
        this.messageFa = messageFa;
        this.messageAr = messageAr;
        this.messageEs = messageEs;
        this.admin = admin;
        this.department = department;
    }

    public Integer getId() {
        return id;
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public String getMessageEn() {
        return messageEn;
    }

    public void setMessageEn(String messageEn) {
        this.messageEn = messageEn;
    }

    public String getMessageFa() {
        return messageFa;
    }

    public void setMessageFa(String messageFa) {
        this.messageFa = messageFa;
    }

    public String getMessageAr() {
        return messageAr;
    }

    public void setMessageAr(String messageAr) {
        this.messageAr = messageAr;
    }

    public String getMessageEs() {
        return messageEs;
    }

    public void setMessageEs(String messageEs) {
        this.messageEs = messageEs;
    }

    public Admin getAdmin() {
        return admin;
    }

    public void setAdmin(Admin admin) {
        this.admin = admin;
    }

    public Department getDepartment() {
        return department;
    }

    public void setDepartment(Department department) {
        this.department = department;
    }


    public String getMessage(String lang) {
        return switch (lang.toLowerCase()) {
            case "en" -> messageEn;
            case "fa" -> messageFa;
            case "ar" -> messageAr;
            case "es" -> messageEs;
            default -> messageEn;
        };
    }
}
