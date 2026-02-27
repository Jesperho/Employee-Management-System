package service;

import dao.AdminDao;
import dao.CompanyDao;
import entity.Admin;
import entity.Company;
import org.mindrot.jbcrypt.BCrypt;

public class AuthService {
    private final AdminDao adminDao;
    private final CompanyDao companyDao;
    private static Admin currentAdmin;

    public AuthService() {
        this.adminDao = new AdminDao();
        this.companyDao = new CompanyDao();
    }

    public AuthService(AdminDao adminDao, CompanyDao companyDao) {
        this.adminDao = adminDao;
        this.companyDao = companyDao;
    }

    public Admin login(String email, String password) {
        Admin admin = adminDao.findByEmail(email);
        if (admin != null && BCrypt.checkpw(password, admin.getPassword())) {
            currentAdmin = admin;
            return admin;
        }
        return null;
    }

    public boolean register(String companyName, String country, String firstName,
                            String lastName, String email, String gender, String password) {
        // Check if email already exists
        if (adminDao.findByEmail(email) != null) {
            return false;
        }

        // Create company
        Company company = new Company();
        company.setName(companyName);
        company.setCountry(country);
        companyDao.create(company);

        // Create admin with hashed password
        Admin admin = new Admin();
        admin.setEmail(email);
        admin.setPassword(BCrypt.hashpw(password, BCrypt.gensalt()));
        admin.setCompany(company);
        adminDao.create(admin);

        return true;
    }

    public static Admin getCurrentAdmin() {
        return currentAdmin;
    }

    public static void logout() {
        currentAdmin = null;
    }
}