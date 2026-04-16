package app;

import entity.Admin;
import entity.Company;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class UserSessionTest {

    private UserSession session;

    @BeforeEach
    void setUp() {
        session = UserSession.getInstance();
    }

    @AfterEach
    void tearDown() {
        // Reset singleton state so other tests are not affected
        session.setUser("Admin", "Administrator");
    }

    @Test
    @DisplayName("getInstance returns the same singleton")
    void testGetInstance() {
        assertSame(session, UserSession.getInstance());
    }

    @Test
    @DisplayName("setUser with two-word name builds correct initials")
    void testSetUserTwoWordName() {
        session.setUser("John Doe", "Developer");
        assertEquals("John Doe", session.getFullName());
        assertEquals("JD", session.getInitials());
        assertEquals("Developer", session.getRole());
    }

    @Test
    @DisplayName("setUser with single-word name uses up to 2 chars as initials")
    void testSetUserSingleWordName() {
        session.setUser("Alice", "Manager");
        assertEquals("Alice", session.getFullName());
        assertEquals("AL", session.getInitials());
    }

    @Test
    @DisplayName("setUser with single-char name uses that char as initials")
    void testSetUserSingleCharName() {
        session.setUser("X", "Admin");
        assertEquals("X", session.getFullName());
        assertEquals("X", session.getInitials());
    }

    @Test
    @DisplayName("setUser falls back to Admin when fullName is null")
    void testSetUserNullName() {
        session.setUser(null, "Developer");
        assertEquals("Admin", session.getFullName());
    }

    @Test
    @DisplayName("setUser falls back to Admin when fullName is blank")
    void testSetUserBlankName() {
        session.setUser("   ", "Developer");
        assertEquals("Admin", session.getFullName());
    }

    @Test
    @DisplayName("setUser falls back to Administrator when role is null")
    void testSetUserNullRole() {
        session.setUser("Jane Doe", null);
        assertEquals("Administrator", session.getRole());
    }

    @Test
    @DisplayName("setUser falls back to Administrator when role is blank")
    void testSetUserBlankRole() {
        session.setUser("Jane Doe", "  ");
        assertEquals("Administrator", session.getRole());
    }

    @Test
    @DisplayName("setUser with Admin object stores it and getAdmin returns it")
    void testSetUserWithAdmin() {
        Company company = new Company("TestCo", "Street 1", "Finland");
        Admin admin = new Admin("a@test.com", "hash", company);

        session.setUser("John Doe", "Admin", admin);
        assertEquals(admin, session.getAdmin());
        assertEquals("John Doe", session.getFullName());
        assertEquals("JD", session.getInitials());
    }

    @Test
    @DisplayName("setUser(3-arg) falls back to Admin when name is null")
    void testSetUserWithAdminNullName() {
        session.setUser(null, "Developer", null);
        assertEquals("Admin", session.getFullName());
        assertNull(session.getAdmin());
    }

    @Test
    @DisplayName("setUser(3-arg) falls back to Administrator when role is blank")
    void testSetUserWithAdminBlankRole() {
        session.setUser("Bob", "  ", null);
        assertEquals("Administrator", session.getRole());
    }
}