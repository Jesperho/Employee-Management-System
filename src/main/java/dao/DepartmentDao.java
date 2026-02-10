package dao;

import entity.Department;
import java.util.List;

public interface DepartmentDao {

    void create(Department department);

    Department find(int id);

    void update(Department department);

    void delete(int id);

    List<Department> findAll();
}
