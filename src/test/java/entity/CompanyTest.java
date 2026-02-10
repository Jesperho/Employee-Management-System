package entity;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for the Company entity class.
 */
class CompanyTest {

    private Company company;

    @BeforeEach
    void setUp() {
        company = new Company("TechCorp", "123 Tech Street", "Finland");
    }

    @Test
    @DisplayName("Company should be created with constructor parameters")
    void testCompanyConstructor() {
        assertNotNull(company);
        assertEquals("TechCorp", company.getName());
        assertEquals("123 Tech Street", company.getAddress());
        assertEquals("Finland", company.getCountry());
    }

    @Test
    @DisplayName("Company default constructor should create empty object")
    void testCompanyDefaultConstructor() {
        Company emptyCompany = new Company();
        assertNotNull(emptyCompany);
        assertNull(emptyCompany.getName());
    }

    @Test
    @DisplayName("setName should update company name")
    void testSetName() {
        company.setName("NewTech");
        assertEquals("NewTech", company.getName());
    }

    @Test
    @DisplayName("setAddress should update company address")
    void testSetAddress() {
        company.setAddress("456 New Avenue");
        assertEquals("456 New Avenue", company.getAddress());
    }

    @Test
    @DisplayName("setCountry should update company country")
    void testSetCountry() {
        company.setCountry("Sweden");
        assertEquals("Sweden", company.getCountry());
    }

    @Test
    @DisplayName("setId should update company id")
    void testSetId() {
        company.setId(5);
        assertEquals(5, company.getId());
    }
}
