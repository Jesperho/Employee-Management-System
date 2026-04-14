package entity;

import jakarta.persistence.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "employee_translation")
public class EmployeeTranslation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private int id;

    private String language_code;
    private String gender;
    private String contractType;
    private String position;

    private String startDate;
    private String salary;

    @ManyToOne
    @JoinColumn(name = "employee_id")
    private Employee employee;


    public EmployeeTranslation() {}

    public EmployeeTranslation(
            String language_code,
            String position,
            String contractType,
            String gender,
            Employee employee,
            String startDate,
            String salary
    ) {

        this.gender = gender;
        this.contractType = contractType;
        this.position = position;
        this.language_code = language_code;
        this.employee = employee;
        this.startDate = startDate;
        this.salary = salary;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getStartDate(){
        return startDate;
    }

    public String getSalary(){
        return salary;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }


    public String getPosition() {
        return position;
    }

    public void setPosition(String position) {
        this.position = position;
    }

    public String getLanguage_code(){
        return language_code;
    }

    public String getContractType(){
        return contractType;
    }

    public void setContractType(String contractType){
        this.contractType = contractType;
    }

    public void setLanguage_code(String language_code){
        this.language_code = language_code;
    }

}
