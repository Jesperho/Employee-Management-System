package dao;

import entity.Company;
import entity.Department;

public class DepartmentDao extends BaseDao<Department> {
    public DepartmentDao() {
        super(Department.class);
    }

    // Add Company-specific methods here if needed
}
