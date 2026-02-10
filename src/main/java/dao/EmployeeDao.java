package dao;

import entity.Employee;
import java.util.List;

public interface EmployeeDao {

    void create(Employee employee);

    Employee find(int id);

    void update(Employee employee);

    void delete(int id);

    List<Employee> findAll();
}
