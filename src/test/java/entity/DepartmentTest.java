package entity;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;


class DepartmentTest {

    private Department department;
    private Company company;

    @BeforeEach
    void setUp() {
        company = new Company("TestCorp", "123 Test Street", "Finland");
        department = new Department(
                25,
                "Engineering",
                "Software Engineering Department",
                LocalDate.of(2020, 5, 1),
                company
        );
    }

    @Test
    @DisplayName("Department should be created with constructor parameters")
    void testDepartmentConstructor() {
        assertNotNull(department);
        assertEquals(25, department.getCapacity());
        assertEquals("Engineering", department.getDepartmentName());
        assertEquals("Software Engineering Department", department.getDescription());
        assertEquals(LocalDate.of(2020, 5, 1), department.getCreationDate());
        assertEquals(company, department.getCompany());
    }

    @Test
    @DisplayName("Department default constructor should create empty object")
    void testDepartmentDefaultConstructor() {
        Department emptyDepartment = new Department();
        assertNotNull(emptyDepartment);
        assertNull(emptyDepartment.getDepartmentName());
    }

    @Test
    @DisplayName("setCapacity should update department capacity")
    void testSetCapacity() {
        department.setCapacity(50);
        assertEquals(50, department.getCapacity());
    }

    @Test
    @DisplayName("setDepartmentName should update department name")
    void testSetDepartmentName() {
        department.setDepartmentName("Marketing");
        assertEquals("Marketing", department.getDepartmentName());
    }

    @Test
    @DisplayName("setDescription should update department description")
    void testSetDescription() {
        department.setDescription("Marketing and Sales Department");
        assertEquals("Marketing and Sales Department", department.getDescription());
    }

    @Test
    @DisplayName("setCreationDate should update department creation date")
    void testSetCreationDate() {
        LocalDate newDate = LocalDate.of(2021, 1, 1);
        department.setCreationDate(newDate);
        assertEquals(newDate, department.getCreationDate());
    }

    @Test
    @DisplayName("setCompany should update department company")
    void testSetCompany() {
        Company newCompany = new Company("NewCorp", "456 New Street", "Sweden");
        department.setCompany(newCompany);
        assertEquals(newCompany, department.getCompany());
    }


    @Test
    @DisplayName("setId should update department id")
    void testSetId() {
        department.setId(10);
        assertEquals(10, department.getId());
    }
}
