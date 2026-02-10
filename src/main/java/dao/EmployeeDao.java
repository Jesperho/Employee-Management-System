package dao;

import entity.Employee;

public class EmployeeDao extends BaseDao<Employee> {
    public EmployeeDao() {
        super(Employee.class);
    }

    // Add Employee-specific methods here if needed
}
