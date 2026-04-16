package entity;

import jakarta.persistence.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Entity
@Table(name = "employee")
public class Employee {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "employee_id")
    private int id;

    private String fullName;
    private LocalDate startDate;
    private Double salary;


    private String gender;
    private String contractType;
    private String position;
    private String role;


    @ManyToOne
    @JoinColumn(name = "company_id")
    private Company company;

    @ManyToOne
    @JoinColumn(name = "department_id")
    private Department department;

    @OneToMany(mappedBy = "employee", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<EmployeeTranslation> translations = new HashSet<>();

    @OneToMany(mappedBy = "employee", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Leave> leaves = new ArrayList<>();

    public Employee() {}

    public Employee(
            String fullName,
            String gender,
            LocalDate startDate,
            Double salary,
            Company company,
            Department department,
            String role,
            String contractType,
            String position
    ) {
        this.fullName = fullName;
        this.gender = gender;
        this.startDate = startDate;
        this.salary = salary;
        this.company = company;
        this.department = department;
        this.role = role;
        this.contractType = contractType;
        this.position = position;
        this.leaves = new ArrayList<>();
    }

    public int getId() {
        return id;
    }

    public EmployeeTranslation getTranslation(String languageCode) {
        if (translations == null) return null;

        for (EmployeeTranslation t : translations) {
            if (t.getLanguage_code().equalsIgnoreCase(languageCode)) {
                return t;
            }
        }
        return null;
    }

    public String getTranslatedGender(String langCode) {
        EmployeeTranslation t = getTranslation(langCode);
        return t != null ? t.getGender() : this.gender;
    }

    public String getTranslatedContract(String langCode) {
        EmployeeTranslation t = getTranslation(langCode);
        return t != null ? t.getContractType() : this.contractType;
    }

    public String getTranslatedPosition(String langCode) {
        EmployeeTranslation t = getTranslation(langCode);
        return t != null ? t.getPosition() : this.position;
    }

    public String getTranslatedStartDate(String langCode){
        EmployeeTranslation t = getTranslation(langCode);
        return t != null ? t.getStartDate() : this.position;
    }

    public String getTranslatedSalary(String langCode){
        EmployeeTranslation t = getTranslation(langCode);
        return t != null ? t.getSalary().toString() : this.position;
    }

    public String getTranslatedDepartment(String langCode){
        Department dep = getDepartment();

        return dep.getTranslatedName(langCode);
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public LocalDate getStartDate() {
        return startDate;
    }

    public void setStartDate(LocalDate startDate) {
        this.startDate = startDate;
    }

    public Double getSalary() {
        return salary;
    }

    public void setSalary(Double salary) {
        this.salary = salary;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public String getContractType() {
        return contractType;
    }

    public void setContractType(String contractType) {
        this.contractType = contractType;
    }

    public String getPosition() {
        return position;
    }

    public void setPosition(String position) {
        this.position = position;
    }

    public Company getCompany() {
        return company;
    }

    public void setCompany(Company company) {
        this.company = company;
    }

    public Department getDepartment() {
        return department;
    }

    public void setDepartment(Department department) {
        this.department = department;
    }

    public List<Leave> getLeaves() {
        return leaves;
    }

    public void setLeaves(List<Leave> leaves) {
        this.leaves = (leaves != null) ? leaves : new ArrayList<>();
    }
}
