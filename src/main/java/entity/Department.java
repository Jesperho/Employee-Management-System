package entity;

import jakarta.persistence.*;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "department")
public class Department {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "department_id")
    private int id;

    private int capacity;
    private String departmentName;
    private String description;

    private LocalDate creationDate;

    @ManyToOne
    @JoinColumn(name = "company_id")
    private Company company;

    @OneToMany(mappedBy = "department", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<DepartmentTranslation> translations = new HashSet<>();


    public Department() {}

    public Department(int capacity, String departmentName, String description, LocalDate creationDate, Company company) {
        this.capacity = capacity;
        this.departmentName = departmentName;
        this.description = description;
        this.creationDate = creationDate;
        this.company = company;
    }

    public int getId() {
        return id;
    }

    public DepartmentTranslation getTranslation(String languageCode) {
        if (translations == null) return null;

        for (DepartmentTranslation t : translations) {
            if (t.getLanguageCode().equalsIgnoreCase(languageCode)) {
                return t;
            }
        }
        return null;
    }

    public String getTranslatedName(String langCode) {
        DepartmentTranslation t = getTranslation(langCode);
        return t != null ? t.getDepartmentName() : this.departmentName;
    }

    public String getTranslatedDate(String langCode) {
        DepartmentTranslation t = getTranslation(langCode);
        return t != null ? t.getCreationDateText() : "";
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getCapacity() {
        return capacity;
    }

    public void setCapacity(int capacity) {
        this.capacity = capacity;
    }

    public String getDepartmentName() {
        return departmentName;
    }

    public void setDepartmentName(String departmentName) {
        this.departmentName = departmentName;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public LocalDate getCreationDate() {
        return creationDate;
    }

    public void setCreationDate(LocalDate creationDate) {
        this.creationDate = creationDate;
    }

    public Company getCompany() {
        return company;
    }

    public void setCompany(Company company) {
        this.company = company;
    }

}
