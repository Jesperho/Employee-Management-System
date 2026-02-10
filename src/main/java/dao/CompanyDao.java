package dao;

import entity.Company;

public class CompanyDao extends BaseDao<Company> {
    public CompanyDao() {
        super(Company.class);
    }

    // Add Company-specific methods here if needed
}
