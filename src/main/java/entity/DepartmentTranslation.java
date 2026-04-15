package entity;

import jakarta.persistence.*;

@Entity
@Table(name = "department_translation")
public class DepartmentTranslation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    private String languageCode;
    private String departmentName;

    private String creationDateText;
    private String capacity;


    @ManyToOne
    @JoinColumn(name = "department_id")
    private Department department;

    public DepartmentTranslation() {}

    public DepartmentTranslation(String languageCode, String departmentName, Department department, String creationDateText, String capacity) {
        this.languageCode = languageCode;
        this.departmentName = departmentName;
        this.department = department;
        this.creationDateText = creationDateText;
        this.capacity = capacity;
    }
    public int getId() {
        return id;
    }

    public String getLanguageCode() {
        return languageCode;
    }

    public String getDepartmentName() {
        return departmentName;
    }

    public Department getDepartment() {
        return department;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getCreationDateText() {
        return creationDateText;
    }

    public String getCapacity() {
        return capacity;
    }

    public void setLanguageCode(String languageCode) {
        this.languageCode = languageCode;
    }

    public void setDepartmentName(String departmentName) {
        this.departmentName = departmentName;
    }

    public void setDepartment(Department department) {
        this.department = department;
    }
}